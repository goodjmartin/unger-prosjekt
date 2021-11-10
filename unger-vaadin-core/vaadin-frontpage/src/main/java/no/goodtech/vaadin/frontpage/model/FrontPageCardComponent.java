package no.goodtech.vaadin.frontpage.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate any UI component with this to make it searchable for the user specific front page.
 * The UI must be of type IFrontPageCard.
 * @see IFrontPageCardComponent
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FrontPageCardComponent {
}
