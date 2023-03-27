package mod.tjt01.lapislib.core;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mod.tjt01.lapislib.LapisLib;
import mod.tjt01.lapislib.core.network.LapisLibPacketHandler;
import mod.tjt01.lapislib.core.network.OpenModListPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.PacketDistributor;

public class LapisLibCommands {
    protected static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(LapisLib.MODID)
                .then(OpenModListCommand.register())
        );
    }

    protected static final class OpenModListCommand implements Command<CommandSourceStack> {
        private static final OpenModListCommand COMMAND = new OpenModListCommand();

        public static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("openmodlist")
                    .requires(commandSourceStack -> commandSourceStack.hasPermission(0))
                    .executes(COMMAND);
        }

        @Override
        public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
            commandContext.getSource().sendSuccess(new TextComponent("Sample Text"), false);
            ServerPlayer player = commandContext.getSource().getPlayerOrException();
            LapisLibPacketHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player), new OpenModListPacket()
            );
            return SINGLE_SUCCESS;
        }
    }
}
