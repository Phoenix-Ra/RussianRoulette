package me.phoenixra.russian_roulette.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ArenaEditor {
    @Getter private final String worldName;
    @Getter private final String arenaName;

    @Getter private ArrayList<CustomLocation> seatPoints = new ArrayList<>();
    @Getter @Setter private int minPlayers;
    @Getter @Setter private int maxPlayers;
    @Getter @Setter private CustomLocation spectatorSpawn;

    @Getter @Setter private CustomLocation center;
    @Getter @Setter private CustomLocation centerEdge1;
    @Getter @Setter private CustomLocation centerEdge2;

    public ArenaEditor(String arenaName, String worldName) {
        this.arenaName=arenaName;
        this.worldName=worldName;
    }
    public ArenaEditor(Arena a) {
        worldName=a.getWorldName();
        arenaName=a.getArenaName();
        minPlayers=a.getMinPlayers();
        maxPlayers=a.getMaxPlayers();
        seatPoints=a.getSeatPoints();
        spectatorSpawn=a.getSpectatorSpawn();
        center=a.getCenter();
        centerEdge1=a.getCenterPos1();
        centerEdge2=a.getCenterPos2();
    }

    public void addSeatPoint(CustomLocation value) {
        seatPoints.add(value);
    }
}
