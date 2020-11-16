package net.ali.modbot.command;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();

    String description();

    CommandType type() default CommandType.Misc;

    String role() default "@everyone";

    Permission permission() default Permission.UNKNOWN;

    String[] aliases() default "";

}
