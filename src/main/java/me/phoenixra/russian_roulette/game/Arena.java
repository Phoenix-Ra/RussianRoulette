package me.phoenixra.russian_roulette.game;

import org.bukkit.Location;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Arena implements Serializable {
    @Serial
    private static final long serialVersionUID = -6716717021664018868L;

    private final String worldName;
    private final String arenaName;
    private int minPlayers;
    private int maxPlayers;
    private final ArrayList<CustomLocation> seatPoints;
    private CustomLocation spectatorSpawn;


    private CustomLocation center;
    private CustomLocation center_pos1;
    private CustomLocation center_pos2;
    public double xMax, xMin, zMax, zMin;

    public Arena(ArenaEditor pc){
        worldName=pc.getWorld();
        arenaName=pc.getArenaName();
        minPlayers=pc.getMinPlayers();
        maxPlayers=pc.getMaxPlayers();
        seatPoints=pc.getSeatPoints();
        spectatorSpawn=pc.getSpectatorSpawn();

        center_pos1=pc.getCenterEdge1();
        center_pos2=pc.getCenterEdge2();
        xMax= Math.max(center_pos1.getX(), center_pos2.getX());
        xMin= Math.min(center_pos1.getX(), center_pos2.getX());
        zMax= Math.max(center_pos1.getZ(), center_pos2.getZ());
        zMin= Math.min(center_pos1.getZ(), center_pos2.getZ());

        Location loc=center_pos2.getLocation();
        loc.setX(xMin+((xMax-xMin)/2.0));
        loc.setZ(zMin+((zMax-zMin)/2.0));
        center=new CustomLocation(loc);
    }

    public String getWorld() {
        return worldName;
    }
    public String getArenaName() {
        return arenaName;
    }

    public ArrayList<CustomLocation> getSeatPoints() {
        return seatPoints;
    }
    public CustomLocation getSpectatorSpawn() {
        return spectatorSpawn;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public int getMinPlayers() {
        return minPlayers;
    }

    public CustomLocation getCenter() {
        return center;
    }
    public CustomLocation getCenterPos1() {
        return center_pos1;
    }
    public CustomLocation getCenterPos2() {
        return center_pos2;
    }

    public void addSeatPoint(CustomLocation value) {
        seatPoints.add(value);
    }
    public void setMaxPlayers(int value) {
        maxPlayers = value;
    }
    public void setMinPlayers(int value) {
        minPlayers = value;
    }
    public void setSpectatorSpawn(CustomLocation value) {
        spectatorSpawn = value;
    }

    public void setCenter(CustomLocation value) {
        center = value;
    }

    public void setCenterPos1(CustomLocation value) {
        center_pos1 = value;
    }
    public void setCenterPos2(CustomLocation value) {
        center_pos2 = value;
    }


}
