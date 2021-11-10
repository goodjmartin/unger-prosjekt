package no.goodtech.vaadin.layout;

import no.cronus.common.xml.XmlMarshaller;
import no.goodtech.vaadin.topMenuLayout.TopMenuLayout;

import java.util.Collections;

class TopMenuLayoutMarshaller extends XmlMarshaller<TopMenuLayout> {

	TopMenuLayoutMarshaller() {
		super(Collections.singletonList("schema/topMenuLayout.xsd"), "no.goodtech.vaadin.topMenuLayout", "urn:goodtech:xml:ns:topMenuLayout");
	}

}
