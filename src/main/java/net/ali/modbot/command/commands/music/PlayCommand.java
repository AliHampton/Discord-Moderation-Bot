package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;


@CommandInfo(name = "Play", description = "Adds a song to the queue", type = CommandType.Music)
public class PlayCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.Play (Link/Search Query)`");
        else if (event.getMember() == null || event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null)
            throw new InvalidCommandStateException("You Must Be In A VoiceChannel To Use The Bot");

        String input = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        if (!(input.startsWith("https://") || input.startsWith("http://")))
            input = "ytsearch:" + input;

        Main.INSTANCE.musicUtils.newTrack(input, event.getMember(), event.getMessage(), false);

    }

}
