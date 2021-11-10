package no.goodtech.admin.tabs.report;

import no.goodtech.admin.report.ReportType;
import no.goodtech.admin.report.Reports;
import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.goodtech.admin.xml.ReportMarshaller;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the list of available reports
 */
public class ReportManager {

    private static final String REPORT_VIEW = "reportView.";
    //TODO avoiding usage of ApplicationResourceBundle since this class is wired up before Environment.java
    //private static final String REPORT_DESCRIPTION_PREFIX = ApplicationResourceBundle.getInstance("vaadin-report").getString("reportView.description.prefix");
	private static final Logger logger = LoggerFactory.getLogger(ReportManager.class);
    private final Map<String, ReportType> reportMap = new HashMap<String, ReportType>();

    public ReportManager(final String reportDefinitionFile) {
        Reports reports = null;
        if (reportDefinitionFile != null) {
			IFileReader fileReader = new FileReader(reportDefinitionFile);
			ReportMarshaller reportMarshaller = new ReportMarshaller();

            reports = reportMarshaller.fromXML(fileReader.read());
            if ((reports != null) && (reports.getReport() != null)) {
                for (ReportType report : reports.getReport()) {
                    reportMap.put(report.getId(), report);
                    AccessFunctionManager.registerAccessFunction(new AccessFunction(REPORT_VIEW + report.getId(), "Rapport:" + " " + report.getName()));
                }
            }
		}

		if (logger.isInfoEnabled()) {
			logger.info("ReportManager: reports read=" + ((reports != null) ? reports.getReport().size() : 0));
		}
	}

    public ReportType getReport(final String id) {
        ReportType report = reportMap.get(id);
        
        if (report == null) {
            logger.error("Requested un-existing report: " + id);
        }

        return report;
    }

    public boolean isAuthorized(final User user, final String reportId) {
        return AccessFunctionManager.isAuthorized(user, REPORT_VIEW + reportId);
    }
}
