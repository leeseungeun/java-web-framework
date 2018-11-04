package io.github.leeseungeun.webframework.beans;

import java.util.Map;

import io.github.leeseungeun.webframework.annotations.Bean;
import io.github.leeseungeun.webframework.controller.Controller;
import io.github.leeseungeun.webframework.enums.BeanType;

/**
 * 
 * 
 * @author 이승은
 *
 */
@Bean(type=BeanType.RequestHandler)
public class RequestHandler {
	
	// 인스턴스 변수 선언
	private Map<String, Controller> requestMapper;

	// getter / setter 
	public Map<String, Controller> getRequestMapper() {
		return requestMapper;
	}

	public void setRequestMapper(Map<String, Controller> requestMapper) {
		this.requestMapper = requestMapper;
	}
	
	public Controller getController(String parsedUri) {
		return requestMapper.get(parsedUri);
	}
	
	/**
	 * uri가 확장자 형식인지, 어플리케이션 이름보다 긴지 유효성을 확인하는 메소드
	 * 
	 * @param uri
	 * @param applicationName
	 * @return
	 */
	public static boolean isValidURI(String uri, String applicationName) {
		return uri.length() > applicationName.length() 
				&& uri.lastIndexOf('.') != -1;
	}
	
	/**
	 * 어플리케이션 이름을 이용해 uri를 파싱하는 메소드
	 * 
	 * @param uri
	 * @param applicationName
	 * @return
	 */
	public static String parse(String uri, String applicationName) {
		
		assert isValidURI(uri, applicationName);
		
		return uri.substring(applicationName.length(), uri.lastIndexOf("."));
	}
}
