package net.ali.modbot.command.commands.misc;

import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@CommandInfo(name = "Developers", description = "Defines the developer role")
public class DevelopersCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        String onlineDevs = !getOnlineDevs(event.getGuild()).isEmpty() ? "\n*Online Devs include:*\n" + getOnlineDevs(event.getGuild()) : "";
        sendEmbedMessage("A developer is an individual that builds and create software and applications. " +
                "He or she writes, debugs and executes the source code of a software application. " + onlineDevs, event.getTextChannel(), false);
    }

    private String getOnlineDevs(Guild guild) {
        List<Member> members = guild.getMembers();
        StringBuilder builder = new StringBuilder();
        for (Member member : members) {
            if (!member.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                if (member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("Developer"))) {
                    builder.append("-").append(member.getUser().getName()).append("\n");
                }
        }
        return new String(builder);
    }
}
