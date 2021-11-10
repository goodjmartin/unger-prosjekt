package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.tabs.status.Globals;
import no.goodtech.vaadin.tabs.status.common.IStatusLogger;
import no.goodtech.vaadin.tabs.status.common.StateType;

import java.time.LocalDateTime;
import java.util.*;

public class StatusLogEntryFinder extends AbstractFinder<StatusLogEntry, StatusLogEntryStub, StatusLogEntryFinder> implements FilterPanel.IMaxRowsAware {

    public StatusLogEntryFinder() {
        super("select sle from StatusLogEntry sle", "sle");
    }

    public StatusLogEntryFinder setMessage(final String message, final boolean wide) {
        addLikeFilter(prefixWithAlias("message"), message, wide);
        return this;
    }

    /**
     * Filter by state type 
     * StateType#SUCCESS is not logged anymore, so you won't get any count on StateType#SUCCESS.
     */
    public StatusLogEntryFinder setStateTypes(final StateType... stateTypes) {
        addInFilter(prefixWithAlias("stateType"), stateTypes);
        return this;
    }

	public StatusLogEntryFinder setStateTypes(Set<StateType> stateTypes) {
		return setStateTypes(stateTypes.toArray(new StateType[stateTypes.size()]));
	}

	public StatusLogEntryFinder setBetween(final LocalDateTime fromDate, final LocalDateTime toDate) {
        addBetween(prefixWithAlias("created"), fromDate, toDate);
        return this;
    }

    /**
     * Filtrer vekk objekter opprettet før angitt tidspunkt
     * @param fromDate tidspunktet
     * @return Søkeobjekt med dette filteret satt
     */
    public StatusLogEntryFinder setCreatedAfter(final LocalDateTime fromDate) {
        addGreaterThanOrEqualFilter(prefixWithAlias("created"), fromDate);
        return this;
    }

	/**
	 * Filtrer vekk objekter opprettet etter angitt tidspunkt
	 * @param toDate tidspunktet
	 * @return Søkeobjekt med dette filteret satt
	 */
	public StatusLogEntryFinder setCreatedBefore(final LocalDateTime toDate) {
		addSmallerThanOrEqualFilter(prefixWithAlias("created"), toDate);
		return this;
	}

    public StatusLogEntryFinder setStatusIndicator(final StatusIndicatorStub statusIndicator) {
        if (statusIndicator != null) {
            addEqualFilter(prefixWithAlias("statusIndicator"), statusIndicator);
        }
        return this;
    }

    public StatusLogEntryFinder setStatusIndicatorId(final String statusIndicatorId) {
        if (statusIndicatorId != null)
            addEqualFilter(prefixWithAlias("statusIndicator.id"), statusIndicatorId);
        return this;
    }

    public StatusLogEntryFinder orderByPk(final boolean ascending) {
        addSortOrder(prefixWithAlias("pk"), ascending);
        return this;
    }
    
	/**
	 * Teller antall logge-meldinger av hver type.
	 * StateType#SUCCESS is not logged anymore, so you won't get any count on StateType#SUCCESS.
	 * @return antall logge-meldinger av hver type
	 */
	@SuppressWarnings("unchecked")
	public SortedMap<StatusIndicatorStub, Map<StateType, Long>> listCountPerStateType() {
		setSelectFromClause("select new list(sle.statusIndicator, sle.stateType, count(sle.statusIndicator.pk)) " +
				"from StatusLogEntry sle ", "sle"); 
		setGroupByClause("sle.statusIndicator, sle.stateType");
		
        List<Object> resultList = getRepository().list(this, List.class);
        SortedMap<StatusIndicatorStub, Map<StateType, Long>> totalCounts = new TreeMap<StatusIndicatorStub, Map<StateType,Long>>();
        
        for (Object object : resultList) {

        	List<Object> row = (List<Object>) object;
        	StatusIndicatorStub indicator = (StatusIndicatorStub) row.get(0);
        	
        	Map<StateType, Long> countPerStateType = totalCounts.get(indicator);
        	if (countPerStateType == null) {
        		countPerStateType = new HashMap<StateType, Long>();
        		for (StateType stateType : StateType.values())
            		//initialiser alle summer for denne indikatoren med 0
        			countPerStateType.put(stateType, 0L);
        	}
        	StateType stateType = (StateType) row.get(1);
        	Long count = (Long) row.get(2);

        	countPerStateType.put(stateType, count); //legg inn sum for denne meldingsTypen
        	totalCounts.put(indicator, countPerStateType);
        }
        return totalCounts;
	}


	public List<StatusIndicatorLogEntryDTO> listStatusIndicatorWithLogEntry(){
		setSelectFromClause("select new no.goodtech.vaadin.tabs.status.model.StatusIndicatorLogEntryDTO(sle.statusIndicator," +
				" sum(case when sle.stateType = 0 then 1 else 0 end)," +
				" sum(case when sle.stateType = 1 then 1 else 0 end)," +
				" sum(case when sle.stateType = 2 then 1 else 0 end)" +
				" ) " +
				"from StatusLogEntry sle ", "sle");
		setGroupByClause("sle.statusIndicator");

		List<StatusIndicatorLogEntryDTO> result = getRepository().list(this, StatusIndicatorLogEntryDTO.class);

		//legger inn evt. status-indikatorer som ikke har logget i aktuelt tidsrom, samt gjeldene loggeverdi
		for (IStatusLogger logger : Globals.getStatusLoggerRepository().getStatusLoggers()) {
			boolean exists = false;
			final StatusIndicatorStub indicator = logger.getStatusIndicator();
			for (StatusIndicatorLogEntryDTO dto : result){
				if (dto.getStatusIndicator() != null && dto.getStatusIndicator().getId().equals(indicator.getId())){
					dto.getStatusIndicator().setOk(indicator.isOk());
					dto.getStatusIndicator().setMessage(indicator.getMessage());
					dto.getStatusIndicator().setLastHeartbeatAt(indicator.getLastHeartbeatAt());
					exists = true;
					break;
				}
			}

			if (!exists){
				result.add(new StatusIndicatorLogEntryDTO(indicator.load(), 0L, 0L,0L));
			}
		}

		Collections.sort(result);
		Collections.reverse(result);

		return result;
	}
}
