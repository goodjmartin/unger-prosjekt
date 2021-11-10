package no.goodtech.vaadin.layout;

import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.goodtech.vaadin.layoutDefinition.LayoutDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the layout definition
 */
public class LayoutManager {

	private static final Logger logger = LoggerFactory.getLogger(LayoutManager.class);
	private final LayoutDefinition layoutDefinition;

	public LayoutManager(final String layoutDefinitionFile) {
        if (layoutDefinitionFile != null) {
            IFileReader fileReader = new FileReader(layoutDefinitionFile);
            LayoutDefinitionMarshaller layoutDefinitionMarshaller = new LayoutDefinitionMarshaller();
            layoutDefinition = layoutDefinitionMarshaller.fromXML(fileReader.read());
        } else {
            logger.warn("No layout definitionFile specified");
            layoutDefinition = null;
        }
	}

	/**
	 * This method should be called to obtain the layout definition
	 *
	 * @return The layout definition
	 */
	public LayoutDefinition getLayoutDefinition() {
		return layoutDefinition;
	}
}
