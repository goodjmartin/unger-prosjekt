package no.goodtech.vaadin.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to a bean to state which resource bundle contains the captions for the bean.
 * Used in Equipment among others.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bundle {
	String resourceBundle() default "vaadin-core";
}
