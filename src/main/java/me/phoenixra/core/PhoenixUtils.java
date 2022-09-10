package me.phoenixra.core;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoenixUtils {
    public static Color decodeHexColor(String value){
        try {
            java.awt.Color color = java.awt.Color.decode(value);
            return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        }catch (Exception e){
            e.printStackTrace();
            Bukkit.getLogger().severe("Incorrect HEX code: "+value);
            return Color.WHITE;
        }
    }
    public static List<String> colorFormat(List<String> list) {
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, colorFormat(list.get(i)));
        }
        return list;
    }
    public static String colorFormat(String s){
        if(s==null||s.isBlank()) return s;
        if (getServerVersion() > 15) {
            try {
                s = translateHexCodes(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        s= ChatColor.translateAlternateColorCodes('&',s);
        return s;
    }
    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");

    private static String translateHexCodes (String textToTranslate) {

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

    }
    public static String getProgressBar(long n, long n2, int n3, String string, String string2, String string3) {
        long n4;
        double f = (double)n / (double)n2;
        long n5 = (long)((double)n3 * f);
        long n6 = n3 - n5;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PhoenixUtils.colorFormat(string2));
        for (n4 = 0; n4 < n5; ++n4) {
            stringBuilder.append(string);
        }
        stringBuilder.append(PhoenixUtils.colorFormat(string3));
        for (n4 = 0; n4 < n6; ++n4) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    public static Color parseColor(String s){
        Color color;
        switch (s.toLowerCase()){
            case "aqua":
                color=Color.AQUA;
                break;
            case "red":
                color=Color.RED;
                break;
            case "green":
                color=Color.GREEN;
                break;
            case "blue":
                color=Color.BLUE;
                break;
            case "fuchsia":
                color=Color.FUCHSIA;
                break;
            case "gray":
                color=Color.GRAY;
                break;
            case "lime":
                color=Color.LIME;
                break;
            case "maroon":
                color=Color.MAROON;
                break;
            case "navy":
                color=Color.NAVY;
                break;
            case "olive":
                color=Color.OLIVE;
                break;
            case "orange":
                color=Color.ORANGE;
                break;
            case "purple":
                color=Color.PURPLE;
                break;
            case "silver":
                color=Color.SILVER;
                break;
            case "teal":
                color=Color.TEAL;
                break;
            case "yellow":
                color=Color.YELLOW;
                break;
            default:
                color=Color.WHITE;
                break;

        }
        return color;
    }
    public static Class<?> getNMSClass( String pack, String name) {
        String className;

        if (getServerVersion() < 17) className = "net.minecraft.server"+ getNMSVersion() + name;
        else className = "net.minecraft." + pack + '.' + name;

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new InternalError("Failed to get NMS class " + className + ". Probably, your currently using unsupported server version", e);
        }
    }
    public static int getServerVersion(){
        return Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
    }
    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
