package no.goodtech.vaadin.properties.ui.table;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.formatting.BooleanObjectToStringConverter;
import no.goodtech.vaadin.formatting.BooleanToStringConverter;
import no.goodtech.vaadin.formatting.PlainDoubleToStringConverter;
import no.goodtech.vaadin.formatting.StringToLongConverter;
import no.goodtech.vaadin.linkField.LinkField;
import no.goodtech.vaadin.properties.model.Property;
import no.goodtech.vaadin.properties.model.PropertyClass;
import no.goodtech.vaadin.properties.model.PropertyMembership;
import no.goodtech.vaadin.properties.model.PropertyStub;
import no.goodtech.vaadin.properties.model.PropertyValue;
import no.goodtech.vaadin.properties.model.PropertyValueFinder;
import no.goodtech.vaadin.properties.ui.IPropertyValueChangeListener;
import no.goodtech.vaadin.properties.ui.Texts;
import no.goodtech.vaadin.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Bønne brukt som datagrunnlag for egenskap tabeller.
 * Klassen inneholder en property som ikke kan være nul
 * Propertyvalue blir enten satt ved opprettelse av bønnen, eller endring på propertyValueField
 * InheritedValueMap som blir satt ved opprettelse av bønnen hvis ønskelig
 * inherited, som sjekkes av Vaadin for å vise rett verdi for inherited kolonnen
 * propertyValueField som er feltet som vises verdien for egenskapen.
 * <p/>
 * Hver bønne inneholder maksimalt en instans av property eller propertyvalue
 */
public class PropertyAndValueBean {
	private static final Locale LOCALE = new Locale("no", "NO");
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private volatile IPropertyValueChangeListener propertyValueChangeListener;
	private Class<?> booleanRepresentation = CheckBox.class;
	private boolean showTooltip = true;
	
	/**
	 * Navn på felter. Disse kan du bruke til å vise/skjule spesifikke felter
	 */
	public static final class Fields {
		public static final String PROPERTY_PREFIX = "property.";
		public static final String PROPERTY_VALUE_PREFIX = "propertyValue.";

		/**
		 * Feltet som representerer egenskapen
		 * @see PropertyAndValueBean#getProperty()
		 */
		public static final String PROPERTY = "property";

		/**
		 * Verdien som tekst
		 * @see PropertyAndValueBean#getPropertyValue()
		 */
		public static final String VALUE_TEXT = "propertyValue";

		/**
		 * Et felt for å redigere verdien
		 * @see PropertyAndValueBean#getPropertyValueField()
		 */
		public static final String VALUE_FIELD = "propertyValueField";

		/**
		 * Arvet verdi som tekst
		 * @see PropertyAndValueBean#getInheritedPropertyValue()
		 */
		public static final String VALUE_INHERITED_TEXT = "inheritedPropertyValue";

		/**
		 * Om verdien er arvet eller ikke
		 * @see PropertyAndValueBean#getInherited()
		 */
		public static final String INHERITED = "inherited";

		private static List<String> LOCAL_FIELDS = new ArrayList<String>(Arrays.asList(PROPERTY, VALUE_TEXT, VALUE_FIELD, VALUE_INHERITED_TEXT, INHERITED, PROPERTY_VALUE_PREFIX + PropertyValue.Fields.DESCRIPTION));

		/**
		 * Felter som hører til {@link Property}, blir prefikset med ".property"
		 */
		public static String get(Object name) {
			if (LOCAL_FIELDS.contains(name)) {
				return (String) name;
			} else if (Property.Fields.getAll().contains(name)) {
				return PROPERTY_PREFIX + name;
			} else {
				throw new IllegalArgumentException("Fant ikke flg. felt: '" + name + "'");
			}
		}

		/**
		 * @return alle tilgjengelige feltnavn
		 */
		public static List<String> getAll() {
			List<String> fields = new ArrayList<String>();
			for (String field : Property.Fields.getAll()) {
				fields.add(PROPERTY_PREFIX + field);
			}
			fields.addAll(LOCAL_FIELDS);
			return fields;
		}
	}

