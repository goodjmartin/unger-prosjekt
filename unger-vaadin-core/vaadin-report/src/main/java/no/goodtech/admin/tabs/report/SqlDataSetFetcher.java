package no.goodtech.admin.tabs.report;

import no.goodtech.persistence.PersistenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bruk denne for å hente data til rapporten
 */
public class SqlDataSetFetcher implements IDataSetFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlDataSetFetcher.class);

	private final String query;
	private final Map<? extends IReportParameter, String> reportParameters;
	private final String dataSource;

	/**
	 * Opprett spørre-objektet
	 * @param query sql-spørringa
	 * @param reportParameters parametre til spørringa
	 * @param dataSource datakilde vi skal spørre. Tilsvarer dataSource i Spring-config. Om denne er null, brukes standard datakilde
	 */
	public SqlDataSetFetcher(final String query, final Map<? extends IReportParameter, String> reportParameters, String dataSource) {
		this.query = query;
		this.reportParameters = reportParameters;
		this.dataSource = dataSource;
	}


	@Override
	public List<List<?>> getRows() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("enter");
		}
		List<List<?>> rows;
		if (reportParameters == null || reportParameters.isEmpty())
			rows = PersistenceFactory.getQueryExecutor(dataSource).execute(query);
		else if (isAllParametersNamed())
			rows = PersistenceFactory.getQueryExecutor(dataSource).execute(query, getNamedParameters());
		else
			rows = PersistenceFactory.getQueryExecutor(dataSource).execute(query, getParameterArguments());
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("leave - size=" + rows.size());
		}
		return rows;
	}


	private List<String> getParameterArguments() {
		List<String> result = new ArrayList<>();
		if (reportParameters != null)
			for (IReportParameter reportParameter : reportParameters.keySet())
				if (reportParameter != null) {
					result.add(reportParameter.getValueAsString());
				}else{
					LOGGER.error("Missing one of the report parameters for query: " + query);
				}
		LOGGER.debug("Parameters: " + result);
		return result;
	}

	private boolean isAllParametersNamed() {
		if (reportParameters != null) {
			for (Map.Entry<? extends IReportParameter, String> entry : reportParameters.entrySet()) {
				String name = entry.getValue();
				if (name == null || name.trim().length() == 0)
					return false;			
			}
		}
		return true;
	}
	
	private Map<String, String> getNamedParameters() {
		Map<String, String> result = new HashMap<>();
		if (reportParameters != null)
			for (Map.Entry<? extends IReportParameter, String> entry : reportParameters.entrySet()) {
				String name = entry.getValue();
				IReportParameter parameter = entry.getKey();
				if (parameter != null) {
					result.put(name, parameter.getValueAsString());
				}else{
					result.put(name, "");
				}
			}
		LOGGER.debug("Parameters: " + result);
		return result;
	}

	@Override
	public List<SelectionEntry> getKeyValue() {
		List<List<?>> rows = getRows();
		List<SelectionEntry> selectionEntries = new ArrayList<>();

	    for (List<?> row : rows) {
    		if (row.get(0) != null) {
    			//hopper over rader med null i første kolonne, fordi det blir krøll hvis nøkkel er null
				final String key = (String) row.get(0);
				if (row.size() > 1) {
					selectionEntries.add(new SelectionEntry(key, String.valueOf(row.get(1))));
				} else {
					selectionEntries.add(new SelectionEntry(key, key));
				}
    		}
	    }
		return selectionEntries;
	}
}
