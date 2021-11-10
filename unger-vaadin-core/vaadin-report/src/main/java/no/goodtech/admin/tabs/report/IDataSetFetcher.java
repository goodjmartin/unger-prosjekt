package no.goodtech.admin.tabs.report;

import java.util.List;

public interface IDataSetFetcher {

	/**
	 * This method should be called to obtain the list of rows for the specific data set
	 *
	 * @return The list of rows for the specific data set
	 */
	public List<List<?>> getRows();

	/**
	 * Kjører spørring og gjør resultatsettet om til en oppslagstabell (key-value). Nøkkel hentes fra første kolonne. 
	 * Hvis resultatsettet har 2 eller flere kolonner hentes verdien fra 2.kolonne. Evt. flere kolonner blir ignorert.
	 * Hvis resultatsettet har kun 1 kolonne blir verdien også satt fra denne kolonna.
	 * Evt. rader hvor første kolonne er null blir ikke tatt med.  
	 * @return oppslagstabell basert på resultatet av spørringa
	 */
	List<SelectionEntry> getKeyValue();

}
