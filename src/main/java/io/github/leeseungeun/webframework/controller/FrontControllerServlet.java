package io.github.leeseungeun.webframework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.leeseungeun.webframework.annotations.RequestMapping;
import io.github.leeseungeun.webframework.beans.RequestHandler;
import io.github.leeseungeun.webframework.utils.Jsonifier;

public class FrontControllerServlet extends HttpServlet{
	
	private Map<String, Object> beans;
	private RequestHandler requestHandler;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		beans = (Map<String, Object>) config.getServletContext().getAttribute("beans");
		requestHandler = (RequestHandler) config.getServletContext().getAttribute("requestHandler");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
		process(request, response);
	}
	
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException ,IOException {
		
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		
		uri = RequestHandler.parse(uri, contextPath);
		
		Map<String, Controller> requestMapper = requestHandler.getRequestMapper();
		Controller controller = (Controller) requestMapper.get(uri);
		
		Object model = controller.handleRequest(request, response);
		String result = Jsonifier.jsonify(model);
		
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println(result);
		} finally {
			if (out != null) out.close();
		}
		
	}
}
