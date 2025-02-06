package com.wtfrank1.velocityhub;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;

@Plugin(id = "velocityhub", name = "VelocityHub", version = "1.0", authors = {"wtfrankjs"})
public class Main {

    private final ProxyServer server;
    private final Logger logger;
    private final ConfigManager configManager;

    @Inject
    public Main(ProxyServer server, Logger logger, @com.velocitypowered.api.plugin.annotation.DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.configManager = new ConfigManager(dataDirectory, logger);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("VelocityHub is starting...");

        // Kanal kaydı
        server.getChannelRegistrar().register("velocity:forward");
        // Konfigürasyon yükleme
        configManager.loadConfig();

        // Dinamik komutları kaydet
        Map<String, ConfigManager.CommandConfig> commands = configManager.getCommands();
        for (Map.Entry<String, ConfigManager.CommandConfig> entry : commands.entrySet()) {
            String commandName = entry.getKey();
            ConfigManager.CommandConfig commandConfig = entry.getValue();

            CommandMeta commandMeta = server.getCommandManager().metaBuilder(commandName).build();
            server.getCommandManager().register(commandMeta, new DynamicCommand(server, commandConfig, configManager));
        }

        // Reload komutu kaydı
        registerReloadCommand();

        logger.info("VelocityHub has started successfully!");
    }

    private void registerReloadCommand() {
        CommandMeta reloadMeta = server.getCommandManager().metaBuilder("vhreload").build();

        server.getCommandManager().register(reloadMeta, new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                CommandSource source = invocation.source();

                if (!source.hasPermission("velocityhub.admin.reload")) {
                    source.sendMessage(Component.text(configManager.getPrefix() + configManager.getMessage("admin.no_permission")));
                    return;
                }

                configManager.loadConfig();
                source.sendMessage(Component.text(configManager.getPrefix() + configManager.getMessage("config.reloaded")));
            }
        });
    }
}
