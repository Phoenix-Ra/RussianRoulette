package me.phoenixra.russian_roulette.files;

import me.phoenixra.core.files.PhoenixFileClass;

import java.util.Arrays;
import java.util.List;

public class ConfigClass extends PhoenixFileClass {
    @ConfigHeader(value = {""})
    @ConfigKey(path="GENERAL", space= "", isSection=true)
    public static String general_section;

    @ConfigHeader(value = { "#If set to true, will log plugin actions in console"})
    @ConfigKey(path="debug", space= "  ")
    public static boolean debug = false;


    @ConfigHeader(value = {""})
    @ConfigKey(path="Timers", space= "", isSection=true)
    public static String timers_section;


    @ConfigHeader(value = { "#playTime limiter in seconds","#-1 - game will be finished when there will be a winner. "})
    @ConfigKey(path="playTime-limit", space="  ")
    public static int playTimeLimit = -1;


    @ConfigHeader(value = { "#Game start delay"})
    @ConfigKey(path="start_delay", space= "  ")
    public static int start_delay = 30;

    @ConfigHeader(value = { "#How much time should game wait for shooter's decision"})
    @ConfigKey(path="WaitingShooter_time", space= "  ")
    public static int waitingShooter_time = 30;

    @ConfigHeader(value = { "#Bid time"})
    @ConfigKey(path="bid_time", space= "  ")
    public static int bid_time = 10;

    @ConfigHeader(value = { "#Pause between rounds"})
    @ConfigKey(path="nextRound-delay", space= "  ")
    public static int nextRound_delay = 10;

    @ConfigHeader(value = { "#Pause before teleporting next shooter"})
    @ConfigKey(path="nextShooter-delay", space= "  ")
    public static int nextShooter_delay = 5;

    @ConfigHeader(value = { "#Pause before kicking the players from finished game"})
    @ConfigKey(path="finish_delay", space= "  ")
    public static int finish_delay = 15;

    @ConfigHeader(value = { "#Allowed commands in game"})
    @ConfigKey(path="cmd_whitelist", space= "  ")
    public static List<String> cmd_whitelist = Arrays.asList("rr leave", "rr sit");

    @Override
    public Object getReference(int number) {
        Object[] ob = {general_section,debug,timers_section,playTimeLimit,start_delay,
                waitingShooter_time,bid_time,nextRound_delay,nextShooter_delay,finish_delay,cmd_whitelist};
        return ob[number];
    }
}
