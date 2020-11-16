package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Remove", description = "Removes track from queue", type = CommandType.Music, role = "DJ")
public class RemoveCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length <= 0)
            throw new InvalidCommandArgumentException("Usage: `.Remove (Index in Queue)`");

        int number;
        try {
            number = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException("Usage: `.Remove (Index in Queue)`");
        }
        if (number == 0)
            throw new InvalidCommandStateException("Cannot stop current track, use .skip");
        String message = Main.INSTANCE.musicUtils.remove(event.getGuild(), number + 1);
        event.getTextChannel().sendMessage(message).queue();
    }
}
