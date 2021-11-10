package no.goodtech.vaadin.layout;

import no.cronus.common.xml.XmlMarshaller;
import no.goodtech.vaadin.layoutDefinition.LayoutDefinition;

import java.util.Collections;

class LayoutDefinitionMarshaller extends XmlMarshaller<LayoutDefinition> {

	LayoutDefinitionMarshaller() {
		super(Collections.singletonList("schema/layout-definition.xsd"), "no.goodtech.vaadin.layoutDefinition", "urn:goodtech:xml:ns:layoutDefinition");
	}
}
