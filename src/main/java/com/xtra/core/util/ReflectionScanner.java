package com.xtra.core.util;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.xtra.core.command.CommandBase;
import com.xtra.core.command.RegisterCommand;

public class ReflectionScanner {
    
    /**
     * Uses reflection to get the commands of the plugin.
     * 
     * @param plugin The plugin
     * @return A set of the commands
     */
    public static Set<CommandBase<?>> getCommands(Object plugin) {
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName(), new SubTypesScanner(false), new TypeAnnotationsScanner());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RegisterCommand.class);
        Set<CommandBase<?>> commands = new HashSet<>();
        for (Class<?> oneClass : classes) {
            try {
                Object o = oneClass.newInstance();
                if (o instanceof CommandBase<?>) {
                    commands.add((CommandBase<?>) o);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return commands;
    }
}
