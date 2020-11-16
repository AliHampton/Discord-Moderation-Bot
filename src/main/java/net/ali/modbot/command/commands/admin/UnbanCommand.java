package net.ali.modbot.command.commands.admin;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;

@CommandInfo(name = "Unban", description = "Unbans a user from the server", type = CommandType.Admin, permission = Permission.BAN_MEMBERS)
public class UnbanCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Unban <UserID || USER#0000>`");

        String input = args[0];
        String regex = ".*#[0-9]{4}";
        boolean searchForName = input.matches(regex);
        if (searchForName || (input.matches("[0-9]+") && input.length() >= 17)) {
            String name = input.matches(regex) ? input.split("#")[0] : "";
            String descriminator = input.matches(regex) ? input.split("#")[1] : "";
            event.getGuild().retrieveBanList().queue(bans -> {
                Optional<User> toUnban = bans.stream().filter(ban -> {
                    if (searchForName)
                        return ban.getUser().getName().equals(name) && ban.getUser().getDiscriminator().equals(descriminator);
                    else
                        return ban.getUser().getId().equals(input);
                }).map(Guild.Ban::getUser).findFirst();
                if (!toUnban.isPresent())
                    sendErrorMessage("Could not find user: " + input, event.getTextChannel(), false);
                toUnban.ifPresent(user -> {
                    event.getGuild().unban(user).queue();
                    sendEmbedMessage("Unbanned User: " + user.getAsMention(), event.getTextChannel(), false);
                });
            });
        } else
            throw new InvalidCommandArgumentException("Enter a valid UserID or enter USER#0000");
    }
}
