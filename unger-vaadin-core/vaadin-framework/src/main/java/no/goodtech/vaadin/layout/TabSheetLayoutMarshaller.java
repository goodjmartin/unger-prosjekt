package no.goodtech.vaadin.layout;

import no.cronus.common.xml.XmlMarshaller;
import no.goodtech.vaadin.tabSheetLayout.TabSheetLayout;

import java.util.Collections;

public class TabSheetLayoutMarshaller extends XmlMarshaller<TabSheetLayout> {

    public TabSheetLayoutMarshaller() {
        super(Collections.singletonList("schema/tabSheetLayout.xsd"), "no.goodtech.vaadin.tabSheetLayout", "urn:goodtech:xml:ns:tabSheetLayout");
    }
}
