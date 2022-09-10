package me.phoenixra.russian_roulette.game;

import java.util.ArrayList;

public class ArenaEditor {
    private final String worldName;
    private final String arenaName;
    private int minPlayers;
    private int maxPlayers;
    private ArrayList<CustomLocation> seatPoints= new ArrayList<>();
    private CustomLocation spectatorSpawn;

    private CustomLocation center;
    private CustomLocation center_edge1;
    private CustomLocation center_edge2;

    public ArenaEditor(String arenaName, String worldName) {
        this.arenaName=arenaName;
        this.worldName=worldName;
    }
    public ArenaEditor(Arena a) {
        worldName=a.getWorld();
        arenaName=a.getArenaName();
        minPlayers=a.getMinPlayers();
        maxPlayers=a.getMaxPlayers();
        seatPoints=a.getSeatPoints();
        spectatorSpawn=a.getSpectatorSpawn();
        center=a.getCenter();
        center_edge1=a.getCenterPos1();
        center_edge2=a.getCenterPos2();
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
    public void setCenterEdge1(CustomLocation value) {
        center_edge1 = value;
    }
    public void setCenterEdge2(CustomLocation value) {
        center_edge2 = value;
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
    public CustomLocation getCenterEdge1() {
        return center_edge1;
    }
    public CustomLocation getCenterEdge2() {
        return center_edge2;
    }
}
