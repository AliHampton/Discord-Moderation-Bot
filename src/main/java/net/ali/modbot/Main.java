package net.ali.modbot;

import net.ali.modbot.listeners.AntiSpam;
import net.ali.modbot.listeners.ModEventsListener;
import net.ali.modbot.managers.CommandManager;
import net.ali.modbot.managers.ConfigManager;
import net.ali.modbot.managers.MessageManager;
import net.ali.modbot.managers.ScheduleManager;
import net.ali.modbot.utils.MessageUtils;
import net.ali.modbot.utils.MusicUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public enum Main {
    INSTANCE;

    public JDA jdaBot;
    public final String cmdPrefix = ".";
    public final MessageUtils msgUtil = new MessageUtils();
    public final MusicUtils musicUtils = new MusicUtils();
    public final CommandManager cmdManager = new CommandManager();
    public final ConfigManager configManager = new ConfigManager();
    public final ScheduleManager scheduleManager = new ScheduleManager();

    public final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LoginException {
        String token = INSTANCE.configManager.getToken(args[0]);
        JDABuilder builder = JDABuilder.create(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES)
                .setStatus(OnlineStatus.ONLINE)
                .setAutoReconnect(true)
                .setActivity(Activity.streaming("Porn", "https://www.twitch.tv/ninja"))
                .addEventListeners(new MessageManager(), new AntiSpam(), new ModEventsListener());

        INSTANCE.jdaBot = builder.build();
        INSTANCE.cmdManager.initCommands();
        INSTANCE.musicUtils.setup();
        INSTANCE.configManager.readData(args[1]);
        INSTANCE.LOGGER.info("Bot Initialised");

        Runtime.getRuntime().addShutdownHook(new Thread(INSTANCE::onShutdown));
    }

    public void onShutdown() {
        configManager.saveData();
        INSTANCE.LOGGER.info("Shutting Down...");
    }

}