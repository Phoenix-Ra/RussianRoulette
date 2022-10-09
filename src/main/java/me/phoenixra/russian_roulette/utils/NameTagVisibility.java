package me.phoenixra.russian_roulette.utils;

import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NameTagVisibility {
    private static HashMap<Game,List<Player>> invisible=new HashMap<>();

    public static void setVisibility(Game game, Player source, boolean value){
        for(Player p : game.getPlayers()) {
            Team team = p.getScoreboard().getTeam("nameTag");
            if (team == null) {
                team = p.getScoreboard().registerNewTeam("nameTag");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }

            if (value){
                team.removeEntry(source.getName());
                if(invisible.containsKey(game)) invisible.get(game).remove(source);
            }
            else {
                team.addEntry(source.getName());

                if(!invisible.containsKey(game)) invisible.put(game,new ArrayList<>());
                invisible.get(game).add(source);
            }
        }
        for(Player p : game.getSpectators()) {
            Team team = p.getScoreboard().getTeam("nameTag");
            if (team == null) {
                team = p.getScoreboard().registerNewTeam("nameTag");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }

            if (value){
                team.removeEntry(source.getName());
                if(invisible.containsKey(game)) invisible.get(game).remove(source);
            }
            else {
                team.addEntry(source.getName());

                if(!invisible.containsKey(game)) invisible.put(game,new ArrayList<>());
                invisible.get(game).add(source);
            }
        }
    }

    public static void addPlayer(Game game, Player player){
        Team team = player.getScoreboard().getTeam("nameTag");
        if (team == null) {
            team = player.getScoreboard().registerNewTeam("nameTag");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        if(!invisible.containsKey(game)) return;

        for(Player source : invisible.get(game)) {
            team.addEntry(source.getName());
        }
    }
}
