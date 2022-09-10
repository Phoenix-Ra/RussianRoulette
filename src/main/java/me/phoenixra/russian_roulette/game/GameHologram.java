package me.phoenixra.russian_roulette.game;

import me.phoenixra.core.PhoenixHologram;
import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.files.ConfigClass;
import me.phoenixra.russian_roulette.files.LangClass;
import org.bukkit.Location;
import java.util.Random;

public class GameHologram extends PhoenixHologram {
    private boolean b=false;

    public GameHologram(Location loc) {
        super(loc);
    }

    public void setGameHolo(Game game) {

        if(game.getState()== Game.GameState.STARTING) {
            clearLines();
            b=false;
            if(game.getState()== Game.GameState.PENDING_FOR_PLAYERS) {
                for(String string: LangClass.hologram_PendingForPlayers) {
                    addLine(Replace(string,game));
                }
            }else {
                for(String s: LangClass.hologram_RoundDelay) {
                    addLine(Replace(s,game));
                }
            }
        }else if(game.getState()== Game.GameState.ACTIVE) {
            switch (game.getTimer().getCurrentTimer()){
                case NEXT_SHOOTER_DELAY:
                    if(game.getRoundCache()!=null&&!b) {
                        clearLines();
                        b=true;
                        Random r=new Random(System.nanoTime());
                        String line= game.getRoundCache().shootSuccess()?
                                        game.getSpectators().contains(game.getRoundCache().getVictim())?
                                                LangClass.hologram_killed_random.get(r.nextInt(LangClass.hologram_killed_random.size()-1))
                                                :
                                                LangClass.hologram_injured_random.get(r.nextInt(LangClass.hologram_injured_random.size()-1))
                                        :
                                        LangClass.hologram_misfire_random.get(r.nextInt(LangClass.hologram_misfire_random.size()-1));

                        addLine(line);
                    }
                    break;
                case NEXT_ROUND_DELAY:
                    clearLines();
                    b=false;
                    for(String s: LangClass.hologram_RoundDelay) {
                        addLine(Replace(s,game));
                    }
                    break;
                case SHOOTER_DECIDING:
                    clearLines();
                    b=false;
                    for(String s: LangClass.hologram_WaitingForShooter) {
                        addLine(Replace(s,game));
                    }
                    break;
                case BID_TIME:
                    clearLines();
                    b=false;
                    for(String s: LangClass.hologram_BetTime) {
                        addLine(Replace(s,game));
                    }
                    break;
            }
        }
    }
    private String Replace(String value, Game game) {
        return value.replace("%arena_name%", game.getArena().getArenaName())
                .replace("%min_players%", ""+game.getArena().getMinPlayers())
                .replace("%max_players%", ""+game.getArena().getMaxPlayers())
                .replace("%current_players%", ""+game.getPlayers().size())
                .replace("%round%", ""+game.getRound().toString())
                .replace("%shooting%",game.getShooter()!=null? game.getShooter().getName():"")
                .replace("%timer%", TimerReplace(game))
                .replace("%victim%", game.getShooter()!=null?(game.getVictim()==null?
                        game.getShooter().getName():game.getVictim().getName()):"")
                .replace("%chance%",game.getShooter()!=null?( game.getVictim()==null
                        ?game.getAlgorithm().getCurrentChanceToDie(game.getShooter())+"":""+game.getAlgorithm().
                        getCurrentChanceToKill(game.getShooter(),game.getVictim())):"");
    }
    private String TimerReplace(Game game) {
        if(game.getState()== Game.GameState.STARTING) {
            return game.getState()== Game.GameState.PENDING_FOR_PLAYERS ? "" : PhoenixUtils.getProgressBar(game.getTimer().getCurrentTimer().timeLeft(), ConfigClass.start_delay, 10, "\u2B1B", "§a", "§7");

        }else if(game.getState()== Game.GameState.ACTIVE) {
            int delay;
            switch (game.getTimer().getCurrentTimer()){
                case NEXT_SHOOTER_DELAY -> delay = ConfigClass.nextShooter_delay;
                case NEXT_ROUND_DELAY -> delay = ConfigClass.nextRound_delay;
                case SHOOTER_DECIDING -> delay = ConfigClass.waitingShooter_time;
                case BID_TIME -> delay = ConfigClass.bid_time;
                default -> delay = 0;
            }
            return PhoenixUtils.getProgressBar(game.getTimer().getCurrentTimer().timeLeft(), delay, 10, "\u2B1B", "§a", "§7");
        }
        return "";

    }
}
