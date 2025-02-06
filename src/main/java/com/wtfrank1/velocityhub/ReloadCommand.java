package com.wtfrank1.velocityhub;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import com.velocitypowered.api.proxy.ProxyServer;

public class ReloadCommand implements SimpleCommand {

    private final ProxyServer server;
    private final ConfigManager configManager;

    public ReloadCommand(ProxyServer server, ConfigManager configManager) {
        this.server = server;
        this.configManager = configManager;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text(configManager.getPrefix() + "This command can only be executed by a player."));
            return;
        }

        if (!player.hasPermission("velocityhub.admin.reload")) {
            player.sendMessage(Component.text(configManager.getPrefix() + "You do not have permission to execute this command."));
            return;
        }

        // Konfigürasyonu yeniden yükleyin
        configManager.loadConfig();

        // Komut başarılı olduğunda mesajı gönderin
        player.sendMessage(Component.text(configManager.getPrefix() + "Configuration reloaded successfully!"));
    }
}