	private final PropertyStub property;
	private PropertyClass propertyClass;
	private PropertyValue propertyValue;
	private PropertyValue inheritedPropertyValue = null;
	private PlainDoubleToStringConverter plainDoubleToStringConverter = new PlainDoubleToStringConverter();
	private StringToLongConverter simpleStringToLongConverter = new StringToLongConverter();

	private boolean readOnly = false;

	public PropertyAndValueBean(PropertyStub property) {
		this.property = property;
		this.propertyClass = null;
	}

	public PropertyStub getProperty() {
		return property;
	}


	public PropertyValue getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(PropertyValue propertyValue) {
		this.propertyValue = propertyValue;
	}

	public PropertyValue getInheritedPropertyValue() {
		return inheritedPropertyValue;
	}

	public void setInheritedPropertyValue(PropertyValue inheritedPropertyValue) {
		this.inheritedPropertyValue = inheritedPropertyValue;
	}

	public void setPropertyClass(PropertyClass propertyClass) {
		this.propertyClass = propertyClass;
	}

	public PropertyClass getPropertyClass() {
		return propertyClass;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		Field<?> field = getPropertyValueField();
		if(getPropertyValueField() != null){
			field.setReadOnly(readOnly);
		}
	}

	/**
	 * Leses av Vaadin ved oppfriskning av tabellen(Ids must exist in the Container or as a generated column)
	 * Sjekker om inheritedValueMap inneholder pk til nøkkelen,
	 * hvis dette er tilfellet gjøres følgende:
	 * Sjekk at propertyValue ikke er null,
	 * hvis propertyValue ikke er null, og arvet verdi er lik propertyvalue sin verdi
	 * returner true, da det er arvet en propertyValue, og verdien er lik.
	 * @return true hvis arvet, false ellers.
	 */
	public String getInherited() {
		if (inheritedPropertyValue != null && inheritedPropertyValue.getProperty().getPk().equals(property.getPk())) {
			if (propertyValue != null) {
				if (inheritedPropertyValue.getValue().equals(propertyValue.getValue())) {
					if (inheritedPropertyValue.getOwnerPk().equals(propertyValue.getOwnerPk())) {
						return "Ja";
					}
				}
			}
		}
		return "Nei";
	}

