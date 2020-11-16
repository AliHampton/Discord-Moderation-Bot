package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Resume", description = "Unpauses current song", type = CommandType.Music, aliases = {"unpause"})
public class ResumeCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.unPause(event.getGuild());
        sendMessage(":arrow_forward: Resumed:", event.getTextChannel());
    }
}
