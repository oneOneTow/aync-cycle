1. @Async 代理对象加载过程
```aidl
1.  doGetBean-->CycleAImple
        |___
            |
            V
         getSigleton--> singletonObjects-> earlySingletonObjects-> singletonFactories
             |
             V
         doCreateBean--> singletonFactories.put("cycleAImple",() -> { getEarlyBeanReference(beanName, mbd, bean) })
             |
             V
         populateBean()--> 
             |
             V
         AutowiredAnnotationBeanPostProcessor.postProcessPropertyValues --> 自动注入属性
             |
             V
         org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate --> 
             |
             v
         doGetBean-->CycleBImple
             |
             V
         doCreateBean
             |
             V
         populateBean --> 注入CycleAImple
             |
             V
         getSigleton --> singletonFactories ==> ObjectFactory<Object>().getObject->AbstractAutoProxyCreator.getEarlyBeanReference(@Async注解不能被AbstractAutoProxyCreator代理) --> 返回没被代理的CycleAImple对象c1
             |
             V
         initializeBean --> AsyncAnnotationBeanPostProcessor.postProcessAfterInitialization 创建@Async代理对象c2
         ___|
        |
        V
    exposedObject(c2) != bean(c1) 报错
2.@Transactional 代理对象加载过程
1.  doGetBean-->CycleAImple
        |___
            |
            V
         getSigleton--> singletonObjects-> earlySingletonObjects-> singletonFactories
             |
             V
         doCreateBean--> singletonFactories.put("cycleAImple",() -> { getEarlyBeanReference(beanName, mbd, bean) })
             |
             V
         populateBean()--> 
             |
             V
         AutowiredAnnotationBeanPostProcessor.postProcessPropertyValues --> 自动注入属性
             |
             V
         org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate --> 
             |
             v
         doGetBean-->CycleBImple
             |
             V
         doCreateBean
             |
             V
         populateBean --> 注入CycleAImple
             |
             V
         getSigleton --> singletonFactories ==> ObjectFactory<Object>().getObject->AbstractAutoProxyCreator.getEarlyBeanReference -->earlyProxyReferences.add("CycleAImple")--> 创建代理对象c1
             |
             V
         initializeBean --> AbstractAutoProxyCreator.postProcessAfterInitialization --> true == earlyProxyReferences.contains("CycleAImple") 直接return原始对象c1
         ___|
        |
        V
    exposedObject(c2) == bean(c1) 没有循环依赖
                                                                                
```

上述是@Async和@Transactional创建代理对象的过程
* @Async是使用AsyncAnnotationBeanPostProcessor创建代理对象
* @Transactional是使用AbstractAutoProxyCreator创建代理对象，由于实现了SmartInstantiationAwareBeanPostProcessor.getEarlyBeanReference

实现代理的两种方式
* Advisor + Advice + Pointcut
```aidl
Advisor: 切面器 
Advice: 切面
Pointcut: 定义切点 常用实现类StaticMethodMatcherPointcut根据方法的matchs判断是否为切点

1. @Async使用
Advisor: AsyncAnnotationAdvisor
Advice: AnnotationAsyncExecutionInterceptor
Pointcut: AnnotationMatchingPointcut
AsyncAnnotationAdvisor并没有托管给spring所以AbstractAutoProxyCreator不会为@Async创建代理对象
AsyncAnnotationBeanPostProcessor（ProxyAsyncConfiguration）会new AsyncAnnotationAdvisor();
AsyncAnnotationBeanPostProcessor作为bean后处理器会在bean初始化后根据维护的AsyncAnnotationAdvisor创建代理对象

2. @Transactional
Advisor: TransactionAttributeSourceAdvisor
Pointcut: TransactionAttributeSourcePointcut
Advice: TransactionInterceptor
使用ProxyTransactionManagementConfiguration将Advisor托管spring
AbstractAutoProxyCreator 在bean初始化后会 查所有已经托管给sping的Advisor实现类 判断是不是需要创建代理对象
所以为注入Advisor是不能够使用AbstractAutoProxyCreator创建代理实现切面
```
* @Aspect

3. aop