package net.ali.modbot.command.commands.misc;


import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@CommandInfo(name = "UserInfo", description = "Lists out a user's information")
public class InfoCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        User user;
        if (args.length > 0)
            if (event.getMessage().getMentionedUsers().size() > 0 && args[0].matches("<@[!]?[0-9]+>"))
                user = event.getMessage().getMentionedUsers().get(0);
            else if (args[0].matches("[0-9]+"))
                try {
                    user = Main.INSTANCE.jdaBot.getUserById(args[0]);
                    if (user == null) {
                        throw new InvalidCommandArgumentException("Could not find user: `" + args[0] + "`");
                    }
                } catch (Exception e) {
                    throw new InvalidCommandArgumentException("Usage: `.UserInfo <@User || UserID>`");
                }
            else
                throw new InvalidCommandArgumentException("Usage: `.UserInfo <@User || UserID>`");
        else
            user = event.getAuthor();
        Member member = event.getGuild().getMember(user);
        if (member != null) {
            EmbedBuilder builder = Main.INSTANCE.msgUtil.getDefaultBuilder();
            builder.setAuthor(user.getName() + "#" + user.getDiscriminator());
            builder.setThumbnail(user.getAvatarUrl());
            builder.addField("User Info",
                    "ID: `" + user.getId()
                            + "`\n Created: `" + user.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
                            + "`\n Status: `" + member.getOnlineStatus()
                            + "`\n Game: `" + member.getActivities().stream().filter(activity -> activity.getType().equals(Activity.ActivityType.DEFAULT)).findFirst().map(Activity::getName).orElse("NONE") + "`", false);
            builder.addField("Member Info",
                    "Nickname: `" + (member.getNickname() != null ? member.getNickname() : "NONE")
                            + "`\n Joined: `" + member.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"))
                            + "`\n Roles: `" + (member.getRoles().isEmpty() ? "NONE" : member.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "))) + "`", false);
            event.getTextChannel().sendMessage(builder.build()).queue();
        } else {
            sendErrorMessage("Could not find member " + String.join(" ", args), event.getTextChannel(), false);
        }
    }
}
