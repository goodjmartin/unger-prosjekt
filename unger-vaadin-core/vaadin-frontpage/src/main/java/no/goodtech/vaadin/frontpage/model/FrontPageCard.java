package no.goodtech.vaadin.frontpage.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.security.model.User;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity for a front-page card
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FrontPageCard extends AbstractEntityImpl<FrontPageCard> {

	public static final String ARGS_SEPARATOR = ",";
	public static final String VALUE_SEPARATOR = "=";

	@ManyToOne
	@JoinColumn(name = "person_pk")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private User user;

	@NotNull
	private String panel;

	private String args;

	@Min(0)
	private Integer indexNo;

	public FrontPageCard() {
	}

	public FrontPageCard(User user, String panel, String args, Integer indexNo) {
		this.user = user;
		this.panel = panel;
		this.args = args;
		this.indexNo = indexNo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPanel() {
		return panel;
	}

	public void setPanel(String panel) {
		this.panel = panel;
	}

	public String getArgs() {
		return args;
	}

	public Map<String, String> getArgsMap() {
		if (args != null) {
			String[] splitted_args = args.split(ARGS_SEPARATOR);
			Map<String, String> argMap = new HashMap<>();
			for (String split : splitted_args){
				if (split.contains(FrontPageCard.VALUE_SEPARATOR)) {
					String parameter = split.split(FrontPageCard.VALUE_SEPARATOR)[0];
					String value = split.split(FrontPageCard.VALUE_SEPARATOR)[1];
					argMap.put(parameter, value);
				}
			}
			return argMap;
		}
		return null;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public Integer getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(Integer indexNo) {
		this.indexNo = indexNo;
	}
}
