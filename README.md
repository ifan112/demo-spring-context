# Condition相关用法

spring在注册bean时，允许开发者自定义注册条件，即只在满足某些条件时才注册该bean，不符合指定条件的情况下取消注册bean。该特性关联的有@Conditional注解与ConfigurationCondition接口。

#### 用法

自定义类实现ConfigurationCondition接口，重写matches和getConfigurationPhase方法。

```java
class OrderServiceCondition implements ConfigurationCondition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 这里简单点返回false，表示任何条件都不满足，不要注册该bean
        return false;
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        // 判断条件发生的阶段，这里是在配置阶段判断是否满足条件
        return ConfigurationPhase.PARSE_CONFIGURATION;
    }
}
```

然后，在待注册的bean class上使用注解@Conditional，设置该注解的value方法返回值为自定义类。

```java
@Service
@Conditional(OrderServiceCondition.class)
public class OrderService {
}
```

此后，在向beanFactory注册该bean时，spring会首先检查是否满足注册条件。不满足（即matches方法返回false）就取消注册。

### 源码解析

```java
public class AnnotatedBeanDefinitionReader {
    // 条件评估器
    private ConditionEvaluator conditionEvaluator;
    
    public void registerBean(Class<?> annotatedClass) {
        doRegisterBean(annotatedClass, null, null, null);
    }
    
    <T> void doRegisterBean(Class<T> annotatedClass, 
                            @Nullable Supplier<T> instanceSupplier, 
                            @Nullable String name,
                            @Nullable Class<? extends Annotation>[] qualifiers,
                            BeanDefinitionCustomizer... definitionCustomizers) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);
        
        // 判断当前是否满足bean的注册条件
        // if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            // 不满足不注册，直接返回
            return;
        }
        
        // ... 继续注册bean
}

public class ConditionEvaluator { 
    public boolean shouldSkip(AnnotatedTypeMetadata metadata) { 
        return shouldSkip(metadata, null); 
    }
    
    public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata,
                              @Nullable ConfigurationPhase phase) {
        if (metadata == null || !metadata.iaAnnotated(Conditional.class.getName())) {
            // 如果bean没有@Conditional注解，那么不用判断直接注册该bean
            return false;
        }
        
        if (phase == null) {
            if (metadata instanceof AnnotationMetadata &&
                ConfigurationClassUtils.isConfigurationCandidate(metadata)) {
                // 如果该bean是一个注解类，且有@Configuration注解，则猜测是解析配置阶段
                return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
            }
            // 否则，是注册bean阶段
            return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
        }
        
        List<Condition> conditions = new ArrayList<>();
        // 遍历bean上的@Conditional注解使用Condition实现类名称
        for (String[] conditionClasses : getConditionClasses(metadata)) {
            for (String conditionClass : conditionClasses) {
                // 加载Condition实现类
                Condition condition
                    = getCondition(conditionClass, this.context.getClassLoader());
                conditions.add(condition);
            }
        }
        
        // 对条件列表排序
        AnnotationAwareOrderComparator.sort(conditions);
        
        for (Condition condition : conditions) {
            ConfigurationPhase requiredPhase = null;
            if (condition instanceof CondigurationCondition) {
                requiredPhase 
                    = ((ConfigurationCondition) condition).getConfigurationPhase();
            }
            if ((requiredPhase == null || requiredPhase == phase)
                && !condition.matches(this.context, metadata)) {	// 判断当前条件是否满足
                // 如果当前条件不满足，则返回true，表示要跳过注册bean
                return true;
            }
        }
        
        return false;
    }
}
```



# bean名称

开发者在使用@Service，@Component等等注解时，可以指定bean名称。如果没有指定，spring容器将会生成一个默认的bean名称。默认规则通常是首字母小写的bean类名称。

#### 源码解析

```java
public class AnnotationBeanNameGenerator implements BeanNameGenerator { 
    // 生成bean的名称
    public String generateBeanName(BeanDefinition definition, 
                                   BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefition) {
            // 如果该bean是一个基于注解配置的类，那么尝试获取该bean的@Component、@ManagedBean、@Named
            // 注解的value()方法返回值，如果该返回值是string且非空，则以该string作为bean的名称
            String beanName 
                = determineBeanNameFromAnnotation((AnnotatedBeanDefintion) definition);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }

        // 默认生成beanFactory全局唯一的bean名称
        return buildDefaultBeanName(definition);
    }
    
    @Override
    protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
        AnnotationMetadata amd = annotatedDef.getMetadata();
        Set<String> types = amd.getAnnotationTypes();
        String beanName = null;
        // 遍历注解
        for (String type : types) {
            AnnotationAttributes attributes 
                = AnnotationConfigUtils.attributesFor(amd, type);
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
    }

    // 判断注解是不是@Component、@ManagedBean、@Named，且是否有value()方法
    protected boolean isStereotypeWithNameValue(String annotationType,
                                                Set<String> metaAnnotationTypes,
                                                @Nullable Map<String, Object> attributes) {
    	boolean isStereotype 
        	= annotationType.equals("org.springframework.stereotype.Component") ||
            	metaAnnotationTypes.contains("org.springframework.stereotype.Component") ||
            	annotationType.equals("javax.annotation.ManagedBean") ||
            	annotationType.equals("javax.inject.Named");

    	return (isStereotype && attributes != null && attributes.containsKey("value")); 
    }
    
    // 创建默认的bean名称
    protected String buildDefaultBeanName(BeanDefinition definition) {
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
    }
}
```

