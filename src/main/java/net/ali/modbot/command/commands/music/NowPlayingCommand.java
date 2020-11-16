package net.ali.modbot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "NowPlaying", description = "Sends Info About the Current Track", type = CommandType.Music, aliases = {"np"})
public class NowPlayingCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        AudioTrack track = Main.INSTANCE.musicUtils.getAudioPlayer(event.getGuild()).getPlayingTrack();
        if (track == null)
            throw new InvalidCommandStateException("There Is No Track Currently Playing");
        Main.INSTANCE.musicUtils.sendNowPlayingMessage(event.getTextChannel());
    }

}
