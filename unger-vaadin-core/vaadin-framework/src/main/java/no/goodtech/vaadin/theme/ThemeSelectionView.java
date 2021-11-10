package no.goodtech.vaadin.theme;


import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import no.goodtech.vaadin.global.Globals;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.IMenuView;

import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "WeakerAccess"})
@UIScope
@SpringView(name = ThemeSelectionView.VIEW_ID)
public class ThemeSelectionView extends VerticalLayout implements IMenuView {
	public static final String VIEW_ID = "ThemeView";
	public static final String VIEW_NAME = ApplicationResourceBundle.getInstance("vaadin-core").getString("themeView.caption");

	public ThemeSelectionView() {
		ComboBox themeComboBox = new ComboBox(getText("themeView.themeSelectionComboBox.caption"));
		themeComboBox.setNullSelectionAllowed(false);
		themeComboBox.setImmediate(true);

		for (String theme : Globals.getAvailableThemes()) {
			Pattern pattern = Pattern.compile("\\[(.*?)\\]");
			Matcher matcher = pattern.matcher(theme);

			//Find themes enclosed like this [name=admin:bundle=vaadin-core]
			while (matcher.find()) {
				String[] themeNameAndBundle = matcher.group(1).split(":");
				String themeName = themeNameAndBundle[0];
				String resourceBundle = themeNameAndBundle[1];
				//Get the bundle for looking up the display name of the theme
				String specificBundle = resourceBundle.split("=")[1];
				//Get the name name, feks admin
				String specificTheme = themeName.split("=")[1];
				themeComboBox.addItem(specificTheme);
				//Set caption of the theme in the comboBox, if the theme is named "admin" it will look for a property in the provided resource bundle named "themeCaption.admin"
				try {
					themeComboBox.setItemCaption(specificTheme, ApplicationResourceBundle.getInstance(specificBundle).getString("themeCaption." + specificTheme));
				} catch (MissingResourceException e) {
					themeComboBox.setItemCaption(specificTheme, specificTheme);
				}
			}
		}

		themeComboBox.addValueChangeListener(
				(Property.ValueChangeListener) event -> {
					if (event.getProperty().getValue() != null) {
						UI.getCurrent().setTheme((String) event.getProperty().getValue());
					}
				});

		addComponent(themeComboBox);
		setSizeFull();
		setSpacing(false);
	}

	private static String getText(final String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString(key);
	}

	@Override
	public void enter(final ViewChangeListener.ViewChangeEvent event) {
	}

	@Override
	public boolean isAuthorized(final User user, final String argument) {
		return true;
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}
}