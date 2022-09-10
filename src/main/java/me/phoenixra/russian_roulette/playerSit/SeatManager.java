package me.phoenixra.russian_roulette.playerSit;

import me.phoenixra.russian_roulette.RussianRoulette;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SeatManager {
    private final Map<UUID, ArmorStand> seats = new HashMap<>();
    public SeatManager() {
        new RotateSeatTask(this);
        Bukkit.getPluginManager().registerEvents(new PlayerSitListener(), RussianRoulette.getInstance());
    }
    public Map<UUID, ArmorStand> getSeats() {
        return this.seats;
    }




    public void setSitting(Player player, boolean value) {
        if (value && !this.isSitting(player)) {
            Location location = player.getLocation();
            ArmorStand seat = location.getWorld().spawn(location.clone().subtract(0.0, 1.7, 0.0), ArmorStand.class);
            seat.setGravity(false);
            seat.setVisible(false);
            seat.addPassenger(player);
            getSeats().put(player.getUniqueId(), seat);
        } else if (!value && this.isSitting(player)) {
            ArmorStand seat = getSeats().get(player.getUniqueId());
            getSeats().remove(player.getUniqueId());
            player.eject();
            player.teleport(seat.getLocation().clone().add(0.0, 1.7, 0.0));
            seat.remove();
        }
    }

    public boolean isSitting(Player player) {
        return getSeats().containsKey(player.getUniqueId());
    }
    public static class SeatArmorStand{
        private final SeatManager seatManager;
        private final ArmorStand armorstand;

        public SeatArmorStand(ArmorStand armorStand) {
            this.armorstand = armorStand;
            this.seatManager = RussianRoulette.getInstance().getSeatManager();
        }

        public boolean isSeat() {
            return this.seatManager.getSeats().containsValue(this.armorstand);
        }
    }

    public static class RotateSeatTask extends BukkitRunnable{
        private final SeatManager seatManager;

        public RotateSeatTask(SeatManager seatManager) {
            this.seatManager = seatManager;
            this.runTaskTimerAsynchronously(RussianRoulette.getInstance(), 0L, 1L);
        }

        public void run() {
            for (ArmorStand armorstand : this.seatManager.getSeats().values()) {
                try {
                    Object entityArmorStand = armorstand.getClass().getMethod("getHandle").invoke(armorstand);
                    Field yaw = entityArmorStand.getClass().getField("yaw");
                    yaw.set(entityArmorStand, armorstand.getPassengers().get(0).getLocation().getYaw());
                }
                catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }



}
