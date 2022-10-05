package me.phoenixra.russian_roulette.game;

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
    private final Arena arena;

    private final List<Player> players = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();
    private final HashMap<CustomLocation, Player> playerSeat = new HashMap<>();
    private final GameTask timer;
    private final GameAlgorithm gameAlgorithm;
    private final BidAlgorithm bidAlgorithm;
    private final ScoreboardBuilder scoreboardBuilder;
    private final GameHologram gameHologram;
    private final int maxLives = 2;
    public HashMap<Player, Integer> playerLives = new HashMap<>();
    protected RoundCache roundCache;
    private CustomLocation shooterSeat;
    private CustomLocation victimSeat;
    private GameState state;
    private GameRound round;
    private RoundState roundstate;
    private boolean isShooting;

    public Game(Arena arena) {
        this.arena = arena;

        gameAlgorithm = new GameAlgorithm(this);
        bidAlgorithm = new BidAlgorithm(this);
        scoreboardBuilder = new ScoreboardBuilder();

        state = GameState.PENDING_FOR_PLAYERS;
        round = GameRound.FIRST;
        roundstate = RoundState.NEXT_SHOOTER_DELAY;

        timer = new GameTask(Game.this);
        gameHologram = new GameHologram(this.getArena().getCenter().getLocation().add(0, 5, 0));
        gameHologram.setGameHolo(this);

    }

    public void startGame() {
        state = GameState.ACTIVE;
        round = GameRound.FIRST;
        for (Player p : this.getPlayers()) {
            p.getInventory().clear();
            ScoreboardBuilder.applyScoreboard(p, ScoreboardBuilder.ScoreboardType.GAME);
        }

        shooterSeat = victimSeat = findNextShooterSeat(true);

        teleportPlayerToCenter(playerSeat.get(shooterSeat));
        setupShooterInventory(playerSeat.get(shooterSeat));

        setRoundState(RoundState.NEXT_SHOOTER_DELAY, true);
    }

    public void enableGame() {
        gameHologram.clearLines();
        state = GameState.PENDING_FOR_PLAYERS;
    }

    public void disableGame() {
        if (state != GameState.PENDING_FOR_PLAYERS) {
            this.forceEnd();
        } else {
            for (Player p : this.players) {
                this.playerLeave(p);
            }
        }
        gameHologram.clearLines();
        state = GameState.DISABLED;
    }

    public void finishGame() {
        clearCache();
    }

    public void forceEnd() {
        for (Player player : this.players) {
            if (player == null) continue;
            this.playerLeave(player);
        }
        this.clearCache();
        this.state = GameState.PENDING_FOR_PLAYERS;
    }


    public boolean playerJoin(Player p) {
        if (state != GameState.PENDING_FOR_PLAYERS && state != GameState.STARTING) {
            addSpectator(p);
            return true;
        }
        if (players.size() >= arena.getMaxPlayers()) {
            addSpectator(p);
            return true;
        }

        players.add(p);
        CustomLocation seat = findFreeSeat();
        if (seat == null) {
            addSpectator(p);
            return true;
        }
        playerSeat.put(seat, p);
        p.teleport(getPlayerSeatLocation(p).getLocation());
        RussianRoulette.getInstance().getSeatManager().setSitting(p, true);

        ScoreboardBuilder.applyScoreboard(p, ScoreboardBuilder.ScoreboardType.STARTING);

        gameAlgorithm.addPlayer(p);
        playerLives.put(p, maxLives);
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
        return true;
    }

    public boolean playerLeave(Player p) {
        if (state == GameState.STARTING || state == GameState.PENDING_FOR_PLAYERS) {
            clearPlayerCache(p);
            if (players.size() < 2) {
                this.getTimer().reset();
            }
            broadcastMessage(LangClass.messages_playerLeft.replace("%player%", p.getName()));

            return true;
        } else {

            if (state == GameState.ACTIVE) {
                clearPlayerCache(p);
                if (!spectators.contains(p)) {
                    if (this.isShooting) {
                        if (this.getShooter() == p) {
                            broadcastMessage(LangClass.messages_playerEscaped.replace("%player%", p.getName()));
                            this.nextShooter();

                        } else if (this.getVictim() == p) {
                            broadcastMessage(LangClass.messages_playerEscaped.replace("%player%", p.getName()));
                            this.nextShooter();
                        }
                        return true;
                    }
                    broadcastMessage(LangClass.messages_playerLeft.replace("%player%", p.getName()));
                }

                return true;
            }

            if (state == GameState.FINISHING) {
                clearPlayerCache(p);
                if (!this.getSpectators().contains(p)) {
                    broadcastMessage(LangClass.messages_playerLeft.replace("%player%", p.getName()));
                }

                return true;
            }
        }

        return false;
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
        ItemStack itemStack = new ItemBuilder(Material.RED_BED).setDisplayName("&cLeave").getItem();
        p.getInventory().setItem(0, itemStack);
        p.getInventory().setItem(8, itemStack);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));

        ScoreboardBuilder.applyScoreboard(p, ScoreboardBuilder.ScoreboardType.SPECTATOR);

        Utils.separatePlayer(p);
        p.sendTitle(LangClass.titles_BecameSpectator, "");
    }


    public void nextRound() {
        isShooting = false;
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
            this.getAlgorithm().setBulletsPlaced(shooter, r.nextInt(5) + 1);
            broadcastMessage(LangClass.messages_shooterBullets
                    .replace("%shooter%", shooter.getName())
                    .replace("%bullets%", getAlgorithm().getBulletsPlaced(shooter) + ""));
        }
        shooter.sendTitle(LangClass.titles_yourTurn, "");

        setRoundState(RoundState.NEXT_SHOOTER_DELAY, true);
    }

    public void nextShooter() {
        if (players.size() <= 1 || this.state == GameState.FINISHING) {
            return;
        }
        isShooting = false;
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
                this.getTimer().activateNextRoundDelay();
                return;
            }
            shooterSeat = victimSeat = findNextShooterSeat(true);

        } else {
            shooterSeat = victimSeat = findNextShooterSeat(false);
            if (shooterSeat == null) {
                if (this.round != GameRound.FINAL) {
                    this.getTimer().activateNextRoundDelay();
                    return;
                }
                shooterSeat = victimSeat = findNextShooterSeat(true);
            }

        }

        Player shooter = playerSeat.get(shooterSeat);
        this.teleportPlayerToCenter(shooter);
        this.setupShooterInventory(shooter);
        playerShotEffect(shooter);
        if (round == GameRound.FINAL) {
            Random r = new Random();
            this.getAlgorithm().setBulletsPlaced(shooter, r.nextInt(5) + 1);
            broadcastMessage(LangClass.messages_shooterBullets
                    .replace("%shooter%", shooter.getName())
                    .replace("%bullets%", getAlgorithm().getBulletsPlaced(shooter) + ""));
        } else if (round == GameRound.FIRST) {
            gameAlgorithm.setBulletsPlaced(shooter, 0);
        }
        shooter.sendTitle(LangClass.titles_yourTurn, "");

        setRoundState(RoundState.NEXT_SHOOTER_DELAY, true);
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

            Random random = new Random();
            if (victim != shooter) {
                int n = random.nextInt(LangClass.messages_player_killed_other.size() - 1);
                broadcastMessage(LangClass.messages_player_killed_other.get(n).replace("%shooter%", shooter.getName()).replace("%victim%", victim.getName()));
            } else {
                int n = random.nextInt(LangClass.messages_player_suicide.size() - 1);
                broadcastMessage(LangClass.messages_player_suicide.get(n).replace("%shooter%", shooter.getName()));
            }
        } else {
            if (shooter != victim) {
                Random r = new Random();
                int n = r.nextInt(LangClass.messages_player_damaged_other.size() - 1);
                broadcastMessage(LangClass.messages_player_damaged_other.get(n).replace("%shooter%", shooter.getName()).replace("%victim%", victim.getName()));
            } else {
                if (this.round == GameRound.FIRST) {
                    this.getAlgorithm().setBulletsPlaced(shooter, 6 - (getAlgorithm().getBulletsPlaced(shooter) + 1));
                }
            }
            victim.setHealth(((double) maxLives / playerLives.get(victim)) * 20);
        }
        this.getTimer().activateNextShooterDelay();
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


        if (this.getAlgorithm().getBulletsPlaced(shooter) == 0 && round == GameRound.FIRST) {
            broadcastMessage(LangClass.messages_ShootWithoutBullets
                    .replace("%shooter%", shooter.getName())
                    .replace("%victim%", victim.getName()));

        }else broadcastMessage(LangClass.messages_misfire
                    .replace("%shooter%", shooter.getName())
                    .replace("%victim%", victim.getName()));

        if (this.round == GameRound.FIRST) {
            int bullets = getAlgorithm().getBulletsPlaced(shooter);
            getAlgorithm().setBulletsPlaced(shooter, bullets == 6 ?
                    1 : bullets == 0 ?
                    6 : 6 - (getAlgorithm().getBulletsPlaced(shooter) + 1));
        }
        this.getTimer().activateNextShooterDelay();
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
                            .replace("%chance%", getAlgorithm().getCurrentChanceToDie(shooter) + "")
                            .replace("%bullets%", getAlgorithm().getBulletsPlaced(shooter) + ""))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .getItem());

        } else if (round == GameRound.SECOND) {
            shooter.getInventory().setItem(4, new ItemBuilder(Material.FIRE_CHARGE)
                    .setDisplayName(LangClass.item_shoot
                            .replace("%chance%", getAlgorithm().getCurrentChanceToDie(shooter) + "")
                            .replace("%bullets%", getAlgorithm().getBulletsPlaced(shooter) + ""))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .getItem());

        } else {
            shooter.getInventory().setItem(4, new ItemBuilder(Material.FIRE_CHARGE)
                    .setDisplayName(LangClass.item_shoot_victim
                            .replace("%chance%", getAlgorithm().getCurrentChanceToDie(shooter) + "")
                            .replace("%bullets%", getAlgorithm().getBulletsPlaced(shooter) + ""))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .getItem());
        }

    }

    private CustomLocation findNextShooterSeat(boolean fromBeginning) {
        if (fromBeginning) {
            for (CustomLocation seat : getArena().getSeatPoints()) {
                if (playerSeat.get(seat) != null) {
                    shooterSeat = seat;
                    victimSeat = seat;
                    break;
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
            if (playerSeat.containsKey(loc)) {
                continue;
            }
            return loc;
        }
        return null;
    }

    public void teleportPlayerToCenter(Player p) {
        Location loc = this.arena.getCenter().getLocation();
        if (loc.getBlock().getType() == Material.AIR) {
            p.teleport(this.arena.getCenter().getLocation());
        } else {
            loc = loc.add(0, 1, 0);
            if (loc.getBlock().getType() != Material.AIR) {
                loc = loc.add(0, 1, 0);
            }
            p.teleport(loc);
        }
        NameTagVisibility.setVisibility(p, false);
    }

    public void teleportPlayerBack(Player p) {
        for (Map.Entry<CustomLocation, Player> entry : playerSeat.entrySet()) {
            if (entry.getValue() == p) {
                p.teleport(playerSeat.get(entry.getKey()).getLocation());
                break;
            }
        }
        p.getInventory().clear();
        NameTagVisibility.setVisibility(p, true);
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
        players.forEach(this::clearPlayerCache);
        spectators.forEach(this::clearPlayerCache);
        players.clear();
        spectators.clear();
        playerSeat.clear();
        shooterSeat = null;
        victimSeat = null;
        state = GameState.PENDING_FOR_PLAYERS;
        round = GameRound.FIRST;
        timer.reset();
        playerLives.clear();
        gameAlgorithm.clearAll();
        isShooting = false;
        setRoundState(RoundState.NEXT_SHOOTER_DELAY, false);
        roundCache = null;
    }

    private void clearPlayerCache(Player p, boolean removeFromList) {
        ScoreboardBuilder.removeScoreboard(p);
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
        if(RussianRoulette.getInstance().getConfigFile().getLobby()==null) {
            Bukkit.getConsoleSender().sendMessage("RussianRoulette: lobby isn't set");

        }else p.teleport(RussianRoulette.getInstance().getConfigFile().getLobby());

        showAll(p);
        Utils.separatePlayer(p);
    }


    public void setRoundState(RoundState value, boolean changeTimer) {
        this.roundstate = value;
        if (changeTimer) {
            switch (value) {
                case SHOOTER_DECIDING -> getTimer().activateShooterDecidingDelay();
                case NEXT_SHOOTER_DELAY -> getTimer().activateNextShooterDelay();
                case BET_TIME -> getTimer().activateBidTime();
                case NEXT_ROUND_DELAY -> getTimer().activateNextRoundDelay();
            }
        }
    }

    public void setIsShooting(boolean b) {
        this.isShooting = b;
    }

    public CustomLocation getPlayerSeatLocation(Player p) {
        for (Map.Entry<CustomLocation, Player> en : playerSeat.entrySet()) {
            if (en.getValue() == p) {
                return en.getKey();
            }
        }
        return null;
    }

    public Arena getArena() {
        return arena;
    }

    public RoundState getRoundState() {
        return roundstate;
    }

    public GameHologram getGameHologram() {
        return gameHologram;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public Player getVictim() {
        return playerSeat.get(victimSeat);
    }

    public void setVictim(Player p) {
        playerSeat.forEach((key, value) -> {
            if (p == value) {
                victimSeat = key;
            }
        });
    }

    public GameAlgorithm getAlgorithm() {
        return gameAlgorithm;
    }

    public BidAlgorithm getBidAlgorithm() {
        return bidAlgorithm;
    }

    public GameRound getRound() {
        return this.round;
    }

    public void setRound(GameRound r) {
        this.round = r;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState s) {
        this.state = s;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameTask getTimer() {
        return timer;
    }

    public Player getShooter() {
        return playerSeat.get(shooterSeat);
    }

    public RoundCache getRoundCache() {
        return roundCache;
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
