# Servlet3.0
###### 原来需要配置文件(web.xml)配置的Servlet、filter、listener以及DispatcherServlet,现在都可以用注解的方式来配置
###### @WebServlet、@WebFilter、@WebListener

* ### shared libraries(共享库)、rumtimes pluggability(运行时插件能力)

* ### ServletContainerInitializer
    1. 容器在启动时，会扫描当前应用的每一个jar包里面的META-INF/services/javax.servlet.ServletContainerInitializer
    指定的实现类，启动并运行这个实现类
    2. 我们需要提供一个ServletContainerInitializer接口的实现类，并重写其onStartup()方法
        * @HandlesTypes(value = Class<?>[]): 容器启动的时候会将这个注解指定的类型下面的子类(实现类、子接口等)传递给onStart()方法
        * onStartup(Set<Class<?>> classes, ServletContext servletContext)方法：
            * Set<Class<?>> classes: 感兴趣的类型的所有子类型：@HandlesTypes注解传入的所有类型
            * ServletContext servletContext: 代表当前Web应用的上下文，要给web应用只有一个ServletContext

* ### 使用ServletContext注册web组件(Servlet、Filter、Listener)(加注解也可以)
    * 代码注册：
        ``` java    (ServletContainerInitializer子类.onStartup()方法中注册web组件)
            // 注册Servlet
            ServletRegistration.Dynamic servlet = servletContext.addServlet("userServlet", new UserServlet());
            // 配置Servlet映射信息
            servlet.addMapping("/user");
            // 其中UserServlet实现了HttpServlet并重写其doGet(HttpServletRequest request, HttpServletResponse response)方法
      
            // 注册Filter
            FilterRegistration.Dynamic filter = servletContext.addFilter("userFilter", UserFilter.class);
            // 配置Filter的映射信息(两种方式)
            // filter.addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames);
            // filter.addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns);
            filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*);
            // 其中UserFilter实现了Filter接口并重写doFilter(ServletRequest request, ServletResponse response, FilterChain chain)方法
            // 注意每个doFilter方法中如果放行则需要调用chain.doFilter()方法
      
            // 注册Listener
            servletContext.addListener(UserListener.class);
            // 其中UserListener实现了ServletContextListener接口并重写了contextInitialized(ServletEvent event)和
            // contextDestroyed(ServletContextEvent event)方法
            // 注意：监听器有很多，ServletContextListener只是其中一种
        ```
    * web组件必须在项目启动时才能添加并可以在以下两个地方添加
        1. ServletContainerInitializer子类.onStartup()方法
        2. ServletContextListener子类.contextInitialized(ServletEvent event)方法中
            * event.getServletContext().add...


# 异步处理
Servlet3.0之前，tomcat从一个request进入、处理、返回，都是用同一个线程，而tomcat中处理请求的线程数量(tomcat线程池中的线程数)是有限的，
此时，若处理的业务逻辑很耗时，会导致处理请求的线程无法及时地释放，因此Servlet3.0之后加入了异步请求
### 异步步骤
1. 开启异步处理支持@WebServlet(value = "test", asyncSupported = true)
2. 开启异步处理(在Servlet的void doGet(request, response)方法中执行): AsyncContext startAsync = request.startAsync();
3. 业务逻辑进行异步处理，开始异步处理
    ``` java
    startAsync.start(new Runnable() {
        @Override
        public void run() {
            // 此处写耗时的业务逻辑
   
            // 异步处理完成及响应
            startAsync.complete();
            // 获取到异步上下文(注意：这里的asyncContext与startAsync是同一个对象)
            AsyncContext asyncContext = request.getAsyncContext();
            // 获取响应
            ServletResponse response = asyncContext.getResponse();
            // 返回结果
            response.getWriter().write("finish async");
        }
    })
   ```
   注意： 这里主副线程所使用的线程在同一个线程池中(tomcat线程池)