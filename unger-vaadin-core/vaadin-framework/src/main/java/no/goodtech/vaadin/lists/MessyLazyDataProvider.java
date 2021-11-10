package no.goodtech.vaadin.lists;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.SortOrder;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Will fetch data for components supporting lazy loading
 * @param <T> the the entity type
 * @param <F> the finder that fetches the data
 * Will also provide listeners with fetched rows, see {@link #addDataProviderListener(DataProviderListener)}
 *           and a {@link RowCountProvider}
 */
public class MessyLazyDataProvider<T extends EntityStub<?>, F extends AbstractFinder> extends AbstractBackEndDataProvider<T, Void> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(MessyLazyDataProvider.class);
	protected final F finder;
	protected final IListFunction<T, F> listFunction;
	protected Set<DataProviderListener> dataProviderListeners = new LinkedHashSet<>();
	protected Set<RowCountProvider> rowCountProviders = new LinkedHashSet<>();

	/**
	 * Create a lazy dataprovider with given finder
	 * @param finder the finder to use to query the backend (e.g. database)
	 * @param listFunction the function to run the query, usually Finder.list()
	 * @param rowCountProvider provide this if you need to get notified about how many rows that matched the query
	 */
	public MessyLazyDataProvider(F finder, IListFunction<T, F> listFunction, final RowCountProvider rowCountProvider) {
		this.finder = finder;
		if (listFunction == null) {
			this.listFunction = AbstractFinder::list;
		} else {
			this.listFunction = listFunction;
		}
		if (rowCountProvider != null) {
			rowCountProviders.add(rowCountProvider);
		}
	}

	@Override
	protected Stream<T> fetchFromBackEnd(Query<T, Void> query) {
		if (query.getSortOrders().size() > 0) {
			//finder.removeSortCriteria();
		}
		for (SortOrder<String> sortOrder : query.getSortOrders()) {
			//finder.addSortCriteria(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING);
		}
		if (finder.getMaxResults() != 0 || query.getLimit() > 0) {
			finder.setMaxResults(query.getLimit());
		}
		if (finder.getPosition() != 0 || query.getOffset() > 0) {
			finder.setPosition(query.getOffset());
		}
		final List<T> rows = listFunction.apply(finder);
		LOGGER.debug("{} returned {} rows, offset = {}, limit = {}, filters: {}", finder.getClass().getSimpleName(), rows.size(), query.getOffset(), query.getLimit(), finder.toString());
		for (DataProviderListener listener : dataProviderListeners) {
			//noinspection unchecked
			listener.onDataChange(new DataFetchedEvent(this, rows)); //notify the clients about the new data fetched
		}
		return rows.stream();
	}

	@Override
	protected int sizeInBackEnd(Query<T, Void> query) {
		final int count = finder.count();
		LOGGER.debug("{} count returned {}, filters: {}", finder.getClass().getSimpleName(), count, finder.toString());
		for (RowCountProvider listener : rowCountProviders) {
			listener.newCountReceived(count);
		}
		return count;
	}

	@Override
	public Registration addDataProviderListener(DataProviderListener<T> listener) {
		dataProviderListeners.add(listener);
		return (Registration) () -> dataProviderListeners.remove(listener);
	}

	/**
	 * @param provider provide this if you need to get notified about how many rows that matched the query
	 */
	public Registration addRowCountProvider(RowCountProvider provider) {
		rowCountProviders.add(provider);
		return (Registration) () -> rowCountProviders.remove(provider);
	}
}

