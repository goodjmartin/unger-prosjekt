package no.goodtech.vaadin.layout;

import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.goodtech.vaadin.topMenuLayout.TopMenuLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the top menu definition
 */
public class TopMenuManager {

	private static final Logger logger = LoggerFactory.getLogger(TopMenuManager.class);
	private final TopMenuLayout topMenuLayout;

	public TopMenuManager(final String fileName) {
        if (fileName != null) {
            IFileReader fileReader = new FileReader(fileName);
			TopMenuLayoutMarshaller topMenuLayoutMarshaller = new TopMenuLayoutMarshaller();
			topMenuLayout = topMenuLayoutMarshaller.fromXML(fileReader.read());
        } else {
            logger.warn("No layout definitionFile specified");
			topMenuLayout = null;
        }
	}

	/**
	 * This method should be called to obtain the layout definition
	 *
	 * @return The layout definition
	 */
	public TopMenuLayout getTopMenuLayout() {
		return topMenuLayout;
	}
}
