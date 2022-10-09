package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerListener implements Listener {
    public ServerListener(){
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }


    @EventHandler
    public void onLoad(ServerLoadEvent event){
        if(event.getType() == ServerLoadEvent.LoadType.STARTUP)
            RussianRoulette.getInstance().loadArenas();
    }
}
