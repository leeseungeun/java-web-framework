package io.github.leeseungeun.webframework.interfaces;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {
	
	/** 실행 규약 메소드 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException;

}
