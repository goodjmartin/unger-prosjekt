package no.goodtech.vaadin.buttons;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

import java.io.IOException;

/**
 * This specializes {@link FileDownloader} in a way, such that both the file name and content can be determined
 * on-demand, i.e. when the user has clicked the component.
 * <p/>
 * Tok den herfra: https://vaadin.com/wiki/-/wiki/Main/Letting+the+user+download+a+file
 */
public class CustomFileDownloader extends FileDownloader {

	/**
	 * Provide both the StreamSource and the filename in an on-demand way.
	 */
	public interface OnDemandStreamResource extends StreamResource.StreamSource {
		String getFilename();
	}

	private static final long serialVersionUID = 1L;
	private final OnDemandStreamResource onDemandStreamResource;

	public CustomFileDownloader(OnDemandStreamResource onDemandStreamResource) {
		super(new StreamResource(onDemandStreamResource, ""));
		this.onDemandStreamResource = onDemandStreamResource;
	}


	@Override
	public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path)
			throws IOException {
		getResource().setFilename(onDemandStreamResource.getFilename());
		super.handleConnectorRequest(request, response, path);
		return true;
	}

	private StreamResource getResource() {
		return (StreamResource) this.getResource("dl");
	}

}
