package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDamage implements Listener {
    public PlayerDamage() {
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game == null) {
            return;
        }
        for (Player p : game.getPlayers()) {
            for (Player s : game.getSpectators()) {
                p.hidePlayer(s);
            }
        }
        new BukkitRunnable() {
            public void run() {
                player.spigot().respawn();
                player.setGameMode(GameMode.ADVENTURE);
            }
        }.runTaskLater(RussianRoulette.getInstance(), 10L);
        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (RussianRoulette.getInstance().getGameM().getPlayerGame(player) != null) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
            if (game != null) event.setCancelled(true);

        }
    }

}
