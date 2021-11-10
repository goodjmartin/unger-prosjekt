package no.goodtech.vaadin.frontpage.model;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class FrontPageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FrontPageService.class);

	private final String basePackage;

	public FrontPageService() {
		basePackage = "no.goodtech";
	}

	public FrontPageService(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * Generic method of trying a method in a front-page component
	 */
	private boolean callMethodByClassIfExists(IFrontPageCardComponent component, String methodName, Object... args) {
		// Try to refresh component
		Method[] methods = component.getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				try {
					m.invoke(component, args);
					return true;
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return false;
	}

	/**
	 * Fetch GUI component based on a FrontPageCard entity
	 */
	public Component getComponentFromCard(FrontPageCard card) {
		try {
			Component component = getComponent(Class.forName(card.getPanel()));
			if (component != null && component instanceof IFrontPageCardComponent) {
				IFrontPageCardComponent argsComponent = (IFrontPageCardComponent) component;
				argsComponent.setArguments(card.getArgsMap());
//				callMethodByClassIfExists(component, "setReadOnly", true);
				refreshComponent(argsComponent);
				return argsComponent;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Refresh a front-page component if possible
	 */
	public void refreshComponent(IFrontPageCardComponent component){
		callMethodByClassIfExists(component, "refresh");
	}

	/**
	 * Instanciate component from class and refresh the component if possible
	 */
	private Component getComponentFromClass(Class<?> componentClass) {
		Component component = getComponent(componentClass);
		if (component != null && component instanceof IFrontPageCardComponent) {
//			callMethodByClassIfExists(component, "setReadOnly", true);
			refreshComponent((IFrontPageCardComponent) component);
		}
		return component;
	}

	/**
	 * Instanciate a component from class and check authorization
	 */
	private Component getComponent(Class<?> componentClass) {
		try {
			Object object = componentClass.newInstance();
			if (object instanceof Component && object instanceof IFrontPageCardComponent) {
				IFrontPageCardComponent argsComponent = (IFrontPageCardComponent) object;
				no.goodtech.vaadin.login.User user = (VaadinSession.getCurrent() != null) ? (User) VaadinSession.getCurrent().getAttribute(Constants.USER) : null;
				if (argsComponent.isAuthorized(user, null)) {
					argsComponent.setArguments(argsComponent.getArguments());
					return argsComponent;
				}
			}
		} catch (InstantiationException | IllegalAccessException ignored) {
			LOGGER.warn("Could not create front-page component of type " + componentClass.getName());
		}
		return null;
	}

	/**
	 * Find UI components with annotation @FrontPageCard
	 */
	public ArrayList<IFrontPageCardComponent> getClasses() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(FrontPageCardComponent.class));
		ArrayList<IFrontPageCardComponent> components = new ArrayList<>();
		for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
			try {
				Class<?> clazz = Class.forName(bd.getBeanClassName());
				Component component = getComponentFromClass(clazz);
				if (component != null && component instanceof IFrontPageCardComponent) {
					components.add((IFrontPageCardComponent) component);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return components;
	}

	/**
	 * Convert the argument map to a string array.
	 * From: <key1, val1>, <key2, val2>. To: [key1, val1, key2, val2]
	 */
	public String[] argumentMapToStringArray(Map<String, String> args){
		if (args == null)
			return new String[0];
		ArrayList<String> argsArray = new ArrayList<>();
		for (String key : args.keySet()){
			argsArray.add(key);
			argsArray.add(args.get(key));
		}
		return argsArray.toArray(new String[0]);
	}
}
