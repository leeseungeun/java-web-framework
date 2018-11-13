# Java Web Framework   
Spring like Java web framework project   
스프링과 유사하게 어노테이션 (Annotation, @)으로써 빈 (Bean) 등록 및 의존성 주입 (Dependency Injection, DI)를 처리하는 웹 프레임워크 프로젝트  


* 배포 방식 : ```maven install``` 명령어로써 생성한 ```jar``` 및 ```pom.xml```   
* 사용 방법 :
    * 로컬의 maven 레파지토리 (Windows 기준 기본값 : ```C:\Users\User\.m2\repository```)에 ```jar``` 파일 추가한 후 ```pom.xml```에 ```groupId```, ```artifactId```, ```version``` 추가
    * 명령창에서 ```mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version>``` 실행 후 ```pom.xml```에 ```groupId```, ```artifactId```, ```version``` 추가
    * ```pom.xml```에 ```maven-install-plugin``` 사용
    * 시스템 스코프 (System Scope) ```<systemPath>${basedir}/lib/yourJar.jar</systemPath>```를 이용한 의존성 추가
        * 컴퓨터 파일 구조에 종속적이므로 비권장
    * 아래의 코드를 이용해 로컬에 다른 maven 레파지토리 생성
        ```
        <repositories>
            <repository>
                <id>maven-repository</id>
                <url>file:///${project.basedir}/maven-repository</url>
            </repository>
        </repositories>
        ```
* ```pom.xml``` 추가 예시
    ```
    <dependency>
        <groupId>io.github.leeseungeun</groupId>
        <artifactId>java-web-framework</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```

* 디렉토리 구조
    ```
    \src\main\java\io\github\leeseungeun\webframework
        \annotations                                    # 프레임워크에서 사용하는 사용자 정의 어노테이션 패키지
            \Bean.java                                  # 서블릿 컨테이너가 실행되며 객체를 생성해야 하는 클래스에 붙이는 어노테이션
            \Inject.java                                # 의존성 주입이 필요한 필드에 붙이는 어노테이션
            \RequestMapping.java                        # 요청 URI의 매핑 정보를 표시할 때 사용하는 어노테이션
        \beans                                          # 프레임워크에서 사용하는 객체 패키지
            \RequestHandler.java                        # 요청 URI 파싱하고 매핑 정보를 저장하는 객체
            \SqlSessionFactory.java                     # SqlSession을 생성해주는 Factory
        \controller
            \Controller.java                            # 모든 세부 컨트롤러가 따라야 하는 인터페이스
            \FrontController.java                       # 서버 측 최전단에서 URI에 따라 요청을 해당 컨트롤러에 전달하고 전달 받은 데이터를 JSON 형식으로 변환해 클라이언트 측에 전달
        \enums                                          # 어노테이션, 빈, JSON으로 변환해야 하는 데이터에 따른 처리를 위해 선언한 ENUM 패키지
            \AnnotationType.java                        # 어노테이션의 종류와 그에 따른 처리를 정의한 ENUM
            \BeanType.java                              # 빈의 종류와 그에 따른 처리 (특히 SqlSessionFactory)를 정의한 ENUM
            \JsonifiedObjectType.java                   # JSON으로 변환 가능한 데이터와 그에 따른 변환 로직이 정의된 ENUM
        \filter
            \CharacterEncodingFilter.java               # 한글 UTF-8 처리를 위한 필터
        \utils                                          # 프레임워크 사용을 위해 제공하는 유용한 클래스 패키지
            \AnnotationProcessor.java                   # 주어진 클래스에 특정 어노테이션이 선언되었는지 확인하고, 어노테이션과 빈 종류에 따라 실질적인 처리를 수행하는 클래스
            \Initiator.java                             # Bean 어노테이션이 붙은 객체를 생성하고, 의존성을 주입하고, URI와 컨트롤러 매핑을 진행하는 클래스
            \Jsonifier                                  # 실제 JSON 변환을 수행하는 클래스
            \PackageScanner                             # 파일 구조를 이용해 주어진 패키지에 포함되는 모든 클래스의 클래스 객체를 반환

    ```

* 해당 프레임워크를 사용하는 프로젝트에서 필요한 절차
    1. ```javax.servlet.ServletContextListener.java```를 구현한 클래스의 ```contextInitialized``` 메소드에 아래의 코드 추가
        ```
        public void contextInitialized(ServletContextEvent event) {

            ServletContext servletContext = event.getServletContext();
            // 컴포넌트 스캔할 패키지명, MyBatis 설정이 담긴 파일의 위치 문자열로 저장
            String configLocation = servletContext.getInitParameter("configLocation");

            // 코드 실행 위치를 가져옴
            URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
            // 현재 코드 실행 위치를 기준으로 주어진 패키지에 등록된 클래스 목록을 얻어 필요한 처리를 진행
            Initiator initiator = new Initiator(url, configLocation);

            // 현재 서블릿 컨테이너에 필요 정보 등록
            Map<String, Object> beans = initiator.getBeans();
            RequestHandler requestHandler = initiator.getRequestHandler();
            
            servletContext.setAttribute("beans", beans);
            servletContext.setAttribute("requestHandler", requestHandler);
        }
        ```
    2. ```web.xml``` 설정
        ```
        <?xml version="1.0" encoding="UTF-8"?>
        <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" version="2.5">
        <display-name>le-vin</display-name>
        <welcome-file-list>
            <welcome-file>index.html</welcome-file>
            <welcome-file>index.htm</welcome-file>
            <welcome-file>index.jsp</welcome-file>
        </welcome-file-list>
        <listener>
            <listener-class>project.common.listener.ServletContextListenerImplement</listener-class>
        </listener>
        <context-param>
            <param-name>configLocation</param-name>
            <param-value>drive:/path/config-file.xml</param-value>
        </context-param>
        <filter>
            <filter-name>CharacterEncodingFilter</filter-name>
            <filter-class>project.common.filter.CharacterEncodingFilter</filter-class>
            <init-param>
                <param-name>encoding</param-name>
                <param-value>utf-8</param-value>
            </init-param>
        </filter>
        <filter-mapping>
            <filter-name>CharacterEncodingFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
        <servlet>
            <servlet-name>frontController</servlet-name>
            <servlet-class>project.common.controller.FrontControllerServlet</servlet-class>
            <load-on-startup>1</load-on-startup>
        </servlet>
        <servlet-mapping>
            <servlet-name>frontController</servlet-name>
            <url-pattern>*.mall</url-pattern>
        </servlet-mapping>
        </web-app>
        ```
    3. 예시 파일
        @Bean(type=BeanType.Controller)
        @RequestMapping(value="/user/list")
        public class UserListController implements Controller {
            
            @Inject
            private UserService userService;

            public UserService getUserService() {
                return userService;
            }

            public void setUserService(UserService userService) {
                this.userService = userService;
            }

            @Override
            public Object handleRequest(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, RequestException {
                
                List<User> list = null;
                try {
                    list = userService.list();
                    //throw new RequestBadRequestException();
                } catch (Exception e) {
                    throw new ServletException("UserService.list() 예외 발생", e);
                }
                
                return list;

            }

        }