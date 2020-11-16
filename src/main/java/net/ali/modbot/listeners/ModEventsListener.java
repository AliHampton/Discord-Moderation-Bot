package net.ali.modbot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ali.modbot.Main;
import net.ali.modbot.command.Command;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ModEventsListener extends ListenerAdapter {

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        Guild guild = event.getGuild();
        Main.INSTANCE.configManager.presets.entrySet().stream().filter(set -> set.getKey().equals(event.getGuild())).findAny().flatMap(entry -> entry.getValue().stream().filter(preset -> preset.getKey().equals("LogChannel")).findAny()).ifPresent(preset -> event.getGuild().retrieveBan(event.getUser()).queue(ban -> {
            String message = ban.getUser().getAsMention() + " Has Been Banned" + (ban.getReason() == null ? "" : ", Reason: ``" + ban.getReason() + "``");
            Command.sendEmbedMessage(message, Objects.requireNonNull(event.getGuild().getTextChannelById(preset.getValue())), false);
        }));
    }

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        Main.INSTANCE.configManager.presets.entrySet().stream().filter(set -> set.getKey().equals(event.getGuild())).findAny().flatMap(entry -> entry.getValue().stream().filter(preset -> preset.getKey().equals("LogChannel")).findAny()).ifPresent(preset -> Command.sendEmbedMessage(event.getUser().getAsMention() + " has Been Unbanned", event.getGuild().getTextChannelById(preset.getValue()), false));
    }
}
