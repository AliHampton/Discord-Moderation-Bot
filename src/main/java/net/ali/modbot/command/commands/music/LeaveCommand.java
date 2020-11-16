package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Leave", description = "Leaves current channel", type = CommandType.Music, aliases = {"disconnect"})
public class LeaveCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        if (guild.getAudioManager().getConnectedChannel() == null && !guild.getAudioManager().isConnected())
            throw new InvalidCommandStateException("Currently not connected to any voice channels");
        Main.INSTANCE.musicUtils.closeAudio(guild);
        sendMessage(":arrow_up: Left channel", event.getTextChannel());
    }
}