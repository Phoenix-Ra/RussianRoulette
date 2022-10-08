package me.phoenixra.russian_roulette.game;

import eu.decentsoftware.holograms.api.DecentHolograms;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.phoenixra.core.PhoenixHologram;
import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.files.ConfigClass;
import me.phoenixra.russian_roulette.files.LangClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class GameHologram extends PhoenixHologram {
    private String currentState="";

    public GameHologram(Location loc) {
        super(loc);
    }

    public void setGameHolo(Game game) {

        if(game.getState()== Game.GameState.STARTING||game.getState()== Game.GameState.PENDING_FOR_PLAYERS) {
            if(game.getState() == Game.GameState.PENDING_FOR_PLAYERS) {
                if(currentState.equals("start0")){
                    int i=0;
                    for(String s: LangClass.hologram_PendingForPlayers) {
                        changeLine(i,replacePlaceholders(s,game));
                        i++;
                    }
                }else {
                    clearLines();
                    for (String s : LangClass.hologram_PendingForPlayers) {
                        addLine(replacePlaceholders(s, game));
                    }
                    currentState = "start0";
                }
            }else {
                if(currentState.equals("start1")){
                    int i=0;
                    for(String s: LangClass.hologram_RoundDelay) {
                        changeLine(i,replacePlaceholders(s,game));
                        i++;
                    }
                }else {
                    clearLines();
                    for(String s: LangClass.hologram_RoundDelay) {
                        addLine(replacePlaceholders(s,game));
                    }
                    currentState = "start1";
                }
            }
        }else if(game.getState()== Game.GameState.ACTIVE) {
            switch (game.getTimer().getCurrentTimer()){
                case NEXT_SHOOTER_DELAY:
                    if(game.getRoundCache()!=null&&!currentState.equals("active0")) {
                        clearLines();
                        Random r=new Random(System.nanoTime());
                        String line= game.getRoundCache().shootSuccess()?
                                        game.getSpectators().contains(game.getRoundCache().getVictim())?
                                                LangClass.hologram_killed_random.get(r.nextInt(LangClass.hologram_killed_random.size()-1))
                                                :
                                                LangClass.hologram_injured_random.get(r.nextInt(LangClass.hologram_injured_random.size()-1))
                                        :
                                        LangClass.hologram_misfire_random.get(r.nextInt(LangClass.hologram_misfire_random.size()-1));

                        Material material=game.getRoundCache().shootSuccess()?
                                game.getSpectators().contains(game.getRoundCache().getVictim())?
                                        Material.SKELETON_SKULL
                                        :
                                        Material.FIRE_CHARGE
                                :
                                Material.COBWEB;

                        addItemLine(material);
                        addLine(line);
                        addItemLine(material);
                        currentState="active0";
                    }
                    break;
                case NEXT_ROUND_DELAY:
                    if(currentState.equals("active1")) {
                        int i=0;
                        for(String s: LangClass.hologram_RoundDelay) {
                            changeLine(i,replacePlaceholders(s,game));
                            i++;
                        }
                    }else {
                        clearLines();
                        for (String s : LangClass.hologram_RoundDelay) {
                            addLine(replacePlaceholders(s, game));
                        }
                        currentState="active1";
                    }
                    break;
                case SHOOTER_DECIDING:
                    if(currentState.equals("active2")) {
                        int i=0;
                        for(String s: LangClass.hologram_WaitingForShooter) {
                            changeLine(i,replacePlaceholders(s,game));
                            i++;
                        }
                    }else {
                        clearLines();
                        for(String s: LangClass.hologram_WaitingForShooter) {
                            addLine(replacePlaceholders(s,game));
                        }
                        currentState="active2";
                    }
                    break;
                case BID_TIME:
                    if(currentState.equals("active3")) {
                        int i=0;
                        for(String s: LangClass.hologram_BetTime) {
                            changeLine(i,replacePlaceholders(s,game));
                            i++;
                        }
                    }else {
                        clearLines();
                        for(String s: LangClass.hologram_BetTime) {
                            addLine(replacePlaceholders(s,game));
                        }
                        currentState="active3";
                    }
                    break;
            }
        }
    }
    protected void clearCache(){
        currentState="";
        clearLines();
    }
    private String replacePlaceholders(String value, Game game) {
        return value
                .replace("%arena_name%", game.getArena().getArenaName())
                .replace("%min_players%", ""+game.getArena().getMinPlayers())
                .replace("%max_players%", ""+game.getArena().getMaxPlayers())
                .replace("%current_players%", ""+game.getPlayers().size())
                .replace("%round%", ""+game.getRound().toString())
                .replace("%shooting%",game.getShooter()!=null? game.getShooter().getName():"")
                .replace("%timer%", timerReplace(game))
                .replace("%victim%", game.getShooter()!=null?(game.getVictim()==null?
                        game.getShooter().getName():game.getVictim().getName()):"")
                .replace("%chance%",game.getShooter()!=null?( game.getVictim()==null
                        ?game.getGameAlgorithm().getCurrentChanceToDie(game.getShooter())+"":""+game.getGameAlgorithm().
                        getCurrentChanceToKill(game.getShooter(),game.getVictim())):"");
    }
    private String timerReplace(Game game) {
        if(game.getState()== Game.GameState.STARTING) {
            return game.getState()== Game.GameState.PENDING_FOR_PLAYERS ? "" : PhoenixUtils.getProgressBar(game.getTimer().getCurrentTimer().timeLeft(), ConfigClass.start_delay, 10, "\u2B1B", PhoenixUtils.colorFormat("&a"), PhoenixUtils.colorFormat("&7"));

        }else if(game.getState()== Game.GameState.ACTIVE) {
            int delay;
            switch (game.getTimer().getCurrentTimer()){
                case NEXT_SHOOTER_DELAY -> delay = ConfigClass.nextShooter_delay;
                case NEXT_ROUND_DELAY -> delay = ConfigClass.nextRound_delay;
                case SHOOTER_DECIDING -> delay = ConfigClass.waitingShooter_time;
                case BID_TIME -> delay = ConfigClass.bid_time;
                default -> delay = 0;
            }
            return PhoenixUtils.getProgressBar(game.getTimer().getCurrentTimer().timeLeft(), delay, 10, "\u2B1B", PhoenixUtils.colorFormat("&a"), PhoenixUtils.colorFormat("&7"));
        }
        return "";

    }
}
