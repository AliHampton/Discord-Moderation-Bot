package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "SkipTo", description = "Skips to a position within the queue", type = CommandType.Music, role = "DJ")
public class SkipToCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.SkipTo (Position)`");
        int position = Integer.parseInt(args[0]);
        Main.INSTANCE.musicUtils.skipTrack(event.getTextChannel(), position);
    }

}
