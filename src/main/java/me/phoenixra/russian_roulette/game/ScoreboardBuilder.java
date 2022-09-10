package me.phoenixra.russian_roulette.game;

import me.phoenixra.russian_roulette.RussianRoulette;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class ScoreboardBuilder {
    public static ArrayList<Player> players = new ArrayList<>();

    public void CreateScoreboard(Game g, Player p, ScoreboardType type) {
        if(type==ScoreboardType.STARTING) {
            StartingScoreboard(g,p);

        }
        if(type==ScoreboardType.GAME) {
            GameScoreboard(g,p);

        }
        if(type==ScoreboardType.SPECTATOR) {
            SpectatorScoreboard(g,p);

        }


    }

    public void removeScoreboard(Player p) {
        players.remove(p);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }
    private void SpectatorScoreboard(Game g,Player p) {
        ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Board", "Dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team[] sb = new Team[6];
        objective.setDisplayName("§eRussian§cRoulette");

        objective.getScore(" ").setScore(12);

        sb[0] = scoreboard.registerNewTeam("sb0");
        sb[0].addEntry("§fSpectators: ");
        sb[0].setSuffix(StrictToAllowedSymbolsAmount("§b"+String.valueOf(g.getSpectators().size())));
        sb[0].setPrefix("");
        objective.getScore("§fSpectators: ").setScore(11);

        objective.getScore("  ").setScore(10);

        sb[1] = scoreboard.registerNewTeam("sb1");
        sb[1].addEntry("§fPlaying: ");
        sb[1].setSuffix(StrictToAllowedSymbolsAmount("§b"+ g.getPlayers().size()));
        sb[1].setPrefix("");
        objective.getScore("§fPlaying: ").setScore(9);

        objective.getScore("   ").setScore(8);

        sb[2] = scoreboard.registerNewTeam("sb2");
        sb[2].addEntry("§fRound: ");
        sb[2].setSuffix(StrictToAllowedSymbolsAmount("§b"+g.getRound().toString()));
        sb[2].setPrefix("");
        objective.getScore("§fRound: ").setScore(7);

        objective.getScore("    ").setScore(6);


        sb[3] = scoreboard.registerNewTeam("sb3");
        sb[3].addEntry("§fCurrent shooter: ");
        sb[3].setSuffix(StrictToAllowedSymbolsAmount(g.getShooter().getName()));
        sb[3].setPrefix("");
        objective.getScore("§fCurrent shooter: ").setScore(5);

        objective.getScore("     ").setScore(4);


        objective.getScore("        ").setScore(3);

        objective.getScore("§fMap: §b" + g.getArena().getArenaName()).setScore(2);
        objective.getScore("       ").setScore(1);
        objective.getScore("§eyour.website.com").setScore(0);

        players.add(p);
        new BukkitRunnable() {


            @Override
            public void run() {
                try {
                    if(p.isOnline()&& players.contains(p)) {
                        sb[0].setSuffix(StrictToAllowedSymbolsAmount("§b" + g.getSpectators().size()));
                        sb[1].setSuffix(StrictToAllowedSymbolsAmount("§b" + g.getPlayers().size()));
                        sb[2].setSuffix(StrictToAllowedSymbolsAmount("§b" + g.getRound().toString()));
                        if(g.getShooter()!=null) {
                            if(g.getShooter().isDead()) {
                                sb[3].setSuffix(StrictToAllowedSymbolsAmount("§c§m"+g.getShooter().getName()));
                            }else {
                                sb[3].setSuffix(StrictToAllowedSymbolsAmount("§b"+g.getShooter().getName()));
                            }
                        }else {
                            sb[3].setSuffix(StrictToAllowedSymbolsAmount("§bNone"));
                        }
                    }
                    else {
                        cancel();

                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimer(RussianRoulette.getInstance(), 0, 10);

        p.setScoreboard(scoreboard);
    }

    private void StartingScoreboard(Game g,Player p) {
        ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Board", "Dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team sb[] = new Team[2];
        objective.setDisplayName("§eRussian§cRoulette");

        objective.getScore(" ").setScore(7);

        sb[0] = scoreboard.registerNewTeam("sb0");
        sb[0].addEntry("§fPlayers: ");
        sb[0].setSuffix("§b"+String.valueOf(g.getPlayers().size())+"/"+String.valueOf(g.getArena().getMaxPlayers()));
        sb[0].setPrefix("");
        objective.getScore("§fPlayers: ").setScore(6);

        objective.getScore(" ").setScore(5);

        sb[1] = scoreboard.registerNewTeam("sb1");
        sb[1].addEntry("§fStart in: ");
        sb[1].setSuffix("§b"+ g.getTimer().getCurrentTimer().timeLeft());
        sb[1].setPrefix("");
        objective.getScore("§fStart in: ").setScore(4);

        objective.getScore("   ").setScore(3);

        objective.getScore("§fMap: §b" + g.getArena().getArenaName()).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§eyour.website.com").setScore(0);

        players.add(p);
        new BukkitRunnable() {


            @Override
            public void run() {
                try {
                    if(p.isOnline()&&players.contains(p)) {
                        sb[0].setSuffix("§b"+ g.getPlayers().size() +"/"+ g.getArena().getMaxPlayers());
                        if(g.getTimer().getCurrentTimer() != GameTask.Timer.DEFAULT) {
                            sb[1].setSuffix("§b"+ g.getTimer().getCurrentTimer().timeLeft());
                        }
                        else {
                            sb[1].setSuffix("§bPending for players..");
                        }
                    }
                    else {
                        cancel();

                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimer(RussianRoulette.getInstance(), 0, 10);

        p.setScoreboard(scoreboard);

    }

    private void GameScoreboard(Game g, Player p) {
        ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Board", "Dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team sb[] = new Team[6];
        objective.setDisplayName("§eRussian§cRoulette");

        objective.getScore(" ").setScore(15);

        sb[0] = scoreboard.registerNewTeam("sb0");
        sb[0].addEntry("§fSpectators: ");
        sb[0].setSuffix(StrictToAllowedSymbolsAmount("§b"+ g.getSpectators().size()));
        sb[0].setPrefix("");
        objective.getScore("§fSpectators: ").setScore(14);

        objective.getScore("  ").setScore(13);

        sb[1] = scoreboard.registerNewTeam("sb1");
        sb[1].addEntry("§fPlaying: ");
        sb[1].setSuffix(StrictToAllowedSymbolsAmount("§b"+ g.getPlayers().size()));
        sb[1].setPrefix("");
        objective.getScore("§fPlaying: ").setScore(12);

        objective.getScore("   ").setScore(11);

        sb[2] = scoreboard.registerNewTeam("sb2");
        sb[2].addEntry("§fRound: ");
        sb[2].setSuffix(StrictToAllowedSymbolsAmount("§b"+g.getRound().toString()));
        sb[2].setPrefix("");
        objective.getScore("§fRound: ").setScore(10);

        objective.getScore("    ").setScore(9);


        sb[3] = scoreboard.registerNewTeam("sb3");
        sb[3].addEntry("§fCurrent shooter: ");
        sb[3].setSuffix(StrictToAllowedSymbolsAmount("§bNone"));
        sb[3].setPrefix("");
        objective.getScore("§fCurrent shooter: ").setScore(8);

        objective.getScore("     ").setScore(7);

        sb[4] = scoreboard.registerNewTeam("sb4");
        sb[4].addEntry("§fBullets: ");
        sb[4].setSuffix(StrictToAllowedSymbolsAmount("§b"+ g.getAlgorithm().getBulletsPlaced(p)));
        sb[4].setPrefix("");
        objective.getScore("§fBullets: ").setScore(6);

        objective.getScore("       ").setScore(5);

        sb[5] = scoreboard.registerNewTeam("sb5");
        sb[5].addEntry("§fLuck bonus: ");
        sb[5].setSuffix(StrictToAllowedSymbolsAmount("§b" + g.getAlgorithm().getGiftChance(p) + "%" + "/10%"));
        sb[5].setPrefix("");
        objective.getScore("§fLuck bonus: ").setScore(4);

        objective.getScore("        ").setScore(3);

        objective.getScore("§fMap: §b" + g.getArena().getArenaName()).setScore(2);
        objective.getScore("       ").setScore(1);
        objective.getScore("§eyour.website.com").setScore(0);

        players.add(p);
        new BukkitRunnable() {


            @Override
            public void run() {
                try {
                    if(p.isOnline()&& players.contains(p)) {
                        sb[0].setSuffix(StrictToAllowedSymbolsAmount("§b"+ g.getSpectators().size()));
                        sb[1].setSuffix(StrictToAllowedSymbolsAmount("§b"+ g.getPlayers().size()));
                        sb[2].setSuffix(StrictToAllowedSymbolsAmount("§b"+g.getRound().toString()));
                        if(g.getShooter()!=null) {
                            if(g.getShooter().isDead()) {
                                sb[3].setSuffix(StrictToAllowedSymbolsAmount("§c§m"+g.getShooter().getName()));
                            }else {
                                sb[3].setSuffix(StrictToAllowedSymbolsAmount("§b"+g.getShooter().getName()));
                            }
                        }else {
                            sb[3].setSuffix(StrictToAllowedSymbolsAmount("§bNone"));
                        }
                        sb[4].setSuffix(StrictToAllowedSymbolsAmount("§b" + g.getAlgorithm().getBulletsPlaced(p)));
                        sb[5].setSuffix(StrictToAllowedSymbolsAmount("§b" + g.getAlgorithm().getGiftChance(p) + "%" + "/10%"));
                    }
                    else {
                        cancel();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimer(RussianRoulette.getInstance(), 0, 10);

        p.setScoreboard(scoreboard);

    }
    private String StrictToAllowedSymbolsAmount(String s) {
        if(s.length()>15) {
            s = s.substring(0, 15);
        }
        return s;
    }



    public enum ScoreboardType {
        STARTING,
        GAME,
        SPECTATOR;

    }

}
