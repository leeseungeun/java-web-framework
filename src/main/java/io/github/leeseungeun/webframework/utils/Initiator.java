package io.github.leeseungeun.webframework.utils;

import static io.github.leeseungeun.webframework.enums.AnnotationType.INJECT_BEAN_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.AnnotationType.INJECT_CLASS_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.AnnotationType.INJECT_FIELD_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.AnnotationType.REQUESTMAPPING_CONTROLLER_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.AnnotationType.REQUESTMAPPING_REQUESTMAPPER_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.AnnotationType.REQUESTMAPPING_URI_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.BeanType.SQLSESSIONFACTORY_CONFIGLOCATION_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.BeanType.SQLSESSIONFACTORY_ENVIRONMENT_DATA_KEY;
import static io.github.leeseungeun.webframework.enums.BeanType.SQLSESSIONFACTORY_SQLSESSIONFACTORY_DATA_KEY;
import static io.github.leeseungeun.webframework.utils.AnnotationProcessor.hasAnnotation;
import static io.github.leeseungeun.webframework.utils.AnnotationProcessor.processAnnotationType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.github.leeseungeun.webframework.annotations.Bean;
import io.github.leeseungeun.webframework.annotations.Inject;
import io.github.leeseungeun.webframework.annotations.RequestMapping;
import io.github.leeseungeun.webframework.beans.RequestHandler;
import io.github.leeseungeun.webframework.beans.SqlSessionFactory;
import io.github.leeseungeun.webframework.controller.Controller;
import io.github.leeseungeun.webframework.enums.BeanType;

public class Initiator {

	// 인스턴스 변수 선언
	// 반환을 위한 변수
	private Map<String, Object> beans;
	private RequestHandler requestHandler;
	
	// 다른 클래스로 전달하기 위해 필요한 변수
	private String projectPackageName;
	private String dataConfigLocation;
	private String dataEnvironment;
	
	// getter / setter
	public String getDataConfigLocation() {
		return dataConfigLocation;
	}
	
	public void setDataConfigLocation(String dataConfigLocation) {
		this.dataConfigLocation = dataConfigLocation;
	}
	
	public String getDataEnvironment() {
		return dataEnvironment;
	}
	
	public void setDataEnvironment(String dataEnvironment) {
		this.dataEnvironment = dataEnvironment;
	}
	
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
	public Initiator(URL location, String xmlFileLocation) {
		
		try {
			// 필요한 변수를 초기화
			beans = new HashMap<String, Object>();
			
			initVariablesFromXMLFile(xmlFileLocation);
			
			// annotation을 검색할 전체 클래스를 가져옴
			List<Class> classList = new ArrayList<Class>();
			// 라이브러리 내 스캔해야 하는 클래스 넣어줌
			classList.add(RequestHandler.class);
			classList.add(SqlSessionFactory.class);
			List<Class> projectClassList = new PackageScanner(location, projectPackageName).getClasses();
			classList.addAll(projectClassList);
			
			// 빈 등록
			setBeans(classList);
			// 의존성 주입
			inject(classList);
			// 컨트롤러 등록
			setControllerMapper(classList);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void initVariablesFromXMLFile(String xmlFileLocation) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = null;
		Document document = null;
		
		try {
			parser = factory.newDocumentBuilder();
			document = parser.parse(xmlFileLocation);
			
			Element scanPackageNode = (Element) document.getElementsByTagName("scan-package").item(0); 
			String projectPackageName = scanPackageNode.getAttribute("name");
			assert projectPackageName.length() > 0;
			this.projectPackageName = projectPackageName;
			
			String dataConfigLocation = null;
			String dataEnvironment = null;

			NodeList sqlSessionSettings = document.getElementsByTagName("sql-session-factory-config");
			
			for (int i = 0; i < sqlSessionSettings.getLength(); i++) {
				
				Element sqlSessionSettingElement = (Element) sqlSessionSettings.item(i);
				String configName = sqlSessionSettingElement.getAttribute("name");
				
				if ("configLocation".equals(configName)) {
					
					dataConfigLocation = sqlSessionSettingElement.getAttribute("value");
					
				} else if ("environment".equals(configName)) {
					
					dataEnvironment = sqlSessionSettingElement.getAttribute("value");
					
				}
			}
			
			assert dataConfigLocation != null && dataConfigLocation.length() > 0;
			assert dataEnvironment != null && dataEnvironment.length() > 0;
			
			this.dataConfigLocation = dataConfigLocation;
			this.dataEnvironment = dataEnvironment;
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private Object findImplementation(Class interfaceName) {
		
		Object result = null;
		
		for (String beanKey : beans.keySet()) {
			
			Object beanToCheck = beans.get(beanKey);
			if (interfaceName.isInstance(beanToCheck)) {
				result = beanToCheck;
				break;
			}
			
		}
		
		return result;
	}
	
	private void setBeans(List<Class> classes) {
		
		Class targetAnnotation = Bean.class;
		BeanType sqlSeesionFactoryType = BeanType.SqlSessionFactory;
		
		for (Class clazz : classes) {
			
			if (hasAnnotation(clazz, targetAnnotation)) {
				Object beanObject = processAnnotationType(clazz, targetAnnotation);
				assert beanObject != null;
				
				Bean annotation = (Bean) clazz.getAnnotation(targetAnnotation);
				if (annotation.type() == sqlSeesionFactoryType) {
					beanObject = buildSqlSessionFactoryBuilder(sqlSeesionFactoryType, beanObject, dataConfigLocation, dataEnvironment);
				}
				
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
					
					Object targetObject = findImplementation(field.getType());
					assert targetObject != null;
					
					Map<String, Object> data = new HashMap<String, Object>();
					data.put(INJECT_CLASS_DATA_KEY, classObject);
					data.put(INJECT_BEAN_DATA_KEY, targetObject);
					data.put(INJECT_FIELD_DATA_KEY, field);
					
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
				
				RequestMapping annotation = (RequestMapping) clazz.getAnnotation(targetAnnotation);
				String uri = annotation.value();
				
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(REQUESTMAPPING_REQUESTMAPPER_DATA_KEY, requestMapper);
				data.put(REQUESTMAPPING_CONTROLLER_DATA_KEY, classObject);
				data.put(REQUESTMAPPING_URI_DATA_KEY, uri);
				
				requestMapper = (Map<String, Controller>) processAnnotationType(data, targetAnnotation);
				
			}
		}
		
		this.requestHandler.setRequestMapper(requestMapper);
	} 
	
	private Object buildSqlSessionFactoryBuilder(BeanType sqlSessionFactoryType, Object sqlSessionFactory, String configLocation, String environment) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(SQLSESSIONFACTORY_SQLSESSIONFACTORY_DATA_KEY, sqlSessionFactory);
		data.put(SQLSESSIONFACTORY_CONFIGLOCATION_DATA_KEY, dataConfigLocation);
		data.put(SQLSESSIONFACTORY_ENVIRONMENT_DATA_KEY, dataEnvironment);
		
		return sqlSessionFactoryType.process(data);
	}
	
}
