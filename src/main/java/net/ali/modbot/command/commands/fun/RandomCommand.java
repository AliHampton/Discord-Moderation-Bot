package net.ali.modbot.command.commands.fun;

import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@CommandInfo(name = "Random", description = "Tags a random member with your message", type = CommandType.Fun)
public class RandomCommand extends Command {

    private Random random = new Random();

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0)
            throw new InvalidCommandArgumentException("Usage: `.Random <Message>*`");
        String message = String.join(" ", args);
        Guild guild = event.getGuild();
        List<Member> members = guild.getMembers().stream().filter(member -> !member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).filter(member -> !member.equals(event.getMember())).filter(member -> !member.getUser().isBot()).collect(Collectors.toList());
        if (members.isEmpty())
            throw new InvalidCommandStateException("Search yielded no results");
        Member randomMember = members.get(random.nextInt(members.size()));
        if (random == null)
            throw new InvalidCommandStateException("Search yielded no results");
        sendEmbedMessage(randomMember.getAsMention() + " " + message, event.getTextChannel(), false);
    }
}
