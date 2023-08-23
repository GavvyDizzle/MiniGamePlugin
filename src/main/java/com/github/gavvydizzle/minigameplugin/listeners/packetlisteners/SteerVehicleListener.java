package com.github.gavvydizzle.minigameplugin.listeners.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.github.gavvydizzle.minigameplugin.boards.SnakeBoard;
import com.github.gavvydizzle.minigameplugin.MiniGamePlugin;
import com.github.gavvydizzle.minigameplugin.boards._2048Board;
import com.github.gavvydizzle.minigameplugin.managers.SnakeManager;
import com.github.gavvydizzle.minigameplugin.managers._2048Manager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class SteerVehicleListener extends PacketAdapter implements PacketListener{

    public SteerVehicleListener(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player p = event.getPlayer();

        // If in the MiniGame world
        if (p.getWorld().getName().equalsIgnoreCase((MiniGamePlugin.GAME_WORLD_NAME))) {

            // If playing snake
            if ("snake".equalsIgnoreCase(Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING)))) {

                SnakeBoard b = null;
                for (SnakeBoard board : SnakeManager.getManager().getGameBoards()) {
                    if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                        b = board;
                        break;
                    }
                }
                // Check arena validity
                if (b == null) {
                    p.sendMessage("Play Command: No linked snake board");
                    return;
                }

                float leftRight = packet.getFloat().read(0);
                float forwardBackward = packet.getFloat().read(1);

                // If all are false it means there is currently no input
                boolean isHoldingLeft = leftRight > 0;
                boolean isHoldingRight = leftRight < 0;
                boolean isHoldingUp = forwardBackward > 0;
                boolean isHoldingDown = forwardBackward < 0;
                boolean isHoldingSpace = packet.getBooleans().read(0);

                // Sets direction of the snake with priority up -> down -> left -> right
                if (isHoldingUp) {
                    b.setMovingUp(true);
                    b.setMovingDown(false);
                    b.setMovingLeft(false);
                    b.setMovingRight(false);
                }
                else if (isHoldingDown) {
                    b.setMovingUp(false);
                    b.setMovingDown(true);
                    b.setMovingLeft(false);
                    b.setMovingRight(false);
                }
                else if (isHoldingLeft) {
                    b.setMovingUp(false);
                    b.setMovingDown(false);
                    b.setMovingLeft(true);
                    b.setMovingRight(false);
                }
                else if (isHoldingRight) {
                    b.setMovingUp(false);
                    b.setMovingDown(false);
                    b.setMovingLeft(false);
                    b.setMovingRight(true);
                }

                if (b.isGameOver() && isHoldingSpace) {
                    SnakeBoard finalB = b;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> SnakeManager.getManager().createGameBoard(p, finalB.getID() - 1), 1);
                }

                event.setCancelled(true);
            }
            // If playing 2048
            else if ("2048".equalsIgnoreCase(Objects.requireNonNull(p.getPersistentDataContainer().get(new NamespacedKey(MiniGamePlugin.getInstance(), "currentGame"), PersistentDataType.STRING)))) {
                _2048Board b = null;
                for (_2048Board board : _2048Manager.getManager().getGameBoards()) {
                    if (board != null && board.getPlayerUUID().equals(p.getUniqueId())) {
                        b = board;
                        break;
                    }
                }
                // Check arena validity
                if (b == null) {
                    p.sendMessage("Play Command: No linked 2048 board");
                    return;
                }

                float leftRight = packet.getFloat().read(0);
                float forwardBackward = packet.getFloat().read(1);

                // If all are false it means there is currently no input
                boolean isHoldingLeft = leftRight > 0;
                boolean isHoldingRight = leftRight < 0;
                boolean isHoldingUp = forwardBackward > 0;
                boolean isHoldingDown = forwardBackward < 0;
                boolean isHoldingSpace = packet.getBooleans().read(0);

                // Sets 2048 priority up -> down -> left -> right
                if (isHoldingUp && !b.isGameOver()) {
                    b.setMovingUp(true);
                    b.setMovingDown(false);
                    b.setMovingLeft(false);
                    b.setMovingRight(false);

                    _2048Board finalB = b;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        finalB.handleMove();
                        finalB.setHoldingDirection(true);
                    }, 0);
                }
                else if (isHoldingDown && !b.isGameOver()) {
                    b.setMovingUp(false);
                    b.setMovingDown(true);
                    b.setMovingLeft(false);
                    b.setMovingRight(false);

                    _2048Board finalB = b;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        finalB.handleMove();
                        finalB.setHoldingDirection(true);
                    }, 0);
                }
                else if (isHoldingLeft && !b.isGameOver()) {
                    b.setMovingUp(false);
                    b.setMovingDown(false);
                    b.setMovingLeft(true);
                    b.setMovingRight(false);

                    _2048Board finalB = b;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        finalB.handleMove();
                        finalB.setHoldingDirection(true);
                    }, 0);
                }
                else if (isHoldingRight && !b.isGameOver()) {
                    b.setMovingUp(false);
                    b.setMovingDown(false);
                    b.setMovingLeft(false);
                    b.setMovingRight(true);

                    _2048Board finalB = b;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        finalB.handleMove();
                        finalB.setHoldingDirection(true);
                    }, 0);
                }
                else {
                    b.setHoldingDirection(false);
                }

                if (b.isGameOver() && isHoldingSpace) {
                    _2048Board finalB = b;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> _2048Manager.getManager().createGameBoard(p, finalB.getID() - 1), 1);
                }

                event.setCancelled(true);
            }
        }
    }
}
