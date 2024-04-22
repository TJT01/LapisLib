package mod.tjt01.lapislib.core.network;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.electronwill.nightconfig.toml.TomlFormat;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class SubmitServerConfigPacket {
    protected final String fileName;

    private final CommentedConfig changes;

    public static SubmitServerConfigPacket decode(FriendlyByteBuf buffer) {
        String fileName = buffer.readUtf();
        CommentedConfig config = TomlFormat.instance().createParser().parse(
                new ByteArrayInputStream(buffer.readByteArray())
        );

        return new SubmitServerConfigPacket(fileName, config);
    }

    public SubmitServerConfigPacket(String fileName, CommentedConfig config) {
        this.fileName = fileName;
        this.changes = config;
    }

    public SubmitServerConfigPacket(String fileName, ConfigChangeTracker tracker) {
        this(fileName, tracker.getCommentedConfig());
    }

    public void encode(FriendlyByteBuf buffer){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TomlFormat.instance().createWriter().write(this.changes, outputStream);
        buffer.writeUtf(this.fileName);
        buffer.writeByteArray(outputStream.toByteArray());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkEvent.Context ctx = context.get();
            if (!ctx.getSender().hasPermissions(2)) {
                return;
            }
            ModConfig config = ConfigTracker.INSTANCE.fileMap().getOrDefault(fileName, null);
            if (config == null) {
                LapisLib.LOGGER.warn(
                        "{} tried to update a config that doesn't exist",
                        ctx.getSender().getName()
                );
                return;
            }
            if (config.getType() != ModConfig.Type.SERVER) {
                LapisLib.LOGGER.warn("{} tried to update a non-server config", ctx.getSender().getName());
                return;
            }
            ForgeConfigSpec spec = toForgeConfigSpec(config.getSpec());
            if (spec == null) {
                LapisLib.LOGGER.warn("Unknown config spec");
                return;
            }

            try {
                config.getConfigData().putAll(changes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            spec.afterReload();
            try {
                Method m = ModConfig.class.getDeclaredMethod("fireEvent", IConfigEvent.class);
                m.setAccessible(true);
                m.invoke(config, new ModConfigEvent.Reloading(config));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                TomlFormat.instance().createWriter().write(this.changes, outputStream);
                LapisLibPacketHandler.CHANNEL.send(
                        PacketDistributor.ALL.noArg(),
                        new ServerConfigResyncPacket(fileName, outputStream.toByteArray())
                );
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        context.get().setPacketHandled(true);
    }

    @Nullable
    public static ForgeConfigSpec toForgeConfigSpec(UnmodifiableConfig spec) {
        if (spec instanceof ForgeConfigSpec forgeConfigSpec) return forgeConfigSpec;
        if (spec instanceof UnmodifiableConfigWrapper<?> wrapper) {
            try {
                return toForgeConfigSpec((UnmodifiableConfig) FieldUtils.readField(wrapper, "config", true));
            } catch (Throwable ignored) {}
        }
        return null;
    }
}
