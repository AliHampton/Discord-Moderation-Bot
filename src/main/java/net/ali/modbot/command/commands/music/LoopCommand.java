package net.ali.modbot.command.commands.music;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

@CommandInfo(name = "Loop", description = "Loops current track", type = CommandType.Music, role = "DJ", aliases = {"Repeat"})
public class LoopCommand extends Command {

    private final static HashMap<Guild, Integer> times = new HashMap<>();

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        int times = getGuildRepeat(guild);
        if (args.length >= 1) {
            int input;
            try {
                input = Integer.parseInt(args[0]);

            } catch (NumberFormatException e) {
                throw new InvalidCommandStateException("Usage: `.Loop (Amount)` \n`.Loop to toggle`");
            }
            times = input + 1;
            setGuildRepeat(guild, times);
            sendMessage(":repeat: Loop: `" + input + "` times", channel);
            return;
        }
        if (times <= -1) {
            times = 1;
            sendMessage(":repeat: Loop: Disabled", channel);
        } else {
            times = -1;
            sendMessage(":repeat: Loop: ∞", channel);
        }
        setGuildRepeat(guild, times);

    }

    private static int getGuildRepeat(Guild guild) {
        return times.entrySet().stream().filter(guildIntegerEntry -> guildIntegerEntry.getKey().equals(guild)).map(Map.Entry::getValue).findFirst().orElse(1);
    }

    private static void setGuildRepeat(Guild guild, Integer value) {
        times.remove(guild);
        times.put(guild, value);
    }

    public static boolean getRepeat(Guild guild) {
        int times = getGuildRepeat(guild);
        if (times != 0)
            times--;
        setGuildRepeat(guild, times);
        return times != 0;
    }

    public static void endRepeat(Guild guild) {
        setGuildRepeat(guild, 0);
    }

}
