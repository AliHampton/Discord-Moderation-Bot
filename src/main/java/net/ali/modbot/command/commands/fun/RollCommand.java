package net.ali.modbot.command.commands.fun;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Roll", description = "Rolls a dice", type = CommandType.Fun)
public class RollCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        int sides = 6;
        if (args.length >= 1)
            try {
                sides = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandStateException("Number of sides of the dice must be a number (Less than or equal to " + Integer.MAX_VALUE + ")");
            }
        long output = (int) Math.round(Math.random() * (sides - 1) + 1);
        sendEmbedMessage("Rolled a " + output + " (" + sides + ")", event.getTextChannel(), false);
    }

}
