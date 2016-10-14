package org.openmrs.ui.framework.page;

public class PageControllerNamingConvention {

    public static String toControllerName(String basePackage, String pageName){
        StringBuilder className = new StringBuilder();
        className.append(basePackage).append('.').append(pageName.replaceAll("/", ".")).append("PageController");
        return capitalizeClassName(className).toString();
    }

    public static String toPageName(String className){
        StringBuilder pageName = new StringBuilder();
        pageName.append(className.substring(className.lastIndexOf("page.controller.")+"page.controller.".length()).replace("PageController", ""));
        return uncapitalizeClassName(pageName).toString();
    }

    private static StringBuilder capitalizeClassName(StringBuilder className) {
        int lastDot = className.lastIndexOf(".");
        className.setCharAt(lastDot + 1, Character.toUpperCase(className.charAt(lastDot + 1)));
        return className;
    }

    private static StringBuilder uncapitalizeClassName(StringBuilder className){
        int lastDot = className.lastIndexOf(".");
        className.setCharAt(lastDot + 1, Character.toLowerCase(className.charAt(lastDot + 1)));
        return className;
    }
}
