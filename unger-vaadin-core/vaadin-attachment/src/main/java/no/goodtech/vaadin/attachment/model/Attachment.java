package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.entity.AbstractChildEntityImpl;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Attachment extends AbstractChildEntityImpl<Attachment> {
/*
	TODO usage of ApplicationRecourseBundle in Entity classes causes bundle to be put on map before Environment class is wired up(defaulting to the system locale)
	TODO instead of using the locale configured by the vaadin-framework.country and vaadin-framework.language properties
	private static final String niceName = ApplicationResourceBundle.getInstance("mes-attachment").getString("attachment.niceName");
	*/
	private final String filePath;
	private volatile String fileDescription;
	private volatile String changedBy;
	private final String fileName;

	private final Date created;

	protected Attachment() {        // Required by Hibernate
		this(null, null, null, null, null, null);
	}

	protected Attachment(final no.goodtech.persistence.entity.EntityStub owner, final String filePath, final String fileName, final String fileDescription, final String changedBy, final Date created) {
		this((owner != null) ? owner.getClass() : null, (owner != null) ? owner.getPk() : null, filePath, fileDescription, fileName, changedBy, created);
	}

	protected Attachment(final Class<?> ownerClass, final Long ownerPk, final String filePath, final String fileName, final String fileDescription, final String changedBy, final Date created) {
		this.ownerClass = ownerClass;
		this.ownerPk = ownerPk;
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileDescription = fileDescription;
		this.created = created;
		this.changedBy = changedBy;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	protected void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public String getChangedBy() {
		return changedBy;
	}

	protected void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	@SuppressWarnings("UnusedDeclaration")
	public Date getCreated() {
		return created;
	}

	@Override
	public String getNiceClassName() {
		return ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachment.niceName");
	}

	@SuppressWarnings("UnusedDeclaration")
	public String getFileName() {
		return fileName;
	}
}
