package io.github.leeseungeun.webframework.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 요청파라메터 한글인코딩 처리 필터
 */
public class CharacterEncodingFilter implements Filter {
	
	private String encoding;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		encoding = filterConfig.getInitParameter("encoding");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 전처리
		if(encoding != null){
			request.setCharacterEncoding(encoding);
		}
		chain.doFilter(request, response);
		// 후처리
	}

	@Override
	public void destroy() {	}

	

}
