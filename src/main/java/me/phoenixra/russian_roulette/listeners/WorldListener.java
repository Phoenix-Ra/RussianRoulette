package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {
    public WorldListener(){
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }


    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game != null) blockBreakEvent.setCancelled(true);

    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game != null) blockPlaceEvent.setCancelled(true);
    }
}
