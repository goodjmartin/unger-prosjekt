package no.goodtech.dashboard.config.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Configuration of a table panel
 */
@Entity
public class TableConfig extends PanelConfig {

	public PanelConfig copy() {
		//TODO
		return new TableConfig();
	}

	//TODO
}
