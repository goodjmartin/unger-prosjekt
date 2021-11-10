package no.goodtech.vaadin.main;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.SizeWithUnit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import no.goodtech.vaadin.buttons.CancelButton;
import no.goodtech.vaadin.buttons.OkButton;

/**
 * A generic popup window for editing operatations.
 * The window contains ok + cancel buttons and a content area you can fill with whatever you like.
 * If the content has a caption, it will be moved to the window 
 */
public class SimpleInputBox extends Window {

	private static final String BOX_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("confirmBox.caption");
	private final OkButton okButton;
	private final Component content;
	private boolean isReadOnly;
	private HorizontalLayout buttonLayout;


	public interface IinputBoxContent {
		/**
		 * @return the content of the window
		 */
		Component getComponent();

		/**
		 * Use this to control height of your component if you want to use expand feature.
		 * Implement it and set your height as either pixels or percentage
		 *
		 * @return overridden component height or this default( setHeightUndefined)
		 */
		default SizeWithUnit getComponentHeight() {
			return new SizeWithUnit(-1, Unit.PIXELS);
		}

		default SizeWithUnit getComponentWidth() {
			return new SizeWithUnit(-1, Unit.PIXELS);
		}
		/**
		 * Will be called when user clicks OK.
		 * Use this to validate and save the data in the form
		 * @return false if you want to to keep the window open when the operator clicks OK
		 */
		boolean commit();
	}

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
	public SimpleInputBox(final IinputBoxContent content) {
		this(content, null);
	}


	/**
	 * Use this to create the popup
	 *
	 * @param content               the panel you like to show in the popup
	 * @param confirmWindowListener you will get a message from this if the operator clicks the OK button
	 */
	public SimpleInputBox(final IinputBoxContent content, final IConfirmListener confirmWindowListener) {
		this(content, null, null, stealCaption(content), confirmWindowListener);
	}
	
	private static String stealCaption(final IinputBoxContent content) {
		String caption = content.getComponent().getCaption();
		if (caption != null) {
			content.getComponent().setCaption(null);
			return caption;
		}
		else 
			return BOX_CAPTION;
	}

	/**
	 * Use this to create the popup if you want to control button captions
	 */
	public SimpleInputBox(final IinputBoxContent content, final String okCaption, final String cancelCaption,
						  final IConfirmListener confirmListener) {
		this(content, okCaption, cancelCaption, stealCaption(content), confirmListener);
	}
	
	/**
	 * Use this to create the popup if you are a control freak
	 */
	public SimpleInputBox(final IinputBoxContent content, final String okCaption, final String cancelCaption,
						  final String caption, final IConfirmListener confirmListener) {
		super(caption);
		this.content = content.getComponent();
		setModal(true);
		setWidthUndefined();

		okButton = new OkButton(new OkButton.IOkListener() {
			@Override
			public void okClicked() {
				if (!(content instanceof IinputBoxContent) || ((IinputBoxContent) content).commit()) {
					getUI().removeWindow(SimpleInputBox.this);
					if (confirmListener != null) {
						UI.getCurrent().access(new Runnable() {
							@Override
							public void run() {
								confirmListener.onConfirm();
							}
						});
					}
				}
			}
		});

		CancelButton cancelButton = createCancelButton(cancelCaption);

		okButton.setClickShortcut(KeyCode.ENTER);
		if (okCaption != null) {
			okButton.setCaption(okCaption);
		}

		HorizontalLayout buttonBar = new HorizontalLayout(okButton, cancelButton);
		buttonBar.setMargin(false);
		buttonBar.setComponentAlignment(okButton, Alignment.MIDDLE_CENTER);
		buttonBar.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);

		VerticalLayout verticalLayout = new VerticalLayout(this.content, buttonBar);
		verticalLayout.setExpandRatio(this.content, 1.0f);

		if (content.getComponentHeight().getSize() != -1) {
			verticalLayout.setHeight(100, Unit.PERCENTAGE);
		}

		setContent(verticalLayout);
		if (content instanceof Focusable) {
			((Focusable) content).focus();
		}

		this.setHeight(content.getComponentHeight().getSize(), content.getComponentHeight().getUnit());
	}

	private CancelButton createCancelButton(String caption) {
		CancelButton button = new CancelButton(new CancelButton.ICancelListener() {
			public void cancelClicked() {
				getUI().removeWindow(SimpleInputBox.this);
			}
		});

		button.setClickShortcut(KeyCode.ESCAPE);

		if (caption != null) {
			button.setCaption(caption);
		}

		return button;
	}

	public HorizontalLayout getButtonLayout() {
		return buttonLayout;
	}

	public void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
		okButton.setEnabled(!readOnly);
		//TODO vaadin8 content.setReadOnly(readOnly);
	}



}