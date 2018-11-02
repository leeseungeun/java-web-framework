package io.github.leeseungeun.webframework.beans;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import io.github.leeseungeun.webframework.annotations.Bean;
import io.github.leeseungeun.webframework.enums.BeanType;

@Bean(type=BeanType.SqlSessionFactory)
public class SqlSessionFactory extends SqlSessionFactoryBuilder{

	// 인스턴스 변수 선언
	private String configLocation;
	private String environment;
	
	
	// getter / setter
	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * 실제 SqlSessionFactory를 만들어주는 메소드
	 * 
	 * @return
	 */
	public org.apache.ibatis.session.SqlSessionFactory build() {
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(configLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.build(reader, environment);
	}

	@Override
	public String toString() {
		return "SqlSessionFactory [configLocation=" + configLocation + ", environment=" + environment + "]";
	}
	
}
