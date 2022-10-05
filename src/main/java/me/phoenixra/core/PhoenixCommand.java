package me.phoenixra.core;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class PhoenixCommand implements CommandExecutor, TabExecutor {
    private final Map<String, Method> subCommands = Maps.newLinkedHashMap();
    protected CommandSender sender;
    protected Player player;
    protected boolean isPlayer;
    private String[] args;

    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private String usage;
    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private int minArgs;
    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private String permission;
    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private boolean allowConsole;
    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private boolean adminCmd = false;
    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private String prefix;


    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private String msg_unknownCommand = "&cUnknown command, /rr help";

    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private String msg_noPermission = "&cYou have no permission";

    @Setter(AccessLevel.PROTECTED) @Getter(AccessLevel.PROTECTED)
    private String msg_notEnoughArgs = "&cMore arguments needed. /rr help";

    public PhoenixCommand() {
        this.usage = null;
        this.permission = null;
        this.allowConsole = true;
        this.minArgs = 0;
        this.prefix = "";

        for (final Method method : this.getClass().getMethods()) {
            final SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                subCommands.put(subCommand.name().isEmpty() ? method.getName().toLowerCase() : subCommand.name(), method);
            }
        }
        try {
            subCommands.put("help", getClass().getMethod("help"));
        }catch (NoSuchMethodException e){
            try {
                subCommands.put("help", getClass().getSuperclass().getMethod("help"));
            }catch (NoSuchMethodException ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
        try {
            this.isPlayer = (sender instanceof Player);
            this.sender = sender;
            this.args = args;
            if (!this.allowConsole && !this.isPlayer) {
                this.reply("&cYou have to be a player to execute this command!");
                return true;
            }
            if (this.isPlayer) {
                this.player = (Player) sender;
            }
            final String subCommand = (getArgLength() > 0) ? getArgument(0).toLowerCase() : "help";
            final Method method = subCommands.get(subCommand.toLowerCase());
            if (method == null) {
                reply(msg_unknownCommand);
                return true;
            }
            final SubCommand info = method.getAnnotation(SubCommand.class);

            if (this.permission != null && !sender.hasPermission(this.permission)||(!info.permission().isBlank() && !sender.hasPermission(info.permission()))) {
                if (adminCmd) {
                    this.reply("Unknown command. Type '/help' for help.");
                } else {
                    this.reply(msg_noPermission);
                }
                return true;
            }
            if (getArgLength() < this.minArgs || getArgLength() < info.minArgs() + 1) {
                this.reply(msg_notEnoughArgs);
                return true;
            }
            method.invoke(this);
        } catch (Exception e) {
            replyException(e, "&cUnexpected error occurred while trying to execute the command!");
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list=new ArrayList<>();
        if(this.permission != null && !sender.hasPermission(this.permission)) return null;

        if(args.length==1){
            for(String cmd: subCommands.keySet()){
                String permission=subCommands.get(cmd).getAnnotation(SubCommand.class).permission();
                if(!permission.isBlank()&&!sender.hasPermission(permission)) continue;
                list.add(cmd);
            }
        }
        return list;
    }

    @SubCommand(description = "", minArgs = -1, usage = "defaultHelp")
    public void help() {
        List<SubCommand> sorted_methods=subCommands.values()
                .stream().map(method -> method.getAnnotation(SubCommand.class))
                .sorted(Comparator.comparingInt(SubCommand::sortOrder)).collect(Collectors.toList());
        this.reply("&aAvailable commands:");
        for (SubCommand info : sorted_methods) {
            if(info.usage().equals("defaultHelp")) continue;
            if(!info.permission().isBlank()&&!sender.hasPermission(info.permission())) continue;
            final String usage = info.usage().isEmpty() ? "" : (" " + (info.usage()));
            final String desc = info.description();
            this.reply("&c " + usage + "&7 - &f" + desc,false);
        }

    }


    protected void reply(final String message) {
        this.reply(this.sender, message,true);
    }
    protected void reply(final String message, boolean prefix) {
        this.reply(this.sender, message, prefix);
    }
    protected void reply(final CommandSender sender, String message, boolean prefix) {
        message = PhoenixUtils.colorFormat(message);

        final String text = (prefix ? this.prefix : "") + message;
        sender.sendMessage(text);
    }

    protected void replyException(final Exception exception, String messageToSender) {
        exception.printStackTrace();
        Bukkit.getLogger().log(Level.SEVERE, prefix + "§cUnexpected error occurred while trying to execute the command! ", exception);
        messageToSender = PhoenixUtils.colorFormat(messageToSender);
        final String text = this.prefix + messageToSender;
        sender.sendMessage(text);
    }

    protected String getArgument(final int index) {
        return this.args[index];
    }

    protected void setArgument(final int index, String value) {
        if (args.length < index + 1) {
            String[] newarr = new String[index + 1];
            System.arraycopy(args, 0, newarr, 0, args.length);
            newarr[index] = value;
            args = newarr;
            return;
        }
        this.args[index] = value;
    }

    protected int getArgumentAsInt(final int index) {
        return Integer.parseInt(this.getArgument(index));
    }

    protected Player getArgumentAsPlayer(final int index) {
        return Bukkit.getPlayer(this.getArgument(index));
    }

    protected int getArgLength() {
        return this.args.length;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface SubCommand {
        String name() default "";

        String description() default "";

        String usage() default "";

        String permission() default "";

        int minArgs() default 0;

        int sortOrder() default 0;
    }

}
