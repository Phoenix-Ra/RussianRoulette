package me.phoenixra.russian_roulette.game;

import lombok.Getter;
import lombok.Setter;
import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.utils.ItemBuilder;
import me.phoenixra.russian_roulette.utils.NameTagVisibility;
import me.phoenixra.russian_roulette.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Game {
    @Getter private final Arena arena;
    @Getter private final GameTask timer;

    @Getter private final List<Player> players = new ArrayList<>();
    @Getter private final List<Player> spectators = new ArrayList<>();
    private final HashMap<CustomLocation, Player> playerSeat = new HashMap<>();

    @Getter private final GameAlgorithm gameAlgorithm;
    @Getter private final BidAlgorithm bidAlgorithm;
    @Getter private final GameHologram gameHologram;

    private final int MAX_LIVES = 2;
    public HashMap<Player, Integer> playerLives = new HashMap<>();
    @Getter protected RoundCache roundCache;

    @Getter @Setter private GameState state;
    @Getter @Setter private GameRound round;
    @Getter private RoundState roundstate;

    @Getter @Setter private boolean shooting;
    private CustomLocation shooterSeat;
    private CustomLocation victimSeat;


    public Game(Arena arena) {
        this.arena = arena;

        gameAlgorithm = new GameAlgorithm(this);
        bidAlgorithm = new BidAlgorithm(this);

        state = GameState.PENDING_FOR_PLAYERS;
        round = GameRound.FIRST;
        roundstate = RoundState.NEXT_SHOOTER_DELAY;

        timer = new GameTask(Game.this);
        gameHologram = new GameHologram(this.getArena().getCenter().getLocation());
        gameHologram.setGameHolo(this);

    }

    public void startGame() {
        state = GameState.ACTIVE;
        round = GameRound.FIRST;
        for (Player p : this.getPlayers()) {
            p.getInventory().clear();
            GameScoreboard.applyScoreboard(p, GameScoreboard.ScoreboardType.GAME);
        }

        shooterSeat = victimSeat = findNextShooterSeat(true);

        teleportPlayerToCenter(playerSeat.get(shooterSeat));
        setupShooterInventory(playerSeat.get(shooterSeat));

        setRoundState(RoundState.SHOOTER_DECIDING, true);
    }
    public void forceStart(){
        if(state==GameState.PENDING_FOR_PLAYERS) setState(GameState.STARTING);
        if(state!=GameState.STARTING) return;
        if(timer.getCurrentTimer().timeLeft()<5) return;
        timer.getCurrentTimer().setTimer(5);
        for(Player player : players){
            player.getInventory().clear(0);
        }

    }

    public void enableGame() {
        state = GameState.PENDING_FOR_PLAYERS;
    }

    public void disableGame() {
        this.clearCache();
        state = GameState.DISABLED;
    }

    public void finishGame() {
        clearCache();
    }


    public void playerJoin(Player p) {
        if (state != GameState.PENDING_FOR_PLAYERS && state != GameState.STARTING) {
            addSpectator(p);
            return;
        }
        if (players.size() >= arena.getMaxPlayers()) {
            addSpectator(p);
            return;
        }

        players.add(p);
        CustomLocation seat = findFreeSeat();
        if (seat == null) {
            addSpectator(p);
            return;
        }
        playerSeat.put(seat, p);
        p.teleport(getPlayerSeatLocation(p).getLocation());
        RussianRoulette.getInstance().getSeatManager().setSitting(p, true);

        GameScoreboard.applyScoreboard(p, GameScoreboard.ScoreboardType.STARTING);

        gameAlgorithm.addPlayer(p);
        playerLives.put(p, MAX_LIVES);
        RussianRoulette.getInstance().getGameM().addPlayerToGame(this, p);

        p.setGameMode(GameMode.SURVIVAL);
        p.setLevel(0);
        p.setExp(0);
        p.setHealth(20);
        p.setExhaustion(0);
        p.setFoodLevel(20);
        p.setFlying(false);
        p.setAllowFlight(false);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        for (PotionEffect potionEffect : p.getActivePotionEffects()) {
            p.removePotionEffect(potionEffect.getType());
        }

        ItemStack itemStack = new ItemBuilder(Material.RED_BED).setDisplayName("&cLeave").getItem();
        p.getInventory().setItem(8, itemStack);
        if (p.isOp()) {
            itemStack = new ItemBuilder(Material.DIAMOND).setDisplayName("&eForce game start").getItem();
            p.getInventory().setItem(0, itemStack);
        }

        this.showAll(p);


        broadcastMessage(LangClass.messages_playerJoined.replace("%player%", p.getName()));
        Utils.separatePlayer(p);

        if (players.size() >= arena.getMinPlayers() && getState() == GameState.PENDING_FOR_PLAYERS) {
            setState(GameState.STARTING);
        }
    }

    public void playerLeave(Player p) {
        if (state == GameState.STARTING || state == GameState.PENDING_FOR_PLAYERS) {
            clearPlayerCache(p,true);
            if (players.size() < 2) {
                setState(GameState.PENDING_FOR_PLAYERS);
            }
            broadcastMessage(LangClass.messages_playerLeft.replace("%player%", p.getName()));

        } else {

            if (state == GameState.ACTIVE) {
                clearPlayerCache(p,true);
                if (!spectators.contains(p)) {
                    if (isShooting()) {
                        if (this.getShooter() == p) {
                            broadcastMessage(LangClass.messages_playerEscaped.replace("%player%", p.getName()));
                            this.nextShooter();

                        } else if (this.getVictim() == p) {
                            broadcastMessage(LangClass.messages_playerEscaped.replace("%player%", p.getName()));
                            this.nextShooter();
                        }
                        return;
                    }
                    broadcastMessage(LangClass.messages_playerLeft.replace("%player%", p.getName()));
                }

                return;
            }

            if (state == GameState.FINISHING) {
                clearPlayerCache(p,true);
                if (!this.getSpectators().contains(p)) {
                    broadcastMessage(LangClass.messages_playerLeft.replace("%player%", p.getName()));
                }

            }
        }
    }

    private void addSpectator(Player p) {
        RussianRoulette.getInstance().getGameM().addPlayerToGame(this, p);
        for (Player pp : this.players) {
            if (!pp.isOnline()) continue;
            pp.hidePlayer(p);
        }
        for (Player pp : this.spectators) {
            if (!pp.isOnline()) continue;
            pp.hidePlayer(p);
        }
        spectators.add(p);
        p.teleport(this.getArena().getSpectatorSpawn().getLocation());

        p.setGameMode(GameMode.ADVENTURE);
        p.setAllowFlight(true);
        p.setFlying(true);
        ItemStack itemStack = new ItemBuilder(Material.RED_BED).setDisplayName("&cLeave").getItem();
        p.getInventory().setItem(0, itemStack);
        p.getInventory().setItem(8, itemStack);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));

        GameScoreboard.applyScoreboard(p, GameScoreboard.ScoreboardType.SPECTATOR);
        //run next tick, to handle an exception with null scoreboard
        RussianRoulette.doSync(()->NameTagVisibility.addPlayer(this, p));

        Utils.separatePlayer(p);
        p.sendTitle(LangClass.titles_BecameSpectator, "");
    }


    public void nextRound() {
        setShooting(false);
        if (players.size() <= 1 || state == GameState.FINISHING) {
            return;
        }
        shooterSeat = victimSeat = findNextShooterSeat(true);

        round = round.nextRound(this);

        Player shooter = playerSeat.get(shooterSeat);
        teleportPlayerToCenter(shooter);
        setupShooterInventory(shooter);
        if (round == GameRound.FINAL) {
            Random r = new Random();
            getGameAlgorithm().setBulletsPlaced(shooter, r.nextInt(5) + 1);
            broadcastMessage(LangClass.messages_shooterBullets
                    .replace("%shooter%", shooter.getName())
                    .replace("%bullets%", getGameAlgorithm().getBulletsPlaced(shooter) + ""));
        }
        shooter.sendTitle(LangClass.titles_yourTurn, "");

        setRoundState(RoundState.SHOOTER_DECIDING, true);
    }

    public void nextShooter() {
        if (players.size() <= 1 || this.state == GameState.FINISHING) {
            return;
        }
        setShooting(false);
        if (getRoundCache() != null) {
            if (!spectators.contains(getRoundCache().getShooter())) {
                teleportPlayerBack(getShooter());
            }
            if (getRoundCache().getShooter() != getRoundCache().getVictim() && !spectators.contains(getRoundCache().getVictim())) {
                teleportPlayerBack(getRoundCache().getVictim());
            }
        }

        if (getArena().getSeatPoints().indexOf(shooterSeat) >= getArena().getSeatPoints().size()) {
            if (this.round != GameRound.FINAL) {
                this.getTimer().setTimer(GameTask.Timer.NEXT_ROUND_DELAY);
                return;
            }
            shooterSeat = victimSeat = findNextShooterSeat(true);

        } else {
            shooterSeat = victimSeat = findNextShooterSeat(false);
            if (shooterSeat == null) {
                if (this.round != GameRound.FINAL) {
                    this.getTimer().setTimer(GameTask.Timer.NEXT_ROUND_DELAY);
                    return;
                }
                shooterSeat = victimSeat = findNextShooterSeat(true);
            }

        }

        Player shooter = playerSeat.get(shooterSeat);
        this.teleportPlayerToCenter(shooter);
        this.setupShooterInventory(shooter);
        if (round == GameRound.FINAL) {
            Random r = new Random();
            getGameAlgorithm().setBulletsPlaced(shooter, r.nextInt(5) + 1);
            broadcastMessage(LangClass.messages_shooterBullets
                    .replace("%shooter%", shooter.getName())
                    .replace("%bullets%", getGameAlgorithm().getBulletsPlaced(shooter) + ""));
        } else if (round == GameRound.FIRST) {
            gameAlgorithm.setBulletsPlaced(shooter, 0);
        }
        shooter.sendTitle(LangClass.titles_yourTurn, "");

        setRoundState(RoundState.SHOOTER_DECIDING, true);
    }

    public void victimDamaged() {
        if (players.size() <= 1 || this.state == GameState.FINISHING) {
            return;
        }
        Player victim = getVictim();
        Player shooter = getShooter();
        shooter.setLevel(0);
        shooter.setExp(0);
        successShootEffect(victim);

        playerLives.put(victim, playerLives.get(victim) - 1);
        if (playerLives.get(victim) <= 0) {
            playerSeat.remove(victimSeat);
            players.remove(victim);
            playerLives.remove(victim);
            gameAlgorithm.removePlayer(victim);
            victim.setHealth(20);
            if (players.size() == 1) {
                this.state = GameState.FINISHING;
            }
            addSpectator(victim);

            Random random = new Random(System.nanoTime());
            if (victim != shooter) {
                int n = random.nextInt(LangClass.messages_player_killed_other.size() - 1);
                broadcastMessage(LangClass.messages_player_killed_other.get(n).replace("%shooter%", shooter.getName()).replace("%victim%", victim.getName()));
            } else {
                int n = random.nextInt(LangClass.messages_player_suicide.size() - 1);
                broadcastMessage(LangClass.messages_player_suicide.get(n).replace("%shooter%", shooter.getName()));
            }
        } else {
            if (shooter != victim) {
                Random r = new Random(System.nanoTime());
                int n = r.nextInt(LangClass.messages_player_damaged_other.size() - 1);
                broadcastMessage(LangClass.messages_player_damaged_other.get(n).replace("%shooter%", shooter.getName()).replace("%victim%", victim.getName()));
            } else {
                if (this.round == GameRound.FIRST) {
                    int bullets = getGameAlgorithm().getBulletsPlaced(shooter);
                    getGameAlgorithm().setBulletsPlaced(shooter, bullets == 6 ?
                            1 : 6 - (getGameAlgorithm().getBulletsPlaced(shooter)));
                }
            }
            victim.setHealth(((double) playerLives.get(victim) / MAX_LIVES) * 20);
        }
        this.getTimer().setTimer(GameTask.Timer.NEXT_SHOOTER_DELAY);
    }

    public void victimSurvived() {
        if (players.size() <= 1 || this.state == GameState.FINISHING) {
            return;
        }
        Player victim = getVictim();
        Player shooter = getShooter();
        shooter.setLevel(0);
        shooter.setExp(0);

        survivedEffect(victim);


        if (getGameAlgorithm().getBulletsPlaced(shooter) == 0 && round == GameRound.FIRST) {
            broadcastMessage(LangClass.messages_ShootWithoutBullets
                    .replace("%shooter%", shooter.getName())
                    .replace("%victim%", victim.getName()));

        }else broadcastMessage(LangClass.messages_misfire
                    .replace("%shooter%", shooter.getName())
                    .replace("%victim%", victim.getName()));

        if (this.round == GameRound.FIRST) {
            int bullets = getGameAlgorithm().getBulletsPlaced(shooter);
            getGameAlgorithm().setBulletsPlaced(shooter, bullets == 6 ?
                    1 : 6 - (getGameAlgorithm().getBulletsPlaced(shooter)));
        }
        this.getTimer().setTimer(GameTask.Timer.NEXT_SHOOTER_DELAY);
    }


    public void setupShooterInventory(Player shooter) {
        shooter.getInventory().clear();
        if (round == GameRound.FIRST) {
            ItemStack itemStack = new ItemBuilder(Material.BARRIER).setDisplayName("&cX").getItem();
            shooter.getInventory().setItem(0, itemStack);
            shooter.getInventory().setItem(8, itemStack);

            itemStack = new ItemBuilder(Material.GUNPOWDER).setDisplayName(LangClass.item_addBullet).getItem();
            shooter.getInventory().setItem(1, itemStack);
            shooter.getInventory().setItem(2, itemStack);
            shooter.getInventory().setItem(3, itemStack);
            shooter.getInventory().setItem(5, itemStack);
            shooter.getInventory().setItem(6, itemStack);
            shooter.getInventory().setItem(7, itemStack);

            shooter.getInventory().setItem(4, new ItemBuilder(Material.FIRE_CHARGE)
                    .setDisplayName(LangClass.item_shoot
                            .replace("%chance%", getGameAlgorithm().getCurrentChanceToDie(shooter) + "")
                            .replace("%bullets%", getGameAlgorithm().getBulletsPlaced(shooter) + ""))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .getItem());

        } else if (round == GameRound.SECOND) {
            shooter.getInventory().setItem(4, new ItemBuilder(Material.FIRE_CHARGE)
                    .setDisplayName(LangClass.item_shoot
                            .replace("%chance%", getGameAlgorithm().getCurrentChanceToDie(shooter) + "")
                            .replace("%bullets%", getGameAlgorithm().getBulletsPlaced(shooter) + ""))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .getItem());

        } else {
            shooter.getInventory().setItem(4, new ItemBuilder(Material.FIRE_CHARGE)
                    .setDisplayName(LangClass.item_shoot_victim
                            .replace("%chance%", getGameAlgorithm().getCurrentChanceToDie(shooter) + "")
                            .replace("%bullets%", getGameAlgorithm().getBulletsPlaced(shooter) + ""))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .getItem());
        }

    }

    private CustomLocation findNextShooterSeat(boolean fromBeginning) {
        if (fromBeginning) {
            for (CustomLocation seat : getArena().getSeatPoints()) {
                if (playerSeat.get(seat) != null) {
                    return seat;
                }
            }
        } else {
            for (CustomLocation seat : getArena().getSeatPoints()) {
                if (playerSeat.get(seat) != null &&
                        getArena().getSeatPoints().indexOf(shooterSeat)
                                < getArena().getSeatPoints().indexOf(seat)) {
                    return seat;
                }
            }
        }
        return null;
    }

    private CustomLocation findFreeSeat() {
        for (CustomLocation loc : getArena().getSeatPoints()) {
            if (playerSeat.containsKey(loc)) continue;

            return loc;
        }
        return null;
    }

    public void teleportPlayerToCenter(Player p) {
        RussianRoulette.getInstance().getSeatManager().setSitting(p,false);
        NameTagVisibility.setVisibility(this, p, false);
        p.teleport(arena.getCenter().getLocation());
    }

    public void teleportPlayerBack(Player p) {
        for (Map.Entry<CustomLocation, Player> entry : playerSeat.entrySet()) {
            if (entry.getValue() == p) {
                p.teleport(entry.getKey().getLocation());
                RussianRoulette.getInstance().getSeatManager().setSitting(p,true);
                p.getInventory().clear();
                NameTagVisibility.setVisibility(this, p, true);
            }
        }
    }

    public void broadcastMessage(String message) {
        players.forEach(player -> {
            if (player.isOnline()) player.sendMessage(PhoenixUtils.colorFormat(message));
        });
    }

    public void broadcastSound(Sound sound, boolean broadcastSpectators) {
        players.forEach(player -> player.playSound(player.getLocation(), sound, 1.0f, 10.0f));
        if (broadcastSpectators)
            spectators.forEach(player -> player.playSound(player.getLocation(), sound, 1.0f, 10.0f));
    }

    public void broadcastTitle(String string, String string2, boolean broadcastSpectators, Player... skip) {
        players.forEach(player -> {
            if (skip == null || Arrays.stream(skip).noneMatch(skipPlayer -> skipPlayer == player)) {
                player.sendTitle(PhoenixUtils.colorFormat(string), PhoenixUtils.colorFormat(string2));
            }
        });
        if (broadcastSpectators)
            spectators.forEach(player -> {
                if (skip == null || Arrays.stream(skip).noneMatch(skipPlayer -> skipPlayer == player)) {
                    player.sendTitle(PhoenixUtils.colorFormat(string), PhoenixUtils.colorFormat(string2));
                }
            });
    }


    public void launchFireworks(Player player, Color color) {
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.BALL_LARGE).build());
        fireworkMeta.setPower(2);
        firework.setFireworkMeta(fireworkMeta);
    }

    public void playerShotEffect(Player p) {
        //effects.PlayerOnShot(p);
    }

    public void successShootEffect(Player p) {
        p.getWorld().strikeLightningEffect(p.getLocation());
        //effects.PlayerKilled(p);
    }

    public void survivedEffect(Player p) {
        broadcastSound(Sound.ENTITY_DONKEY_ANGRY, true);
    }

    public void showAll(Player player) {
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            player.showPlayer(player2);
        }
    }

    private void clearCache() {
        players.forEach(player -> clearPlayerCache(player,false));
        spectators.forEach(player -> clearPlayerCache(player,false));
        players.clear();
        spectators.clear();
        playerSeat.clear();
        shooterSeat = null;
        victimSeat = null;
        playerLives.clear();
        gameAlgorithm.clearAll();
        gameHologram.clearCache();
        shooting = false;
        roundCache = null;
        setState(GameState.PENDING_FOR_PLAYERS);
        setRound(GameRound.FIRST);
        setRoundState(RoundState.NEXT_SHOOTER_DELAY, false);
    }

    private void clearPlayerCache(Player p, boolean removeFromList) {
        GameScoreboard.removeScoreboard(p);
        if(removeFromList) players.remove(p);
        if(removeFromList) spectators.remove(p);
        playerLives.remove(p);
        playerSeat.remove(getPlayerSeatLocation(p));
        gameAlgorithm.removePlayer(p);
        RussianRoulette.getInstance().getGameM().removePlayerFromGame(p);

        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setExp(0);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        for (PotionEffect potionEffect : p.getActivePotionEffects()) {
            p.removePotionEffect(potionEffect.getType());
        }
        RussianRoulette.getInstance().getSeatManager().setSitting(p, false);

        if(RussianRoulette.getInstance().getConfigFile().getLobby()!=null)
            p.teleport(RussianRoulette.getInstance().getConfigFile().getLobby());


        showAll(p);
        Utils.separatePlayer(p);
    }


    public void setRoundState(RoundState value, boolean changeTimer) {
        this.roundstate = value;
        if (changeTimer) {
            switch (value) {
                case SHOOTER_DECIDING -> getTimer().setTimer(GameTask.Timer.SHOOTER_DECIDING);
                case NEXT_SHOOTER_DELAY -> getTimer().setTimer(GameTask.Timer.NEXT_SHOOTER_DELAY);
                case BET_TIME -> getTimer().setTimer(GameTask.Timer.BID_TIME);
                case NEXT_ROUND_DELAY -> getTimer().setTimer(GameTask.Timer.NEXT_ROUND_DELAY);
            }
        }
    }

    public CustomLocation getPlayerSeatLocation(Player p) {
        for (Map.Entry<CustomLocation, Player> en : playerSeat.entrySet()) {
            if (en.getValue() == p) {
                return en.getKey();
            }
        }
        return null;
    }


    public void setVictim(Player p) {
        playerSeat.forEach((key, value) -> {
            if (p == value) {
                victimSeat = key;
            }
        });
    }
    public Player getVictim() {
        return playerSeat.get(victimSeat);
    }
    public Player getShooter() {
        return playerSeat.get(shooterSeat);
    }


    public enum GameState {
        DISABLED(LangClass.other_round_Disabled),
        PENDING_FOR_PLAYERS(LangClass.other_round_PendingForPlayers),
        STARTING(LangClass.other_round_starting),
        ACTIVE(LangClass.other_round_active),
        FINISHING(LangClass.other_round_finishing);
        private String name;

        GameState(String n) {
            name = n;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public enum RoundState {
        NEXT_SHOOTER_DELAY,
        SHOOTER_DECIDING,
        NEXT_ROUND_DELAY,
        BET_TIME

    }

    public enum GameRound {
        FIRST(LangClass.other_round_name_1),
        SECOND(LangClass.other_round_name_2),
        FINAL(LangClass.other_round_name_final);
        private String name;

        GameRound(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public GameRound nextRound(Game g) {
            if (g.getRound() == GameRound.FIRST && (g.getState() == GameState.STARTING || g.getState() == GameState.PENDING_FOR_PLAYERS)) {
                return GameRound.FIRST;
            } else if (g.getRound() == GameRound.FIRST) {
                return GameRound.SECOND;
            } else if (g.getRound() == GameRound.SECOND) {
                return GameRound.FINAL;
            } else {
                return GameRound.FIRST;
            }
        }
    }

    public static class RoundCache {
        private final Player lastShooter;
        private final Player lastVictim;
        private final boolean shootSuccess;

        public RoundCache(Player shooter, Player victim, boolean shootSuccess) {
            this.lastShooter = shooter;
            this.lastVictim = victim;
            this.shootSuccess = shootSuccess;
        }

        public Player getShooter() {
            return lastShooter;
        }

        public Player getVictim() {
            return lastVictim;
        }

        public boolean shootSuccess() {
            return shootSuccess;
        }

    }
}
