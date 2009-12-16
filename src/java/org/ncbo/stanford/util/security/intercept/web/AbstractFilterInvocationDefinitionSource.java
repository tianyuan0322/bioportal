/**
 * 
 */
package org.ncbo.stanford.util.security.intercept.web;

import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;

/**
 * @author s.reddy
 *
 */
public abstract class AbstractFilterInvocationDefinitionSource implements FilterInvocationDefinitionSource {

    //~ Methods ========================================================================================================

    public ConfigAttributeDefinition getAttributes(Object object)
        throws IllegalArgumentException {
        if ((object == null) || !this.supports(object.getClass())) {
            throw new IllegalArgumentException("Object must be a FilterInvocation");
        }

        String url = ((FilterInvocation) object).getRequestUrl();

        return this.lookupAttributes(url);
    }

    /**
     * Performs the actual lookup of the relevant <code>ConfigAttributeDefinition</code> for the specified
     * <code>FilterInvocation</code>.
     * <p>Provided so subclasses need only to provide one basic method to properly interface with the
     * <code>FilterInvocationDefinitionSource</code>.
     * </p>
     * <p>Public visiblity so that tablibs or other view helper classes can access the
     * <code>ConfigAttributeDefinition</code> applying to a given URI pattern without needing to construct a mock
     * <code>FilterInvocation</code> and retrieving the attibutes via the {@link #getAttributes(Object)} method.</p>
     *
     * @param url the URI to retrieve configuration attributes for
     *
     * @return the <code>ConfigAttributeDefinition</code> that applies to the specified <code>FilterInvocation</code>
     */
    public abstract ConfigAttributeDefinition lookupAttributes(String url);

    @SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