	/**
	 * Brukes av Vaadin for å hente propertyValueField.
	 * Hvert felt blir tildelt en validator og en valuechangelistener.
	 * Typen felt som blir vist er definert opp i createField metoden
	 * @return felt som vises i tabellen
	 */
	public Field<?> getPropertyValueField() {
		final Field<?> propertyValueField = createField();
		if (showTooltip && property.getDescription() != null) {
			if (propertyValueField instanceof AbstractComponent) {
				AbstractComponent component = (AbstractComponent) propertyValueField;
				component.setDescription(property.getDescription());
			}
		}
		
		/**
		 * Hvis verdien skrevet inn i feltet ikke er null blir den validert mot property sin
		 * validate funksjon. Hvis det returneres en feilmeldig fra property.validate blir den vist
		 * i feltet.
		 */

		propertyValueField.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				PropertyMembership propertyMembership = null;
				if (propertyClass != null) {
					propertyMembership = propertyClass.getPropertyMembership(property);
				}
				// Field is readonly when membership is editable = false
				if (propertyMembership != null) {
					propertyValueField.setReadOnly(!propertyMembership.isEditable());
				}else{
					propertyValueField.setReadOnly(readOnly);
				}
				final boolean required = propertyMembership != null && propertyMembership.isRequired();
				propertyValueField.setRequired(required);

				if (propertyValueField instanceof LinkField) {
					LinkField linkField = (LinkField) propertyValueField;
					value = linkField.getValue();
				}

				if (value != null) {
					String message = property.validate(value.toString());
					if (message != null) {
						throw new InvalidValueException(message);
					}
				} else if (required) {
					throw new InvalidValueException("Obligatorisk felt!");
				}
			}
		});

		/**
		 * Ved valuechange på propertyValueField blir feltet først validert(se validate metoden).
		 * Hvis verdien er godkjent blir det kontrollert om det dreier seg om en propertyvalue, hvis
		 * det ikke er opprettet noen propertyValue blir den opprettet her, fordi det da er et felt for en
		 * property som har blitt endret.
		 */
		propertyValueField.addValueChangeListener(new com.vaadin.v7.data.Property.ValueChangeListener() {
			@Override
			//Opprett propertyvalue hvis den ikke finnes(skjer hvis felt endres når propertyvalue er null)
			public void valueChange(com.vaadin.v7.data.Property.ValueChangeEvent event) {
				if (propertyValueField.isValid()) {
					//Hvis propertyValue ikke er satt, opprett ny og sett property til denne property
					if (propertyValue == null) {
						propertyValue = new PropertyValue();
						propertyValue.setProperty(property);
					}
					propertyValue.setValueAsObject(event.getProperty().getValue());
				}
			}
		});
		//Returner feltet, med valuechangelistener og validator initialisert
		return propertyValueField;
	}

	private Field<?> createField() {
		Class<?> dataType = property.getDataType();
		//Hent ut propertyvalue sin value, gitt at propertyvalue finnes.
		Object propVal = propertyValue != null ? propertyValue.getValue() : null;

		//Hvis property har options, så lag en ny select.
		boolean hasOptions = property.getOptionsList().size() > 0;
		if (hasOptions) {
			return createPropertyOptionsComboboxField(propVal, property.getOptionsList());
		}

		if (String.class.isAssignableFrom(dataType) || Long.class.isAssignableFrom(dataType) || Double.class.isAssignableFrom(dataType)) {
			return createTextField(propVal, dataType); //Hvis datatypen er heltall, desimaltall eller tekst, opprett tekstfelt
		}

		if (Boolean.class.isAssignableFrom(dataType)) {
			if (ComboBox.class.equals(booleanRepresentation) || NativeSelect.class.equals(booleanRepresentation))
				return createBooleanComboBoxField(propVal); //Hvis datatypen er av typen Boolean, opprett Boolean select
			else if (OptionGroup.class.equals(booleanRepresentation))
				return createBooleanOptionGroupField(propVal);
			return createCheckBoxField(propVal);
		}

		if (Date.class.isAssignableFrom(dataType)) {
			return createDateField(propVal);
		}

		logger.warn("Returning null for datatype: " + property.getDataType() + ", property PK: " + property.getPk());
		return null;
	}

	private Field<?> createBooleanOptionGroupField(Object propVal) {
		OptionGroup optionGroup = new OptionGroup();
		configureBooleanSelect(optionGroup);
		optionGroup.setMultiSelect(false);
		optionGroup.addStyleName("horizontal");
		optionGroup.setValue(propVal);
		return optionGroup;
	}

	private void configureBooleanSelect(AbstractSelect select) {
		select.setNullSelectionAllowed(false);
		select.addItem(false);
		select.addItem(true);
		select.setItemCaption(false, Texts.get("propertyValueFormatter.booleanValue.false"));
		select.setItemCaption(true, Texts.get("propertyValueFormatter.booleanValue.true"));
		select.setImmediate(true);
		select.setWidth("100%");
		select.setConverter(new BooleanObjectToStringConverter());
	}

	private Field<?> createDateField(Object propVal) {
		DateField dateField = new DateField();
		dateField.setImmediate(true);
		dateField.setLocale(LOCALE);
		dateField.setResolution(Resolution.MINUTE);
		
		if (property.getFormatPattern() != null)
			dateField.setDateFormat(property.getFormatPattern());
		else
			dateField.setDateFormat(Utils.DATETIME_FORMAT);
		
		dateField.setValue((Date) propVal);
		dateField.setWidth("100%");
		return dateField;
	}

	/**
	 * Oppretter komboboks med true false verdier
	 * @param value verdi, hvis propertyvalue er satt
	 * @return combobox med verdiene satt
	 */
	private Field<?> createBooleanComboBoxField(Object value) {
		ComboBox booleanComboBox = new ComboBox();
		booleanComboBox.setNullSelectionAllowed(false);
		booleanComboBox.addItem(false);
		booleanComboBox.addItem(true);
		booleanComboBox.setItemCaption(true, "Ja");
		booleanComboBox.setItemCaption(false, "Nei");
		booleanComboBox.select(value);
		booleanComboBox.setImmediate(true);
		booleanComboBox.setWidth("100%");
		booleanComboBox.setConverter(new BooleanObjectToStringConverter());
		return booleanComboBox;
	}

	/**
	 * Creates a yes/no field
	 * @param value verdi, hvis propertyvalue er satt
	 * @return checkbox with the right value; true = checked, false = unchecked
	 */
	private Field<?> createCheckBoxField(Object value) {
		CheckBox booleanComboBox = new CheckBox();
		booleanComboBox.setValue((Boolean) value);
		booleanComboBox.setImmediate(true);
		booleanComboBox.setWidth("100%");
		booleanComboBox.setConverter(new BooleanToStringConverter());
		return booleanComboBox;
	}

	/**
	 * Oppretter komboboks med verdier fra property sine options, og
	 * setter valgt verdi til gitt verdi
	 * @param value   verdien til PropertyValue sin Value, eller null hvis propertyvalue er null.
	 * @param options options til Property
	 * @return propertyOptionsComboBox, combobox som inneholder alle options i Property
	 */
	private Field<?> createPropertyOptionsComboboxField(Object value, List<String> options) {
		ComboBox propertyOptionsComboBox = new ComboBox();
		propertyOptionsComboBox.setNullSelectionAllowed(false);
		for (Object option : options) {
			propertyOptionsComboBox.addItem(option);
		}
		propertyOptionsComboBox.select(value);
		propertyOptionsComboBox.setImmediate(true);
		propertyOptionsComboBox.setWidth("100%");
		return propertyOptionsComboBox;
	}

	/**
	 * Oppretter tekstfelt og setter verdi til value
	 * @param value verdien til PropertyValue sin Value, eller null hvis propertyvalue er null.
	 * @return tekstfelt med eventuel verdi satt
	 */
	private Field<?> createTextField(Object value, Class<?> dataType) {
		if (value != null && Utils.convertStringOk(value.toString())) {
			return new LinkField(value.toString(),propertyValue.getProperty().getFormatPattern());
		}

		TextField field = new TextField();
		if (dataType == Double.class) {
			field.setConverter(plainDoubleToStringConverter);
		} else if (dataType == Long.class) {
			field.setConverter(simpleStringToLongConverter);
		}

		field.setConvertedValue(value);
		field.setNullRepresentation("");
		field.setImmediate(true);
		field.setWidth("100%");
		return field;
	}

	/**
	 * Kontrollerer og lagrer propertyValue.
	 * <p/>
	 * Ved lagring benyttes inheritedValueMap for å kontrollere om verdien er den samme som eventuell arvet verdi, hvis verdien
	 * er den samme blir det ikke opprettet noen ny verdi.
	 * <p/>
	 * Hvis den er forskjellig fra arvet verdi kontrolleres det om det eksisterer en allerede lagret egenskapsverdi(som betyr at det allerede
	 * finnes en overskrevet egenskapsverdi for den arvede egenskapsverdien), hvis dette er tilfellet blir den
	 * overskrevne egenskapsverdien oppdatert med ny verdi.
	 * <p/>
	 * Hvis den er forskjellig fra arvet verdi, og det ikke allerede eksisterer noen overskrevet egenskapsverdi for den
	 * arvede verdien blir det opprettet en ny egenskapsverdi
	 * <p/>
	 * Hvis det ikke finnes noen arvet egenskapsverdi blir propertyvalue lagret med ny verdi.
	 * (Enten er propertyvalue da satt ved opprettelse av denne bønnen, og hvis den ikke var satt blir den
	 * opprettet ved endring av verdi på feltet.)
	 */
	public void saveNewPropertyValue(Long ownerPk, Class<?> ownerClass) {
		//Kontroller først at propertyValue ikke er null(som betyr at verdi er satt, enten via felt eller fra init)
		if (propertyValue != null) {
			//Sjekk at propertyValue sin value ikke er null og propertyValue enten er ny eller at verdien er ulik den originale verdien
			if (propertyValue.getValue() != null && (propertyValue.isNew() || propertyValue.load().getValue()==null || !propertyValue.load().getValue().equals(propertyValue.getValue()))) {
				//Sjekk om det finnes en verdi som arves gitt at den ikke er overskrevet for denne property
				if (inheritedPropertyValue != null && inheritedPropertyValue.getProperty().getPk().equals(property.getPk())) {
					//Det finnes en arvet verdi. opprett finder for å sjekke om det finnes overskrevet verdi
					PropertyValueFinder propertyValueFinder = new PropertyValueFinder();
					propertyValueFinder.setOwnerClass(ownerClass);
					propertyValueFinder.setProperty(property);
					ArrayList<Long> longs = new ArrayList<Long>();
					longs.add(ownerPk);
					propertyValueFinder.setOwnerPk(longs);
					// Sjekk om det allerede finnes en propertyValue som overskriver den arvede verdien.
					// (samme ownerPk, ownerClass som parameteren for lagre metoden
					if (!propertyValueFinder.exists()) {
						//Det finnes ingen overskrevet verdi, opprett ny PropertyValue i tilfelle det skal lagres ny.
						PropertyValue propertyValue = new PropertyValue();
						propertyValue.setProperty(property);
						propertyValue.setOwnerClass(ownerClass);
						propertyValue.setOwnerPk(ownerPk);
						propertyValue.setValueAsObject(this.propertyValue.getValue());
						//Hent den arvede verdien
						Object originalPropertyValue = inheritedPropertyValue.getValue();
						//Kontroller den arvede verdien mot propertyValue sin verdi.
						if (!originalPropertyValue.equals(propertyValue.getValue())) {
							//Hvis verdien er forskjellig lagres den nye PropertyValuen
							this.propertyValue = propertyValue.save();
							logger.debug("Lagrer ny PropertyValue. OwnerPK: " + ownerPk + "' OwnerClass: '" + ownerClass +
									"' Verdi: '" + propertyValue.getValue() + "'" + "Verdi som ikke arves: " + originalPropertyValue);
						}
						//Hvis verdien ikke er forskjellig blir det ikke lagret noen ny PropertyValue
						else {
							logger.debug("Bruker arvet egenskapsverdi for:'" + propertyValue.getValue() + "'");
						}
					}
					//det finnes en propertyValue som overskriver den arvede verdien.
					else {
						//Hent den arvede verdien.
						Object originalPropertyValue = inheritedPropertyValue.getValue();
						//Hent propertyvalue som overskriver den arvede verdien
						PropertyValue overriddenPropertyValue = propertyValueFinder.find();
						//Hvis arvet verdi er lik propertyValue sin verdi så slett propertyvalue og bruk arv
						if (originalPropertyValue.equals(propertyValue.getValue())) {
							logger.debug("Bruker arvet egenskapsverdi for:'" + propertyValue.getValue() + "' " +
									"propertyValue med pk: " + overriddenPropertyValue.getPk() + " slettes.");
							overriddenPropertyValue.delete();
							ArrayList<Long> inheritedPropertyValuePk = new ArrayList<Long>();
							inheritedPropertyValuePk.add(inheritedPropertyValue.getPk());
							this.propertyValue = new PropertyValueFinder().setPk(inheritedPropertyValuePk).find().load();
						}
						//Hvis verdien ikke er lik propertyvalue sin verdi, så lagre endringen på den overskrevne
						//PropertyValuen.
						else {
							overriddenPropertyValue.setValueAsObject(this.propertyValue.getValue());
							this.propertyValue = overriddenPropertyValue.save();
							logger.debug("Lagrer endring på overskrevet PropertyValue. OwnerPK: " + ownerPk + "' OwnerClass: '" + ownerClass +
									"' Verdi: '" + propertyValue.getValue() + "'" + "Verdi som ikke arves: " + originalPropertyValue);
						}
					}
				}
				//Hvis propertyvalue ikke er arvet settes eierPk og eierKlasse og den lagres.
				else {
					logger.debug("Lagrer propertyvalue som ikke er arvet, ownerPk: '" + ownerPk + "' ownerclass: '" +
							ownerClass + "' verdi: '" + propertyValue.getValue() + "'");
					propertyValue.setOwnerPk(ownerPk);
					propertyValue.setOwnerClass(ownerClass);
					if (propertyValue.isNew()) {
						this.propertyValue = propertyValue.save();
						if (propertyValueChangeListener != null) {
							propertyValueChangeListener.propertyValueCreated(propertyValue);
						}
					} else {
						if (propertyValueChangeListener != null) {
							//Called before save so that the original value can be retrieved with propertyValue.load():
							propertyValueChangeListener.propertyValueChanged(propertyValue);
						}
						this.propertyValue = propertyValue.save();
					}
				}
			}
		}
	}

	/**
	 * Denne blir brukt ved forsøk på å slette en propertyValue
	 * Hvis det finnes en arvet propertyValue blir denne PropertyValue
	 * satt til den arvede verdien.
	 * Ved å gjøre det blir det enten aldri lagret en ny PropertyValue(hvis den ikke eksisterer enda), ellers
	 * blir den slettet ved lagring
	 * @return false hvis det finnes en arvet verdi(raden skal da ikke fjernes), true hvis det ikke finnes arvet verdi.
	 */
	public boolean canBeRemoved() {
		if (inheritedPropertyValue != null && inheritedPropertyValue.getProperty().getPk().equals(property.getPk())) {
			ArrayList<Long> inheritedPropertyValuePk = new ArrayList<Long>();
			inheritedPropertyValuePk.add(inheritedPropertyValue.getPk());
			this.propertyValue = new PropertyValueFinder().setPk(inheritedPropertyValuePk).find().load();
			return false;
		} else {
			if (propertyValueChangeListener != null) {
				propertyValueChangeListener.propertyValueDeleted(propertyValue);
			}
			propertyValue.delete();
			return true;
		}
	}

	/**
	 * Register a listener for handling creation,changes and deletion of propertyValues
	 * @param propertyValueChangeListener listener used for handling creation,changes and deletion of propertyValues
	 */
	public void setPropertyValueChangeListener(final IPropertyValueChangeListener propertyValueChangeListener) {
		this.propertyValueChangeListener = propertyValueChangeListener;
	}

	/**
	 * @return shows property description as tooltip, default = true
	 */
	public boolean isShowTooltip() {
		return showTooltip;
	}

	/**
	 * @param showTooltip true = show property description as tooltip, false = do not show tooltip
	 */
	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

	/**
	 * @return how to show boolean values
	 * @see #setBooleanRepresentation(Class)
	 */
	public Class<?> getBooleanRepresentation() {
		return booleanRepresentation;
	}

	/**
	 * @param booleanRepresentation how to show boolean values. Use {@link OptionGroup}, {@link ComboBox}/{@link NativeSelect} or {@link CheckBox}. null => CheckBox
	 */
	public void setBooleanRepresentation(Class<?> booleanRepresentation) {
		this.booleanRepresentation = booleanRepresentation;
	}
}
