AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Application.class) {
    this() {
        // 初始化context内部的AnnotatedBeanDefinitionReader和ClassPathBeanDefinitionScanner类
        // 在初始化reader时，向beanFactory注册了一些spring内部类
        this.reader = new AnnotatedBeanDefinitionReader(this);
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    };

    // 将注解配置类注册到beanFactory中
    register(annotatedClasses) {
        // 实际上是调用applicationContext内部的AnnotatedBeanDefinitionReader来解析该注解配置类
        // 在解析后，将其注册到beanFactory中
        this.reader.register(annotatedClasses) {
            void doRegisterBean(Class<T> annotatedClass, ...) {
                // bean定义
                AnnotatedGenericBeanDefinition adb = new AnnotatedGenericBeanDefinition(annotatedClass);

                // 判断当前是否满足注册条件，如果不满足则直接返回，不进行bean注册
                if (this.conditionEvaluator.shouldSkip(adb.getMetadata())) {
                    return;
                }

                // registerBean()时开发者自定义bean实例提供者，默认为null表示将由spring来生成
                // 默认生成方式包括：调用构造器、cglib
                abd.setInstanceSupplier(instanceSupplier);

                // 解析@Scope注解，获取bean的作用域。默认为singleton
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
                adb.setScope(scopeMetadata.getScopeName());

                // 获取bean名称
                // 如果没有指定了bean名称，则解析@Component、@ManagedBean、@Named注解获取指定的bean名称
                // 否则，默认以首字母小写的bean类名称作为bean名称
                String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(adb, this.registry));

                // 处理常见的注解
                AnnotationConfigUtils.processCommonDefinitionAnnotations(abd) {
                    // bean的元数据
                    AnnotatedTypeMetadata metadata = abd.getMetadata()

                    // 处理@Lazy
                    AnnotationAttributes lazy = attributesFor(metadata, Lazy.class);
                    if (lazy != null) {
                        abd.setLazyInit(lazy.getBoolean("value"));
                    }

                    // 处理@Primary
                    if (metadata.isAnnotated(Primary.class.getName())) {
                        abd.setPrimary(true);
                    }

                    // 处理@DependsOn
                    AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
                    if (dependsOn != null) {
                        abd.setDependsOn(dependsOn.getStringArray("value"));
                    }

                    // 处理@Role
                    AnnotationAttributes role = attributesFor(metadata, Role.class);
                    if (role != null) {
                        abd.setRole(role.getNumber("value").initValue());
                    }

                    // 处理@Description
                    AnnotationAttributes description = attributesFor(metadata, Description.class);
                    if (description != null) {
                        abd.setDescription(description.getString("value"));
                    }
                };

                if (qualifiers != null) {
                    // 
                }

                for (BeanDefinitionCustomizer customizer : definitionCustoizers) {
                    customizer.customize(abd);
                }

                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
                definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry) {
                    ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
                    if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
                        return definition;
                    }
                    boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
                    return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
                };

                // 将bean注册到beanFactory中
                BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry) {
                    String beanName = definitionHolder.getBeanName();
                    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition()) {
                        if (beanDefinition instanceof AbstractBeanDefinition) {
                            try {
                                // 校验bean定义是否合法
                                ((AbstractBeanDefinition) beanDefinition).validate() {

                                };
                            } catch (BeanDefinitionValidationException ex) {
                                throw new 
                            }
                        }

                        // 如果已经存在bean的定义，那么根据
                        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
                        if (existingDefinition != null) {
                            if (!isAllowBeanDefinitionOverriding()) {
                                // 如果不允许同名的bean覆盖，则抛出异常
                                throw new ..
                            } else if (existingDefinition.getRole() < beanDefinition.getRole()) {

                            } else if (!beanDefinition.equals(existingDefinition)) {

                            } else {
                                // 覆盖同名bean定义
                                this.beanDefinitionMap.put(beanName, beanDefinition);
                            }
                         } else {
                            if (hasBeanCreationStarted()) {
                                synchronized (this.beanDefinitionMap) {
                                    this.beanDefinitionMap.put(beanName, beanDefinition);
                                    // ...
                                }
                            } else {
                                // 将bean定义存放到beanFactory中
                                this.beanDefinitionMap.put(beanName, beanDefinition);
                                // 已经解析了bean定义的bean名称
                                this.beanDefinitionNames.add(beanName);
                                this.manualSingletonNames.remove(beanName);
                            }

                            this.frozenBeanDefinitionNames = null;
                         }

                         if (existingDefinition != null || containsSingleton(beanName)) {
                             resetBeanDefinition(beanName);
                         }
                    };

                    // 注册bean的别名。实际上是添加从bean别名到bean名称的映射
                    // 当以后通过别名获取bean时，先根据bean别名查询bean名称，然后再根据bean名称获取bean
                    String[] aliases = definitionHolder.getAliases();
                    if (aliases != null) {
                        for (String alias : aliases) {
                            registry.registerAlias(beanName, alias);
                        }
                    }
                };

            }
        };
    };

    // 刷新context。这是spring ioc容器最为核心的方法了
    // 包括：解析注解配置类，扫描、解析bean，实例化bean
    refresh() {
        synchronized (this.startupShutdownMonitor) {    // 首先，取得排他锁，防止多个线程同时刷新或者关闭容器
            // context刷新的准备工作
            prepareRefresh() {
                this.startupDate = System.currentTimeMillis();  // context刷新时间
                this.closed.set(false); // context状态
                this.active.set(true);

                // 留给子类的回调。目前为空操作
                initPropertySources();
                // 验证必须有的属性是否已经解析了
                getEnvironment().validateRequiredProperties();

                this.earylyApplicationEvents = new LinkedHashSet<>();
            };

            // 获取beanFactory，对于不同类型的context，这个方法执行的操作会有不同
            // 对于AnnotationConfigApplicationContext来说，由于之前已经注册了一个注解配置类，beanFactory已经初始化。因此，这里直接返回beanFactory即可
            // 但是对于ClassPathXmlApplicationContext 和 AnnotationConfigWebApplicationContext 等这类可刷新的context来说，
            // 之前不管是设置xml路径，还是添加注解配置类，都没有初始化beanFactory。
            // 因此，这里需要初始化beanFactory，然后根据xml或者注解的配置，从classpath中扫描所有bean注册到beanFactory中。
            ConfigurableListBeanFactory beanFactory = obtainFreshBeanFactory() {
                // 对于一般类型的context来说，这个方法的内容很简单，即返回context持有的beanFactory即可
                // 但是，对于web类型的context来说，这个方法的内容会很复杂。它包括了
            };

            // 准备好beanFactory
            prepareBeanFactory(beanFactory) {
                beanFactory.setBeanClassLoader(getClassLoader());
                beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResovler(beanFactory.getBeanClassLoader()));
                beanFactory.addPropertyEditorRegister(new ResourceEditorRegister(this, getEnvironment()));

                beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
                beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

                // 添加忽略的依赖接口，即如果bean中有对这几个接口的依赖，那么context将会忽略注入这些依赖
                // context将以其它的方式解决这些依赖
                beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
                beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
                beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
                beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
                beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
                beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
                
                // 添加一些预配置的可解决的依赖，即如果bean中有对这几个类的依赖，那么context可以根据该配置解析并注入依赖关系
                // 例如，某个bean依赖了ApplicationContext，那么context就可以将当前context注入进去
                beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
                beanFactory.registerResolvableDependency(ResourceLoader.class, this);
                beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
                beanFactory.registerResolvableDependency(ApplicationContext.class, this);

                if (beanFactory.containBean("loadTimeWeaver")) {
                    beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
                    beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
                }

                if (!beanFactory.containsLocalBean("environment")) {
                    // 将Environment注册到beanFactory中
                    beanFactory.registerSingleton("environment", getEnvironment());
                }
                if (!beanFactory.containsLocalBean("systemProperties")) {
                    // 将System.getProperties()注册到beanFactory中
                    beanFactory.registerSingleton("systemProperties", getEnvironment().getSystemProperties());
                }
                if (!beanFactory.containsLocalBean("systemEnvironment")) {
                    // 将System.env()注册到beanFactory中
                    beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
                }
            };

            try {
                // 空
                postProcessBeanFactory(beanFactory);

                // 
                invokeBeanFactoryPostProcessors(beanFactory) {
                    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors()) {
                        // ConfigurationClassParser查找beanFactory中有@Configuration注解的类，然后解析它的所有注解，例如@ComponentScan
                        // 解析到@ComponentScan注解后，使用ClassPathBeanDefinitionScanner扫描目标路径下所有class文件，然后按照JVM关于class文件的规范读取class文件内容
                        // 如果该class

                    };

                    if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean("loadTimeWeaver")) {
                        
                    }
                };
            }
        }
    };
};

