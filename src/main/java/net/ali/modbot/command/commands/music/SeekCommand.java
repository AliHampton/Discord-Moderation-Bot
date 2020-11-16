package net.ali.modbot.command.commands.music;

import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@CommandInfo(name = "Seek", description = "Sets position of the current track", type = CommandType.Music, role = "DJ")
public class SeekCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length < 1)
            throw new InvalidCommandArgumentException("Usage: `.Seek <time:stamp> \n .Seek <(+/-) amount (s/m/h)>`");
        String arg = args[0];
        long time;
        if (arg.matches("(\\d?\\d)[:]([012345]?\\d)")) {
            String minutes = arg.split(":")[0];
            String seconds = arg.split(":")[1];
            time = Long.parseLong(minutes) * 60000 + Long.parseLong(seconds) * 1000;
        } else if (arg.matches("(\\d?\\d*)[:]([012345]?\\d)[:]([012345]?\\d)")) {
            String hours = arg.split(":")[0];
            String minutes = arg.split(":")[1];
            String seconds = arg.split(":")[2];
            time = Long.parseLong(hours) * 3600000 + Long.parseLong(minutes) * 60000 + Long.parseLong(seconds) * 1000;
        } else if (arg.matches("[?:+|-]?\\d*([.]\\d*)?[smh]")) {
            char format = arg.charAt(arg.length() - 1);
            double amount = Double.parseDouble(arg.substring((arg.startsWith("-") || arg.startsWith("+")) ? 1 : 0, arg.length() - 1));
            if (arg.startsWith("-"))
                amount *= -1;
            long position = Main.INSTANCE.musicUtils.getAudioPlayer(event.getGuild()).getPlayingTrack().getPosition();
            if (format == 's')
                time = position + (int) (amount * 1000);
            else if (format == 'm')
                time = position + (int) (amount * 60000);
            else if (format == 'h')
                time = position + (int) (amount * 3600000);
            else
                time = position;
        } else {
            throw new InvalidCommandArgumentException("Usage: `.Seek <time:stamp> \n .Seek <(+/-) amount (s/m/h)>`");
        }
        sendMessage(":mag: Seeked: `" + arg + "`", event.getTextChannel());
        Main.INSTANCE.musicUtils.seek(event.getGuild(), time);
    }
}
