package me.phoenixra.russian_roulette.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Arena implements Serializable {
    @Serial
    private static final long serialVersionUID = -6716717021664018868L;

    @Getter private final String worldName;
    @Getter private final String arenaName;
    @Getter @Setter private int minPlayers;
    @Getter @Setter private int maxPlayers;
    @Getter private final ArrayList<CustomLocation> seatPoints;
    @Getter @Setter private CustomLocation spectatorSpawn;


    @Getter @Setter private CustomLocation center;
    @Getter @Setter private CustomLocation centerPos1;
    @Getter @Setter private CustomLocation centerPos2;

    @Getter private final double xMax;
    @Getter private final double xMin;
    @Getter private final double zMax;
    @Getter private final double zMin;

    public Arena(ArenaEditor pc){
        worldName=pc.getWorldName();
        arenaName=pc.getArenaName();
        minPlayers=pc.getMinPlayers();
        maxPlayers=pc.getMaxPlayers();
        seatPoints=pc.getSeatPoints();
        spectatorSpawn=pc.getSpectatorSpawn();

        centerPos1=pc.getCenterEdge1();
        centerPos2=pc.getCenterEdge2();
        xMax= Math.max(centerPos1.getX(), centerPos2.getX());
        xMin= Math.min(centerPos1.getX(), centerPos2.getX());
        zMax= Math.max(centerPos1.getZ(), centerPos2.getZ());
        zMin= Math.min(centerPos1.getZ(), centerPos2.getZ());

        Location loc=centerPos2.getLocation();
        loc.setX(xMin+((xMax-xMin)/2.0));
        loc.setZ(zMin+((zMax-zMin)/2.0));
        center=new CustomLocation(loc);
    }

    public void addSeatPoint(CustomLocation value) {
        seatPoints.add(value);
    }



}
