package no.goodtech.vaadin.help.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class HelpText extends AbstractEntityImpl<HelpText> {

	@NotNull
	@Length(max = 255)
	private String id;

	@Length(max = 8000)
	private String text;

	public HelpText() {
		this(null);
	}

	public HelpText(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
