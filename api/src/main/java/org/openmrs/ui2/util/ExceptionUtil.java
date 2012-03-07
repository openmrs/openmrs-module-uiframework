package org.openmrs.ui2.util;

import org.springframework.util.TypeUtils;

/**
 * Utility methods related to exceptions
 */
public class ExceptionUtil {
	
	/**
	 * Finds the first exception of type clazz in the chain, and returns it. (Or null, if no exception
	 * of that type occurs in the chain.)
	 * @param <T>
	 * @param ex
	 * @param clazz
	 * @return
	 */
	public static <T> T findExceptionInChain(Throwable ex, Class<T> clazz) {
		while (ex != null) {
			if (clazz.isAssignableFrom(ex.getClass()))
				return (T) ex;
			else if (ex.getCause() == ex)
				break;
			else
				ex = ex.getCause();
		}
		return null;
	}
	
}
