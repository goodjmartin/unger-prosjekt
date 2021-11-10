package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyFinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Layout for properties
 */
public class PropertyGridLayout extends GridLayout {
    private HashMap<Property, Field<?>> propertyFieldMap = new HashMap<Property, Field<?>>();

    public PropertyGridLayout() {
        super(1, 2);
    }

    public void refreshPropertyGridLayout() {
        propertyFieldMap = new HashMap<Property, Field<?>>();
        PropertyFinder propertyFinder = new PropertyFinder();
        List<Property> properties = propertyFinder.loadList();
        for (Property property : properties) {
            PropertyAndValueBean propertyAndValueBean = new PropertyAndValueBean(property);
            Field<?> propertyField = propertyAndValueBean.getPropertyValueField();
            propertyField.setCaption(property.getId());
            propertyField.setWidth("100%");
            if(propertyField instanceof ComboBox){
                ((ComboBox)propertyField).setNullSelectionAllowed(true);
            }
            addComponent(propertyField);
            propertyFieldMap.put(property, propertyField);
        }
    }

    /**
     * Returnerer hvilke egenskapsfiltre som er valgt
     *
     * @return egenskapen og dens verdi
     */
    public Map<Property, String> getPropertyFilters() {
        Map<Property, String> filter = new HashMap<Property, String>();
        for (Map.Entry<Property, Field<?>> entry : propertyFieldMap.entrySet()) {
            Property property = entry.getKey();
            Field<?> component = entry.getValue();
            if (component.getValue() != null && !String.valueOf(component.getValue()).isEmpty()) {
                if (property.getDataType().equals(Boolean.class)) {
                    if ((Boolean)component.getValue()){
                        filter.put(property, "1.0");
                    }
                    else
                    {
                        filter.put(property, "0.0");
                    }
                } else {
                    filter.put(property, String.valueOf(component.getValue()));
                }
            }
        }
        return filter;
    }


    public void emptySearchFields() {
        for (Component component : this) {
            Field<?> propertyField = (Field<?>) component;
            propertyField.setValue(null);
        }
    }
}
