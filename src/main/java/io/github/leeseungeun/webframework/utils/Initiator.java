package io.github.leeseungeun.webframework.utils;

import static io.github.leeseungeun.webframework.enums.AnnotationType.INJECT_BEAN_DATA_NAME;
import static io.github.leeseungeun.webframework.enums.AnnotationType.INJECT_CLASS_DATA_NAME;
import static io.github.leeseungeun.webframework.enums.AnnotationType.INJECT_FIELD_DATA_NAME;
import static io.github.leeseungeun.webframework.enums.AnnotationType.REQUESTMAPPING_CONTROLLER_DATA_NAME;
import static io.github.leeseungeun.webframework.enums.AnnotationType.REQUESTMAPPING_REQUESTMAPPER_DATA_NAME;
import static io.github.leeseungeun.webframework.enums.AnnotationType.REQUESTMAPPING_URI_DATA_NAME;
import static io.github.leeseungeun.webframework.utils.AnnotationProcessor.hasAnnotation;
import static io.github.leeseungeun.webframework.utils.AnnotationProcessor.processAnnotationType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import io.github.leeseungeun.webframework.annotations.Bean;
import io.github.leeseungeun.webframework.annotations.Inject;
import io.github.leeseungeun.webframework.annotations.RequestMapping;
import io.github.leeseungeun.webframework.beans.RequestHandler;
import io.github.leeseungeun.webframework.interfaces.Controller;

public class Initiator {

	// 인스턴스 변수 선언
	private Map<String, Object> beans;
	private RequestHandler requestHandler;
	
	// getter / setter
	public Map<String, Object> getBeans() {
		return beans;
	}
	
	public void setBeans(Map<String, Object> beans) {
		this.beans = beans;
	}
	
	public Object getBean(String className) {
		return beans.get(className);
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	// 생성자
	public Initiator(String projectPackage) {
		
		try {
			
			beans = new HashMap<String, Object>();
			
			// annotation을 검색할 전체 클래스를 가져옴
			List<Class> classList = new PackageScanner().getClasses();
			List<Class> projectClassList = new PackageScanner(projectPackage).getClasses();
			classList.addAll(projectClassList);
			
			setBeans(classList);
			inject(classList);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void setBeans(List<Class> classes) {
		
		Class targetAnnotation = Bean.class;
		
		for (Class clazz : classes) {
			
			if (hasAnnotation(clazz, targetAnnotation)) {
				Object beanObject = processAnnotationType(clazz, targetAnnotation);
				assert beanObject != null;
				
				beans.put(clazz.getName(), beanObject);
			}
		}
		
	}
	
	private void inject(List<Class> classes) {
		
		Class targetAnnotation = Inject.class;
		
		for (Class clazz : classes) {
			
			Field[] fields = clazz.getDeclaredFields();
			
			for (Field field : fields) {
				if (hasAnnotation(field, targetAnnotation)) {
					
					Object classObject = getBean(clazz.getName());
					assert classObject != null;
					
					Object targetObject = getBean(field.getType().getName());
					assert targetObject != null;
					
					Map<String, Object> data = new HashMap<String, Object>();
					data.put(INJECT_CLASS_DATA_NAME, classObject);
					data.put(INJECT_BEAN_DATA_NAME, targetObject);
					data.put(INJECT_FIELD_DATA_NAME, field);
					
					Object beanObject = processAnnotationType(data, Inject.class);
					assert beanObject != null;
					
					beans.put(clazz.getName(), beanObject);
				}
			}
			
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void setControllerMapper(List<Class> classes) {
		
		Class targetAnnotation = RequestMapping.class;
		
		requestHandler = (RequestHandler) getBean(RequestHandler.class.getName());
		Map<String, Controller> requestMapper = new Hashtable<String, Controller>();
		
		for (Class clazz : classes) {	
			
			if (hasAnnotation(clazz, targetAnnotation)) {
				
				Object classObject = getBean(clazz.getName());
				assert classObject != null;
				
				Annotation annotation = clazz.getAnnotation(targetAnnotation);
				assert annotation != null;
				String uri = ((RequestMapping) annotation).value();
				
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(REQUESTMAPPING_REQUESTMAPPER_DATA_NAME, requestMapper);
				data.put(REQUESTMAPPING_CONTROLLER_DATA_NAME, classObject);
				data.put(REQUESTMAPPING_URI_DATA_NAME, uri);
				
				requestMapper = (Map<String, Controller>) processAnnotationType(clazz, targetAnnotation);
				
			}
		}
		
		this.requestHandler.setRequestMapper(requestMapper);
	} 
	
}
