package com.wtfrank1.velocityhub;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class EventListener {
    private final ConfigManager configManager;
    private final ProxyServer server;

    public EventListener(ConfigManager configManager, ProxyServer server) {
        this.configManager = configManager;
        this.server = server;
    }

}
