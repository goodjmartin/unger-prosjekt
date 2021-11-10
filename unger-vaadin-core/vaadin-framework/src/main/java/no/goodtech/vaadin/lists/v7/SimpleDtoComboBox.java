package no.goodtech.vaadin.lists.v7;

import no.goodtech.persistence.search.ISimpleDto;

@Deprecated
public class SimpleDtoComboBox extends MessyComboBox<ISimpleDto> {

	public SimpleDtoComboBox(String caption) {
		super(caption);
	}

	public void setValue(String dtoId){
		this.select(dtoId);
	}

	@Override
	protected String getId(ISimpleDto item) {
		if (item.getId() != null)
			return item.getId();
		return String.valueOf(item.getPk());
	}

	@Override
	protected String getName(ISimpleDto item) {
		if (item.getName() != null)
			return item.getName();
		return getId(item);
	}
}
