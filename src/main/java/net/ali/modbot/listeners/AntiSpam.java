package net.ali.modbot.listeners;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import net.ali.modbot.Main;
import net.ali.modbot.command.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AntiSpam extends ListenerAdapter {

    private final Map<User, Integer> warned = new HashMap<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        List<Message> history = event.getChannel().getIterableHistory().complete().stream().limit(10).filter(msg -> !msg.equals(event.getMessage())).collect(Collectors.toList());
        int spam = (int) history.stream().filter(message -> message.getAuthor().equals(event.getAuthor()) && !message.getAuthor().isBot()).filter(msg -> (event.getMessage().getTimeCreated().toEpochSecond() - msg.getTimeCreated().toEpochSecond()) < 6).count();
        if (spam > 2 && event.getMember() != null && event.getGuild().getOwner() != null && !event.getGuild().getOwner().equals(event.getMember()) && !PermissionUtil.checkPermission(event.getMember(), Permission.ADMINISTRATOR)) {
            int timesWarned = 0;
            if (warned.containsKey(event.getAuthor()))
                timesWarned = warned.get(event.getAuthor());

            warned.put(event.getAuthor(), timesWarned + 1);
            if (timesWarned >= 5)
                coolDownUser(event.getChannel(), event.getAuthor());
            else
                Command.sendErrorMessage("Please leave 2 seconds between messages, " + event.getAuthor().getAsMention(), event.getChannel(), true);
            event.getMessage().delete().queue();
        }
    }

    private void coolDownUser(TextChannel channel, User user) {
        Guild guild = channel.getGuild();
        Role muteRole = guild.getRoles().stream().filter(role -> role.getName().equalsIgnoreCase("Cooldown")).findFirst().orElse(null);
        Member member = guild.getMember(user);
        if (member != null && muteRole != null && !PermissionUtil.checkPermission(channel, member, Permission.ADMINISTRATOR) && member.getRoles().stream().noneMatch(role -> role.getName().equalsIgnoreCase("Cooldown"))) {
            member.getRoles().forEach(role -> guild.removeRoleFromMember(member, role).queue());
            guild.addRoleToMember(member, muteRole).queue(success -> channel.sendMessage(Main.INSTANCE.msgUtil.wrapMessage("Added " + user.getAsMention() + " To Cooldown")).queue(callback -> warned.put(user, 0)));
        }
    }

}
