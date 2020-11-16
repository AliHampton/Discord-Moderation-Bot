package net.ali.modbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.ali.modbot.managers.TrackManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.ali.modbot.Main;
import net.ali.modbot.audio.AudioTrackInfo;
import net.ali.modbot.audio.PlayerSendHandler;
import net.ali.modbot.command.commands.music.LoopCommand;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MusicUtils {

    public final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();
    private final AudioPlayerManager manager;
    private final Map<Guild, Message> nowPlaying = new HashMap<>();

    public MusicUtils() {
        manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(manager);
    }

    public void setup() {
        scheduleAudioClose();
        scheduleNowPlayingEdit();
    }

    public AudioPlayer getAudioPlayer(Guild guild) {
        if (players.containsKey(guild))
            return players.get(guild).getKey();

        return newPlayer(guild);
    }

    public TrackManager getTrackManager(Guild guild) {
        if (!players.containsKey(guild))
            newPlayer(guild);

        return players.get(guild).getValue();
    }

    private AudioPlayer newPlayer(Guild guild) {
        AudioPlayer player = manager.createPlayer();
        TrackManager trackManager = new TrackManager(player);
        player.addListener(trackManager);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        players.put(guild, new AbstractMap.SimpleEntry<>(player, trackManager));

        return player;

    }

    public void newTrack(String identifier, Member author, Message msg, boolean top) {
        Guild guild = author.getGuild();
        TextChannel channel = msg.getTextChannel();
        getAudioPlayer(guild);
        System.out.println(identifier);
        manager.setFrameBufferDuration(500);
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                TrackManager manager = getTrackManager(guild);
                manager.queue(audioTrack, author, channel, top);
                if (getTrackManager(guild).getQueue().size() != 1) {
                    sendMessage(audioTrack, msg, channel);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                TrackManager manager = getTrackManager(guild);
                List<AudioTrack> playlist = audioPlaylist.getTracks();
                if (playlist.isEmpty())
                    throw new InvalidCommandStateException("Search query returned nothing");
                AudioTrack audioTrack = playlist.get(0);
                if (identifier.startsWith("ytsearch:")) {
                    manager.queue(audioTrack, author, channel, top);
                    if (getTrackManager(guild).getQueue().size() != 1) {
                        sendMessage(audioTrack, msg, channel);
                    }
                } else {
                    for (AudioTrack track : playlist) {
                        manager.queue(track, author, channel, top);
                    }
                    if (getTrackManager(guild).getQueue().size() != 1) {
                        sendPlaylistMessage(audioPlaylist, msg, channel);
                    }
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage(Main.INSTANCE.msgUtil.wrapMessage("Search returned nothing")).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                if (e.severity.equals(FriendlyException.Severity.COMMON))
                    Main.INSTANCE.msgUtil.wrapErrorMessage(e.getMessage());
                else
                    e.printStackTrace();
            }
        });
    }

    public void skipTrack(TextChannel channel, int position) {
        Guild guild = channel.getGuild();
        List<AudioTrackInfo> Queue = new ArrayList<>(getTrackManager(guild).getQueue());
        List<AudioTrackInfo> toRemove = Queue.stream().filter(info -> Queue.indexOf(info) != 0).limit(position - 1).collect(Collectors.toList());
        getTrackManager(guild).removeCollectionFromQueue(toRemove);
        AudioTrackInfo current = Queue.stream().findFirst().orElse(null);
        AudioTrackInfo next = Queue.isEmpty() ? null : Queue.get(position - 1);
        String skippedTrack = position == 1 ? (current != null ? current.getTrack().getInfo().title : "") : next != null ? next.getTrack().getInfo().title : "NONE";
        channel.sendMessage(":track_next: Skipped" + (position != 1 ? " To: `" + skippedTrack + "`" : ": `" + skippedTrack + "`")).queue();
        LoopCommand.endRepeat(guild);
        getAudioPlayer(guild).stopTrack();

    }

    public void seek(Guild guild, Long pos) {
        AudioTrack track = getAudioPlayer(guild).getPlayingTrack();
        if (track == null)
            throw new InvalidCommandStateException("No song is playing");
        if (pos > track.getInfo().length)
            throw new InvalidCommandStateException("Cannot seek longer than the current song!");
        track.setPosition(pos);

    }

    public void pause(Guild guild) {
        getAudioPlayer(guild).setPaused(true);
    }

    public void unPause(Guild guild) {
        getAudioPlayer(guild).setPaused(false);
    }

    public void clear(Guild guild) {
        getTrackManager(guild).clearQueue(false);
    }

    public String remove(Guild guild, int number) {
        return getTrackManager(guild).removeItem(number);
    }

    public void shuffle(Guild guild) {
        getTrackManager(guild).shuffleQueue();
    }

    public void closeAudio(Guild guild) {
        players.remove(guild);
        getTrackManager(guild).clearQueue(true);
        guild.getAudioManager().closeAudioConnection();

    }

    public void sendNowPlayingMessage(TextChannel channel) {
        MessageEmbed nowPlayingMessage = getNowPlayingMessage(channel);
        if (nowPlayingMessage != null)
            channel.sendMessage(nowPlayingMessage).queue(message -> nowPlaying.put(message.getGuild(), message));
    }

    private void sendMessage(AudioTrack audioTrack, Message msg, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Added:", audioTrack.getInfo().uri, msg.getAuthor().getAvatarUrl());
        builder.setColor(Color.getHSBColor(0f, 1f, 1f));
        String title = audioTrack.getInfo().title;
        AudioTrackInfo info = players.get(msg.getGuild()).getValue().getQueue().stream().filter(trackInfo -> trackInfo.getTrack().equals(audioTrack)).findFirst().orElse(null);
        String requested = info == null ? null : info.getAuthor().getEffectiveName();
        String position = getPositionInQueue(audioTrack, msg.getGuild());
        String length = getFormattedTime(audioTrack.getInfo().length);
        builder.addField("Title:", title, false);
        builder.addField("Position:", position, true);
        builder.addField("Length:", length, true);
        builder.addField("Requested:", requested, true);
        builder.setFooter("Created by " + MessageUtils.Author, MessageUtils.Author_Image);
        channel.sendMessage(builder.build()).queue();
    }

    private MessageEmbed getNowPlayingMessage(TextChannel channel) {
        Guild guild = channel.getGuild();
        TrackManager trackManager = Main.INSTANCE.musicUtils.getTrackManager(guild);
        AudioTrack track = Main.INSTANCE.musicUtils.getAudioPlayer(guild).getPlayingTrack();
        if (track != null) {
            AudioTrackInfo info = Objects.requireNonNull(trackManager.getQueue().stream().findFirst().orElse(null));
            EmbedBuilder builder = new EmbedBuilder();
            AudioTrackInfo upNext = Main.INSTANCE.musicUtils.getTrackManager(guild).getQueue().stream().filter(audio -> !audio.getTrack().equals(track)).findFirst().orElse(null);
            builder.setAuthor("Now Playing:", info.getTrack().getInfo().uri, info.getAuthor().getUser().getAvatarUrl());
            builder.setDescription("`" + info.getTrack().getInfo().title + "`\n\n`" + createSlider(track) + "`\n");
            builder.setColor(Color.red);
            builder.addField("Requested:", info.getAuthor().getEffectiveName(), true);
            builder.addField("UpNext:", upNext != null ? upNext.getTrack().getInfo().title : "Nothing", true);
            builder.setFooter("Created By " + MessageUtils.Author, MessageUtils.Author_Image);
            return builder.build();
        }
        return null;
    }

    private void sendPlaylistMessage(AudioPlaylist playlist, Message msg, TextChannel channel) {
        if (playlist.getTracks().size() > 1) {
            EmbedBuilder builder = new EmbedBuilder();
            AudioTrack audioTrack = playlist.getTracks().get(1);
            builder.setAuthor("Added Playlist:", audioTrack.getInfo().uri, msg.getAuthor().getAvatarUrl());
            builder.setColor(Color.getHSBColor(0f, 1f, 1f));
            builder.setDescription("**Enqueued:**` " + playlist.getTracks().size() + "` \nUse .Queue to see all enqueued songs");
            channel.sendMessage(builder.build()).queue();
        }
    }

    public String getFormattedTime(Long length) {
        double lengthSeconds = (double) length / 1000;
        double hours = lengthSeconds / 3600;
        double minutes = (hours - Math.floor(hours)) * 60;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        String secondStr = (int) seconds + "";
        if (secondStr.toCharArray().length < 2) {
            secondStr = "0" + secondStr;
        }
        if (hours > 1) {
            String minutesStr = (int) minutes + "";
            if (minutesStr.toCharArray().length < 2) {
                minutesStr = "0" + minutesStr;
            }
            return (int) hours + ":" + minutesStr + ":" + secondStr;
        } else {
            return (int) minutes + ":" + secondStr;
        }
    }

    private String getPositionInQueue(AudioTrack audioTrack, Guild guild) {
        int i = 0;
        for (AudioTrackInfo track : getTrackManager(guild).getQueue()) {
            if (track.getTrack().equals(audioTrack)) {
                break;
            }
            i++;
        }
        return i + "";
    }

    private String createSlider(AudioTrack track) {
        long position = track.getPosition();
        long length = track.getInfo().length;
        StringBuilder slider = new StringBuilder();
        slider.append(Main.INSTANCE.musicUtils.getFormattedTime(position)).append(" ");
        for (int i = 0; i < 35; i++) {
            if (i != (int) (35 * ((double) position / (double) length)))
                slider.append("-");
            else
                slider.append("\uD83D\uDD18");
        }
        slider.append(" ").append(Main.INSTANCE.musicUtils.getFormattedTime(length));
        return slider.toString();
    }

    private void scheduleAudioClose() {
        Main.INSTANCE.scheduleManager.scheduler.scheduleAtFixedRate(() -> Main.INSTANCE.jdaBot.getAudioManagers().forEach(audioManager -> {
            if (audioManager.getConnectedChannel() == null || audioManager.getConnectedChannel().getMembers().size() <= 1) {
                Guild guild = audioManager.getGuild();
                Main.INSTANCE.scheduleManager.schedule(guild, () -> {
                    if (guild.getAudioManager().getConnectedChannel() == null || guild.getAudioManager().getConnectedChannel().getMembers().size() <= 1)
                        Main.INSTANCE.musicUtils.closeAudio(guild);
                    Main.INSTANCE.scheduleManager.remove(guild);
                }, 1, TimeUnit.MINUTES);
            }
        }), 1, 1, TimeUnit.MINUTES);
    }

    private void scheduleNowPlayingEdit() {
        Main.INSTANCE.scheduleManager.scheduler.scheduleAtFixedRate(() -> nowPlaying.forEach((guild, message) -> {
            MessageEmbed nowPlayingMessage = getNowPlayingMessage(message.getTextChannel());
            if (nowPlayingMessage != null )
                message.editMessage(nowPlayingMessage).queue();
        }), 1, 1, TimeUnit.SECONDS);
    }

}
