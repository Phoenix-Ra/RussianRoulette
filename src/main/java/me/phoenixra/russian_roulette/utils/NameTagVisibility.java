package me.phoenixra.russian_roulette.utils;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class NameTagVisibility {
    private static HashMap<Player, ArmorStand> invisible=new HashMap<>();

    public static void setVisibility(Player player, boolean visibility){
        if(visibility&&invisible.containsKey(player)){
            invisible.get(player).remove();
            invisible.remove(player);
        }else if(!visibility && !invisible.containsKey(player)){
            ArmorStand armorStand=player.getLocation().getWorld().spawn(player.getLocation().clone().subtract(0.0, 0, 0.0), ArmorStand.class);
            armorStand.addPassenger(player);
            armorStand.setCustomNameVisible(false);
            invisible.put(player,armorStand);
        }
    }
}
