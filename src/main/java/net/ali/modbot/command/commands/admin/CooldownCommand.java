package net.ali.modbot.command.commands.admin;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Cooldown", description = "Adds user to cooldown role", type = CommandType.Admin, permission = Permission.MESSAGE_MANAGE, aliases = {"mute"})
public class CooldownCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Cooldown <@User>`");
        Member toMute = event.getMessage().getMentionedUsers().size() > 0 ? event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0)) : null;
        if (toMute != null) {
            Guild guild = event.getGuild();
            Role muteRole = guild.getRoles().stream().filter(role -> role.getName().equalsIgnoreCase("Cooldown")).findFirst().orElse(null);
            if (muteRole == null)
                throw new InvalidCommandStateException("You need a role called Cooldown to use this");
            toMute.getRoles().forEach(role -> guild.removeRoleFromMember(toMute, role).queue());
            guild.addRoleToMember(toMute, muteRole).queue();
            sendEmbedMessage("Added " + toMute.getAsMention() + " To Cooldown", event.getTextChannel(), false);
        } else
            throw new InvalidCommandArgumentException("Could not find User");
    }
}
