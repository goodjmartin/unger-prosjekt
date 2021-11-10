package no.goodtech.vaadin.frontpage.model;

public class FrontPageFactory {

	private static volatile FrontPageService service;

	public FrontPageFactory(final FrontPageService service) {
		FrontPageFactory.service = service;
	}

	public static FrontPageService getService() {
		return service;
	}
}
