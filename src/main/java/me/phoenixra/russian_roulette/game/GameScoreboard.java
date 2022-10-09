package me.phoenixra.russian_roulette.game;

import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.core.scoreboard.Board;
import me.phoenixra.core.scoreboard.PlaceholderTask;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import org.bukkit.entity.Player;

import java.util.Collections;

public class GameScoreboard {
    public GameScoreboard(){
        loadBoards();
    }

    private void loadBoards(){
        RussianRoulette.getInstance().getBoardsManager().clearCache();
        PlaceholderTask placeholder_players=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";

                return game.getPlayers().size()+"";
            }

            @Override
            public String getPlaceholder() {
                return "%players%";
            }
        };
        PlaceholderTask placeholder_spectators=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";

                return game.getSpectators().size()+"";
            }

            @Override
            public String getPlaceholder() {
                return "%spectators%";
            }
        };
        PlaceholderTask placeholder_MaxPlayers=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";

                return game.getArena().getMaxPlayers()+"";
            }

            @Override
            public String getPlaceholder() {
                return "%players_max%";
            }
        };
        PlaceholderTask placeholder_timer=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                if(game.getState() != Game.GameState.PENDING_FOR_PLAYERS
                        && game.getState()!= Game.GameState.STARTING) return "&cGAME STARTED";

                if(game.getState() == Game.GameState.PENDING_FOR_PLAYERS) {
                    return "&l\u231B\u231B\u231B";
                }
                else {
                    return game.getTimer().getCurrentTimer().timeLeft()+"";
                }
            }

            @Override
            public String getPlaceholder() {
                return "%timer%";
            }
        };
        PlaceholderTask placeholder_round=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                if(game.getState()!= Game.GameState.ACTIVE&&
                        game.getState()!= Game.GameState.FINISHING) return "&cGAME WASN'T STARTED";
                return game.getRound().toString();
            }

            @Override
            public String getPlaceholder() {
                return "%round%";
            }
        };
        PlaceholderTask placeholder_shooter=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                if(game.getState()!= Game.GameState.ACTIVE&&
                        game.getState()!= Game.GameState.FINISHING) return "&cGAME WASN'T STARTED";

                if(game.getShooter()!=null) {
                    if(game.playerLives.get(game.getShooter())<1) {
                        return PhoenixUtils.colorFormat("&m")+game.getShooter().getName();
                    }else {
                        return game.getShooter().getName();
                    }
                }else {
                    return "\u274c";
                }
            }

            @Override
            public String getPlaceholder() {
                return "%shooter%";
            }
        };
        PlaceholderTask placeholder_victim=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                if(game.getState()!= Game.GameState.ACTIVE&&
                        game.getState()!= Game.GameState.FINISHING) return "&cGAME WASN'T STARTED";

                if(game.getVictim()!=null) {
                    if(game.playerLives.get(game.getVictim())<1) {
                        return PhoenixUtils.colorFormat("&m")+game.getVictim().getName();
                    }else {
                        return game.getVictim().getName();
                    }
                }else {
                    return "\u274c";
                }
            }

            @Override
            public String getPlaceholder() {
                return "%victim%";
            }
        };
        PlaceholderTask placeholder_luck=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                if(game.getState()!= Game.GameState.ACTIVE&&
                        game.getState()!= Game.GameState.FINISHING) return "&cGAME WASN'T STARTED";

                return game.getGameAlgorithm().getPlayerLuck(player)+"";
            }

            @Override
            public String getPlaceholder() {
                return "%luck%";
            }
        };
        PlaceholderTask placeholder_MaxLuck=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                if(game.getState()!= Game.GameState.ACTIVE&&
                        game.getState()!= Game.GameState.FINISHING) return "&cGAME WASN'T STARTED";

                return "10";
            }

            @Override
            public String getPlaceholder() {
                return "%luck_max%";
            }
        };
        PlaceholderTask placeholder_map=new PlaceholderTask() {
            @Override
            public String getReplacement(Player player) {
                Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
                if(game==null) return "&cNOT IN GAME";
                return game.getArena().getArenaName();
            }

            @Override
            public String getPlaceholder() {
                return "%map%";
            }
        };
        Board board= new Board("start",
                Collections.singletonList(LangClass.scoreboard_displayName_starting),
                LangClass.scoreboard_score_starting);
        board.getReplacer()
                .addPlaceholder(placeholder_players)
                .addPlaceholder(placeholder_spectators)
                .addPlaceholder(placeholder_MaxPlayers)
                .addPlaceholder(placeholder_timer)
                .addPlaceholder(placeholder_round)
                .addPlaceholder(placeholder_shooter)
                .addPlaceholder(placeholder_victim)
                .addPlaceholder(placeholder_luck)
                .addPlaceholder(placeholder_MaxLuck)
                .addPlaceholder(placeholder_map);
        RussianRoulette.getInstance().getBoardsManager().addBoard(board);

        board= new Board("game",
                Collections.singletonList(LangClass.scoreboard_displayName_game),
                LangClass.scoreboard_score_game);
        board.getReplacer()
                .addPlaceholder(placeholder_players)
                .addPlaceholder(placeholder_spectators)
                .addPlaceholder(placeholder_MaxPlayers)
                .addPlaceholder(placeholder_timer)
                .addPlaceholder(placeholder_round)
                .addPlaceholder(placeholder_shooter)
                .addPlaceholder(placeholder_victim)
                .addPlaceholder(placeholder_luck)
                .addPlaceholder(placeholder_MaxLuck)
                .addPlaceholder(placeholder_map);
        RussianRoulette.getInstance().getBoardsManager().addBoard(board);
        board= new Board("spectator",
                Collections.singletonList(LangClass.scoreboard_displayName_spectator),
                LangClass.scoreboard_score_spectator);
        board.getReplacer()
                .addPlaceholder(placeholder_players)
                .addPlaceholder(placeholder_spectators)
                .addPlaceholder(placeholder_MaxPlayers)
                .addPlaceholder(placeholder_timer)
                .addPlaceholder(placeholder_round)
                .addPlaceholder(placeholder_shooter)
                .addPlaceholder(placeholder_victim)
                .addPlaceholder(placeholder_luck)
                .addPlaceholder(placeholder_MaxLuck)
                .addPlaceholder(placeholder_map);
        RussianRoulette.getInstance().getBoardsManager().addBoard(board);
    }

    public static void applyScoreboard(Player player, ScoreboardType type) {
        if(type==ScoreboardType.STARTING) {
            RussianRoulette.getInstance().getBoardsManager().addPlayerToBoard(player,"start");

        }
        if(type==ScoreboardType.GAME) {
            RussianRoulette.getInstance().getBoardsManager().addPlayerToBoard(player,"game");
        }
        if(type==ScoreboardType.SPECTATOR) {
            RussianRoulette.getInstance().getBoardsManager().addPlayerToBoard(player,"spectator");

        }

    }

    public static void removeScoreboard(Player p) {
        RussianRoulette.getInstance().getBoardsManager().removePlayer(p);
    }


    public enum ScoreboardType {
        STARTING,
        GAME,
        SPECTATOR;

    }

}
