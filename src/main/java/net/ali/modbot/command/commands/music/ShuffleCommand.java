package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Shuffle", description = "Shuffles the queue", type = CommandType.Music, role = "DJ")
public class ShuffleCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Main.INSTANCE.musicUtils.shuffle(event.getGuild());
        sendMessage(":twisted_rightwards_arrows: Shuffled Queue:", event.getTextChannel());
    }
}
