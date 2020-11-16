package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Clear", description = "Clears the current queue", type = CommandType.Music, role = "DJ")
public class ClearCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (Main.INSTANCE.musicUtils.getTrackManager(event.getGuild()).getQueue().isEmpty())
            throw new InvalidCommandStateException("The Queue is already empty");
        Main.INSTANCE.musicUtils.clear(event.getGuild());
        sendMessage(":arrows_counterclockwise: Cleared Queue:", event.getTextChannel());
    }
}
