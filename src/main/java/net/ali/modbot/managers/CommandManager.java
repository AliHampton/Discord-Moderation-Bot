package net.ali.modbot.managers;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.commands.admin.*;
import net.ali.modbot.command.commands.fun.PingCommand;
import net.ali.modbot.command.commands.fun.RandomCommand;
import net.ali.modbot.command.commands.music.*;
import net.ali.modbot.exceptions.IllegalCommandAccessException;
import net.ali.modbot.exceptions.InvalidCommandException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import net.ali.modbot.Main;
import net.ali.modbot.command.commands.fun.RollCommand;
import net.ali.modbot.command.commands.misc.DevelopersCommand;
import net.ali.modbot.command.commands.misc.HelpCommand;
import net.ali.modbot.command.commands.misc.InfoCommand;

import java.util.*;

public class CommandManager {

    public static final HashMap<String, Command> commands = new LinkedHashMap<>();

    static void execute(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        Guild guild = event.getGuild();
        User user = event.getAuthor();
        Member member = guild.getMember(user);
        TextChannel channel = event.getTextChannel();
        if (msg.getContentDisplay().startsWith(Main.INSTANCE.cmdPrefix)) {
            String message = msg.getContentRaw().substring(Main.INSTANCE.cmdPrefix.length());
            String[] args = message.split(" ");
            if (!message.isEmpty()) {
                for (Command cmd : commands.values()) {
                    if (cmd.getName().equalsIgnoreCase(args[0]) || Arrays.stream(cmd.getAlias()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                        if (member != null) {
                            if (hasPermission(cmd, guild, channel, member)) {
                                if (hasRole(cmd, guild, member) || PermissionUtil.checkPermission(channel, member, Permission.ADMINISTRATOR)) {
                                    String[] arguments = Arrays.copyOfRange(args, 1, args.length);
                                    cmd.execute(arguments, event);
                                    return;
                                } else {
                                    throw new IllegalCommandAccessException("You don't have the required role (" + cmd.getRole() + ")");
                                }
                            } else {
                                throw new IllegalCommandAccessException("You don't have the required permission (" + cmd.getPermission().toString() + ")");
                            }
                        } else {
                            throw new IllegalCommandAccessException("A backend cache error occurred");
                        }
                    }
                }
            }
            throw new InvalidCommandException("Unknown command: Use .help ");
        }
    }

    public void initCommands() {
        //Administrative
        commands.put("BanCommand", new BanCommand());
        commands.put("CleanCommand", new CleanCommand());
        commands.put("CooldownCommand", new CooldownCommand());
        commands.put("KickCommand", new KickCommand());
        commands.put("LogEventCommand", new LogEventCommand());
        commands.put("UnbanCommand", new UnbanCommand());

        //Fun
        commands.put("PingCommand", new PingCommand());
        commands.put("RandomCommand", new RandomCommand());
        commands.put("RollCommand", new RollCommand());

        //Misc
        commands.put("DeveloperCommand", new DevelopersCommand());
        commands.put("HelpCommand", new HelpCommand());
        commands.put("InfoCommand", new InfoCommand());

        //Music
        commands.put("ClearCommand", new ClearCommand());
        commands.put("PlayCommand", new PlayCommand());
        commands.put("PlayTopCommand", new PlayTopCommand());
        commands.put("JoinCommand", new JoinCommand());
        commands.put("QueueCommand", new QueueCommand());
        commands.put("LeaveCommand", new LeaveCommand());
        commands.put("LoopCommand", new LoopCommand());
        commands.put("NowPlayingCommand", new NowPlayingCommand());
        commands.put("SeekCommand", new SeekCommand());
        commands.put("SkipCommand", new SkipCommand());
        commands.put("SkipToCommand", new SkipToCommand());
        commands.put("ShuffleCommand", new ShuffleCommand());
        commands.put("StopCommand", new StopCommand());
        commands.put("RemoveCommand", new RemoveCommand());
        commands.put("ResumeCommand", new ResumeCommand());

    }

    public boolean isCommand(Message msg) {
        if (!msg.getContentDisplay().startsWith(Main.INSTANCE.cmdPrefix))
            return false;
        String message = msg.getContentDisplay().substring(1);
        String[] args = message.split(" ");
        for (Command cmd : commands.values()) {
            if (cmd.getName().equalsIgnoreCase(args[0]) || Arrays.stream(cmd.getAlias()).anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRole(Command cmd, Guild guild, Member member) {
        List<Role> roles = guild.getRoles();
        int hierarchyofperm = 10000;
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getName().equalsIgnoreCase(cmd.getRole()))
                hierarchyofperm = i;
        }
        int hierarchyofuser = roles.indexOf(member.getRoles().get(0));
        return hierarchyofperm >= hierarchyofuser;
    }

    private static boolean hasPermission(Command cmd, Guild guild, TextChannel channel, Member member) {
        return cmd.getPermission() == Permission.UNKNOWN || PermissionUtil.checkPermission(channel, member, cmd.getPermission());
    }

}
