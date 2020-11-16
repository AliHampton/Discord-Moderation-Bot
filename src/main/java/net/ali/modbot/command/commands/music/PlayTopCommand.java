package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;


@CommandInfo(name = "Playtop", description = "Adds a song to the queue", type = CommandType.Music)
public class PlayTopCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1) {
            throw new InvalidCommandArgumentException("Usage: `.Playtop (Link/Search Query)`");
        }
        String input = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        if (!(input.startsWith("https://") || input.startsWith("http://"))) {
            input = "ytsearch:" + input;
        }
        if (event.getMember() != null)
            Main.INSTANCE.musicUtils.newTrack(input, event.getMember(), event.getMessage(), true);
    }

}
