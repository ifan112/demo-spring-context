AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Application.class) {
    this() {
        // 初始化context内部的AnnotatedBeanDefinitionReader和ClassPathBeanDefinitionScanner类
        // 在初始化reader时，向beanFactory注册了一些spring内部类
        this.reader = new AnnotatedBeanDefinitionReader(this) {
            this(applicationContext, getOrCreateEnvironment(applicationContext)) {
                // AnnotatedBeanDefinitionReader所属的context
                this.registry = applicationContext;
                // 注册条件解析器，用于判断当前是否满足bean操作的条件
                this.conditionEvaluator = new ConditionEvaluator(applicationContext, environment, null);

                // 向context中注册后面将会用到的一些bean，如beanFactory的后置处理器
                AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext) {
                    registerAnnotationConfigProcessors(applicationContext, Object source = null) {
                        // 获取context持有的beanFactory
                        DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(applicationContext);

                        if (beanFactory != null) {
                            // 设置beanFactory内部组件
                            if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
                                beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
                            }
                            if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
                                beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
                            }
                        }

                        Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);
                        
                        // CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME
                        // = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
                        if (!context.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
                            // 对于spring内部的bean，使用RootBeanDefinition类来保存该bean的定义
                            // 而对于开发者的bean，使用GenericBeanDefinition类来保存bean的定义
                            // ConfigurationClassPostProcessor是用于在后置处理beanFactory时，
                            // 即context刷新过程中invokeBeanFactoryPostProcessors方法，解析@Configuration类的
                            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
                            def.setSource(source);
                            // 将ConfigurationClassPostProcessor注册到beanFactory中
                            beanDefs.add(registerPostProcessor(applicationContext, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
                        }

                        // 注册其它spring内部bean，暂时不分析

                        return beanDefs;
                    }
                };
            }
        };
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
            // 之前不管是设置xml路径，还是添加注解配置类，都没有初始化beanFactory，因此在这里必须要初始化beanFactory。
            // 不过，对于ClassPathXmlApplicationContext和AnnotationConfigWebApplicationContext两种不同类型的context来说，这里完成的操作并不完全一样。
            // 对于ClassPathXmlApplicationContext来说，在这一步中自然是解析xml配置文件，解析其中所有注册的bean，然后将这些beanDefinition注册到beanFactory中。
            // 然而，对于AnnotationConfigWebApplicationContext来说，在这一步中注册之前指定的注解配置类，如果设置了扫描路径，则扫描指定路径下的bean，然后注册到beanFactory中。
            ConfigurableListBeanFactory beanFactory = obtainFreshBeanFactory() {
                // 对于AnnotationConfigApplicationContext来说，这一步仅仅是返回之前已经初始化，并且注册了注解配置类的beanFactory。
                // 对于ClassPathXmlApplicationContext来说，这一步创建beanFactory，然后读取xml配置文件，解析其中注册的bean，然后将这些beanDefinition注册到beanFactory中。
            };

            // 对于AnnotationConfigApplicationContext 和 AnnotationConfigWebApplicationContext来说，此时，beanFactory中只有注解配置类（spring内部类不考虑）。
            // 稍后，在invokeBeanFactoryPostProcessors()方法中，会有注解配置类的处理器，解析配置类上的注解，将其它待注册的bean注册到beanFactory中。
            // 对于ClassPathXmlApplicationContext来说，此时，beanFactory中已经注册有所有bean了。

            // 准备好beanFactory
            prepareBeanFactory(beanFactory) {
                beanFactory.setBeanClassLoader(getClassLoader());
                beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResovler(beanFactory.getBeanClassLoader()));
                beanFactory.addPropertyEditorRegister(new ResourceEditorRegister(this, getEnvironment()));

                // 添加bean创建后的后置处理器，用于在创建标准bean之后，对bean进行自定义设置
                // 以ApplicationContextAwareProcessor为例，在bean创建之后，该处理器会检查bean是否实现了ApplicationContextAware接口，
                // 如果bean实现了该接口，则表明bean希望在创建之后，获取到ApplicationContext以对bean进行自定义设置。
                beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
                // 检查bean是否是ApplicationListener的处理器
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
                // 留给子类重写以自定义的beanFactory的后置处理操作
                // 对于AnnotationConfigApplicationContext和ClassPathXmlApplicationContext来说，这是个空操作
                // 但是对于AnnotationConfigWebApplicationContext来说，这个操作包括了添加ServletContextAwareProcessor等
                postProcessBeanFactory(beanFactory);

                // 在创建beanFactory之后，调用beanFactory的后置处理器，对beanFactory中的beanDefinition进行处理。
                // 例如，对于AnnotationConfigApplicationContext来说，当前beanFactory中有注解配置类。
                // 那么就需要一个用于解析注解配置类的beanFactory后置处理器来解析该注解配置类，以获取开发者自定义的配置信息，进一步完成context初始化
                // beanFactory的后置处理器BeanFactoryPostProcessor又分为了：BeanFactoryPostProcessor和BeanDefinitionRegistryPostProcessor
                invokeBeanFactoryPostProcessors(beanFactory) {
                    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, 
                        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = getBeanFactoryPostProcessors()) {
                        // beanFactoryPostProcessors通常是开发者自定义的beanFactory后置处理器列表

                        Set<String> processBeans = new HashSet<>();

                        ///////////////////////////// 首先，调用BeanDefinitionRetisryPostProcessor /////////////////////////////

                        if (beanFactory instanceof BeanDefinitionRegistry) {
                            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

                            // 开发者自定义的BeanFactoryPostProcessor列表
                            List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
                            // 所有的BeanDefinitionRegistryPostProcessor列表，包括开发者自定义的和spring内部的
                            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

                            // 对开发者自定义的BeanFactoryPostProcess分类。并且，触发开发者自定义的BeanDefinitionRegistryPostProcessor
                            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                                    BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
                                    // 触发开发者自定义的BeanDefinitionRegistryPostProcessor
                                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                                    registryProcessors.add(registryPostProcessor);
                                } else {
                                    regularPostProcessors.add(postProcessor);
                                }
                            }

                            List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();


                            // --------------- 首先，调用实现了PriorityOrdered接口的BeanDefinitionRetisryPostProcessor --------------- //

                            // 获取beanFactory中已经注册的spring内部BeanDefinitionRegistryPostProcessor的实现类名称
                            // 主要是来自之前spring注册的内部beanFactory后置处理器。例如，对于AnnotationConfigApplicationContext来说，
                            // 在创建AnnotationBeanDefinitionReader时，向beanFactory中注册了ConfigurationClassPostProcessor这个beanFactory的后置处理器
                            String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                            for (String ppName : postProcessorNames) {
                                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                                    // 获取实现了PriorityOrdered接口的实现类的实例，并且添加到currentRegistryProcessors列表中
                                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                                    processedBeans.add(ppName);
                                }
                            }

                            // 对后置处理器排序
                            sortPostProcessors(currentRegistryProcessors, beanFactory);
                            // 至此，registryProcessors包含了开发者自定义和spring内部的所有BeanDefinitionRegistryPostProcessor列表
                            registryProcessors.addAll(currentRegistryProcessors);
                            // 以默认情况为例，至此，currentRegistryProcessors和registryProcessors列表中只有ConfigurationClassPostProcessor
                            // 触发spring内部的BeanDefinitionRegistryPostProcessor
                            // 即，调用ConfigurationClassPostProcessor对注册到beanFactory中的注解配置类进行解析
                            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry) {
                                // 遍历BeanDefinitionRegistryPostProcessor列表，对beanFactory中已注册的beanDefinition进行后置处理
                                for (BeanDefinitionRegistryPostProcessor postProcessor : currentRegistryProcessors) {
                                    postProcessor.postProcessBeanDefinitionRegistry(registry) {
                                        // 下面是ConfigurationClassPostProcessor的postProcessBeanDefinitionRegistry方法

                                        // 可作为配置类的beanDefinition列表
                                        List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
                                        // beanFactory中当前已注册的bean名称列表
                                        String[] candidateNames = registry.getBeanDefinitionNames();

                                        // 遍历beanFactory中当前已注册的bean名称列表，找到可作为配置类的beanDefinition
                                        for (String beanName : candidateNames) {
                                            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
                                            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef)
                                                    || ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
                                                // logger
                                            } else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                                                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
                                            }
                                        }

                                        // 如果没有@Configuration注解的类，那么直接返回
                                        if (configCandidates.isEmpty()) {
                                            return;
                                        }

                                        // 对配置类进行排序
                                        configCandidates.sort((bd1, bd2) -> {
                                            int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
                                            int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
                                            return Integer.compare(i1, i2);
                                        });

                                        // ...其它准备操作

                                        // 构造一个@Configuration注解配置类的解析器
                                        ConfigurationClassParse parser = new ConfigurationClassParse(..)

                                        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
                                        Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
                                        do {
                                            // 解析注解配置类，并且注册bean
                                            parser.parse(candidates) {

                                            };
                                            parser.validate();

                                            // 解析过程中得到的所有可配置类，包括所有@Component注解主键类
                                            Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
                                            configClasses.removeAll(alreadyParsed);

                                            if (this.reader == null) {
                                                this.reader = new ConfigurationClassBeanDefinitionReader(...);
                                            }

                                            this.reader.loadBeanDefinition(configClasses);
                                            alreadyParsed.addAll(configClasses);

                                            candidates.clear();

                                            // ...
                                        } while (!candidates.isEmpty());

                                        // ...
                                    };
                                }
                            };
                            // 清空
                            currentRegistryProcessors.clear();


                            // --------------- 然后，调用实现了Ordered接口的BeanDefinitionRetisryPostProcessor --------------- //

                            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                            for (String ppName : postProcessorNames) {
                                if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                                    processedBeans.add(ppName);
                                }
                            }
                            sortPostProcessors(currentRegistryProcessors, beanFactory);
                            registryProcessors.addAll(currentRegistryProcessors);
                            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
                            currentRegistryProcessors.clear();


                            // --------------- 然后，调用所有的BeanDefinitionRetisryPostProcessor --------------- //

                            boolean reiterate = true;
                            while (reiterate) {
                                reiterate = false;
                                postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                                for (String ppName : postProcessorNames) {
                                    if (!processedBeans.contains(ppName)) {
                                        currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                                        processedBeans.add(ppName);
                                        reiterate = true;
                                    }
                                }
                                sortPostProcessors(currentRegistryProcessors, beanFactory);
                                registryProcessors.addAll(currentRegistryProcessors);
                                invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
                                currentRegistryProcessors.clear();
                            }

                            invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
                            invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);

                        } else {
                            invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
                        }

                        ///////////////////////////// 然后，调用BeanFactoryPostProcessor /////////////////////////////

                        // ... 待分析

                    };

                    if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean("loadTimeWeaver")) {
                        
                    }
                };

                // 注册bean的后置处理器。当bean初始化之后，调用这些后置处理器，对bean进行处理
                registerBeanPostProcessors(beanFactory);

                initMessageSource();

                // 初始化应用事件发布器
                initApplicationEventMulticaster();

                onRefresh();

                // 初始化应用事件监听器
                registerListeners();

                // 初始化所有注册的bean
                finishBeanFactoryInitialization(beanFactory);

                finishRefresh();
            } catch (BeanException ex) {
                // 刷新context过程中出现异常

                // logger 记录日志
                // 销毁bean
                destoryBeans();
                // 设置context状态为非active状态
                cancelRefresh(ex);

                // 抛出该异常
                throw ex;
            } finally {
                resetCommonCaches();
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
    // BeanFactory的后置处理器，默认为空。可以通过context.addBeanFactoryProcessor()注册
    // 当context刷新时，invokeBeanFactoryPostProcessors()方法会调用BeanFactory的后置处理器
    -- List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
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
            -- Comparator<Object> dependencyComparator; // 依赖比较器。在初始化AnnotationBeanDefinitionReader时，设置了该字段的值是一个AnnotationAwareOrderComparator对象
    
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
AnnotatedGenericBeanDefinition
    // bean的类类型，这里为什么不用Class而是Object？
    -- volatile Object beanClass = Class<?>
    // bean的注解元信息
    -- AnnotationMetadata metadata = new StandaraAnnotationMetadata();
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