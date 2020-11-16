package net.ali.modbot.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.ali.modbot.Main;
import net.ali.modbot.audio.AudioTrackInfo;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.ali.modbot.managers.TrackManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(name = "Queue", description = "Lists the current queue", type = CommandType.Music)
public class QueueCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgumentException("Usage: `.Queue <Page Number>`");
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Queue:");
        builder.setColor(Color.red);
        String author = "Legenda";
        String author_Image = "https://cdn.discordapp.com/avatars/348464305164910605/11ec4dd6feaea03dc613a47efb1f6b27.jpg";
        builder.setFooter("Created By " + author, author_Image);
        AudioPlayer player = Main.INSTANCE.musicUtils.getAudioPlayer(event.getGuild());
        TrackManager manager = Main.INSTANCE.musicUtils.players.get(event.getGuild()).getValue();
        AudioTrackInfo info = manager.getQueue().stream().findFirst().orElse(null);
        Member user = info == null ? null : info.getAuthor();
        StringBuilder queue = new StringBuilder();
        if (info == null)
            throw new InvalidCommandStateException("The Queue is empty");
        List<AudioTrackInfo> tracks = manager.getQueue().stream().skip(1).collect(Collectors.toList());
        double numTracks = tracks.size() + 1;
        int pages = numTracks / 5 > (int) (numTracks / 5) ? (int) numTracks / 5 + 1 : (int) numTracks / 5;
        if (page > pages || page < 1) {
            throw new InvalidCommandStateException("Invalid Page Number");
        }
        if (numTracks != 1)
            for (int i = page * 5 - 4; i <= page * 5; i++) {
                if (i >= numTracks)
                    break;
                queue.append(i).append(".     `").append(tracks.get(i - 1).getTrack().getInfo().title).append("` Requested By `").append(user.getEffectiveName()).append("`\n");
            }
        StringBuilder description = new StringBuilder();
        if (page == 1)
            description.append("__Currently Playing:__\n`").append(player.getPlayingTrack().getInfo().title).append("` Requested By` ").append(user.getEffectiveName()).append("`\n");
        description.append("\n__Coming up__:\n").append(queue.toString()).append("\n\n").append("**")
                .append(tracks.size()).append(" Songs")
                .append(" : ").append(pages).append(" Pages**");

        builder.setDescription(description.toString());
        event.getTextChannel().sendMessage(builder.build()).queue();
    }
}
