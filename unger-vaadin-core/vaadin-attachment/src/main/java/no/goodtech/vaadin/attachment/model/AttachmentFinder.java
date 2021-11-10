package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.persistence.search.SearchFilterGroup;
import no.goodtech.vaadin.search.FilterPanel;

import java.util.Date;

public class AttachmentFinder extends AbstractFinder<Attachment, Attachment, AttachmentFinder> implements FilterPanel.IMaxRowsAware {
	public AttachmentFinder() {
		super("select a from Attachment a", "a");
	}

	public AttachmentFinder setOwner(final EntityStub owner) {
		if (owner == null) {
			addNullFilter(prefixWithAlias("ownerPk"));
			addNullFilter(prefixWithAlias("ownerClass"));
		} else {
			addEqualFilter(prefixWithAlias("ownerPk"), owner.getPk());
			addEqualFilter(prefixWithAlias("ownerClass"), owner.getClass());
		}
		return this;
	}

	public AttachmentFinder setOwnerClass(final Class<?> ownerClass) {
		if (ownerClass == null) {
			addNullFilter(prefixWithAlias("ownerClass"));
		} else {
			addEqualFilter(prefixWithAlias("ownerClass"), ownerClass);
		}
		return this;
	}

	public AttachmentFinder setOwnerPk(final Long ownerPk) {
		if (ownerPk == null) {
			addNullFilter(prefixWithAlias("ownerPk"));
		} else {
			addEqualFilter(prefixWithAlias("ownerPk"), ownerPk);
		}
		return this;
	}

	public AttachmentFinder setFilePath(final String filePath) {
		if (filePath == null) {
			addNullFilter(prefixWithAlias("filePath"));
		} else {
			addEqualFilter(prefixWithAlias("filePath"), filePath);
		}
		return this;
	}

	public AttachmentFinder setFileDescription(final String fileDescription) {
		if (fileDescription == null) {
			addNullFilter(prefixWithAlias("fileDescription"));
		} else {
			addEqualFilter(prefixWithAlias("fileDescription"), fileDescription);
		}
		return this;
	}

	public AttachmentFinder setFileName(final String fileName) {
		if (fileName == null) {
			addNullFilter(prefixWithAlias("fileName"));
		} else {
			addEqualFilter(prefixWithAlias("fileName"), fileName);
		}
		return this;
	}

	public AttachmentFinder orderByCreated(final boolean ascending) {
		addSortOrder(prefixWithAlias("created"), ascending);
		return this;
	}

	public AttachmentFinder setCreatedAfterOrOn(Date date){
		if (date != null) {
			addGreaterThanOrEqualFilter("created", date);
		}
		return this;
	}

	public AttachmentFinder setCreatedBeforeOrOn(Date date) {
		if (date != null) {
			addSmallerThanOrEqualFilter("created", date);
		}
		return this;
	}

	public AttachmentFinder setSearchAll(String text){
		addGroup(SearchFilterGroup.GroupingOperator.OR);
		addLikeFilter(prefixWithAlias("filePath"), text, true);
		addLikeFilter(prefixWithAlias("fileName"), text, true);
		addLikeFilter(prefixWithAlias("fileDescription"), text, true);
		addLikeFilter(prefixWithAlias("changedBy"), text, true);
		return this;
	}
}
