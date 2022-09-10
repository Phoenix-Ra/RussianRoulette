package me.phoenixra.russian_roulette.files;

import me.phoenixra.core.files.PhoenixFile;
import me.phoenixra.core.files.PhoenixFileManager;
import me.phoenixra.core.PhoenixUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConfigFile extends PhoenixFile {
    public ConfigFile(PhoenixFileManager fileM) {
        super(fileM, "config", new ConfigClass());
    }

    @Override
    public boolean handleLoad() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean reloadAction() {
        // TODO Auto-generated method stub
        return true;
    }

    public Location getLobby() {
        if(!getFileC().contains("Lobby")){
            Bukkit.getConsoleSender().sendMessage(PhoenixUtils.colorFormat("&c[RussianRoulette] Lobby does not specified"));
            return null;
        }
        if(!getFileC().contains("Lobby.world")){
            Bukkit.getConsoleSender().sendMessage(PhoenixUtils.colorFormat("&c[RussianRoulette] Lobby's world does not specified"));
            return null;
        }
        if(!getFileC().contains("Lobby.pos")){
            Bukkit.getConsoleSender().sendMessage(PhoenixUtils.colorFormat("&c[RussianRoulette] Lobby's position does not specified"));
            return null;
        }
        if(!getFileC().contains("Lobby.yaw")){
            Bukkit.getConsoleSender().sendMessage(PhoenixUtils.colorFormat("&c[RussianRoulette] Lobby's yaw does not specified"));
            return null;
        }
        if(!getFileC().contains("Lobby.pitch")){
            Bukkit.getConsoleSender().sendMessage(PhoenixUtils.colorFormat("&c[RussianRoulette] Lobby's pitch does not specified"));
            return null;
        }
        double posX=Double.parseDouble(getFileC().getString("Lobby.pos").split(";")[0]);
        double posY=Double.parseDouble(getFileC().getString("Lobby.pos").split(";")[1]);
        double posZ=Double.parseDouble(getFileC().getString("Lobby.pos").split(";")[2]);
        Location loc=new Location(Bukkit.getWorld(getFileC().getString("Lobby.world")),posX,posY,posZ);
        loc.setPitch((float)getFileC().getDouble("Lobby.pitch"));
        loc.setYaw((float)getFileC().getDouble("Lobby.yaw"));

        return loc;
    }
    public void setLobby(Location loc) {
        this.getFileC().set("Lobby.world", loc.getWorld().getName());
        this.getFileC().set("Lobby.pos", loc.getX()+";"+loc.getY()+";"+loc.getZ());
        this.getFileC().set("Lobby.yaw", loc.getYaw());
        this.getFileC().set("Lobby.pitch", loc.getPitch());
        save();
    }
}
