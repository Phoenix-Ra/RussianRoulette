package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.ConfigClass;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMiscellaneous implements Listener {
    public PlayerMiscellaneous(){
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }

    @EventHandler
    public void playerTeleported(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(p);
        if (game == null) return;

        if (game.getSpectators().contains(p)) {
            p.teleport(game.getArena().getSpectatorSpawn().getLocation());
            return;
        }
        p.teleport(game.getPlayerSeatLocation(p).getLocation());

    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event) {
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame((Player) event.getEntity());
        if (game != null) event.setCancelled(true);
    }

    @EventHandler
    public void playerCmd(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (RussianRoulette.getInstance().getGameM().getPlayerGame(player) == null) {
            return;
        }
        if (event.getMessage().equals("/sit")) {
            event.setMessage("/rr sit");
            return;
        }
        if (!player.hasPermission("rr.bypass")) {
            String string = event.getMessage();
            if (!ConfigClass.cmd_whitelist.contains(string.replace("/", ""))) {
                player.sendMessage(LangClass.general_no_permission);
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game == null) return;
        if (game.getSpectators().contains(player)) return;

        if (game.getShooter() == player || game.getVictim() == player) {
            Location to = event.getTo();
            if (to.getX() >= game.getArena().xMax
                    || to.getX() <= game.getArena().xMin
                    || to.getZ() >= game.getArena().zMax
                    || to.getZ() <= game.getArena().zMin)
                event.setTo(event.getFrom());
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())
            event.setTo(game.getPlayerSeatLocation(player).getLocation());


    }
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }
}
