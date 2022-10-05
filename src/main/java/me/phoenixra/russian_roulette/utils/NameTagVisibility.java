package me.phoenixra.russian_roulette.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.stream.Collectors;

public class NameTagVisibility {
    private static HashMap<Player, ArmorStand> invisible=new HashMap<>();

    public static void setVisibility(Player player, boolean visibility){
        /*@TODO
        Team team = new Team(new net.minecraft.world.scores.Scoreboard(), player.getName());
        team.setNameTagVisibility(EnumNameTagVisibility.b);
        team.getPlayerNameSet().addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        PlayerConnection connection = ((CraftPlayer) p).getHandle().b;
        connection.sendPacket(PacketPlayOutScoreboardTeam.a(team));
        connection.sendPacket(PacketPlayOutScoreboardTeam.a(team, true));*/
    }
}
