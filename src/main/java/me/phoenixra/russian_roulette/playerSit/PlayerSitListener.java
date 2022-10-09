package me.phoenixra.russian_roulette.playerSit;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerSitListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        SeatManager seatManager = RussianRoulette.getInstance().getSeatManager();
        seatManager.setSitting(e.getEntity(),false);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        SeatManager seatManager = RussianRoulette.getInstance().getSeatManager();
        seatManager.setSitting(e.getPlayer(),false);

    }
    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        SeatManager.SeatArmorStand simpleSitArmorStand = new SeatManager.SeatArmorStand(e.getRightClicked());
        if (simpleSitArmorStand.isSeat()) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerStairsInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game == null) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            if (!(b.getType().toString().contains("STAIRS"))) {
                return;
            }
            Stairs stairs = (Stairs) b.getState().getData();
            switch (stairs.getFacing()) {
                case EAST-> {
                    player.teleport(new Location(player.getWorld(), b.getLocation().getX() + 0.1, b.getLocation().getY() + 0.5, b.getLocation().getZ() + 0.5, 90.0f, 0.0f));
                }
                case WEST -> {
                    player.teleport(new Location(player.getWorld(), b.getLocation().getX() + 0.9, b.getLocation().getY() + 0.5, b.getLocation().getZ() + 0.5, -90.0f, 0.0f));

                }
                case NORTH -> {
                    player.teleport(new Location(player.getWorld(), b.getLocation().getX() + 0.5, b.getLocation().getY() + 0.5, b.getLocation().getZ() + 0.1, -180.0f, 0.0f));

                }
                case SOUTH -> {
                    player.teleport(new Location(player.getWorld(), b.getLocation().getX() + 0.5, b.getLocation().getY() + 0.5, b.getLocation().getZ() + 0.9, 0.0f, 0.0f));

                }
            }
            RussianRoulette.getInstance().getSeatManager().setSitting(player,true);
        }
    }
}
