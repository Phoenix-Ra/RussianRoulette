package me.phoenixra.russian_roulette.game;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serial;
import java.io.Serializable;

public class CustomLocation implements Serializable {
    @Serial
    private static final long serialVersionUID = 6489345419923447755L;

    @Getter private final double x;
    @Getter private final double y;
    @Getter private final double z;
    @Getter private final float yaw;
    @Getter private final float pitch;

    private final String world;

    public CustomLocation(Location l){
        this.x=l.getX();
        this.y=l.getY();
        this.z=l.getZ();
        this.yaw=l.getYaw();
        this.pitch=l.getPitch();
        this.world=l.getWorld().getName();
    }
    public World getWorld() {
        return Bukkit.getServer().getWorld(world);
    }
    public Block getBlock() {
        return new Location(getWorld(), x, y, z, yaw, pitch).getBlock();
    }
    public Location getLocation() {
        return new Location(getWorld(),x,y,z,yaw,pitch);
    }

}
