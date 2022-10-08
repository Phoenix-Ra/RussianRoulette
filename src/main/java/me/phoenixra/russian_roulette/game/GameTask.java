package me.phoenixra.russian_roulette.game;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.ConfigClass;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.utils.GameSounds;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {
    private final Game game;

    private Timer timer;

    GameTask(Game game) {
        this.game = game;
        timer = Timer.DEFAULT;
        this.runTaskTimer(RussianRoulette.getInstance(), 0L, 20L);
    }


    @Override
    public void run() {
        Bukkit.getConsoleSender().sendMessage(getState()+":"+timer.timeLeft());
        switch (getState()) {
            case 2 -> {
                if(timer != Timer.STARTING_GAME) setTimer(Timer.STARTING_GAME);
                if (timer.timeLeft() > 0) {
                    timer.update();
                } else {
                    game.broadcastSound(GameSounds.gameStart(), true);
                    game.startGame();
                }
                if (timer.timeLeft() == 30 || timer.timeLeft() == 15 || (timer.timeLeft() < 6 && timer.timeLeft() > 0)) {
                    game.broadcastMessage(LangClass.messages_startTimer.replace("%timer%", timer.timeLeft() + ""));
                    game.broadcastSound(GameSounds.timeTick(), true);
                }
            }
            case 3 -> {
                if (game.getPlayers().size() < 2) {
                    game.setState(Game.GameState.FINISHING);
                    setTimer(Timer.FINISH_DELAY);
                }
                if (timer == Timer.NEXT_SHOOTER_DELAY) {
                    if (timer.timeLeft() > 0) {
                        timer.update();
                    } else {
                        setTimer(Timer.SHOOTER_DECIDING);
                        game.nextShooter();
                    }
                } else if (timer == Timer.NEXT_ROUND_DELAY) {
                    if (timer.timeLeft() > 0) {
                        timer.update();
                        if (timer.timeLeft() == 5) {
                            game.broadcastSound(GameSounds.roundChange(), true);
                            if (game.getRound() == Game.GameRound.FIRST) {
                                game.broadcastTitle(LangClass.titles_secondRoundStart_title, LangClass.titles_secondRoundStart_subtitle, true);
                            } else {
                                game.broadcastTitle(LangClass.titles_finalRoundStart_title, LangClass.titles_finalRoundStart_subtitle, true);
                            }
                        }
                    } else {
                        game.nextRound();
                    }
                } else if (timer == Timer.SHOOTER_DECIDING) {
                    if (!game.getPlayers().contains(game.getShooter())) {
                        game.broadcastMessage(LangClass.messages_playerEscaped.replace("%player%", game.getShooter().getName()));
                        game.nextShooter();
                        break;
                    }
                    timer.update();
                    if (timer.timeLeft() <= 0) {
                        setTimer(Timer.BID_TIME);
                        game.broadcastMessage(LangClass.messages_shooterDecideTooLong.replace("%shooter%", game.getShooter().getName()));
                        game.getShooter().closeInventory();
                        game.getGameAlgorithm().shootItselfPrepare(game.getShooter());

                        break;
                    }
                    game.getShooter().setLevel(timer.timeLeft());
                    game.getShooter().setExp((float) (timer.timeLeft()) / ConfigClass.waitingShooter_time);
                } else if (timer == Timer.BID_TIME) {
                    timer.update();
                    if (!game.getPlayers().contains(game.getShooter())) {
                        game.broadcastMessage(LangClass.messages_playerEscaped.replace("%player%", game.getShooter().getName()));
                        game.nextShooter();
                    }
                    if (timer.timeLeft() <= 0) {
                        setTimer(Timer.NEXT_SHOOTER_DELAY);
                        game.getGameAlgorithm().endBid();

                    }
                }
            }
            case 4 -> {
                if (timer != Timer.FINISH_DELAY) setTimer(Timer.FINISH_DELAY);
                if (timer.timeLeft() > 0 && game.getPlayers().size() > 0) {
                    timer.update();
                } else {
                    game.finishGame();
                    break;
                }
                if (timer.timeLeft() == ConfigClass.finish_delay - 1) {
                    StringBuilder message = new StringBuilder();
                    LangClass.messages_finish.forEach(line ->
                            message.append(line.replace("%winner%", game.getPlayers().get(0).getName()))
                                    .append("\n")
                    );
                    game.broadcastMessage(message.toString());

                    game.broadcastTitle("&a\u2728\u2B50\u2728", "", false);

                }
                if (timer.timeLeft() == 9) {
                    for (Player player : game.getPlayers()) {
                        game.launchFireworks(player, Color.GREEN);
                        game.launchFireworks(player, Color.BLUE);
                    }
                } else if (timer.timeLeft() == 8) {
                    for (Player player : game.getPlayers()) {
                        game.launchFireworks(player, Color.RED);
                        game.launchFireworks(player, Color.AQUA);
                    }
                } else if (timer.timeLeft() == 7) {
                    for (Player player : game.getPlayers()) {
                        game.launchFireworks(player, Color.PURPLE);
                        game.launchFireworks(player, Color.OLIVE);
                    }
                } else if (timer.timeLeft() == 6) {
                    for (Player player : game.getPlayers()) {
                        game.launchFireworks(player, Color.FUCHSIA);
                        game.launchFireworks(player, Color.ORANGE);
                    }
                } else if (timer.timeLeft() == 5) {
                    for (Player player : game.getPlayers()) {
                        game.launchFireworks(player, Color.LIME);
                        game.launchFireworks(player, Color.YELLOW);
                    }
                    game.broadcastMessage(LangClass.messages_teleport.replace("%timer%", "5"));
                } else if (timer.timeLeft() == 4)
                    game.broadcastMessage(LangClass.messages_teleport.replace("%timer%", "4"));
                else if (timer.timeLeft() == 3)
                    game.broadcastMessage(LangClass.messages_teleport.replace("%timer%", "3"));
                else if (timer.timeLeft() == 2)
                    game.broadcastMessage(LangClass.messages_teleport.replace("%timer%", "2"));
                else if (timer.timeLeft() == 1)
                    game.broadcastMessage(LangClass.messages_teleport.replace("%timer%", "1"));
            }
        }
        game.getGameHologram().setGameHolo(game);

    }

    private int getState() {
        if (game.getState() == Game.GameState.DISABLED) {
            return 0;
        }
        if (game.getState() == Game.GameState.PENDING_FOR_PLAYERS) {
            return 1;
        }
        if (game.getState() == Game.GameState.STARTING) {
            return 2;
        }
        if (game.getState() == Game.GameState.ACTIVE) {
            return 3;
        }
        if (game.getState() == Game.GameState.FINISHING) {
            return 4;
        }
        return 0;
    }

    public void setTimer(Timer timer){
        this.timer=timer;
        this.timer.resetTimer();
    }

    public Timer getCurrentTimer() {
        return timer;
    }



    public enum Timer {
        DEFAULT(-1),
        STARTING_GAME(ConfigClass.start_delay),
        BID_TIME(ConfigClass.bid_time),
        SHOOTER_DECIDING(ConfigClass.waitingShooter_time),
        NEXT_SHOOTER_DELAY(ConfigClass.nextShooter_delay),
        NEXT_ROUND_DELAY(ConfigClass.nextRound_delay),
        FINISH_DELAY(ConfigClass.finish_delay);

        private final int DURATION;
        private int timer;

        Timer(int duration) {
            DURATION=duration;
            timer = duration;
        }

        private void update() {
            timer--;
        }
        private void resetTimer() {
            timer=DURATION;
        }

        protected int timeLeft() {
            return timer;
        }

    }

}
