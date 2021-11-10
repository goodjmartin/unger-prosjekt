package no.goodtech.vaadin.properties.ui;


import com.vaadin.addon.tableexport.v7.ExportableColumnGenerator;
import com.vaadin.ui.Link;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.Table;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.linkField.LinkField;
import no.goodtech.vaadin.properties.model.IPropertyOwner;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.model.PropertyValueStub;
import no.goodtech.vaadin.properties.model.PropertyValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generate cell values for each property value that belongs to the property owner (row).
 * A property owner can either be an {@link IPropertyOwner} or an {@link EntityStub}
 * Usage: {@link #addColumns(Table)}
 */
public class PropertyValueColumnGenerator implements ExportableColumnGenerator {

	public interface IOwnerPkProvider {
		Long getOwnerPk(Object itemId);
	}
	
	private final PropertyValues propertyValues;
	private final IOwnerPkProvider ownerPkProvider;
	private final String version;

	/**
	 * Create the column generator and show only properties configured to show for given group
	 * The column generator will load the property values for you
	 * @param propertyClassId the property group that configures which properties to show
	 * @param owners the owners of the properties (usually the rows of the table with a BeanItemContainer)
	 * @see PropertyMembership#isShowInCrosstab()
	 */
	public PropertyValueColumnGenerator(String propertyClassId, List<? extends EntityStub<?>> owners) {
		this(new PropertyValueFinder().setOwners(owners).setShowInCrosstab(propertyClassId).getPropertyValues(), null);
	}

	/**
	 * Create the column generator
	 * @param propertyValues the property values to display
	 */
	public PropertyValueColumnGenerator(PropertyValues propertyValues) {
		this(propertyValues, null);
	}

	/**
	 * Create the column generator
	 * Use this when you need to display property values with a different owner than what the table bean contains
	 * @see #PropertyValueColumnGenerator(PropertyValues, IOwnerPkProvider, String)
	 */
	public PropertyValueColumnGenerator(PropertyValues propertyValues, IOwnerPkProvider ownerPkProvider) {
		this(propertyValues, ownerPkProvider, null);
	}

	/**
	 * Create the column generator
	 * @param propertyValues the property values to display
	 * @param ownerPkProvider if the properties to show does not belong to the main object in the row but a nested object, 
	 * you may provide how to fetch the right property owner for each row through this interface
	 * @param version TODO: What is this for? Is it a qualifier to avoid naming conflicts if you have multiple owners that share same properties? 
	 */
	public PropertyValueColumnGenerator(PropertyValues propertyValues, IOwnerPkProvider ownerPkProvider, String version) {
		this.propertyValues = propertyValues;
		this.ownerPkProvider = ownerPkProvider;
		this.version = version;
	}

	/**
	 * Add all properties as columns to the table.
	 * @param table to get generated columns
	 * @return a list of columnids
	 */
	public List<PropertyValueColumn> addColumns(final Table table) {
		List<PropertyValueColumn> propertyValueColumns = new ArrayList<>();
		for (PropertyStub property : propertyValues.getProperties()) {
			PropertyValueColumn columnId = new PropertyValueColumn(property, version);
			removeExistingColumn(table, property, version);
			table.addGeneratedColumn(columnId, this);
			propertyValueColumns.add(columnId);
			table.setColumnHeader(columnId, columnId.getDefaultName());
		}
		return propertyValueColumns;
	}

	/**
	 * Removes existing propertyValueColumns
	 */
	private void removeExistingColumn(final Table table, PropertyStub property, String version){
		for (Object column : table.getVisibleColumns()){
			if(column instanceof PropertyValueColumn) {
				PropertyValueColumn propertyValueColumn = (PropertyValueColumn) column;
				if(propertyValueColumn.getProperty().getId().equals(property.getId()) &&
						Objects.equals(propertyValueColumn.getVersion(), version)){
					table.removeGeneratedColumn(column);
				}
			}
		}
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		PropertyValueStub propertyValue = getPropertyValue(itemId, columnId);
		if (propertyValue != null) {
			PropertyStub property = propertyValue.getProperty();
			if (property.getDataType().equals(Link.class) && propertyValue.getValue() != null) {
				final LinkField linkField = new LinkField(String.valueOf(propertyValue.getValue()), property.getFormatPattern());
				final Link link = linkField.getLink();
				return link;
			}
			return PropertyValueFormatter.formatValue(propertyValue, property);
		}
		return null;
	}


	private PropertyValueStub getPropertyValue(Object itemId, Object columnId) {
		PropertyValueColumn propertyValueColumn = (PropertyValueColumn) columnId;
		PropertyStub property = propertyValueColumn.getProperty();
		if (propertyValues.contains(propertyValueColumn.getProperty()) &&
				Objects.equals(propertyValueColumn.getVersion(), version)) {
			final Long ownerPk = getOwnerPk(itemId);

			return propertyValues.getValue(ownerPk, property.getId());
		}
		return null;
	}

	private Long getOwnerPk(Object itemId) {
		if (ownerPkProvider != null)
			return ownerPkProvider.getOwnerPk(itemId);
		
		if (IPropertyOwner.class.isAssignableFrom(itemId.getClass())) {
			IPropertyOwner owner = (IPropertyOwner) itemId;
			return owner.getPk();
		}
		if (EntityStub.class.isAssignableFrom(itemId.getClass())) {
			EntityStub<?> owner = (EntityStub<?>) itemId;
			return owner.getPk();
		}
		throw new IllegalArgumentException(String.format("No property owner in this row: %s. The row bean must either implement IPropertyOwner or EntityStub or you must provide an IOwnerPkProvider", itemId));
	}

	@Override
	public Property getGeneratedProperty(Object itemId, Object columnId) {
		PropertyValueStub propertyValue = getPropertyValue(itemId, columnId);
		if (propertyValue != null) {
			final Class<?> dataType = propertyValue.getProperty().getDataType();
			return new ObjectProperty(propertyValue.getValue(), dataType);
		}
		return null;
	}

	//TODO: Should have a better way to return the right datatype for each column, in order to get numbers instead of text in Excel
	@Override
	public Class<?> getType() {
		return String.class;
	}
}
