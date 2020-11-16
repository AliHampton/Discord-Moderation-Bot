package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@CommandInfo(name = "Join", description = "Joins the user's voice channel", type = CommandType.Music, aliases = {"connect"})
public class JoinCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        Member member = event.getMember();
        if (member != null ) {
            GuildVoiceState voiceState = member.getVoiceState();
            if (voiceState == null)
                throw new InvalidCommandStateException("You must be in a VoiceChannel to summon the bot");
            Guild guild = event.getGuild();
            guild.getAudioManager().openAudioConnection(voiceState.getChannel());
            Main.INSTANCE.scheduleManager.schedule(guild, () -> {
                Main.INSTANCE.musicUtils.closeAudio(guild);
                Main.INSTANCE.scheduleManager.remove(guild);
            }, 1, TimeUnit.MINUTES);
            sendMessage(":arrow_down: Joined:", event.getTextChannel());
        }
    }
}
