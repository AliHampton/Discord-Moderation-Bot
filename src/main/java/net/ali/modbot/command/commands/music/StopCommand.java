package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Stop", description = "Pauses current song", type = CommandType.Music, role = "DJ", aliases = {"pause"})
public class StopCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.pause(event.getGuild());
        sendMessage(":pause_button: Paused:", event.getTextChannel());
    }
}
