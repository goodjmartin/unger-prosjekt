package no.goodtech.vaadin.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for generic entities made for "plug-ins" (etc. comments and attachments)
 * @see no.goodtech.vaadin.utils.IEntityTabPanel
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityTabPanel {
	String id();
}