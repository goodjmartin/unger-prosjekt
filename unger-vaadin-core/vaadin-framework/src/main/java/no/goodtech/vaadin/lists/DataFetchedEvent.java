package no.goodtech.vaadin.lists;

import com.vaadin.data.provider.DataChangeEvent;
import com.vaadin.data.provider.DataProvider;
import no.goodtech.persistence.entity.EntityStub;

import java.util.List;

public class DataFetchedEvent<T extends EntityStub<?>> extends DataChangeEvent<T> {

	private List<T> rows;

	/**
	 * Creates a new {@code DataChangeEvent} event originating from the given data provider
	 * @param source the data provider, not null
	 */
	public DataFetchedEvent(DataProvider<T, ?> source) {
		super(source);
	}

	/**
	 * Creates a new event originating from the given data provider
	 * @param source the data provider, not null
	 * @param rows last rows fetched
	 */
	public DataFetchedEvent(DataProvider<T, ?> source, List<T> rows) {
		super(source);
		this.rows = rows;
	}

	/**
	 * @return the last rows fetched from backend
	 */
	public List<T> getRows() {
		return rows;
	}
}
