package no.goodtech.vaadin.test;

import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.help.model.HelpText;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyClass;
import no.goodtech.vaadin.properties.model.PropertyMembership;
import no.goodtech.vaadin.security.MD5Hash;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.tabs.status.Globals;
import no.goodtech.vaadin.tabs.status.model.StatusIndicator;

import java.awt.*;

public class DefaultTestDataCreator {

	public void init(){
		createStatusLogs();
		createUsers();
		createHelpText();
		createProperties();
		createCategories();
	}

	private void createStatusLogs() {
		new StatusIndicator("TEST", "Testing1").save();
		new StatusIndicator("TEST3", "Testing3").save();
		new StatusIndicator("TEST2", "Testing2").save();
		new StatusIndicator("NO", "NoLoggedValues").save();
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST").success("First success");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST").success("Second success");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST").warning("Warning!");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST").failure("FAIL!");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST").failure("FAIL2!");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST").success("Ok again");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST2").failure("Failing");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST3").warning("WARN");
		Globals.getStatusLoggerRepository().lookupStatusLogger("TEST3").warning("WARN", new Throwable("This is a test"));
	}

	private void createUsers(){
		final String md5Password = MD5Hash.convert("admin");
		new User("admin", md5Password, new String[]{"admin"}).save();

		AccessRole role = new AccessRole();
		role.setId("DummyRole");
		role = role.save();
	}

	private void createHelpText(){
		HelpText helpText = new HelpText("FRONTPAGE");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			sb.append("<p>This is a test...</p>");
		}
		helpText.setText(sb.toString());
		helpText.save();
	}

	private void createProperties(){
		PropertyClass propertyClass = new PropertyClass("Attachment");
		propertyClass.setDescription("Attachment");
		propertyClass = propertyClass.save();
		Property p1 = new Property("p1");
		p1.setName("Property 1");
		p1 = p1.save();
		Property p2 = new Property("p2");
		p2.setName("Property 2");
		p2 = p2.save();
		PropertyMembership propertyMembership = new PropertyMembership();
		propertyMembership.setPropertyClass(propertyClass);
		propertyMembership.setProperty(p1);
		propertyMembership.setIndexNo(1);
		propertyMembership.setEditable(true);
		propertyMembership.save();
	}

	private void createCategories() {
		new Category("C1", "Category 1", Property.class, 1).save();
		Category c2 = new Category("C2", "Category 2", Property.class, 1);
		c2.setColor(Color.green.getRGB());
		c2.setIconName("CHECK");
		c2.save();
	}
}
