package org.ncbo.stanford.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
	ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SPARQLVariableName {
	String value();
}