package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Skip", description = "Skips the current playing song", type = CommandType.Music, role = "DJ")
public class SkipCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.skipTrack(event.getTextChannel(), 1);
    }
}
