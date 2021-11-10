package no.goodtech.vaadin.lists.v7;

import com.vaadin.v7.data.Container.Indexed;

import java.util.List;

@Deprecated
public interface MessyContainer<T> extends Indexed {

	void refresh(List<? extends T> raws);
}
