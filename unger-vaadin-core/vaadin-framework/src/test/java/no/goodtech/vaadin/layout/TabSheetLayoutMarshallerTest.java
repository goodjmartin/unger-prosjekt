package no.goodtech.vaadin.layout;

import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.cronus.common.logging.LogIt;
import no.goodtech.vaadin.tabSheetLayout.PanelGroupType;
import no.goodtech.vaadin.tabSheetLayout.TabSheetLayout;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TabSheetLayoutMarshallerTest {

	@Test
    public void testTabSheetLayout() {
        // Read layout definition file
        IFileReader fileReader = new FileReader("classpath://tabSheetLayout.xml");

        // Un-marshal test XML
        TabSheetLayoutMarshaller tabSheetLayoutMarshaller = new TabSheetLayoutMarshaller();
        TabSheetLayout tabSheetLayout = tabSheetLayoutMarshaller.fromXML(fileReader.read());

        // Validate: Groups
		List<PanelGroupType> panelGroups = tabSheetLayout.getPanelGroups().getPanelGroup();
        Assert.assertEquals(3, tabSheetLayout.getPanelGroups().getPanelGroup().size());

		// Validate: First group
		PanelGroupType panelGroup = panelGroups.get(0);
		Assert.assertEquals(1, panelGroup.getPanel().size());
		Assert.assertEquals("group1", panelGroup.getId());
		Assert.assertEquals("no.goodtech.vaadin.test.dummy.DummyTab1", panelGroup.getPanel().get(0).getClazz());

		// Validate: Second group
		panelGroup = panelGroups.get(1);
		Assert.assertEquals(1, panelGroup.getPanel().size());
		Assert.assertEquals("group2", panelGroup.getId());
		Assert.assertEquals("no.goodtech.vaadin.test.dummy.DummyTab2", panelGroup.getPanel().get(0).getClazz());

		// Validate: Third group
		panelGroup = panelGroups.get(2);
		Assert.assertEquals("all", panelGroup.getId());
		Assert.assertEquals(2, panelGroup.getPanel().size());
		Assert.assertEquals("no.goodtech.vaadin.test.dummy.DummyTab1", panelGroup.getPanel().get(0).getClazz());
		Assert.assertEquals("no.goodtech.vaadin.test.dummy.DummyTab2", panelGroup.getPanel().get(1).getClazz());

		// Marshal to XML
        String xml = tabSheetLayoutMarshaller.toXML(tabSheetLayout);
        LogIt.log(xml);
    }

}
