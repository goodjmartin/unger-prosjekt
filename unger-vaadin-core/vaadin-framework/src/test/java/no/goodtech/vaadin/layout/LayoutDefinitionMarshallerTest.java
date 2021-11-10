package no.goodtech.vaadin.layout;

import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.cronus.common.logging.LogIt;
import no.goodtech.vaadin.layoutDefinition.GroupType;
import no.goodtech.vaadin.layoutDefinition.LayoutDefinition;
import no.goodtech.vaadin.layoutDefinition.MenuItemType;
import org.junit.Assert;
import org.junit.Test;

public class LayoutDefinitionMarshallerTest {

	@Test
    public void testTabSheetLayout() {
        // Read layout definition file
        IFileReader fileReader = new FileReader("classpath://layout-definition.xml");

        // Un-marshal test XML
        LayoutDefinitionMarshaller layoutDefinitionMarshaller = new LayoutDefinitionMarshaller();
        LayoutDefinition layoutDefinition = layoutDefinitionMarshaller.fromXML(fileReader.read());

		// Validate: Group
		GroupType group = (GroupType)layoutDefinition.getMenu().getMenuItemOrGroup().get(0);
		Assert.assertEquals("Rapporter", group.getName());
		Assert.assertEquals("FILM", group.getIcon());
		Assert.assertEquals(false, group.getExpand());

		// Validate: Menu items
		MenuItemType menuItem = (MenuItemType)group.getMenuItemOrGroup().get(0);
		Assert.assertEquals("Access Role Report", menuItem.getName());
		Assert.assertEquals("reportView", menuItem.getViewId());
		Assert.assertEquals("report=accessRoles", menuItem.getParameters());
		Assert.assertEquals("VOLUME_UP", menuItem.getIcon());

		menuItem = (MenuItemType)group.getMenuItemOrGroup().get(1);
		Assert.assertEquals("Queue Report", menuItem.getName());
		Assert.assertEquals("reportView", menuItem.getViewId());
		Assert.assertEquals("report=queueReport", menuItem.getParameters());
		Assert.assertEquals("VOLUME_DOWN", menuItem.getIcon());

		menuItem = (MenuItemType)layoutDefinition.getMenu().getMenuItemOrGroup().get(1);
		Assert.assertEquals("NavigatorTest", menuItem.getName());
		Assert.assertEquals("breadCrumbView", menuItem.getViewId());
		Assert.assertEquals("1", menuItem.getIndex());
		Assert.assertNull(menuItem.getParameters());
		Assert.assertEquals("HEART", menuItem.getIcon());

        // Marshal to XML
        String xml = layoutDefinitionMarshaller.toXML(layoutDefinition);
        LogIt.log(xml);
    }

}
