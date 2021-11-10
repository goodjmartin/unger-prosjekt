package no.goodtech.vaadin.ui;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import no.goodtech.vaadin.buttons.RefreshButton;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.logs.LogInfo;
import no.goodtech.vaadin.logs.LogSearcherService;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static no.cronus.common.utils.CollectionFactory.newArrayList;


@UIScope
@SpringView(name = LoggerControlView.VIEW_ID)
public class LoggerControlView extends VerticalLayout implements IMenuView {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private LogSearcherService logSearcher;

	public static final String VIEW_ID = "LoggerControlView";
	public static final String VIEW_NAME = Texts.get("loggerControlView.viewName");
	private static final String ACCESS_EDIT = "loggerControlView.edit";

	private DateTimeField logStartDate;
	private DateTimeField logEndDate;
	private Label numLinesShowingLabel;
	private TextArea lastLogs;

	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_EDIT, ApplicationResourceBundle.getInstance("vaadin-core").getString("accessFunction." + ACCESS_EDIT)));
	}

	public LoggerControlView() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);

		TextField loggerName = new TextField("Logger");
		ComboBox levels = new ComboBox("Level", newArrayList(Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE));
		levels.setValue(Level.DEBUG);
		levels.setEmptySelectionAllowed(false);

		SaveButton saveButton = new SaveButton(() -> {
			if (!loggerName.isEmpty()) {
				String loggerNameValue = loggerName.getValue();
				LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				if (loggerContext.exists(loggerNameValue) != null) {
					loggerContext.getLogger(loggerNameValue).setLevel((Level) levels.getValue());
					Notification.show(loggerNameValue + " ble oppdatert til " + levels.getValue());
				} else {
					Notification.show("Fant ikke logger...", Notification.Type.WARNING_MESSAGE);
				}
			}
		});
		HorizontalLayout loggerLevelLayout = new HorizontalLayout(loggerName, levels, saveButton);
		loggerLevelLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_CENTER);

		lastLogs = new TextArea();
		lastLogs.setSizeFull();
		lastLogs.setWidth("100%");
		lastLogs.setReadOnly(true);


		logStartDate = new DateTimeField("Start");
		logEndDate = new DateTimeField("Slutt");
		numLinesShowingLabel = new Label("");
		RefreshButton refreshButton = new RefreshButton(this::refreshLogs);
		HorizontalLayout filter = new HorizontalLayout(logStartDate, logEndDate, refreshButton, numLinesShowingLabel);
		filter.setComponentAlignment(refreshButton, Alignment.BOTTOM_CENTER);
		filter.setComponentAlignment(numLinesShowingLabel, Alignment.BOTTOM_CENTER);

		addComponents(new Label("<h3>Endre systemlogging</h3>", ContentMode.HTML), new Label("Dette panelet skal kun brukes av autorisert personal (dvs. Goodtech)"), loggerLevelLayout,
				new Label("</hr>", ContentMode.HTML),
				new Label("<h3>Søk i logg</h3>", ContentMode.HTML), filter, lastLogs,
				new Label("</hr>", ContentMode.HTML));
		setExpandRatio(lastLogs, 1.0f);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}

	private void refreshLogs() {
		LogInfo result = logSearcher.fetchLogging(logStartDate.getValue(), logEndDate.getValue());
		numLinesShowingLabel.setValue("Viser " + String.valueOf(result.getFoundLines()) + " linjer av " + String.valueOf(result.getTotalLines()));
		lastLogs.setValue(result.getLog());
	}

	@Override
	public boolean isAuthorized(User user, String argument) {
		return AccessFunctionManager.isAuthorized(user, ACCESS_EDIT);
	}


	@Override
	public String getViewName() {
		return VIEW_NAME;
	}
}
