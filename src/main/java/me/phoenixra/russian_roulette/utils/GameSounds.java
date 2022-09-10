package me.phoenixra.russian_roulette.utils;

import org.bukkit.Sound;

public class GameSounds {
    public static Sound menuOpen() {
        return Sound.BLOCK_NOTE_BLOCK_HARP;
    }
    public static Sound actionAllowed() {
        return Sound.BLOCK_NOTE_BLOCK_PLING;
    }
    public static Sound actionDenied() {
        return Sound.BLOCK_NOTE_BLOCK_GUITAR;
    }
    public static Sound cLickGUI() {
        return Sound.UI_BUTTON_CLICK;
    }

    public static Sound timeTick(){return Sound.BLOCK_NOTE_BLOCK_PLING;}
    public static Sound gameStart(){return Sound.ENTITY_PLAYER_LEVELUP;}
    public static Sound roundChange(){return Sound.BLOCK_END_PORTAL_SPAWN;}

}
