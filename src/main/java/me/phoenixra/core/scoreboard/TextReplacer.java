package me.phoenixra.core.scoreboard;

import me.phoenixra.core.PhoenixUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextReplacer {
    private final HashMap<String,PlaceholderTask> placeholders=new HashMap<>();



    public TextReplacer addPlaceholder(PlaceholderTask placeholder){
        placeholders.put(placeholder.getPlaceholder(),placeholder);
        return this;
    }

    public String replace(Player player,String text){
        for(PlaceholderTask task: placeholders.values()){
            text=text.replace(task.getPlaceholder(),task.getReplacement(player));
        }
        return PhoenixUtils.colorFormat(text);
    }
    public List<String> replace(Player player,List<String> list){
        List<String> newList=new ArrayList<>();
        list.forEach(s -> newList.add(replace(player,s)));
        return newList;
    }
}
