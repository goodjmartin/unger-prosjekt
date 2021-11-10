package no.goodtech.vaadin.remotecontrol.gui;
/**
 * Created by rsan on 06.08.13.
 */

//import com.vaadin.addon.touchkit.settings.TouchKitSettings;
//import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;
import org.vaadin.touchkit.settings.TouchKitSettings;
import org.vaadin.touchkit.ui.NavigationManager;

@Theme("mobile")
@Title("Fjernstyring")
public class TouchKitMain extends UI {
	@Override
	protected void init(VaadinRequest request) {
		TouchKitSettings touchKitSettings = new TouchKitSettings(VaadinService.getCurrent());
		touchKitSettings.getApplicationIcons().addApplicationIcon("/images/customerLogo.gif");
		final NavigationManager manager = new NavigationManager(new IndexPage());
		setSizeFull();
		setContent(manager);
	}
}