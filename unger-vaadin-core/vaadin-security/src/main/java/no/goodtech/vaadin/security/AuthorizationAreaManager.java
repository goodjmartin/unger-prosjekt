package no.goodtech.vaadin.security;

import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.security.ui.Texts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthorizationAreaManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationAreaManager.class);
	public static final String ACCESS_VIEW = "view.area.";
	private String areas;

	public AuthorizationAreaManager(String areas) {
		this.areas = areas;
	}


	private void init() {
		List<String> areaList = Stream.of(areas.split(",", -1))
				.collect(Collectors.toList());
		for (String area : areaList) {
			AccessFunctionManager.registerAccessFunction(new AccessFunction(ACCESS_VIEW + area, Texts.get("accessFunction.area.view") + " " + area));
		}
	}

	public boolean isAuthorized(final User user, final String area) {
		return AccessFunctionManager.isAuthorized(user, ACCESS_VIEW + area);
	}
}
