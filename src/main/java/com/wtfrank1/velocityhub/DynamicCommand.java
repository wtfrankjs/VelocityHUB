package com.wtfrank1.velocityhub;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DynamicCommand implements SimpleCommand {

    private final ProxyServer server;
    private final ConfigManager.CommandConfig commandConfig;
    private final ConfigManager configManager;

    public DynamicCommand(ProxyServer server, ConfigManager.CommandConfig commandConfig, ConfigManager configManager) {
        this.server = server;
        this.commandConfig = commandConfig;
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!commandConfig.isEnabled()) {
            sendMessage(invocation.source(), "command.disabled");
            return;
        }

        if (commandConfig.getPermission() != null && !invocation.source().hasPermission(commandConfig.getPermission())) {
            sendMessage(invocation.source(), "command.no_permission");
            return;
        }

        if (invocation.source() instanceof Player player) {
            String commandToExecute = commandConfig.getTargetServer();
            player.getCurrentServer().ifPresent(serverConnection -> {
                byte[] message = prepareMessage(commandToExecute);
                if (message != null) {
                    serverConnection.sendPluginMessage(
                            MinecraftChannelIdentifier.create("velocity", "forward"),
                            message
                    );
                    sendMessage(player, "command.success");
                } else {
                    sendMessage(player, "command.message_error");
                }
            });
        } else {
            sendMessage(invocation.source(), "command.player_only");
        }
    }

    private byte[] prepareMessage(String command) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out)) {
            dataOut.writeUTF("forward");
            dataOut.writeUTF("ALL");
            dataOut.writeUTF(command);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendMessage(CommandSource source, String messageKey) {
        String message = configManager.getMessage(messageKey);
        source.sendMessage(Component.text(configManager.getPrefix() + message));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return commandConfig.getPermission() == null || invocation.source().hasPermission(commandConfig.getPermission());
    }
}
