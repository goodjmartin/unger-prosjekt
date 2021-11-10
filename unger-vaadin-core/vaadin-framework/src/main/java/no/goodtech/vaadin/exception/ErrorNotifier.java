package no.goodtech.vaadin.exception;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextArea;
import no.cronus.common.utils.StackTraceUtils;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.IErrorNotifier;

import java.util.Date;

public class ErrorNotifier implements IErrorNotifier {

	public static final String ERROR_ID = "errorWindow";

	public void showUnhandledExceptions(Throwable exception) {
		// Create popup window
		Window window = new Window(getText("caption"));
		window.setWidth(60, Unit.PERCENTAGE);
		window.setHeight(60, Unit.PERCENTAGE);
		window.setModal(true);
		window.setId(ERROR_ID);

		// Set window content
		final Image creativeUser = new Image(null, new ThemeResource("images/errorMessage.jpg"));
		
		final Label message = new Label(formatMessage(exception), ContentMode.HTML);
		message.setSizeFull();
		message.setContentMode(ContentMode.HTML);
		HorizontalLayout horizontalLayout = new HorizontalLayout(message, creativeUser);
		horizontalLayout.setWidth("100%");
		horizontalLayout.setSpacing(true);
		horizontalLayout.setExpandRatio(message, 1);
		
		final TextArea stacktrace = createTextArea(new Date() + "\n\n" + StackTraceUtils.getStackTrace(exception));
		final VerticalLayout content = new VerticalLayout(horizontalLayout, stacktrace);
		content.setSizeFull();
		content.setExpandRatio(stacktrace, 1);
		window.setContent(content);

		UI.getCurrent().addWindow(window); // Show window
	}
	
	private TextArea createTextArea(String content) {
		final TextArea textArea = new TextArea(null, content);
		textArea.setSizeFull();
		textArea.selectAll();
		textArea.setReadOnly(true);
		return textArea;
	}

	private String formatMessage(Throwable exception) {
		StringBuilder formattedMessage = new StringBuilder();

        // Check if an error message is provided
        String message = exception.getMessage();

        if (message == null) {
            // Check if an error cause is provided
            Throwable cause = exception.getCause();
            if ((cause != null) && (cause.getMessage() != null)) {
                message = cause.getMessage();
            }
        }

        formattedMessage.append("<h2>" + getText("title") + "</h2>");
        
        if (message != null) {
            formattedMessage.append("<p><b>").append(getText("message")).append(":</b> ").append(message).append("</p>");
        }

        // Add the time of the exception
//		formattedMessage.append("<b>").append(getText("time")).append(":</b> ").append(new Date());

		// Add the please copy stack trace message
        formattedMessage.append("<p><b>").append(getText("stackTrace")).append(":</p>");
        
        return formattedMessage.toString();
	}
	
	private String getText(String key) {
        return ApplicationResourceBundle.getInstance("vaadin-core").getString("main.terminalError." + key);
	}
	
	public void hideUnhandledExceptions() {
		// TODO Auto-generated method stub
	}
}
