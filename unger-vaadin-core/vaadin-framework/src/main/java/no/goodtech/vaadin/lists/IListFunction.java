package no.goodtech.vaadin.lists;

import com.vaadin.server.SerializableFunction;
import no.goodtech.persistence.jpa.AbstractFinder;

import java.util.List;

@FunctionalInterface
public interface IListFunction<T, F extends AbstractFinder> extends SerializableFunction<F, List<T>> {

	@Override
	List<T> apply(F finder);

}
