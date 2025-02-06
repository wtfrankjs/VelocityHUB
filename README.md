# VelocityHub Plugin - Simple Command Teleportation
Dependencies
Requires the VHUBBridge plugin for command forwarding to function properly.
Extract and put VHUBBridge into your Spigot/Bukkit Server's plugins folder.

## Description
VelocityHub is a Minecraft plugin designed to seamlessly manage server commands and teleportation within a Velocity proxy network. This plugin enables smooth communication between the Spigot server and the Velocity proxy, allowing commands to be executed on the proxy based on the player’s current world.

## Features:
Server Teleportation: Seamlessly transfers players between servers in the Velocity proxy network.
Server Blocking: Prevents commands from executing if the player is in a blocked server, ensuring controlled access.
Permission Control: Ensures only authorized users can execute specific commands or access designated servers.
Unlimited Configuration: Supports an unlimited number of custom commands and server connections for maximum flexibility.

## Installation
Download Required Files:
Download the VelocityHub and VHUBBridge plugin .jar files.
Add to Plugins Folder:
Place the VelocityHub.jar into the plugins directory of your Velocity server.
Place the VHUBBridge.jar into the plugins directory of your Spigot/Bukkit server.
Restart the Server:
Start or restart your servers to load the plugins.

## Configure:
Adjust settings in the config.yml from plugins/VelocityHub/config.yml file as needed.

## Usage
The plugin automatically forwards player commands to the Velocity proxy.
Commands are processed and checked against blocked servers and permissions.
If a command is set and player has permission, the plugin will send the player to the server is set.

## Commands:
exmaple: /hub: Teleports the player to the hub server.
/vhreload: Reloads the VelocityHub plugin configuration.
Permissions
velocityhub.use: Grants access to the commands.
velocityhub.reload: Grants access to the /vhreload command.
velocityhub.admin: Grants access to administrative commands and features.
