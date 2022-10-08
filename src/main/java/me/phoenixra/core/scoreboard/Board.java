package me.phoenixra.core.scoreboard;

import lombok.Getter;
import me.phoenixra.core.PhoenixUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class Board {
    @Getter private final String id;
    public int update = 0;
    protected HashMap<Player, List<String>> players = new HashMap<>();
    protected HashMap<Player, Scoreboard> sb = new HashMap<>();
    @Getter private TextReplacer replacer;
    @Getter private List<String> displayName;
    @Getter private List<String> scores;

    public Board(String id, List<String> displayName, List<String> scores) {
        this.id = id;
        this.displayName=new ArrayList<>();
        displayName.forEach(s -> this.displayName.add(PhoenixUtils.colorFormat(s)));
        this.scores=scores;
        replacer = new TextReplacer();

    }

    public void update() {


        for (Entry<Player, List<String>> entry : players.entrySet()) {
            try {

                if (!entry.getKey().isOnline()) continue;
                final Objective objective = sb.get(entry.getKey()).getObjective(this.id);
                ++this.update;
                if (this.update >= this.displayName.size()) {
                    this.update = 0;
                }
                objective.setDisplayName(displayName.get(update));

                for (int i = scores.size(); i > 0; --i) {
                    String old = players.get(entry.getKey()).get(i-1);
                    String s = replacer.replace(entry.getKey(), scores.get(scores.size() - i));
                    if (s.equals(old)||old.isBlank()) continue;
                    if (s.isBlank()) s = " ".repeat(i);


                    sb.get(entry.getKey()).resetScores(old);
                    players.get(entry.getKey()).set(i-1, s);
                    Score score = objective.getScore(s);
                    score.setScore(i-1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void setScoreboard(Player player) {
        try {

            sb.put(player, Bukkit.getScoreboardManager().getNewScoreboard());
            players.put(player, new ArrayList<>());
            Objective objective = sb.get(player).registerNewObjective(id, "dummy", PhoenixUtils.colorFormat(displayName.get(0)));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            for (int i = scores.size(); i > 0; --i)
                players.get(player).add(replacer.replace(player, scores.get(scores.size() - i)));

            for (int i = scores.size(); i > 0; --i) {
                String s = replacer.replace(player, scores.get(scores.size() - i));
                if (s.isBlank()) s = " ".repeat(i);

                objective.getScore(replacer.replace(player, s)).setScore(i-1);
                players.get(player).set(i-1, PhoenixUtils.colorFormat(s));
            }
            player.setScoreboard(sb.get(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeScoreboard(Player p) {
        players.remove(p);
        sb.remove(p);
        if (p.isOnline()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }



    public boolean has(Player player) {
        return player.getScoreboard() == sb.get(player);
    }


}