package me.phoenixra.russian_roulette.files;

import me.phoenixra.core.files.PhoenixFileClass;

import java.util.Arrays;
import java.util.List;

public class LangClass extends PhoenixFileClass {
    @ConfigHeader(value= {""})
    @ConfigKey(path="GENERAL", isSection = true)
    public static String section_general;

    @ConfigHeader(value= {""})
    @ConfigKey(path="GENERAL.unknown_cmd", space="  ")
    public static String general_unknown_cmd = "&cUnknown command, /rr help";

    @ConfigHeader(value= {""})
    @ConfigKey(path="GENERAL.no_permission", space="  ")
    public static String general_no_permission = "&cYou have no permission";

    @ConfigHeader(value= {""})
    @ConfigKey(path="GENERAL.not_enough_args", space="  ")
    public static String general_not_enough_args = "&cMore arguments needed. /rr help";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES", isSection = true)
    public static String section_messages;

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.playerJoined", space="  ")
    public static String messages_playerJoined = "&aPlayer &l%player%&a joined the game";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.playerLeft", space="  ")
    public static String messages_playerLeft = "&cPlayer &l%player%&c left the game";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.startTimer", space="  ")
    public static String messages_startTimer = "&aGame starts in &l%timer%";

    @ConfigHeader(value= {"#Final round message"})
    @ConfigKey(path="MESSAGES.shooterBullets", space="  ")
    public static String messages_shooterBullets = "%shooter%&e received&c %bullets%&e  We all wonder, who will become a victim";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.shooterDecideTooLong", space="  ")
    public static String messages_shooterDecideTooLong = "%shooter%&с - Was too scared to shoot, the evil developers took control of him";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.playerEscaped", space="  ")
    public static String messages_playerEscaped = "%player%&с - Escaped. What a coward!";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.Misfire", space="  ")
    public static String messages_misfire= "&ePlayer &l%victim%&e - Survived! Looks like other players are disappointed";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.ShootWithoutBullets", space="  ")
    public static String messages_ShootWithoutBullets= "&ePlayer &l%shooter%&e haven't installed a single bullet. What a coward! He will regret in the next round";

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.bid_started_suicide", space="  ")
    public static List<String> messages_bid_started_suicide = Arrays.asList(
            "&e**************** RussianRoulette ****************",
            "&aTime to bid!",
            "&a%shooter% - shooting himself",
            "&aBullets loaded: %bullets%",
            "&aShooter's luck: %shooter_luck%",
            "",
            "&aChance to shoot: %shoot_chance%",
            "&e**************** RussianRoulette ****************");

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.bid_started_killOther", space="  ")
    public static List<String> messages_bid_started_killOther = Arrays.asList(
            "&e**************** RussianRoulette ****************",
            "&aTime to bid!",
            "&a%shooter% decided to kill %victim%",
            "&aBullets loaded: %bullets%",
            "&aShooter's luck: %shooter_luck%",
            "&aVictim's luck: %victim_luck%",
            "",
            "&aChance to shoot: %shoot_chance%",
            "&e**************** RussianRoulette ****************");

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.bid_finished", space="  ")
    public static List<String> messages_bidFinished = Arrays.asList(
            "&e**************** RussianRoulette ****************",
            "&aWon bet:",
            "&7%winners%",
            "&cLost bet:",
            "&7%losers%",
            "&e**************** RussianRoulette ****************");

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.player_suicide", space="  ")
    public static List<String> messages_player_suicide = Arrays.asList(
            "&cPlayer &l%shooter%&c was too unlucky to live.",
            "&cPlayer &l%shooter%&c died by accident",
            "&cPlayer &l%shooter%&c committed a suicide. ",
            "&cPlayer &l%shooter%&c forgot to say last words",
            "&cPlayer &l%shooter%&c shouldn't play the casino. Well, he won't be able to, I guess...",
            "&cPlayer &l%shooter%&c was killed by the evil developers");

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.player_killed_other", space="  ")
    public static List<String> messages_player_killed_other = Arrays.asList(
            "&cPlayer &l%victim%&c will visit &l%shooter%&c at night as an evil spirit",
            "&cPlayer &l%shooter%&c killed &l%victim%&c by accident",
            "&cPlayer &l%shooter%&c committed a crime by killing &l%victim%&c, but we won't tell anyone");

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.player_damaged_other", space="  ")
    public static List<String> messages_player_damaged_other = Arrays.asList(
            "&cPlayer &l%shooter%&c hurt feelings of &l%victim%",
            "&cPlayer &l%shooter%&c shot the &l%victim%&c by accident",
            "&cPlayer &l%shooter%&c almost killed the &l%victim%",
            "&cPlayer &l%shooter%&c apologizing for shooting at &l%victim%  He didn't want to!",
            "&cPlayer &l%victim%&c is planning to get a revenge from &l%shooter%&c for that unfair shot!");


    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.finish", space="  ")
    public static List<String> messages_finish = Arrays.asList(
            "&e**************** RussianRoulette ****************",
            "&aWinner:",
            "&a&l%winner%",
            "&e**************** RussianRoulette ****************");

