package net.ali.modbot.command.commands.admin;


import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CommandInfo(name = "Kick", description = "Kicks a user from the server", type = CommandType.Admin, permission = Permission.KICK_MEMBERS)
public class KickCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().replaceAll("<@.*?>", "").split(" ");
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Kick <@User (As many as you want)> <Reason>*`");

        String kickReason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        List<Member> toKick = event.getMessage().getMentionedUsers().stream().map(user -> event.getGuild().getMember(user)).collect(Collectors.toList());
        if (!toKick.isEmpty()) {
            StringBuilder kicked = new StringBuilder();
            StringBuilder notKicked = new StringBuilder();
            toKick.stream().filter(Objects::nonNull).forEach(member -> {
                try {
                    event.getGuild().kick(member, kickReason).queue();
                    kicked.append(" ").append(member.getAsMention());
                } catch (Exception e) {
                    notKicked.append(" ").append(member.getAsMention());
                }
            });
            if (notKicked.length() > 0)
                sendErrorMessage("Unable To Kick: " + notKicked.toString(), event.getTextChannel(), false);
            if (kicked.length() > 0)
                sendEmbedMessage("Kicked:" + kicked.toString(), event.getTextChannel(), false);
        } else {
            throw new InvalidCommandArgumentException("Usage: `.Kick <@User (As many as you want)> <Reason>*`");
        }
    }
}
