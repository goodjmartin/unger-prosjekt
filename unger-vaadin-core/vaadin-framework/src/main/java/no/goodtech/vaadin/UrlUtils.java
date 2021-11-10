package no.goodtech.vaadin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import no.goodtech.vaadin.utils.Utils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

/**
 * Helper class for handling urls
 * TODO: move to package 'utils'
 */
public class UrlUtils {

	public static final String VIEW_PREFIX = "#!";
	public static final String DEFAULT_PARAMETER_DELIMITER = "/";
	public static final String DEFAULT_PARAMETER_KEY_VALUE_DELIMITER = "=";
	public static final String DEFAULT_PARAMETER_ARRAY_DELIMITER = ",";
	private final String parameters;
	private final String parameterDelimiter;
	private final String keyValueDelimiter;
	private final String parameterArrayDelimiter;

	/**
	 * Create utils with "/" as delimiter between parameters and "=" as delimiter between parameter parameter key and value 
	 */
	public UrlUtils() {
		this("");
	}

	/**
	 * Create utils to operate on given parameter url 
	 * Expects "/" as delimiter between parameters and "=" as delimiter between parameter parameter key and value 
	 */
	public UrlUtils(String parameters) {
		this(parameters, DEFAULT_PARAMETER_DELIMITER, DEFAULT_PARAMETER_KEY_VALUE_DELIMITER, DEFAULT_PARAMETER_ARRAY_DELIMITER);
	}
	
	/**
	 * Create utils to operate on the url in given event 
	 */
	public UrlUtils(ViewChangeEvent event) {
		this(event.getParameters());
	}

	public UrlUtils(String parameters, String parameterDelimiter, String parameterKeyValueDelimiter, String parameterArrayDelimiter) {
		this.parameters = parameters;
		this.parameterDelimiter = parameterDelimiter;
		this.keyValueDelimiter = parameterKeyValueDelimiter;
		this.parameterArrayDelimiter = parameterArrayDelimiter;
	}

	/**
	 * @param key the name of the parameter
	 * @return true if given parameter key exists in the parameter string, false if not
	 */
	public boolean isParameterPresent(String key) {
		return isParameterPresent(key, parameters);
	}
	
