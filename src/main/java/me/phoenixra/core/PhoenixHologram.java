package me.phoenixra.core;

import com.google.common.base.Preconditions;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class PhoenixHologram {
    @Getter private final String id;
    private Hologram hologram;

    public PhoenixHologram(Location location) {
        Preconditions.checkNotNull(location, "Location cannot be null");
        id = UUID.randomUUID().toString();

        hologram = DHAPI.createHologram(id,location);
    }

    public void setLines(List<String> lines) {
        if (lines == null)
            return;

        for (int i = 0; i < lines.size(); i++) {
            if (isValidIndex(i)) {
                if (!hologram.getPage(0).getLines().get(i).getContent().equals(lines.get(i)))
                    hologram.getPage(0).setLine(i,lines.get(i));
            } else {
                addLine(lines.get(i));

            }
        }
        setVisible(true);
    }
    public void addLine(String line){
        DHAPI.addHologramLine(hologram,line);
    }
    public void addItemLine(Material material){
        DHAPI.addHologramLine(hologram,material);
    }
    public void changeLine(int line,String value){
        if(!isValidIndex(line)) return;
        DHAPI.setHologramLine(hologram,line,value);
    }
    public void teleport(Location loc) {
        if (loc == null)
            return;

        DHAPI.moveHologram(hologram, loc);

    }
    public void clearLines() {
        Location location = hologram.getLocation();
        hologram.delete();
        hologram = DHAPI.createHologram(id,location);

    }
    public void setVisible( boolean visible) {
        if(!visible) hologram.disable();
        if(!visible) hologram.enable();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < hologram.getPage(0).getLines().size();
    }

    public Location getLocation(){
        return hologram.getLocation();
    }
}
