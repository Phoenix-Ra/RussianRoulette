package me.phoenixra.russian_roulette.utils;

import org.bukkit.Effect;
import org.bukkit.entity.Player;

//TODO rewrite that
public class GameEffects {

    public static void PlayerKilled(Player p) {
        /*PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                EnumParticle.CRIT,    // particle type.
                true,                           // true
                p.getLocation().getBlockX(),     // x coordinate
                p.getLocation().getBlockY()+2,     // y coordinate
                p.getLocation().getBlockZ(),     // z coordinate
                1,                              // x offset
                1,                              // y offset
                1,                              // z offset
                1,                             // speed
                1000,                         // number of particles
                null
        );
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);*/
    }
    public static void PlayerSurvived(Player p) {
        /*
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                EnumParticle.WATER_SPLASH,    // particle type.
                true,                           // true
                p.getLocation().getBlockX(),     // x coordinate
                p.getLocation().getBlockY()+2,     // y coordinate
                p.getLocation().getBlockZ(),     // z coordinate
                1,                              // x offset
                1,                              // y offset
                1,                              // z offset
                1,                             // speed
                1000,                         // number of particles
                null
        );
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);*/

    }
    public static void PlayerOnShot(Player p) {
        /*
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                EnumParticle.CLOUD,    // particle type.
                true,                           // true
                p.getLocation().getBlockX(),     // x coordinate
                p.getLocation().getBlockY()+2,     // y coordinate
                p.getLocation().getBlockZ(),     // z coordinate
                1,                              // x offset
                1,                              // y offset
                1,                              // z offset
                1,                             // speed
                1000,                         // number of particles
                null
        );
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);*/

    }
    public static void PlayerTeleport(Player p) {

        p.getWorld().playEffect(p.getLocation(), Effect.PORTAL_TRAVEL, 10);
        p.getWorld().playEffect(p.getLocation().add(0.2, 0, 0), Effect.PORTAL_TRAVEL, 10);
        p.getWorld().playEffect(p.getLocation().add(0, 0.2, 0), Effect.PORTAL_TRAVEL, 10);
        p.getWorld().playEffect(p.getLocation().add(0, 0, 0.2), Effect.PORTAL_TRAVEL, 10);
        p.getWorld().playEffect(p.getLocation().add(0.2, 0, 0.2), Effect.PORTAL_TRAVEL, 10);
        p.getWorld().playEffect(p.getLocation().add(0.2, 0.2, 0.2), Effect.PORTAL_TRAVEL, 10);

    }
}
