package net.ali.modbot.command.commands.admin;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.config.Preset;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Logbans", description = "Logs bans to a specified channel", type = CommandType.Admin, permission = Permission.ADMINISTRATOR)
public class LogEventCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.Logbans <#Channel/NONE>`");

        String preset = "NONE";
        if (!args[0].equalsIgnoreCase("none"))
            if (event.getMessage().getMentionedChannels().isEmpty()) {
                throw new InvalidCommandArgumentException("Usage: `.Logbans <#Channel/NONE>`");
            } else {
                TextChannel channel = event.getMessage().getMentionedChannels().get(0);
                preset = channel.getId();
                sendEmbedMessage("Logging Bans In " + channel.getAsMention(), event.getTextChannel(), false);
            }
        else
            sendEmbedMessage("No Longer Logging Bans", event.getTextChannel(), false);

        Main.INSTANCE.configManager.appendPreset(event.getGuild(), new Preset("LogChannel", preset));
    }
}
