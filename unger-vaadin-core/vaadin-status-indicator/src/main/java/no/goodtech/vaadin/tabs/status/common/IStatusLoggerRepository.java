package no.goodtech.vaadin.tabs.status.common;

import java.util.List;

/**
 * Denne klassen definerer grensesnittet til StatusIndicatorRepository klassen
 * <p/>
 * User: bakke
 */
public interface IStatusLoggerRepository {

	/**
	 * Denne metoden benyttes for å slå opp en status indikator
	 *
	 * @param id ID til status indikator
	 *
	 * @return Funnet status indikator
	 */
	public IStatusLogger lookupStatusLogger(final String id);

	/**
	 * Denne metoden returnerer listen over opprettede status indikatorer
	 *
	 * @return Listen over opprettede status indikatorer
	 */
	public List<IStatusLogger> getStatusLoggers();

}
