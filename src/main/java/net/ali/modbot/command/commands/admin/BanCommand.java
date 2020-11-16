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

@CommandInfo(name = "Ban", description = "Bans a user from the server", type = CommandType.Admin, permission = Permission.BAN_MEMBERS)
public class BanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().replaceAll("<@.*?>", "").split(" ");
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Ban <@User (As many as you want)> <Reason>*`");

        String banReason = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        List<Member> toBan = event.getMessage().getMentionedUsers().stream().map(user -> event.getGuild().getMember(user)).collect(Collectors.toList());
        if (!toBan.isEmpty()) {
            StringBuilder banned = new StringBuilder();
            StringBuilder notBanned = new StringBuilder();
            toBan.stream().filter(Objects::nonNull).forEach(member -> {
                try {
                    member.ban(7, banReason).queue();
                    banned.append(" ").append(member.getAsMention());
                } catch (Exception e) {
                    notBanned.append(" ").append(member.getAsMention());
                }
            });
            if (notBanned.length() > 0)
                sendErrorMessage("Unable To Ban: " + notBanned.toString(), event.getTextChannel(), false);
            if (banned.length() > 0)
                sendEmbedMessage("Banned:" + banned.toString(), event.getTextChannel(), false);
        } else {
            throw new InvalidCommandArgumentException("Usage: `.Ban <@User (As many as you want)> <Reason>*`");
        }
    }
}