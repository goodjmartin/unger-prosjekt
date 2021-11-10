package no.goodtech.vaadin.main;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import no.goodtech.vaadin.buttons.CancelButton;
import no.goodtech.vaadin.buttons.OkButton;

public class ConfirmWindow extends Window {

	private static final String BOX_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("confirmBox.caption");

	private OkButton okButton;
	private Label message;

	public ConfirmWindow(final String text, final IConfirmWindowListener confirmWindowListener) {
		init(text, confirmWindowListener);
	}

	public ConfirmWindow(final String text, final String okButtonText, final IConfirmWindowListener confirmWindowListener) {
		init(text, confirmWindowListener);
		okButton.setCaption(okButtonText);
	}

	public void init(final String text, final IConfirmWindowListener confirmWindowListener) {
		setModal(true);
		setClosable(false);
		setResizable(false);
		setSizeUndefined();

		generateShortcuts(confirmWindowListener);

        // Create Accept button
		okButton = new OkButton(() -> {
			getUI().removeWindow(ConfirmWindow.this);
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					confirmWindowListener.onAccept();
				}
			});
		});

        // Create Cancel button
        CancelButton cancelButton = new CancelButton(new CancelButton.ICancelListener() {
			@Override
			public void cancelClicked() {
				getUI().removeWindow(ConfirmWindow.this);
				UI.getCurrent().access(new Runnable() {
					@Override
					public void run() {
						confirmWindowListener.onCancel();
					}
				});
			}
		});

		// Create horizontal layout
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setMargin(false);
		horizontalLayout.addComponents(okButton, cancelButton);
		horizontalLayout.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER);
		horizontalLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);

		// Create message label
		message = new Label(text);

		// Create spacer label
		Label spacer = new Label();
		spacer.setHeight(15, Unit.PIXELS);

		// Create vertical layout
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponents(message, spacer, horizontalLayout);

		setContent(verticalLayout);
		setCaption(BOX_CAPTION);
	}

	public interface IConfirmWindowListener {
		public void onAccept();
		public void onCancel();
	}

	/**
	 * Generates the shorcutlisteners for the window
	 * @param confirmWindowListener copies the functions for onAccept and onCancel
	 */
	private void generateShortcuts(final IConfirmWindowListener confirmWindowListener) {
		addShortcutListener(new ShortcutListener("Accept listener", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target instanceof ConfirmWindow) {
					getUI().removeWindow(ConfirmWindow.this);
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
							confirmWindowListener.onAccept();
						}
					});
				}
			}
		});

		addShortcutListener(new ShortcutListener("Cancel listener", ShortcutAction.KeyCode.ESCAPE, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target instanceof ConfirmWindow) {
					getUI().removeWindow(ConfirmWindow.this);
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
							confirmWindowListener.onCancel();
						}
					});
				}
			}
		});
	}

	public void setMessageIcon(Resource icon){
		this.message.setIcon(icon);
	}

	public void setMessage(String message){
		this.message.setValue(message);
	}

	public void setMessageStyle(String style){
		message.setStyleName(style);
	}

	public void setOkButtonCaption(String caption){
		okButton.setCaption(caption);
	}
}
