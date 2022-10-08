package me.phoenixra.russian_roulette.game;

import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.utils.GameSymbols;
import me.phoenixra.russian_roulette.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class GameAlgorithm {
    Game game;
    HashMap<Player, Integer> bulletsAmount = new HashMap<>();
    HashMap<Player, Integer> playerLuck = new HashMap<>();
    private int bulletsLimit=6;
    private final int MAX_LUCK=10;
    private final int MIN_LUCK=-25;
    public GameAlgorithm(Game game){
        this.game=game;
    }

    public void shootItselfPrepare(Player shooter) {
        if(game.isShooting()) {
            return;
        }
        game.setShooting(true);
        game.getBidAlgorithm().clearBids();
        game.broadcastSound(Sound.ENTITY_WITHER_SPAWN,true);
        game.broadcastTitle(PhoenixUtils.colorFormat("&a")+shooter.getName(), GameSymbols.shoot("&c"),true);

        StringBuilder message=new StringBuilder();
        for(String line : LangClass.messages_bid_started_suicide){
            message.append(
                    line
                            .replace("%shooter%",shooter.getName())
                            .replace("%victim%",shooter.getName())
                            .replace("%bullets%",getBulletsPlaced(shooter)+"")
                            .replace("%shooter_luck%",game.getGameAlgorithm().getPlayerLuck(shooter)+"")
                            .replace("%victim_luck%",game.getGameAlgorithm().getPlayerLuck(shooter)+"")
                            .replace("%shoot_chance%",game.getGameAlgorithm().getCurrentChanceToDie(shooter)+"")
            );
            message.append("\n");
        }
        game.broadcastMessage(message.toString());

        ItemStack itemStack = new ItemBuilder(Material.BARRIER).setDisplayName("&c&kXXXXX").getItem();
        shooter.getInventory().setItem(0, itemStack);shooter.getInventory().setItem(1, itemStack);
        shooter.getInventory().setItem(2, itemStack);shooter.getInventory().setItem(3, itemStack);
        shooter.getInventory().setItem(4, itemStack);shooter.getInventory().setItem(5, itemStack);
        shooter.getInventory().setItem(6, itemStack);shooter.getInventory().setItem(7, itemStack);
        shooter.getInventory().setItem(8, itemStack);
        game.getBidAlgorithm().startBid(shooter, shooter);

    }

    public void shootVictimPrepare(Player shooter,Player victim) {
        if(game.isShooting()) {
            return;
        }
        game.teleportPlayerToCenter(victim);

        game.setShooting(true);
        game.getBidAlgorithm().clearBids();
        game.broadcastSound(Sound.ENTITY_WITHER_SPAWN,true);
        game.broadcastTitle(PhoenixUtils.colorFormat("&e")+shooter.getName(),
                GameSymbols.shoot("&c")+
                        PhoenixUtils.colorFormat("&e")+victim.getName()+GameSymbols.shoot("&c"),true);

        StringBuilder message=new StringBuilder();
        for(String line : LangClass.messages_bid_started_killOther){
            message.append(
                    line
                            .replace("%shooter%",shooter.getName())
                            .replace("%victim%",victim.getName())
                            .replace("%bullets%",getBulletsPlaced(shooter)+"")
                            .replace("%shooter_luck%",game.getGameAlgorithm().getPlayerLuck(shooter)+"")
                            .replace("%victim_luck%",game.getGameAlgorithm().getPlayerLuck(victim)+"")
                            .replace("%shoot_chance%",game.getGameAlgorithm().getCurrentChanceToDie(shooter)+"")
            );
            message.append("\n");
        }
        game.broadcastMessage(message.toString());

        ItemStack itemStack = new ItemBuilder(Material.BARRIER).setDisplayName("&c&kXXXXX").getItem();
        shooter.getInventory().setItem(0, itemStack);shooter.getInventory().setItem(1, itemStack);
        shooter.getInventory().setItem(2, itemStack);shooter.getInventory().setItem(3, itemStack);
        shooter.getInventory().setItem(4, itemStack);shooter.getInventory().setItem(5, itemStack);
        shooter.getInventory().setItem(6, itemStack);shooter.getInventory().setItem(7, itemStack);
        shooter.getInventory().setItem(8, itemStack);
        game.getBidAlgorithm().startBid(shooter, victim);

    }
    public boolean shootItselfResult(Player shooter) {
        int randomResult = new Random(System.nanoTime()).nextInt(99)+1;
        double chance = ((double)bulletsAmount.get(shooter)/bulletsLimit) * 100 - playerLuck.get(shooter);
        return randomResult <= chance;
    }
    public boolean shootVictimResult(Player shooter, Player victim) {
        int randomResult = new Random(System.nanoTime()).nextInt(99)+1;
        double chance = ((double)bulletsAmount.get(shooter)/bulletsLimit) * 100 + playerLuck.get(shooter) - playerLuck.get(victim);
        return randomResult <= chance;
    }


    public void endBid() {
        Player shooter = game.getShooter();
        Player victim = game.getVictim();
        shooter.getInventory().clear();

        boolean result = victim == shooter ? shootItselfResult(shooter) : shootVictimResult(shooter, victim);

        game.getBidAlgorithm().endBid( shooter, victim, result);
        if(result) game.victimDamaged();
        else game.victimSurvived();

        game.roundCache=new Game.RoundCache(shooter,victim, result);
        game.setShooting(false);
    }
    public void changeAmountOfBullets(Player p) {
        int i=0;
        if (p.getInventory().getItem(1).getType()==Material.HOPPER) i++;
        if (p.getInventory().getItem(2).getType()==Material.HOPPER) i++;
        if (p.getInventory().getItem(3).getType()==Material.HOPPER) i++;
        if (p.getInventory().getItem(5).getType()==Material.HOPPER) i++;
        if (p.getInventory().getItem(6).getType()==Material.HOPPER) i++;
        if (p.getInventory().getItem(7).getType()==Material.HOPPER) i++;

        bulletsAmount.put(p, i);
    }

    public void addPlayer(Player p) {
        bulletsAmount.put(p, 0);
        playerLuck.put(p, 0);
    }
    public void removePlayer(Player p) {
        bulletsAmount.remove(p);
        playerLuck.remove(p);
    }
    public void clearAll() {
        bulletsAmount.clear();
        playerLuck.clear();
    }


    public void setBulletsPlaced(Player p,int i) {
        bulletsAmount.replace(p, i);
    }
    public void setPlayerLuck(Player p, int amount) {
        if(amount>MAX_LUCK||amount<MIN_LUCK || !playerLuck.containsKey(p)) {
            return;
        }
        playerLuck.put(p, amount);
    }


    public int getCurrentChanceToDie(Player p) {
        return (int)((float)(bulletsAmount.get(p))/(float)(bulletsLimit)*100-playerLuck.get(p));
    }
    public int getCurrentChanceToKill(Player shooter, Player shootedAt) {
        return (int)((float)(bulletsAmount.get(shooter))/(float)(bulletsLimit)*100+playerLuck.get(shooter)-+playerLuck.get(shootedAt));
    }
    public int getPlayerLuck(Player p) {
        if(playerLuck.get(p)==null) {
            this.addPlayer(p);
        }
        return playerLuck.get(p);
    }
    public int getBulletsPlaced(Player p) {
        return bulletsAmount.get(p);
    }

}
