package no.goodtech.vaadin.security;

import no.goodtech.vaadin.login.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccessFunctionManager {
	private static final List<AccessFunction> ACCESS_FUNCTIONS = new ArrayList<>();

	/**
	 * Register new distinct accessFunction
	 */
	public static synchronized void registerAccessFunction(final AccessFunction accessFunction) {
		if (!accessFunctionsContainsId(ACCESS_FUNCTIONS, accessFunction.getId())) {
			ACCESS_FUNCTIONS.add(accessFunction);
		}
	}

	/**
	 * @param id to remove
	 * @return true - success, false - did not find any AccessFunction to remove
	 */
	public static synchronized boolean removeAccessFunction(final String id) {
		AccessFunction removeFunction = null;
		for (AccessFunction accessFunction : ACCESS_FUNCTIONS) {
			if (Objects.equals(accessFunction.getId(), id)) {
				removeFunction = accessFunction;
				break;
			}
		}
		if (removeFunction != null) {
			ACCESS_FUNCTIONS.remove(removeFunction);
			return true;
		}
		return false;
	}

	/**
	 * Method for testing if an AccessFunction with a specified id is in a list of AccessFunctions
	 */
	public static boolean accessFunctionsContainsId(List<AccessFunction> list, String id) {
		for (AccessFunction accessFunction : list) {
			if (Objects.equals(accessFunction.getId(), id)) {
				return true;
			}
		}
		return false;
	}

	public static synchronized boolean isAuthorized(final User user, final String accessFunctionId) {
		if ((user != null) && (accessFunctionId != null)) {
			if ((user.getAccessFunctionIds() == null) || user.getAccessFunctionIds().contains(accessFunctionId)) {
				return true;
			}
		}

		return false;
	}

	public static List<AccessFunction> getAccessFunctions() {
		return ACCESS_FUNCTIONS;
	}
}
