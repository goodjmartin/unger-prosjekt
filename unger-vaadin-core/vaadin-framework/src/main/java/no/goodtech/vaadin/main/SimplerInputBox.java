package no.goodtech.vaadin.main;

import com.vaadin.data.Binder;
import com.vaadin.data.StatusChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.vaadin.buttons.CancelButton;
import no.goodtech.vaadin.buttons.OkButton;
import no.goodtech.vaadin.ui.Texts;

/**
 * A generic popup window for editing operatations.
 * The window contains ok + cancel buttons and a content area you can fill with whatever you like.
 * If the content has a caption, it will be moved to the window 
 */
public class SimplerInputBox<ENTITY extends Entity> extends Window {

	private static final String BOX_CAPTION = Texts.get("confirmBox.caption");

	protected final Binder<ENTITY> binder;

	public interface IConfirmListener {

		/**
		 * Use this on if you want to get noticed when the operator clicks OK and the window closes
		 */
		void onConfirm();
	}

	/**
	 * Use this to create the popup if you don't need to know if the user confirmed
	 * @param content the panel you like to show in the popup
	 */
	public SimplerInputBox(final SimplerInputComponent<ENTITY> content) {
		this(content, null);
	}


	/**
	 * Use this to create the popup
	 *
	 * @param content               the panel you like to show in the popup
	 * @param confirmWindowListener you will get a message from this if the operator clicks the OK button
	 */
	public SimplerInputBox(final SimplerInputComponent<ENTITY> content, final IConfirmListener confirmWindowListener) {
		this(content, null, null, stealCaption(content), confirmWindowListener);
	}

	private static String stealCaption(final SimplerInputComponent content) {
		String caption = content.getCaption();
		if (caption != null) {
			content.setCaption(null);
			return caption;
		}

		return BOX_CAPTION;
	}

	/**
	 * Use this to create the popup if you want to control button captions
	 */
	public SimplerInputBox(final SimplerInputComponent<ENTITY> content, final String okCaption, final String cancelCaption,
						   final IConfirmListener confirmListener) {
		this(content, okCaption, cancelCaption, stealCaption(content), confirmListener);
	}

	/**
	 * Use this to create the popup if you are a control freak
	 */
	private SimplerInputBox(final SimplerInputComponent<ENTITY> content, final String okCaption, final String cancelCaption,
							final String caption, final IConfirmListener confirmListener) {
		super(caption);
		this.binder = content.getBinder();

		setModal(true);
		setWidthUndefined();

		// Create OK button
		OkButton okButton = new OkButton(() -> {
			if (content.save()) {
				getUI().removeWindow(SimplerInputBox.this);
				if (confirmListener != null) {
					UI.getCurrent().access(confirmListener::onConfirm);
				}
			}
		});

		// Set ENTER as short-cut listener
		okButton.setClickShortcut(KeyCode.ENTER);
		if (okCaption != null) {
			okButton.setCaption(okCaption);
		}

		// Set initial state of OK button
		okButton.setEnabled(!content.isReadOnly() && content.getBinder().isValid());

		// Change state of OK button base on user input (validity)
		binder.addStatusChangeListener((StatusChangeListener) statusChangeEvent -> okButton.setEnabled(!content.isReadOnly() && statusChangeEvent.getBinder().isValid()));

		// Create Cancel button
		CancelButton cancelButton = createCancelButton(cancelCaption);

		// Create button layout
		HorizontalLayout buttonLayout = new HorizontalLayout(okButton, cancelButton);
		buttonLayout.setMargin(false);
		buttonLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);

		// Create layout for all components
		VerticalLayout verticalLayout = new VerticalLayout(content, buttonLayout);
		verticalLayout.setExpandRatio(content, 1.0f);
		setContent(verticalLayout);

		// Set focus
		if (content instanceof Focusable) {
			((Focusable) content).focus();
		}

		// Set height and width
		this.setHeight(content.getComponentHeight().getSize(), content.getComponentHeight().getUnit());
		if (content.getComponentHeight().getSize() != -1) {
			verticalLayout.setHeight(100, Unit.PERCENTAGE);
		}
	}

	private CancelButton createCancelButton(String caption) {
		CancelButton button = new CancelButton(() -> getUI().removeWindow(SimplerInputBox.this));

		// Set ESCAPE as short-cut listener
		button.setClickShortcut(KeyCode.ESCAPE);

		if (caption != null) {
			button.setCaption(caption);
		}

		return button;
	}

}