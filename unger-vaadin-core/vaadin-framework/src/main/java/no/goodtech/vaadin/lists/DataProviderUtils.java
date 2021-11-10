package no.goodtech.vaadin.lists;


import com.vaadin.data.provider.BackEndDataProvider;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;

public class DataProviderUtils {

	/**
	 * Create a data provider that uses given finder to deliver data to a grid on demand
	 * @param <T> the entity type
	 * @param finder the finder that fetches the data
	 * @return the data provider
	 * @deprecated you should provide a {@link RowCountProvider}, please use {@link #createDataProvider(AbstractFinder, RowCountProvider)}
	 */
	public static <T extends EntityStub<?>> BackEndDataProvider createDataProvider(AbstractFinder finder) {
		return createDataProvider(finder, AbstractFinder::list, null);

	}

	/**
	 * Create a data provider that uses given finder to deliver data to a grid on demand
	 * @param finder the finder that fetches the data
	 * @param listFunction a functional interface to define a custom made list function
	 * @param <T> the entity type
	 * @return the data provider
	 * @deprecated you should provide a {@link RowCountProvider}, please use {@link #createDataProvider(AbstractFinder, IListFunction, RowCountProvider)}
	 */
	public static <T extends EntityStub<?>, F extends AbstractFinder> BackEndDataProvider<T, Void> createDataProvider(F finder, IListFunction<T, F> listFunction) {
		return new MessyLazyDataProvider<>(finder, listFunction, null);
	}

	/**
	 * Create a data provider that uses given finder to deliver data to a grid on demand
	 * @param <T> the entity type
	 * @param finder the finder that fetches the data
	 * @param rowCountProvider will give you new row count when filter changes
	 * @return the data provider
	 */
	public static <T extends EntityStub<?>, F extends AbstractFinder> MessyLazyDataProvider createDataProvider(F finder, RowCountProvider rowCountProvider) {
		//noinspection unchecked
		return new MessyLazyDataProvider<>(finder, AbstractFinder::list, rowCountProvider);
	}

	/**
	 * Create a data provider that uses given finder to deliver data to a grid on demand
	 * @param <T> the entity type
	 * @param finder the finder that fetches the data
	 * @param listFunction a functional interface to define a custom made list function
	 * @param rowCountProvider will give you new row count when filter changes
	 * @return the data provider
	 */
	public static <T extends EntityStub<?>, F extends AbstractFinder> BackEndDataProvider<T, Void> createDataProvider(F finder, IListFunction<T, F> listFunction, RowCountProvider rowCountProvider) {
		return new MessyLazyDataProvider<>(finder, listFunction, rowCountProvider);
	}
}
