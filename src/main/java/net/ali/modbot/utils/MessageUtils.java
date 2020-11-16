package net.ali.modbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class MessageUtils {

    public static final String Author = "Ali";
    public static final String Author_Image = "https://cdn.discordapp.com/avatars/348464305164910605/c4073c3ec6712be751122e7f5693b3e2.png";

    private static float hue = 0f;

    public MessageEmbed wrapMessage(String msg) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.getHSBColor(hue, 1f, 1f));
        hue += 0.12;
        if (hue > 1)
            hue = 0;
        eb.setDescription(msg);
        eb.setFooter("Created by " + Author, Author_Image);
        return eb.build();
    }

    public MessageEmbed wrapErrorMessage(String msg) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":warning:Error:warning:");
        eb.setColor(Color.RED);
        eb.setDescription(msg);
        eb.setFooter("Created by " + Author, Author_Image);
        return eb.build();
    }

    public EmbedBuilder getDefaultBuilder() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.getHSBColor(hue, 1f, 1f));
        hue += 0.12;
        if (hue > 1)
            hue = 0;
        eb.setFooter("Created by " + Author, Author_Image);
        return eb;
    }

}
