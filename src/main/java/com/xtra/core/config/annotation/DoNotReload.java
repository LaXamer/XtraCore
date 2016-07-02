package com.xtra.core.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spongepowered.api.event.game.GameReloadEvent;

/**
 * Signifies that this configuration file should NOT be reloaded during the
 * {@link GameReloadEvent}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoNotReload {
}
