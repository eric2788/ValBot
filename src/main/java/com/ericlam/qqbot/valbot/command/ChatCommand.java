package com.ericlam.qqbot.valbot.command;

import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatCommand {

    String name();

    String[] alias() default {};

    String description();

    String[] placeholders() default {};

    Class<? extends GroupCommand>[] subCommands() default {};


}
