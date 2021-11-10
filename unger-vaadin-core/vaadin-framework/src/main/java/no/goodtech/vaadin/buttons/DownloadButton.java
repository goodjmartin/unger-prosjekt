package no.goodtech.vaadin.buttons;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

public class DownloadButton extends Button {

	private static final Resource ICON = VaadinIcons.DOWNLOAD;
	private static final String STYLENAME = "downloadButton";
	private static final String BUTTON_CAPTION = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.download");
	private static final String BUTTON_CAPTION_EXCEL = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.download.excel");
	private static final String BUTTON_CAPTION_CSV = ApplicationResourceBundle.getInstance("vaadin-core").getString("button.download.csv");

	public DownloadButton(final IDownloadListener downloadListener) {
		setCaption(BUTTON_CAPTION);
		setIcon(VaadinIcons.DOWNLOAD);
		addStyleName("downloadButton");
		addClickListener((ClickListener) event -> downloadListener.downloadClicked());
	}

    public interface IDownloadListener {
        void downloadClicked();
    }

    /**
	 * Create a button to download a file
	 * @param filename name of the file to download
	 * @param streamSource provide file content through this when the button is clicked
	 */
	public DownloadButton(String filename, String mimeType, final StreamSource streamSource) {
		this(BUTTON_CAPTION, ICON, filename, mimeType, streamSource);
	}

	/**
	 * Create a button to download a custom file when you don't know the file name in advance
	 * @param onDemandStreamResource you must provide the file content and name through this when the button is clicked 
	 */
	public DownloadButton(final CustomFileDownloader.OnDemandStreamResource onDemandStreamResource) {
		this(BUTTON_CAPTION, ICON, onDemandStreamResource);
	}

	/**
	 * Create a button to download a custom file when you don't know the file name in advance
	 * @param onDemandStreamResource you must provide the file content and name through this when the button is clicked 
	 */
	public DownloadButton(String caption, Resource icon, final CustomFileDownloader.OnDemandStreamResource onDemandStreamResource) {
		super(caption, icon);
		addStyleName(STYLENAME);
		new CustomFileDownloader(onDemandStreamResource).extend(this);
	}

	/**
	 * Create a button to download an Excel file
	 * @param filename name of the file to download
	 * @param streamSource provide file content through this when the button is clicked
	 */
	public static DownloadButton excel(String filename, final StreamSource streamSource) {
		return new DownloadButton(BUTTON_CAPTION_EXCEL, VaadinIcons.TABLE, filename, "application/vnd.ms-excel",
				streamSource);
	}

	/**
	 * Create a button to download a csv file
	 * @param filename name of the file to download
	 * @param streamSource provide file content through this when the button is clicked
	 */
	public static DownloadButton csv(String filename, final StreamSource streamSource) {
		return new DownloadButton(BUTTON_CAPTION_CSV, VaadinIcons.FILE_TEXT, filename, " text/csv", streamSource);
	}

	private DownloadButton(String caption, Resource icon, String filename, String mimeType, final StreamSource streamSource) {
		super(caption, icon);
		addStyleName(STYLENAME);

		StreamResource resource = new StreamResource(streamSource, filename);
		resource.setCacheTime(0);
		resource.setMIMEType(mimeType);
		FileDownloader fileDownloader = new FileDownloader(resource);
		fileDownloader.extend(this);
	}
}
