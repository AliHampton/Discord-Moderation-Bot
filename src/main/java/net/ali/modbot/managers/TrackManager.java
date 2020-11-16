package net.ali.modbot.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.ali.modbot.Main;
import net.ali.modbot.audio.AudioTrackInfo;
import net.ali.modbot.command.commands.music.LoopCommand;
import net.ali.modbot.utils.MessageUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer player;
    private final Queue<AudioTrackInfo> queue;

    public TrackManager(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author, TextChannel channel, boolean top) {
        AudioTrackInfo info = new AudioTrackInfo(track, author, channel);
        Set<AudioTrackInfo> currentQueue = new LinkedHashSet<>();
        if (top) {
            currentQueue = getQueue().stream().skip(1).collect(Collectors.toSet());
            clearQueue(false);
        }
        queue.add(info);
        queue.addAll(currentQueue);

        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        }
    }

    public Set<AudioTrackInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public void clearQueue(boolean current) {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        queue.clear();
        if (!current)
            queue.add(oldQueue.get(0));
    }

    public String removeItem(int number) {
        if (number > queue.size())
            throw new InvalidCommandStateException("Could not find track");
        AudioTrackInfo toRemove = getQueue().stream().skip(number - 1).findFirst().orElse(null);
        if (toRemove == null)
            throw new InvalidCommandStateException("Could not find track");
        queue.remove(toRemove);
        return "Removed: " + (number - 1) + ". `" + toRemove.getTrack().getInfo().title + "`";
    }

    public void shuffleQueue() {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        AudioTrackInfo currentTrack = oldQueue.get(0);
        oldQueue.remove(0);
        Collections.shuffle(oldQueue);
        oldQueue.add(0, currentTrack);
        clearQueue(true);
        queue.addAll(oldQueue);
    }

    private void addTop(AudioTrackInfo info) {
        List<AudioTrackInfo> oldQueue = new ArrayList<>(getQueue());
        if (!oldQueue.isEmpty())
            oldQueue.remove(0);
        oldQueue.add(0, info);
        clearQueue(true);
        queue.addAll(oldQueue);
    }

    public void removeCollectionFromQueue(Collection<AudioTrackInfo> c) {
        queue.removeAll(c);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioTrackInfo info = queue.element();
        GuildVoiceState voiceState = info.getAuthor().getVoiceState();
        VoiceChannel connected = info.getAuthor().getGuild().getAudioManager().getConnectedChannel();
        VoiceChannel channel;
        Guild guild = info.getAuthor().getGuild();
        if (connected != null)
            channel = connected;
        else if (voiceState == null)
            throw new InvalidCommandStateException("You must be in a VoiceChannel to summon the bot");
        else
            channel = voiceState.getChannel();
        Main.INSTANCE.musicUtils.sendNowPlayingMessage(info.getChannel());
        guild.getAudioManager().openAudioConnection(channel);
        Main.INSTANCE.scheduleManager.remove(guild);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioTrackInfo audioTrackInfo = getQueue().stream().findFirst().orElse(null);
        if (audioTrackInfo == null)
            return;
        Guild guild = audioTrackInfo.getAuthor().getGuild();
        if (!LoopCommand.getRepeat(guild)) {
            queue.poll();
            if (!queue.isEmpty()) {
                player.playTrack(queue.element().getTrack());
            } else {
                Main.INSTANCE.scheduleManager.schedule(guild, () -> {
                    Main.INSTANCE.musicUtils.closeAudio(guild);
                    Main.INSTANCE.scheduleManager.remove(guild);
                }, 1, TimeUnit.MINUTES);
            }
        } else {
            addTop(queue.element());
            player.playTrack(track.makeClone());
        }
    }

    private void sendNextTrackMessage(AudioTrackInfo info, Guild guild, AudioTrack track) {
        EmbedBuilder builder = new EmbedBuilder();
        AudioTrackInfo upNext = Main.INSTANCE.musicUtils.getTrackManager(guild).getQueue().stream().filter(audio -> !audio.getTrack().equals(track)).findFirst().orElse(null);
        builder.setAuthor("Now Playing:", info.getTrack().getInfo().uri, info.getAuthor().getUser().getAvatarUrl());
        builder.setDescription("`" + info.getTrack().getInfo().title + "`");
        builder.setColor(Color.red);
        builder.addField("Requested:", info.getAuthor().getEffectiveName(), true);
        builder.addField("Length: ", Main.INSTANCE.musicUtils.getFormattedTime(track.getInfo().length), true);
        builder.addField("UpNext:", upNext != null ? upNext.getTrack().getInfo().title : "Nothing", true);
        builder.setFooter("Created By " + MessageUtils.Author, MessageUtils.Author_Image);
        info.getChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        player.playTrack(track);
    }
}
