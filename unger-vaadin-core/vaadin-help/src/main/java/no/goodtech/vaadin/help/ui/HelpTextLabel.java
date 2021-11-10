package no.goodtech.vaadin.help.ui;

import com.vaadin.ui.*;
import no.goodtech.vaadin.buttons.EditCancelSaveButton;
import no.goodtech.vaadin.buttons.EditCancelSaveButton.IEditCancelSaveListener;
import no.goodtech.vaadin.help.model.HelpText;
import no.goodtech.vaadin.help.model.HelpTextFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

/**
 * Viser en hjelpetekst
 * Om bruker har tilgang til å redigere, vises en knapperad som gir tilgang til redigering
 */
public class HelpTextLabel extends VerticalLayout {

	private static final int MAX_LENGTH = 8000;
	private final RichTextArea textArea;
	private final Label idLabel = new Label();
	private final HorizontalLayout horizontalLayout;
	private volatile HelpText helpText;

	/**
	 * Opprett komponent
	 */
	public HelpTextLabel() {
		setSizeFull();
		setMargin(false);
		addStyleName("helptext");

		// Create size label
		final Label sizeLabel = new Label();

		// Create text area
		textArea = new RichTextArea();
		textArea.setSizeFull();
		textArea.addValueChangeListener(event -> {
			final int length = textArea.getValue().length();
			sizeLabel.setValue(String.format(getText("numCharacters"), length, MAX_LENGTH));
		});


		EditCancelSaveButton editCancelSaveButton = new EditCancelSaveButton(new IEditCancelSaveListener() {
			public void saveClicked() {
				// Update text and save changes
				helpText.setText(textArea.getValue());
				helpText = helpText.save();
				textArea.setReadOnly(true);
			}

			public void editClicked() {
				textArea.setReadOnly(false);
			}

			public void cancelClicked() {
				refresh();
			}

			public boolean validate() {
				final int length = textArea.getValue().length();
				if (length > MAX_LENGTH) {
					Notification.show(getText("tooManyCharacters"));
					return false;
				} else {
					return true;
				}
			}
		});

		// Create horizontal layout for the 'sizeLabel' and 'editCancelSaveButton'
		horizontalLayout = new HorizontalLayout(editCancelSaveButton, idLabel, sizeLabel);
		horizontalLayout.setMargin(false);
		horizontalLayout.setComponentAlignment(idLabel, Alignment.BOTTOM_RIGHT);
		horizontalLayout.setComponentAlignment(sizeLabel, Alignment.BOTTOM_RIGHT);

		addComponents(textArea, horizontalLayout);
		setExpandRatio(textArea, 1.0f);
	}
	
	public void refresh(final String helpTextId, boolean isEditable) {
		idLabel.setValue("ID: " + helpTextId);
		this.helpText = getHelpText(helpTextId);
		horizontalLayout.setVisible(isEditable);
		refresh();
	}

	private void refresh() {
		textArea.setReadOnly(false);
		if ((helpText != null) && (helpText.getText() != null) && (helpText.getText().trim().length() > 0)) {
			textArea.setValue(helpText.getText());
		} else {
			textArea.clear();
		}
		textArea.setReadOnly(true);
	}

	private HelpText getHelpText(final String helpTextId) {
		HelpText helpText = new HelpTextFinder().setId(helpTextId).find();
		if (helpText == null) {
			helpText = new HelpText(helpTextId);
		}
		return helpText;
	}

	private static String getText(final String key) {
		return ApplicationResourceBundle.getInstance("vaadin-help").getString(key);
	}

}
