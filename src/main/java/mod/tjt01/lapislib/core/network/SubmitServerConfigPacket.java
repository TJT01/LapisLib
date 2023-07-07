package mod.tjt01.lapislib.core.network;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.client.config.ConfigChangeTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SubmitServerConfigPacket {
    protected static record UnknownEnum(int val){
        protected <T extends Enum<T>> T cast(Class<T> clazz) {
            return clazz.getEnumConstants()[val];
        }
    }
    private static final byte TYPE_BOOLEAN = 0;
    private static final byte TYPE_BYTE = 1;
    private static final byte TYPE_SHORT = 2;
    private static final byte TYPE_INT = 3;
    private static final byte TYPE_LONG = 4;
    private static final byte TYPE_FLOAT = 5;
    private static final byte TYPE_DOUBLE = 6;
    private static final byte TYPE_STRING = 7;
    private static final byte TYPE_LIST = 8;
    private static final byte TYPE_ENUM = 9;

    protected final String modId;

    private final Map<String, Object> changes = new HashMap<>();

    private static List<Object> readList(FriendlyByteBuf buf) {
        short len = buf.readShort();
        ArrayList<Object> objects = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            objects.add(readValue(buf));
        }
        return objects;
    }

    private static Object readValue(FriendlyByteBuf buf) {
        byte type = buf.readByte();
        return switch (type) {
            case TYPE_BOOLEAN -> buf.readBoolean();
            case TYPE_BYTE -> buf.readByte();
            case TYPE_SHORT -> buf.readShort();
            case TYPE_INT -> buf.readInt();
            case TYPE_LONG -> buf.readLong();
            case TYPE_FLOAT -> buf.readFloat();
            case TYPE_DOUBLE -> buf.readDouble();
            case TYPE_STRING -> buf.readUtf();
            case TYPE_LIST -> readList(buf);
            case TYPE_ENUM -> new UnknownEnum(buf.readInt());
            default -> throw new IllegalArgumentException("Invalid type " + type);
        };
    }

    private static void writeList(FriendlyByteBuf buf, List<?> list) {
        buf.writeByte(TYPE_LIST);
        buf.writeShort(list.size());
        for (Object o: list) {
            writeValue(buf, o);
        }
    }

    private static void writeValue(FriendlyByteBuf buf, Object object) {
        if (object instanceof Boolean b) {
            buf.writeByte(TYPE_BOOLEAN);
            buf.writeBoolean(b);
        } else if (object instanceof Byte b) {
            buf.writeByte(TYPE_BYTE);
            buf.writeByte(b);
        } else if (object instanceof Short s) {
            buf.writeByte(TYPE_SHORT);
            buf.writeShort(s);
        } else if (object instanceof Integer i) {
            buf.writeByte(TYPE_INT);
            buf.writeInt(i);
        } else if (object instanceof Long l) {
            buf.writeByte(TYPE_LONG);
            buf.writeLong(l);
        } else if (object instanceof Float f) {
            buf.writeByte(TYPE_FLOAT);
            buf.writeFloat(f);
        } else if (object instanceof Double d) {
            buf.writeByte(TYPE_DOUBLE);
            buf.writeDouble(d);
        } else if (object instanceof String s) {
            buf.writeByte(TYPE_STRING);
            buf.writeUtf(s);
        } else if (object instanceof List<?> l) {
            writeList(buf, l);
        } else if (object instanceof Enum<?> e) {
            buf.writeByte(TYPE_ENUM);
            buf.writeInt(e.ordinal());
        } else throw new IllegalArgumentException("Unsupported type " + object.getClass().toString());
    }

    public static SubmitServerConfigPacket decode(FriendlyByteBuf buffer) {
        SubmitServerConfigPacket packet = new SubmitServerConfigPacket(buffer.readUtf());
        short len = buffer.readShort();
        for (int i = 0; i < len; i++) {
            String key = buffer.readUtf();
            packet.changes.put(key, readValue(buffer));
        }
        return packet;
    }

    private SubmitServerConfigPacket(String modId) {
        this.modId = modId;
    }

    public SubmitServerConfigPacket(String modId, ConfigChangeTracker tracker) {
        this(modId);
        this.changes.putAll(tracker.changes);
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeUtf(this.modId);
        buffer.writeShort(this.changes.size());
        for (String key: changes.keySet()) {
            buffer.writeUtf(key);
            writeValue(buffer, changes.get(key));
        }
    }

    @SuppressWarnings("unchecked")
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            NetworkEvent.Context ctx = context.get();
            if (!ctx.getSender().hasPermissions(2)) {
                ctx.setPacketHandled(true);
                return;
            }
            ModConfig config = null;
            try {
                config = (
                        (ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>>)
                                FieldUtils.readField(ConfigTracker.INSTANCE, "configsByMod", true)
                ).getOrDefault(modId, Collections.emptyMap()).getOrDefault(ModConfig.Type.SERVER, null);
            } catch (Throwable ignored) {}
            if (config == null) {
                LapisLib.LOGGER.warn("{} tried to update a config that doesn't exist", ctx.getSender().getName());
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
                CommentedConfig modified = CommentedConfig.copy(config.getConfigData());
                for (Map.Entry<String, Object> entry: changes.entrySet()) {
                    if (entry.getValue() instanceof UnknownEnum e) {
                        ForgeConfigSpec.ValueSpec s = spec.get(entry.getKey());
                        if (s.getDefault() instanceof Enum<?> en) {
                            modified.set(entry.getKey(), e.cast(en.getClass()));
                        }
                    } else {
                        modified.set(entry.getKey(), entry.getValue());
                    }
                }
                config.getConfigData().putAll(modified);
            } catch (Exception e) {
                e.printStackTrace();
            }

            spec.afterReload();
            try {
                Method m = ModConfig.class.getDeclaredMethod("fireEvent", IConfigEvent.class);
                m.setAccessible(true);
                m.invoke(config, new ModConfigEvent.Reloading(config));
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