------------------------------------------------------------------------------------
AnnotationConfigApplicationContext applicationContext
    -- Object startupShutdownMonitor    // context刷新和关闭的锁
    // context是否已经刷新过了，默认false
    // 对于GenericApplicationContext来说，context只能刷新一次
    // 但是对于AbstractRefreshApplicationContext来说，context可以重复刷新
    -- AtomicBoolean refreshed
    // 环境变量
    -- Environment environment = new StandardEnvironment()
    -- DefaultListableBeanFactory beanFactory
            -- BeanFactory parentBeanFactory    // 父bean工厂
            -- ClassLoader beanClassLoader = new AppClassLoader()   // beanFactory类的加载器
            -- Map<String, BeanDefinition> beanDefinitionMap    // 用于存储bean定义的map
            // 用于检查bean定义是否是autowire候选者的解析器
            -- AutowireCandidateResolver autowireCandidateResolver = new ContextAnnotationAutowireCandidateResolver()
            -- Map<String, Object> singletonObjects     // beanFactory中存放的单例对象 <bean名称, bean单例对象>
            -- Set<String> registeredSingletons         // 已注册到beanFactory中的单例对象名称
            -- Map<String, BeanDefinition> beanDefinitionMap;   // bean定义 <bean名称, bean定义>
            -- List<String> beanDefinitionNames;    // 所有解析的bean名称列表
            -- List<BeanPostProcessor> beanPostProcessors;  // bean后继处理器列表。例如，处理ApplicationContextAware接口的处理器
            -- Set<Class<?>> ignoredDependencyInterfaces;
    
    // 注解配置bean解析与注册
    -- AnnotatedBeanDefinitionReader reader
            -- BeanDefinitionRegistry registry = applicationContext
            // 注册条件的解析器。解析@Conditional注解和自定义的ConfigurationCondition实现类
            -- ConditionEvaluator conditionEvaluator
            // bean作用域解析器。解析@Scope注解的方法返回值。
            // 如果bean上没有@Scope注解，则默认是singleton
            -- ScopeMetadataResovler scopeMetadataResolver = new AnnotationScopeMetadataResolver()
            // bean注册时名称生成器
            -- BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator()
    
    // bean class文件扫描，解析与注册
    -- ClassPathBeanDefinitionScanner scanner
            -- BeanDefinitionRegistry registry = applicationContext
            -- Environment environment = environment
            -- BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator()
            -- ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver()
            // 包含bean的类型过滤列表。需要扫描的注解列表，默认只有@Component。（还支持javaee的@ManagedBean和@Named注解）
            -- List<TypeFilter> includeFilters
            // 排除bean的类型过滤列表。默认排除@Configuration类
            -- List<TypeFilter> excludeFilters