	/**
	 * @param key the name of the parameter
	 * @return true if given parameter key exists in the parameter string, false if not
	 * @deprecated please use {@link #isParameterPresent(String)} instead
	 */
	public boolean isParameterPresent(String key, String parameters) {
		if (parameters == null)
			return false;
		for (String parameter : parameters.split(parameterDelimiter)) {
			if (parameter.contains(keyValueDelimiter)) {
				final String[] split = parameter.split(keyValueDelimiter);
				if (split[0].equals(key)) {
					return true;
				}
			} else if (parameter.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return parameter value with given key / name
	 * @param key the name of the parameter
	 * @return the parameter string returned from {@link ViewChangeEvent#getParameters()}
	 */
	public String getParameter(String key) {
		return getParameter(key, parameters);
	}
	
	/**
	 * Return parameter value with given key / name
	 * @param key the name of the parameter
	 * @return the parameter string returned from {@link ViewChangeEvent#getParameters()}
	 * @deprecated please use {@link #getParameter(String)} instead
	 */
	public String getParameter(String key, String parameters) {
		if (parameters == null)
			return null;
		for (String parameter : parameters.split(parameterDelimiter)) {
			if (parameter.contains(keyValueDelimiter)) {
				final String[] split = parameter.split(keyValueDelimiter);
				if (split[0].equals(key) && split.length > 1) {
					final String value = split[1].trim();
					if (value.length() > 0) {
						if (value.equalsIgnoreCase("null"))
							return null;
						else
							return value;
					}
				}
			}
		}
		return null;
	}


	/**
	 * Fetches a map of parameterKey:parameterValue for the parameters specified in the url
	 */
	public Map<String, String> getParameterMap(){
		Map<String, String> parameterMap = new HashMap<>();
		for (String parameter : parameters.split(parameterDelimiter)) {
			if (parameter.contains(keyValueDelimiter)) {
				final String[] split = parameter.split(keyValueDelimiter);
				if (split.length >= 2) {
					parameterMap.put(split[0], split[1]);
				}
			}
		}
		return parameterMap;
	}

	/**
	 * Return date parameter value with given key / name.
	 * Shows a notification if not able to parse parameter
	 * @param key the name of the parameter
	 * @return the parameter returned from {@link ViewChangeEvent#getParameters()} or null if not found
	 */
	public Date getDateParameter(String key, String dateFormat) {
		final String dateParameter = getParameter(key);
		if (dateParameter != null) {
			try {
				final Date parse = new SimpleDateFormat(dateFormat).parse(dateParameter);
				return parse;
			} catch (ParseException e) {
				final String message = Utils.getText("urlUtils.illegalDateFormat");
				final String format = String.format(message, key, dateParameter, dateFormat);
				Notification.show(format);
			}			
		}
		return null;
	}

	/**
	 * Return parameter value with given key / name
	 * @param key the name of the parameter
	 * @return the parameter value or null if not found
	 * @deprecated please use {@link #getParameter(String)} instead
	 */
	public String getParameter(String key, ViewChangeEvent event) {
		return getParameter(key, event.getParameters());
	}

	/**
	 * Return parameter list with given key / name. Use [] around items in array
	 * Example: status=[P,A]
	 * @return empty list if no values found or a list of parameter values 
	 */
	public List<String> getParameters(String key) {
		return getParameters(key, parameters);
	}

	/**
	 * Return parameter list with given key / name. Use [] around items in array
	 * Example: status=[P,A]
	 * @return empty list if no values found or a list of parameter values 
	 */
	public List<String> getParameters(String key, String parameters){
		String arrayString = getParameter(key, parameters);
		if(arrayString != null && arrayString.length()>0 && !arrayString.equals("[]")){
			arrayString = arrayString.replaceAll("\\]", "").replaceAll("\\[", "");
			return Arrays.asList(arrayString.split(parameterArrayDelimiter));
		}
		return new ArrayList<String>();
	}

	/**
	 * @see #getParameter(String, String)
	 */
	public List<String> getParameters(String key, ViewChangeEvent event){
		return getParameters(key, event.getParameters());
	}

	/**
	 * Build vaadin-friendly internal url with with given key / name list as parameters
	 * @param viewname the name of the view to go to, example "monsterDetailView"
	 * @param parameters key-value pairs of parameters, example: "color", "green", "size", "small"
	 * @return the complete url with parameters, example: "#!monsterDetailView/color=green/size=small"
	 */
	public String createUrl(String viewname, Object... parameters) {
		StringBuilder url = new StringBuilder(viewname);
		int counter = 0;
		for (Object parameter : parameters) {
			if(counter % 2 == 0) 
				url.append(parameterDelimiter);
			else
				url.append(keyValueDelimiter);

			url.append(parameter);
			counter++;
		}
		return url.toString();
	}
	
	/**
	 * Set or remove an URL parameter to current page.
	 * @param parameterName name of parameter
	 * @param parameterValue value of parameter. If null, the parameter will be removed
	 */
	public static void applyUrlParameterToCurrentPage(String parameterName, String parameterValue) {
		Page page = UI.getCurrent().getPage();
		String uriFragment = page.getUriFragment();
		UrlUtils urlUtils = new UrlUtils(uriFragment);
		String oldParameterValue = urlUtils.getParameter(parameterName);
		String oldParameter = parameterName + urlUtils.keyValueDelimiter + oldParameterValue;
		if (parameterValue != null) {
			String newParameter = parameterName + urlUtils.keyValueDelimiter + parameterValue;
			if (oldParameterValue == null) {
				if (uriFragment.endsWith(urlUtils.parameterDelimiter))
					page.setUriFragment(uriFragment + newParameter);
				else
					page.setUriFragment(uriFragment + urlUtils.parameterDelimiter + newParameter);
			} else {
				page.setUriFragment(uriFragment.replace(oldParameter, newParameter));
			}	
		} else {
			page.setUriFragment(uriFragment.replace(urlUtils.parameterDelimiter + oldParameter, "")); //remove parameter if it exists
		}
	}

}
