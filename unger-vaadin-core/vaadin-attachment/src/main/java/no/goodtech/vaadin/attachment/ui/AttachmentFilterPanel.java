package no.goodtech.vaadin.attachment.ui;

import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.attachment.model.Attachment;
import no.goodtech.vaadin.attachment.model.AttachmentFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.search.OwnerFilterPanel;
import no.goodtech.vaadin.utils.Utils;

import javax.management.timer.Timer;
import java.util.Date;

public class AttachmentFilterPanel  extends FilterPanel<AttachmentFinder> {
	private final TextField freeText;
	private final DateField fromDate, toDate;
	private final OwnerFilterPanel ownerFilterPanel;

	public AttachmentFilterPanel(FilterActionListener<AttachmentFinder> listener) {
		super(listener, 30, 500);

		ownerFilterPanel = new OwnerFilterPanel(Attachment.class, getCaption("ownerFilterPanel.ownerClasses"));
		refresh();

		freeText = new TextField(getCaption("freeText"));

		fromDate = new DateField(getCaption("fromDate"));
		fromDate.setDateFormat(Utils.DATE_FORMAT);
		toDate = new DateField(getCaption("toDate"));
		toDate.setDateFormat(Utils.DATE_FORMAT);

		addComponents(ownerFilterPanel, fromDate, toDate, freeText);
	}

	public void refresh() {
		ownerFilterPanel.refresh();
	}

	public static String getCaption(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-attachment").getString("attachmentFilterPanel.caption." + key);
	}

	@Override
	public AttachmentFinder getFinder() {
		AttachmentFinder finder = new AttachmentFinder();

		Class<?> clazz = ownerFilterPanel.getSelectedOwnerClass();
		if (clazz != null) {
			finder.setOwnerClass(clazz);
		}

		if (fromDate.getValue() != null) {
			finder.setCreatedAfterOrOn(fromDate.getValue());
		}

		if (toDate.getValue() != null) {
			finder.setCreatedBeforeOrOn(new Date(toDate.getValue().getTime() + Timer.ONE_DAY));
		}

		if (freeText.getValue() != null && freeText.getValue().trim().length() > 0) {
			finder.setSearchAll(freeText.getValue());
		}

		return finder;
	}
}
