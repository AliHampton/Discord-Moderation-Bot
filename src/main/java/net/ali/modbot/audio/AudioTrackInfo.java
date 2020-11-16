package net.ali.modbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioTrackInfo {

    private final AudioTrack track;
    private final Member author;
    private final TextChannel channel;

    public AudioTrackInfo(AudioTrack track, Member author, TextChannel channel) {
        this.track = track;
        this.author = author;
        this.channel = channel;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Member getAuthor() {
        return author;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
