package no.goodtech.vaadin.frontpage.model;

import no.goodtech.persistence.jpa.AbstractFinder;

import java.util.List;

public class FrontPageCardFinder extends AbstractFinder<FrontPageCard, FrontPageCard, FrontPageCard> {

	public FrontPageCardFinder() {
		super("select u from FrontPageCard u", "u");
		addSortOrder(prefixWithAlias("indexNo"), true);
	}

	public FrontPageCardFinder setUserId(String id){
		addEqualFilter(prefixWithAlias("user.id"), id);
		return this;
	}

	public int getLastIndexNo(){
		setSelectFromClause("select u.indexNo from FrontPageCard u", "u");
		addSortOrder(prefixWithAlias("indexNo"), false);
		setMaxResults(1);
		List<Integer> indexNo = (List<Integer>) getRepository().list(this, Integer.class);
		if (indexNo.size() == 0)
			return 0;
		return indexNo.get(0);
	}

}
