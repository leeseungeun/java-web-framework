package io.github.leeseungeun.webframework.utils;

import io.github.leeseungeun.webframework.enums.AnnotationType;
import io.github.leeseungeun.webframework.enums.BeanType;

/**
 * 어노테이션에 따라 처리를 하는 클래스
 * 
 * @author 이승은
 *
 */
public class AnnotationProcessor {
	
	/**
	 * 특정 어노테이션을 포함하는지 확인하는 메소드
	 * 
	 * @param targetClass
	 * @param annotationClass
	 * @return 인자의 어노테이션을 해당 클래스가 갖고 있는지에 대한 논리값
	 */
	public static boolean hasAnnotation(Class targetClass, Class annotationClass) {
		
		return targetClass.getAnnotation(annotationClass) != null;
	
	}
	
	/**
	 * 어노테이션에 따른 처리를 수행하는 메소드
	 * 
	 * @param target
	 * @param annotationClass
	 */
	public static void processAnnotationType(Object target, Class annotationClass) {
		
		AnnotationType.valueOf(annotationClass.getSimpleName()).process(target);
	
	}
	
	/**
	 * 빈의 종류에 따른 처리를 수행하는 메소드
	 * 
	 * @param target
	 * @param annotationClass
	 */
	public static void processBeanType(Object target, Class annotationClass) {
		
		BeanType.valueOf(annotationClass.getSimpleName()).process(target);
	
	}
}
