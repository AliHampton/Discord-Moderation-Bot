package net.ali.modbot.managers;

import net.ali.modbot.exceptions.InvalidCommandException;
import net.ali.modbot.exceptions.InvalidCommandStateException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ali.modbot.Main;
import net.ali.modbot.command.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ali.modbot.exceptions.IllegalCommandAccessException;
import net.ali.modbot.exceptions.InvalidCommandArgumentException;

import javax.annotation.Nonnull;

public class MessageManager extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            CommandManager.execute(event);
        } catch (IllegalCommandAccessException | InvalidCommandArgumentException | InvalidCommandException | InvalidCommandStateException e) {
            Command.sendErrorMessage(e.getLocalizedMessage(), event.getTextChannel(), false);
        } catch (Exception e) {
            Main.INSTANCE.LOGGER.warn("UNHANDLED EXCEPTION :" + e.getMessage());
            System.out.print("-----------------------------------------------------\n");
            e.printStackTrace();
        }
    }
}
