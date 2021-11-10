package no.goodtech.vaadin.linkField;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.TextField;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

/**
 * A field that represents a link
 * In read-write mode, the field shows as a TextField, where you can edit the link
 * In read-only mode you can click an icon to navigate to the link
 * @see #setReadOnly(boolean)
 */
public class LinkField extends CustomField<String>
{
	private final String encoding;
	private Link link;
	private TextField textField = createTextField();

	public LinkField(String url, String encoding) {
		this.encoding = encoding;
		setUrl(url);
	}

	public LinkField(String url) {
		this(url, null);
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	@Override
	protected Component initContent() {
		VerticalLayout verticalLayout = new VerticalLayout(textField, link);
		verticalLayout.setWidth("100%");
		verticalLayout.setMargin(false);
		verticalLayout.setSpacing(false);
		return verticalLayout;
	}

	private TextField createTextField() {
		textField = new TextField();
		textField.setNullRepresentation("");
		textField.setImmediate(true);
		textField.setWidth("100%");
		return textField;
	}

	private Link createLink(String url) {
		Link link = new Link(null, new ExternalResource(encode(url)));
		link.setTargetName("_blank");
		link.setIcon(FontAwesome.EXTERNAL_LINK);
		if (url == null)
			link.setEnabled(false);
		return link;
	}

	private String encode(String url) {
		if (url == null)
			return "";
		if (encoding != null && !encoding.isEmpty()) {
			try {
				return UriUtils.encodeQuery(url, encoding); //Make sure æøå is converted
			} catch (UnsupportedEncodingException e) {
			}
		}
		return url;
	}

	@Override
	public void setValue(String url) throws ReadOnlyException, ConversionException {
		setUrl(url);
	}

	private void setUrl(String url) {
		textField.setValue(url);
		link = createLink(url);
	}

	@Override
	protected String getInternalValue() {
		return textField.getValue();
	}

	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		textField.setVisible(!readOnly);
		link.setVisible(readOnly);
	}

	public Link getLink() {
		return link;
	}
}
