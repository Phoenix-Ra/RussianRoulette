package me.phoenixra.russian_roulette.game;

import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map.Entry;

public class BidAlgorithm {
    private HashMap<Player, Boolean> playerBid = new HashMap<>();
    private Game game;
    private int loseBonus = 1;
    private int winBonus = 1;
    private boolean bidStarted;
    public BidAlgorithm(Game game) {
        this.game = game;
    }

    public void startBid(Player shooter, Player victim) {
        if (this.bidStarted) {
            return;
        }
        for (Player player : game.getPlayers()) {
            if (player == shooter || player == victim) {
                continue;
            }
            player.getInventory().clear();
            setInventoryBid(player);
        }
        game.setRoundState(Game.RoundState.BET_TIME,true);
        bidStarted = true;
    }


    public void endBid(Player shooter, Player victim, boolean successShoot) {
        if (!this.bidStarted) return;

        for (Player player : game.getPlayers()) {
            if (player == shooter || player == victim || !playerBid.containsKey(player)) continue;
            player.getInventory().clear();
            game.getAlgorithm().setPlayerLuck(player,
                    game.getAlgorithm().getGiftChance(player) + (successShoot == playerBid.get(player) ? winBonus : loseBonus));

        }

        broadcastResult(successShoot);
        bidStarted =false;
    }


    private void setInventoryBid(Player p) {

        ItemStack itemStack = new ItemBuilder(Material.RED_TERRACOTTA)
                .setDisplayName(LangClass.item_bidYes).getItem();
        p.getInventory().setItem(3, itemStack);

        itemStack = new ItemBuilder(Material.GREEN_TERRACOTTA)
                .setDisplayName(LangClass.item_bidNo).getItem();
        p.getInventory().setItem(5, itemStack);

    }

    private void broadcastResult(boolean result) {
        StringBuilder losers = new StringBuilder();
        StringBuilder winners = new StringBuilder();
        for (Entry<Player, Boolean> en : playerBid.entrySet()) {
            if (!en.getKey().isOnline()) {
                continue;
            }
            if (en.getValue() == result) winners.append(winners.length() == 0 ? "" : ", ").append(en.getKey().getName());
            else losers.append(losers.length() == 0 ? "" : ", ").append(en.getKey().getName());
        }

        if (losers.length() == 0) losers.append("-");
        if (winners.length() == 0) winners.append("-");


        StringBuilder resultMessage=new StringBuilder();
        for(String line : LangClass.messages_bidFinished){
            resultMessage.append(line.replace("%winners%",winners.toString()).replace("%losers%",losers.toString()));
            resultMessage.append("\n");
        }
        game.broadcastMessage(resultMessage.toString());
    }

    public void playerMadeBid(Player player, boolean bid) {
        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 1.0f, 1.0f);
        player.getInventory().clear();
        if (playerBid.containsKey(player)) {
            playerBid.replace(player, bid);
            return;
        }
        playerBid.put(player, bid);
    }

    public void clearBids() {
        playerBid.clear();
    }


    public boolean isBidStarted() {
        return this.bidStarted;
    }

    public int getWinBonus() {
        return this.winBonus;
    }

    public int getLoseBonus() {
        return this.loseBonus;
    }

}
