package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerSpectator implements Listener {
    public PlayerSpectator() {
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }

    @EventHandler
    public void gameModeChange(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
        final Player player = playerGameModeChangeEvent.getPlayer();
        final Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game != null) {
            if (playerGameModeChangeEvent.getNewGameMode() == GameMode.ADVENTURE) {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

            } else if (playerGameModeChangeEvent.getNewGameMode() == GameMode.SURVIVAL) {
                playerGameModeChangeEvent.getPlayer().setAllowFlight(false);
                playerGameModeChangeEvent.getPlayer().setFlying(false);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        final Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game == null) {
            return;
        }
        playerInteractEvent.setCancelled(true);

        if (game.getSpectators().contains(player)) {
            if (player.getInventory().getHeldItemSlot() == 8 || player.getInventory().getHeldItemSlot() == 0) {
                player.performCommand("rr leave");
            }

        }

    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (RussianRoulette.getInstance().getGameM().getPlayerGame(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

}