    @ConfigHeader(value= {""})
    @ConfigKey(path="MESSAGES.teleport", space="  ")
    public static String messages_teleport = "&eTeleportation in &l%timer%";

    @ConfigHeader(value= {""})
    @ConfigKey(path="TITLES", isSection = true)
    public static String section_titles;

    @ConfigHeader(value = {""})
    @ConfigKey(path="TITLES.BecameSpectator", space= "  ")
    public static String titles_BecameSpectator = "&cYou are spectator";

    @ConfigHeader(value = {""})
    @ConfigKey(path="TITLES.YourTurn", space= "  ")
    public static String titles_yourTurn = "&cYour turn";

    @ConfigHeader(value = {""})
    @ConfigKey(path="TITLES.secondRoundStart_title", space= "  ")
    public static String titles_secondRoundStart_title = "&aRound 2";

    @ConfigHeader(value = {""})
    @ConfigKey(path="TITLES.secondRoundStart_subtitle", space= "  ")
    public static String titles_secondRoundStart_subtitle = "&e'Idk how to name it:('";

    @ConfigHeader(value = {""})
    @ConfigKey(path="TITLES.finalRoundStart_title", space= "  ")
    public static String titles_finalRoundStart_title = "&aFinal round";

    @ConfigHeader(value = {""})
    @ConfigKey(path="TITLES.finalRoundStart_subtitle", space= "  ")
    public static String titles_finalRoundStart_subtitle = "&e'Firefight!'";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="GUI", space= "", isSection=true)
    public static String gui_section;

    @ConfigHeader(value = { " "})
    @ConfigKey(path="GUI.Game", space= "  ", isSection=true)
    public static String gui_game_section;

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Game.chooseVictim", space= "    ")
    public static String gui_game_chooseVictim="&cChoose a victim";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="GUI.Selector", space= "  ", isSection=true)
    public static String gui_selector_section;

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.GameSelectorMainPage", space= "    ")
    public static String gui_GameSelectorMainPage="Play the RussianRoulette";

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.GameSelectorArenasPage", space= "    ")
    public static String gui_GameSelectorArenasPage="Choose arena";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="GUI.Selector.Items", space= "    ", isSection=true)
    public static String gui_items_section;

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.Items.ArenaItem_Lore", space= "      ")
    public static List<String> gui_ArenaItem_Lore=Arrays.asList(
            "",
            "&7Players: &a%rr_arena_online%/%rr_arena_max%",
            "&7State: &a%rr_arena_state%",
            "",
            "&eClick to play!");


    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.Items.closeItem_name", space= "      ")
    public static String gui_closeItem_name="&cClose";

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.Items.playItem_name", space= "      ")
    public static String gui_playItem_name="&aRandom game";

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.Items.playItem_lore", space= "      ")
    public static List<String> gui_playItem_lore=Arrays.asList(
            "&7Click to join the random game",
            "&7The algorithm will send you to the game with most amount of players");

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.Items.chooseArenaItem_name", space= "      ")
    public static String gui_chooseArenaItem_name="&aChoose specific arena";

    @ConfigHeader(value = {""})
    @ConfigKey(path="GUI.Selector.Items.chooseArenaItem_lore", space= "      ")
    public static List<String> gui_chooseArenaItem_lore=Arrays.asList(
            "&7Do you have your favorite game map?",
            "&7<Click here>");

    @ConfigHeader(value = { " "})
    @ConfigKey(path="HOLOGRAM", space= "", isSection=true)
    public static String hologram_section;

    @ConfigHeader(value = {""})
    @ConfigKey(path="HOLOGRAM.PendingForPlayers", space= "  ")
    public static List<String> hologram_PendingForPlayers=Arrays.asList(
            "&7>>&cRussian Roulette&7<<",
            "&eFunny game to test your luck",
            "",
            "&eArena: &a%arena_name%",
            "&eMin players: &a%min_players%",
            "",
            "&ePending for players...",
            "&e%current_players%/%max_players%");
    @ConfigHeader(value = {""})
    @ConfigKey(path="HOLOGRAM.RoundDelay", space= "  ")
    public static List<String> hologram_RoundDelay=Arrays.asList(
            "&7>>&e%round%&7<<",
            "",
            "&aStart in",
            "%timer%");

    @ConfigHeader(value = {""})
    @ConfigKey(path="HOLOGRAM.WaitingForShooter", space= "  ")
    public static List<String> hologram_WaitingForShooter=Arrays.asList(
            "&7>>&e%round%&7<<",
            "&eShooting: &a%shooting%&7->&c%victim%",
            "&eChance to hit: &a%chance%%",
            "",
            "&aWaiting for player's decision",
            "%timer%");

    @ConfigHeader(value = {""})
    @ConfigKey(path="HOLOGRAM.BetTime", space= "  ")
    public static List<String> hologram_BetTime=Arrays.asList(
            "&7>>&e%round%&7<<",
            "&eShooting: &a%shooting%&7->&c%victim%",
            "&eChance to hit: &a%chance%%",
            "",
            "&aTime to bet!",
            "%timer%");

    @ConfigHeader(value= {"#Random holo line when victim death"})
    @ConfigKey(path="HOLOGRAM.killed_random", space="  ")
    public static List<String> hologram_killed_random = Arrays.asList(
            "&cELIMINATED!",
            "&cANNIHILATED!",
            "&cDEAD!",
            "&cKILLED!",
            "&cKILLED!");

    @ConfigHeader(value= {"#Random holo line when victim injured"})
    @ConfigKey(path="HOLOGRAM.injured_random", space="  ")
    public static List<String> hologram_injured_random = Arrays.asList(
            "&eINJURED!",
            "&eALMOST DEAD!",
            "&eMISFORTUNE!",
            "&eWHOOPS");

    @ConfigHeader(value= {"#Random holo line when shooter misfire"})
    @ConfigKey(path="HOLOGRAM.misfire_random", space="  ")
    public static List<String> hologram_misfire_random = Arrays.asList(
            "&aMISFIRE!",
            "&aMISFIRE!",
            "&aLUCKY GUY!",
            "&aSURVIVED!");

    @ConfigHeader(value = { " "})
    @ConfigKey(path="SCOREBOARD", space= "", isSection=true)
    public static String scoreboard_section;

    @ConfigHeader(value = { " "})
    @ConfigKey(path="SCOREBOARD.displayName_starting", space= "  ")
    public static String scoreboard_displayName_starting="&eRussian&cRoulette";

    @ConfigHeader(value = {""})
    @ConfigKey(path="SCOREBOARD.score_starting", space= "  ")
    public static List<String> scoreboard_score_starting=Arrays.asList(
            "",
            "&fPlayers:&b %players%/%players_max%",
            "",
            "&fStart in:&b %timer%",
            "",
            "&fMap:&b %map%",
            "",
            "&eyour.website.com"
    );

    @ConfigHeader(value = { " "})
    @ConfigKey(path="SCOREBOARD.displayName_game", space= "  ")
    public static String scoreboard_displayName_game="&eRussian&cRoulette";

    @ConfigHeader(value = {""})
    @ConfigKey(path="SCOREBOARD.score_game", space= "  ")
    public static List<String> scoreboard_score_game=Arrays.asList(
            "",
            "&fSpectators:&b %spectators%",
            "",
            "&fPlaying:&b %players%",
            "",
            "&fRound:&b %round%",
            "&fCurrent shooter:&b %shooter%",
            "",
            "&fLuck bonus:&b %luck%",
            "",
            "&fMap:&b %map%",
            "",
            "&eyour.website.com"
    );

    @ConfigHeader(value = { " "})
    @ConfigKey(path="SCOREBOARD.displayName_spectator", space= "  ")
    public static String scoreboard_displayName_spectator="&eRussian&cRoulette";

    @ConfigHeader(value = {""})
    @ConfigKey(path="SCOREBOARD.score_spectator", space= "  ")
    public static List<String> scoreboard_score_spectator=Arrays.asList(
            "",
            "&fSpectators:&b %spectators%",
            "",
            "&fPlaying:&b %players%",
            "",
            "&fRound:&b %round%",
            "",
            "&fCurrent shooter:&b %shooter%",
            "",
            "&fMap:&b %map%",
            "",
            "&eyour.website.com"
    );

    @ConfigHeader(value = { " "})
    @ConfigKey(path="ITEMS", space= "", isSection=true)
    public static String items_section;

    @ConfigHeader(value = {""})
    @ConfigKey(path="ITEMS.bidYes", space= "  ")
    public static String item_bidYes="&cWill shoot";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="ITEMS.bidYes", space= "  ")
    public static String item_bidNo="&aWon't shoot";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="ITEMS.addBullet", space= "  ")
    public static String item_addBullet="&cAdd bullet(-1 bullet next round)";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="ITEMS.removeBullet", space= "  ")
    public static String item_removeBullet="&cRemove bullet(+1 bullet next round)";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="ITEMS.shoot", space= "  ")
    public static String item_shoot="&cShoot&7(&eChance:&c %chance%% &eBullets:&c %bullets%&7)";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="ITEMS.shoot_victim", space= "  ")
    public static String item_shoot_victim="&cShoot&7(&eBullets:&c %bullets%&7)";

    @ConfigHeader(value = { " "})
    @ConfigKey(path="OTHER", space= "", isSection=true)
    public static String other_section;

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.chatFormat_spectator", space= "  ")
    public static String other_chatFormat_spectator="&7EvilSpirit >> %vault_prefix% &r%player%: %msg%";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.chatFormat_player", space= "  ")
    public static String other_chatFormat_player="&aPlayer >> %vault_prefix% &r%player%: %msg%";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_Disabled", space= "  ")
    public static String other_round_Disabled="&cOffline";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_PendingForPlayers", space= "  ")
    public static String other_round_PendingForPlayers="&aPending for players";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_name_1", space= "  ")
    public static String other_round_name_1="Round 1";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_name_2", space= "  ")
    public static String other_round_name_2="Round 2";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_name_final", space= "  ")
    public static String other_round_name_final="Final round";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_starting", space= "  ")
    public static String other_round_starting="&aStarting";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_active", space= "  ")
    public static String other_round_active="&aActive";

    @ConfigHeader(value = {""})
    @ConfigKey(path="OTHER.round_finishing", space= "  ")
    public static String other_round_finishing="&eFinishing";

}