------------------------------------------------------------------------------------
BeanFactory注册的bean

----- beanDefinitionMap -----
// 初始化AnnotatedBeanDefinitionReader时注册
org.springframework.context.annotation.internalConfigurationAnnotationProcessor -> class org.springframework.context.annotation.ConfigurationClassPostProcessor
org.springframework.context.annotation.internalAutowiredAnnotationProcessor     -> class org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
org.springframework.context.event.internalEventListenerFactory                  -> class org.springframework.context.event.DefaultEventListenerFactory
org.springframework.context.event.internalEventListenerProcessor                -> class org.springframework.context.event.EventListenerMethodProcessor
org.springframework.context.annotation.internalCommonAnnotationProcessor        -> class org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
// 开发者主动注册的注解配置类
application     -> class com.king.onlyone.Application

----- singletonObjects -----
org.springframework.context.annotation.internalConfigurationAnnotationProcessor -> ConfigurationClassPostProcessor
// prepareBeanFactory(beanFactory)时注册
systemProperties    -> Map<String, String> == System.getProperties()
environment         -> StandardEnvironment
systemEnvironment   -> Map<String, String> == System.env()

------------------------------------------------------------------------------------
AnnotatedBeanDefinition
    // 是否等到获取该bean的时候才初始化，来自@Lazy
    -- boolean lazyInit = false;
    // 来自@DependsOn
    -- String[] dependsOn;
    // 来自@Primary
    -- boolean primary = false;
    // 来自@Role
    -- int role = BeanDefinition.ROLE_APPLICATION;
    // 来自@Description
    -- String description;

