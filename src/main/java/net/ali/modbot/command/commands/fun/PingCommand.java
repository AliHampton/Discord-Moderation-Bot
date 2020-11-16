package net.ali.modbot.command.commands.fun;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.ali.modbot.command.CommandInfo;
import net.ali.modbot.command.CommandType;

@CommandInfo(name = "Ping", description = "Pong", type = CommandType.Fun)
public class PingCommand extends Command {

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        long time = System.currentTimeMillis();
        event.getChannel().sendMessage(Main.INSTANCE.msgUtil.wrapMessage("Pong: ")).queue(sent ->
                sent.editMessage(Main.INSTANCE.msgUtil.wrapMessage("Pong:  `" + (System.currentTimeMillis() - time) + "ms`")).queue());
    }
}
