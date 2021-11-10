package no.goodtech.vaadin.formatting;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.DefaultConverterFactory;

public class GoodtechConverterFactory extends DefaultConverterFactory {

	@Override
	public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        if (String.class == presentationType && Double.class == modelType)
        	return (Converter<PRESENTATION, MODEL>) new PlainDoubleToStringConverter();

        if (String.class == presentationType && Long.class == modelType)
        	return (Converter<PRESENTATION, MODEL>) new StringToLongConverter();
		
		return super.createConverter(presentationType, modelType);
	}
}