------------------------------------------------------------------------------------
AnnotationScopeMetadataResolver
public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
    ScopeMetadata metadata = new ScopeMetadata();
    if (definition instanceof AnnotatedBeanDefinition) {
        AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
        // 获取bean上的@Scope注解
        AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(annDef.getMetadata(), Scope.class);
        if (attributes != null) {
            metadata.setScopeName(attributes.getString("value"));
            ScopedProxyMode proxyMode = attributes.getEnum("proxyMode");
            if (proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = this.defaultProxyMode;
            }
            metadata.setScopedProxyMode(proxyMode);
        }
    }
    return metadata;
}

------------------------------------------------------------------------------------
AnnotationBeanNameGenerator

// 生成bean的名称
public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
    if (definition instanceof AnnotatedBeanDefition) {
        // 如果该bean是一个基于注解配置的类，那么尝试获取该bean的@Component、@ManagedBean、@Named
        // 注解的value()方法返回值，如果该返回值是string且非空，则以该string作为bean的名称
        String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefintion) definition) {
            AnnotationMetadata amd = annotatedDef.getMetadata();
            Set<String> types = amd.getAnnotationTypes();
            String beanName = null;
            // 遍历注解
            for (String type : types) {
                AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(amd, type);
                // 判断注解是不是@Component、@ManagedBean、@Named，且是否有value()方法
                if (attributes != null && isStereotypeWithNameValue(type, amd.getMetaAnnotationTypes(type), attributes)) {
                    // 获取这些注解的value()方法返回值，如果返回值是string类型，
                    // 且返回值是非空字符，则以该名称作为bean名称
                    Object value = attributes.get("value");
                    if (value instanceof String) {
                        String strVal = (String) value;
                        if (StringUtils.hasLength(strVal)) {
                            if (beanName != null && !strVal.equals(beanName)) {
                                // 已经有名称了，抛出异常
                            }
                            beanName = strVal;
                        }
                    }
                }
            }
            return beanName;
        };
        if (StringUtils.hasText(beanName)) {
            return beanName;
        }
    }
    // 默认生成beanFactory全局唯一的bean名称
    return buildDefaultBeanName(definition, registry) {
        // 含有路径的全限定的类名
        String beanClassName = definition.getBeanClassName();
        // 类名
        String shortClassName = ClassUtils.getShortName(beanClassName) {
            int lastDotIndex = className.lastIndexOf(".");
            // 处理cglib生成类的带有$$字符的类名称 
            int nameEndIndex = className.indexOf("$$");
            if (nameEndIndex == -1) {
                nameEndIndex = className.length();
            }
            String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
            shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
            return shortName;
        };
        // 返回首字母小写
        return Introspector.decapitialize(shortClassName);
    };
}

// 判断注解是不是@Component、@ManagedBean、@Named，且是否有value()方法
protected boolean isStereotypeWithNameValue(String annotationType,
			Set<String> metaAnnotationTypes, @Nullable Map<String, Object> attributes) {
    boolean isStereotype = annotationType.equals("org.springframework.stereotype.Component") ||
            metaAnnotationTypes.contains("org.springframework.stereotype.Component") ||
            annotationType.equals("javax.annotation.ManagedBean") ||
            annotationType.equals("javax.inject.Named");

    return (isStereotype && attributes != null && attributes.containsKey("value"));
}