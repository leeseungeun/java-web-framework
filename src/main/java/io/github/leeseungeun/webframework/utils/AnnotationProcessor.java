package io.github.leeseungeun.webframework.utils;

public class AnnotationProcessor {
	
	public static boolean hasAnnotation(Class targetClass, Class annotationClass) {
		return targetClass.getAnnotation(annotationClass) != null;
	}
	
}
