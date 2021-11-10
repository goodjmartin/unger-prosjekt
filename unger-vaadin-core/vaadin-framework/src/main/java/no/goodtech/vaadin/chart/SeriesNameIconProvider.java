package no.goodtech.vaadin.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinServlet;

/**
 * Provides different icons for different names on a chart series
 */
public class SeriesNameIconProvider implements ISeriesIconProvider {

	protected static final Logger LOGGER = LoggerFactory.getLogger(SeriesNameIconProvider.class);
	
	protected String filePrefix, filePostfix;
	
	public String getIconUrl(TimeSeries series) {
		final String contextPath = VaadinServlet.getCurrent().getServletContext().getContextPath();
		final String name = series.getName();
		String url = contextPath + "/VAADIN/themes/admin/images/warning-red-16.png";
		if (name != null) {
			url = contextPath + filePrefix + name + filePostfix;
		}
		LOGGER.debug("url = {}", url);
		return url;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public void setFilePostfix(String filePostfix) {
		this.filePostfix = filePostfix;
	}
}
