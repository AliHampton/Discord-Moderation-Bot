package net.ali.modbot.command.commands.misc;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.ali.modbot.managers.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Help", description = "A help command")
public class HelpCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        StringBuilder msg = new StringBuilder();
        if (args.length < 1) {
            msg.append("__Admin Commands:__ \n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(CommandType.Admin)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));

            msg.append("\n__Music Commands:__ \n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(CommandType.Music)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));

            msg.append("\n__Fun Commands:__\n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(CommandType.Fun)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));

            msg.append("\n__Other Commands:__\n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(CommandType.Misc)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));
        } else {
            String casez = args[0];
            CommandType type = null;
            switch (casez.toLowerCase()) {
                case "admin":
                    type = CommandType.Admin;
                    break;
                case "music":
                    type = CommandType.Music;
                    break;
                case "fun":
                    type = CommandType.Fun;
                    break;
                case "misc":
                    type = CommandType.Misc;
            }
            if (type == null)
                throw new InvalidCommandArgumentException("Usage: `.help <cmdType>`");
            final CommandType type2 = type;
            msg.append("__").append(type2.name()).append(" Commands__:\n\n");
            CommandManager.commands.values().stream().filter(cmd -> cmd.getType().equals(type2)).forEach(cmd -> msg.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));
        }

        String message = new String(msg);
        sendEmbedMessage(message, event.getTextChannel(), false);

    }
}
