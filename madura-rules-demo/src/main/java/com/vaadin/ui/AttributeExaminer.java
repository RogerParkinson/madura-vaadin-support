package com.vaadin.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

public class AttributeExaminer {
	
	private static String[] ignore = new String[]{
		"getPageLength",
		"getContainerDataSource",
		"getItemIds",
		"getItemCaptionPropertyId",
		"getVisibleItemIds",
		"getNewItemHandler",
		"getItemIconPropertyId",
		"getType",
		"getPropertyDataSource",
		"getConversionError",
		"getErrorMessage",
		"getTabIndex",
		"getConverter",
		"getCaption",
		"getWidth",
		"getHeight",
		"getWidthUnits",
		"getHeightUnits",
		"isCaptionAsHtml",
		"getParent",
		"getDescription",
		"getStateType",
		"getUI",
		"getFormFieldFactory",
		"getFieldFactory",
		"getItemDataSource",
		"getContent",
		"getScrollTop",
		"getScrollLeft",
		"getLayout",
		"getComponentIterator",
		"getItemPropertyIds",
		"getFooter",
		"getAssistiveDescription",
		"getPage",
		"getSession",
		"getEmbedId",
		"getLocaleService",
		"getOverlayContainerLabel",
		"getLoadingIndicatorConfiguration",
		"getTooltipConfiguration",
		"getLastHeartbeatTimestamp",
		"getWindows",
		"getCurrent",
		"getPopupWindow",
		"getOrder",
		"getCustomer",
		"getEventRouter",
		"getCustomerView",
		"getTheme",
		"getComponentCount",
		"getNavigator",
		"getExtensions",
		"getComponentCount",
		"getConnectorId",
		"getOrderView"
		};
	
	public static String getAttributes(AbstractComponent ac) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StringBuilder ret = new StringBuilder();
		ret.append("\n-------");
		ret.append(ac.getClass());
		ret.append(" ");
		Set<String> attributes = new TreeSet<String>();
		Method[] methods = ac.getClass().getMethods();
		for (Method m: methods) {
			if ((m.getName().startsWith("get") || m.getName().startsWith("is")) && (m.getParameterTypes().length == 0) && !isIgnored(m.getName())) {
				// this is an attribute we want to see
				Object att = m.invoke(ac, new Object[]{});
				attributes.add(m.getName()+"="+att);
			}
		}
		for (String a:attributes) {
			ret.append("\n");ret.append(a);
		}
		return ret.toString();
	}
	private static boolean isIgnored(String name) {
		for (String n: ignore) {
			if (n.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
