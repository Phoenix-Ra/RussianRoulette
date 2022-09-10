package me.phoenixra.russian_roulette.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serial;
import java.io.Serializable;

public class CustomLocation implements Serializable {
    @Serial
    private static final long serialVersionUID = 6489345419923447755L;

    private double x, y, z;
    private float yaw, pitch;
    private String world;

    public CustomLocation(Location l){
        this.x=l.getX();
        this.y=l.getY();
        this.z=l.getZ();
        this.yaw=l.getYaw();
        this.pitch=l.getPitch();
        this.world=l.getWorld().getName();
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double getYaw() {
        return yaw;
    }
    public double getPitch() {
        return pitch;
    }
    public World getWorld() {
        return Bukkit.getServer().getWorld(world);
    }
    public int getBlockX() {
        return new Location(getWorld(), x, y, z).getBlockX();
    }
    public int getBlockY() {
        return new Location(getWorld(), x, y, z).getBlockY();
    }
    public int getBlockZ() {
        return new Location(getWorld(), x, y, z).getBlockZ();
    }
    public Block getBlock() {
        return new Location(getWorld(), x, y, z, yaw, pitch).getBlock();
    }
    public Location getLocation() {
        return new Location(getWorld(),x,y,z,yaw,pitch);
    }

}
