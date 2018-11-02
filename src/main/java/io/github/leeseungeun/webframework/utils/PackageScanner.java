package io.github.leeseungeun.webframework.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 지정된 패키지의 모든 자바 클래스 파일을 스캔하는 클래스
 * 
 * @author 이승은
 *
 */
public class PackageScanner {
	
	// 상수
	private static final String SEPERATOR_STRING = File.separatorChar == '\\'
												? "\\" + File.separator 
												: File.separator;
	private static final String URL_PROTOCOL = "file" + File.separator;
	private static final String LOCATION = PackageScanner.class.getProtectionDomain().getCodeSource().getLocation()
										.toString()
										.replace(File.separator, SEPERATOR_STRING)
										.substring(URL_PROTOCOL.length());
	
	// 인스턴스 변수
	private List<Class> classes = new ArrayList<Class>();

	// getter /setter 메소드
	public List<Class> getClasses() {
		return classes;
	}

	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}
	
	// 생성자
	public PackageScanner() throws ClassNotFoundException {
		scan("io.github.leeseungeun.webframework.beans");
	}
	
	public PackageScanner(String packageName) throws ClassNotFoundException {
		scan(packageName);
	}
	
	/**
	 * 패키지 이름의 유효성 검사 메소드
	 * 
	 * @param packageName 검사할 패키지 이름
	 * @return 패키지 이름이 유효한지 논리값으로 반환
	 */
	private boolean isValidPackageName(String packageName) {
		return packageName.matches("[\\w&&\\D]([\\w\\.]*[\\w])?");
	}
	
	/**
	 * 주어진 패키지명에서 클래스를 스캔하는 메소드
	 * 
	 * @param packageName
	 * @throws ClassNotFoundException 
	 */
	private void scan(String packageName) throws ClassNotFoundException {
		
		// 패키지명이 유효하지 않을 경우 Exception 발생
		assert isValidPackageName(packageName);
		
		// 패키지의 물리적 위치를 변수로 선언
		String packagePath = LOCATION + packageName.replace(".", SEPERATOR_STRING);
		// 패키지 내 클래스를 찾기 위해서 File 객체 생성
		File packageDirectory = new File(packagePath);

		// 클래스를 추가하는 메소드 호출
		addClasses(packageDirectory);
	}
	
	/**
	 * 주어진 디렉토리에서 자바 파일을 찾아 리스트에 추가하는 메소드
	 * 
	 * @param directory
	 */
	private void addClasses(File directory) throws ClassNotFoundException{
		// 디렉토리에 속하는 모든 파일 객체를 가져옴
		File[] files = directory.listFiles();

		for (File file : files) {

			String fileName = file.getName();
			int indexBeforeExtension = fileName.lastIndexOf('.');
			
			// 자바 클래스 파일인 경우
			if (file.isFile() && fileName.substring(indexBeforeExtension).equals(".class")) {
				
				String className = extractClassNameWithPackage(file.getAbsolutePath());
				// 패키지를 포함한 클래스명이 1글자 이상이 아닐 경우 Exception 발생
				assert className.length() > 0;
				
				Class cls = Class.forName(className);
				classes.add(cls);
				
			// 디렉토리인 경우 재귀 호출
			} else if (file.isDirectory()) {
				addClasses(file);
			}
		}
	}
	
	/**
	 * 물리적 경로가 인자로 주어지면 패키지를 포함한 클래스명을 반환하는 메소드
	 * 
	 * @param path
	 * @return 패키지를 포함한 클래스명
	 */
	private String extractClassNameWithPackage(String path) {
		
		// 반환할 결과를 변수로 선언
		String result = "";
		
		// 경로 구분자를 기준으로 문자열 분리
		String[] tonizedPath = path.split(File.separator);
		// 주어진 배열에서 값 classes가 몇 번째 인덱스에 위치하는지 변수로 선언
		int indexBeforePackage = Arrays.asList(tonizedPath).indexOf("classes");
		// 향상된 for문에서 이용할 index 변수 선언
		int indexInIterator = 0;
		
		// 경로 구분자를 기준으로 나눈 문자열의 배열을 순회하며 classes 이하 패키지명부터 파일명을 결과에 추가
		for (String token : tonizedPath) {
			if (indexInIterator > indexBeforePackage) {
				result += (token + ".");
			}
			// 인덱스 값을 증가시킴
			indexInIterator++;
		}
		
		// 확장자와 문자열 맨 마지막에 삽입된 온점의 길이를 변수로 선언
		int unneccessaryCharWithExtentions = ".class".length() + 1;
		
		// 문자열의 길이가 0을 초과할 경우에는 불필요한 부분 제거
		result = result.length() > 0 
				? result.substring(0, result.length() - unneccessaryCharWithExtentions)
				: result;
		
		return result;
	}
}
