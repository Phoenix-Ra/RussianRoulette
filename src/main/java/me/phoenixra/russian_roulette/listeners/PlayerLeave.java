package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.game.Game;
import me.phoenixra.russian_roulette.utils.NameTagVisibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    public PlayerLeave(){
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }
    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player p = event.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(p);
        if (game == null) {
            return;
        }
        game.playerLeave(p);
        NameTagVisibility.setVisibility(p,true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(p);
        if (game == null) {
            return;
        }
        game.playerLeave(p);
        NameTagVisibility.setVisibility(p,true);
    }
}
