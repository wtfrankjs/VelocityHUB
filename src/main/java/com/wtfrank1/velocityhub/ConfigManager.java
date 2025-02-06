package com.wtfrank1.velocityhub;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    private final Path configPath;
    private final Logger logger;
    private Map<String, CommandConfig> commands;
    private List<String> blockedCommands;
    private List<String> blockedWorlds;
    private String prefix;
    private Map<String, String> messages;

    public ConfigManager(Path dataDirectory, Logger logger) {
        this.configPath = dataDirectory.resolve("config.yml");
        this.logger = logger;
    }

    public void loadConfig() {
        logger.info("Loading configuration...");
        Yaml yaml = new Yaml();

        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath.getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                    writer.write(getDefaultConfig());
                }
                logger.info("Default configuration created at " + configPath);
            } catch (IOException e) {
                logger.error("Failed to create default configuration!", e);
                return;
            }
        }

        try {
            Map<String, Object> config = yaml.load(Files.newBufferedReader(configPath));

            // General settings
            prefix = (String) config.getOrDefault("prefix", "&6[VelocityHub]&r ");
            messages = new HashMap<>();
            Object messagesObj = config.getOrDefault("messages", new HashMap<>());
            if (messagesObj instanceof Map<?, ?>) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) messagesObj).entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                        messages.put((String) entry.getKey(), (String) entry.getValue());
                    }
                }
            }

            // Commands
            commands = new HashMap<>();
            Object commandsObj = config.get("commands");
            if (commandsObj instanceof Map<?, ?> rawCommands) {
                for (Map.Entry<?, ?> entry : rawCommands.entrySet()) {
                    if (entry.getKey() instanceof String commandName && entry.getValue() instanceof Map<?, ?> commandDataMap) {
                        Map<String, Object> commandData = new HashMap<>();
                        for (Map.Entry<?, ?> dataEntry : commandDataMap.entrySet()) {
                            if (dataEntry.getKey() instanceof String key) {
                                commandData.put(key, dataEntry.getValue());
                            }
                        }

                        String targetServer = (String) commandData.get("server");
                        boolean enabled = (Boolean) commandData.getOrDefault("enabled", true);
                        String permission = (String) commandData.getOrDefault("permission", null);
                        List<String> blockedWorlds = new ArrayList<>();
                        Object blockedWorldsObj = commandData.get("blocked-worlds");
                        if (blockedWorldsObj instanceof List<?>) {
                            for (Object obj : (List<?>) blockedWorldsObj) {
                                if (obj instanceof String) {
                                    blockedWorlds.add((String) obj);
                                }
                            }
                        }
                        List<String> blockedServers = new ArrayList<>();
                        Object blockedServersObj = commandData.get("blocked-servers");
                        if (blockedServersObj instanceof List<?>) {
                            for (Object obj : (List<?>) blockedServersObj) {
                                if (obj instanceof String) {
                                    blockedServers.add((String) obj);
                                }
                            }
                        }

                        commands.put(commandName, new CommandConfig(targetServer, enabled, permission, blockedWorlds, blockedServers));
                    }
                }
            } else {
                logger.warn("Invalid commands configuration in config.yml");
            }

            Object blockedCommandsObj = config.get("general.blocked-commands");
            if (blockedCommandsObj instanceof List<?>) {
                blockedCommands = new ArrayList<>();
                for (Object obj : (List<?>) blockedCommandsObj) {
                    if (obj instanceof String) {
                        blockedCommands.add((String) obj);
                    }
                }
            } else {
                blockedCommands = new ArrayList<>();
            }

            Object blockedWorldsObj = config.get("general.blocked-worlds");
            if (blockedWorldsObj instanceof List<?>) {
                blockedWorlds = new ArrayList<>();
                for (Object obj : (List<?>) blockedWorldsObj) {
                    if (obj instanceof String) {
                        blockedWorlds.add((String) obj);
                    }
                }
            } else {
                blockedWorlds = new ArrayList<>();
            }
            logger.info("Configuration loaded successfully!");
        } catch (IOException e) {
            logger.error("Failed to load configuration!", e);
        }
    }

    public Map<String, CommandConfig> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "&cMessage not configured.");
    }

    public boolean isCommandBlocked(String command) {
        return blockedCommands.contains(command.toLowerCase());
    }

    public boolean isWorldBlocked(String worldName) {
        return blockedWorlds.contains(worldName.toLowerCase());
    }

    private String getDefaultConfig() {
        return """
                prefix: "&6[VelocityHub]&r "
                messages:
                  success: "&aSuccessfully connected to the {server} server!"
                  blocked: "&cThis command is blocked in this world or server!"
                  error: "&cAn error occurred. Please try again later!"
                commands:
                  hub:
                    server: "lobby"
                    enabled: true
                    permission: "velocityhub.command.hub"
                    blocked-worlds:
                      - "nether"
                      - "end"
                    blocked-servers:
                      - "creative"
                  survival:
                    server: "survival"
                    enabled: true
                    permission: "velocityhub.command.survival"
                    blocked-worlds:
                      - "test_world"
                    blocked-servers:
                      - "lobby"
                general:
                  blocked-commands:
                    - "/op"
                    - "/reload"
                  blocked-worlds:
                    - "restricted_world"
                """;
    }

    public static class CommandConfig {
        private final String targetServer;
        private final boolean enabled;
        private final String permission;
        private final List<String> blockedWorlds;
        private final List<String> blockedServers;

        public CommandConfig(String targetServer, boolean enabled, String permission, List<String> blockedWorlds, List<String> blockedServers) {
            this.targetServer = targetServer;
            this.enabled = enabled;
            this.permission = permission;
            this.blockedWorlds = blockedWorlds;
            this.blockedServers = blockedServers;
        }

        public String getTargetServer() {
            return targetServer;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getPermission() {
            return permission;
        }

        public List<String> getBlockedWorlds() {
            return blockedWorlds;
        }

        public List<String> getBlockedServers() {
            return blockedServers;
        }
    }
}