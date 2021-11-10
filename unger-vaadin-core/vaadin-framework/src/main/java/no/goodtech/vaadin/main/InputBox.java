package no.goodtech.vaadin.main;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import no.goodtech.vaadin.buttons.CancelButton;
import no.goodtech.vaadin.buttons.OkButton;

/**
 * A generic popup window for getting a reponse from the operator.
 * The window contains ok + cancel buttons and a content area you can fill with whatever you like.  
 * @param <RESPONSE> the response you like to collect from the operator
 */
public class InputBox<RESPONSE> extends Window {

	private static final String BOX_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("confirmBox.caption");

	public interface ResponsiveContent<RESPONSE> {
		Component getComponent();
		RESPONSE getResponse();
	}
	
	public interface ValidatableContent<RESPONSE> extends ResponsiveContent<RESPONSE> {
		/**
		 * Use this to validate the content
		 * @return false if you wanto to keep the window open when the operator clicks OK
		 */
		boolean isItOkToClose();
	}

	public interface IConfirmListener<RESPONSE> {
		void onConfirm(RESPONSE response);
	}
	
	/**
	 * Use this to create the popup
	 * @param content the panel you like to show in the popup
	 * @param confirmWindowListener you will get a message from this if the operator clicks the OK button
	 */
	public InputBox(final ResponsiveContent<RESPONSE> content, final IConfirmListener<RESPONSE> confirmWindowListener) {
		this(content, null, null, BOX_CAPTION, confirmWindowListener);
	}

	/**
	 * Use this to create the popup if you are a control freak
	 */
	public InputBox(final ResponsiveContent<RESPONSE> content, final String okCaption, final String cancelCaption,
			final String caption, final IConfirmListener<RESPONSE> confirmListener) {
		super(caption);
		setModal(true);
		setClosable(false);
		setResizable(false);
		setSizeUndefined();

		OkButton okButton = new OkButton(new OkButton.IOkListener() {
			@Override
			public void okClicked() {
				if (!(content instanceof ValidatableContent) || ((ValidatableContent<?>) content).isItOkToClose()) { 
					getUI().removeWindow(InputBox.this);
					UI.getCurrent().access(new Runnable() {
						@Override
						public void run() {
							confirmListener.onConfirm(content.getResponse());
						}
					});
				}
			}
		});

		CancelButton cancelButton = createCancelButton(cancelCaption);

		okButton.setClickShortcut(KeyCode.ENTER);
		if (okCaption != null)
			okButton.setCaption(okCaption);

		HorizontalLayout buttonBar = new HorizontalLayout(okButton,	cancelButton);
		buttonBar.setMargin(false);
		buttonBar.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER);
		buttonBar.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);

		VerticalLayout verticalLayout = new VerticalLayout(content.getComponent(), buttonBar);
		setContent(verticalLayout);
	}

	private CancelButton createCancelButton(String caption) {
		CancelButton button = new CancelButton(new CancelButton.ICancelListener() {
					public void cancelClicked() {
						getUI().removeWindow(InputBox.this);
					}
				});
		
		button.setClickShortcut(KeyCode.ESCAPE);
		
		if (caption != null)
			button.setCaption(caption);
		
		return button;
	}
}