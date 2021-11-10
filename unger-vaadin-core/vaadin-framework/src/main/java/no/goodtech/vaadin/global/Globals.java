package no.goodtech.vaadin.global;

import no.goodtech.vaadin.layout.LayoutManager;
import no.goodtech.vaadin.layout.TopMenuManager;

/**
 * Tjenester som er tilgjengelige som singletons for hele applikasjonen.
 * De fleste av disse tjenestene er konfigurert opp vha. Spring
 */
public class Globals {

	private static String applicationTitle;
	private static LayoutManager layoutManager;
	private static TopMenuManager topMenuManager;
	private static int maxLoginFailureAttempts;
	private static String goodtechLogo;
	private static String customerLogo;
	private static String[] availableThemes;

	/**
	 * Denne kjøres av Spring, se vaadin-core-server.xml
	 */
	public Globals(final String applicationTitle, final LayoutManager layoutManager, final TopMenuManager topMenuManager, final int maxLoginFailureAttempts, final String goodtechLogo, final String customerLogo, final String[] availableThemes) {
		Globals.applicationTitle = applicationTitle;
		Globals.layoutManager = layoutManager;
		Globals.topMenuManager = topMenuManager;
		Globals.maxLoginFailureAttempts = maxLoginFailureAttempts;
		Globals.goodtechLogo = goodtechLogo;
		Globals.customerLogo = customerLogo;
		Globals.availableThemes = availableThemes;
	}

	public static String getApplicationTitle() {
		return applicationTitle;
	}

	public static LayoutManager getLayoutManager() {
		return Globals.layoutManager;
	}

	public static TopMenuManager getTopMenuManager() {
		return topMenuManager;
	}

	/**
	 * Denne metoden returnerer maks antall tillatte login feil
	 *
	 * @return Maks antall tillatte login feil
	 */
	public static int getMaxLoginFailureAttempts() {
		return Globals.maxLoginFailureAttempts;
	}

	/**
	 * Denne metoden returnerer navnet på Goodtech logo-fila for en spesifikk installasjon
	 *
	 * @return navnet på Goodtech logo-fila
	 */
	public static String getGoodtechLogo() {
		return Globals.goodtechLogo;
	}

	/**
	 * Denne metoden returnerer navnet på kunde-logo fila for en spesifikk installasjon
	 *
	 * @return navnet på kunde-logo fila
	 */
	public static String getCustomerLogo() {
		return Globals.customerLogo;
	}

	public static String[] getAvailableThemes() {
		return availableThemes;
	}
}
