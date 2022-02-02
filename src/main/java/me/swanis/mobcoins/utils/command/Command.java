package me.swanis.mobcoins.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String command();
    String permission() default "";
    boolean subCommand() default false;
    String[] subCommands() default {};
    String baseCommand() default "";
    String[] aliases() default {};
}
