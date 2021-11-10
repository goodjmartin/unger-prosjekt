package no.goodtech.admin.xml;

import no.goodtech.admin.report.Reports;
import no.cronus.common.xml.XmlMarshaller;

import java.util.Collections;

public class ReportMarshaller extends XmlMarshaller<Reports> {

	public ReportMarshaller() {
		super(Collections.singletonList("report-definition.xsd"), "no.goodtech.admin.report", "urn:goodtech:xml:ns:report");
	}

}
