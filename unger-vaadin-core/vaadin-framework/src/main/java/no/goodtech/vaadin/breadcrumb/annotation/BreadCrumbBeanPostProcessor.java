package no.goodtech.vaadin.breadcrumb.annotation;

import no.goodtech.vaadin.breadcrumb.BreadCrumbManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

public class BreadCrumbBeanPostProcessor implements BeanPostProcessor {

	private static Map<String, String> parentViewOverridesMap = new HashMap<>();

	public BreadCrumbBeanPostProcessor(final String parentViewOverridesConfig) {
		String[] parentViewOverrides = parentViewOverridesConfig.trim().split(",");

		for (String parentViewOverride : parentViewOverrides) {
			String[] viewConfig = parentViewOverride.trim().split("=");

			if (viewConfig.length == 2) {
				String viewId = viewConfig[0];
				String parentViewId = viewConfig[1];
				parentViewOverridesMap.put(viewId, parentViewId);
			}
		}
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		Class clazz = bean.getClass();

		// Check if the bean is annotated with '@BreadCrumb'
		if (clazz.isAnnotationPresent(BreadCrumb.class)) {
			BreadCrumb breadCrumb = (BreadCrumb)clazz.getAnnotation(BreadCrumb.class);

			// Register bread crumb entry (check if there is a parent view override)
			String parentViewId = parentViewOverridesMap.get(breadCrumb.viewId());
			BreadCrumbManager.registerBreadCrumb(breadCrumb.viewId(), (parentViewId != null) ? parentViewId : breadCrumb.parentViewId());
		}

		return bean;
	}

}
