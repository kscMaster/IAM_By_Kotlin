package nancal.iam.base.config

import nbcp.comm.ToJson
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils
import java.io.IOException

import org.springframework.beans.factory.FactoryBean
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


/**
 * 用于Spring动态注入自定义接口
 * @author lichuang
 */
@Component
class ServiceBeanDefinitionRegistry : BeanDefinitionRegistryPostProcessor, ResourceLoaderAware,
    ApplicationContextAware {
    @Throws(BeansException::class)
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        //这里一般我们是通过反射获取需要代理的接口的clazz列表
        //比如判断包下面的类，或者通过某注解标注的类等等
        val beanClazzs = scannerPackages("com.nancal.integration.service")
        for (beanClazz in beanClazzs) {
            val builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz)
            val definition = builder.rawBeanDefinition as GenericBeanDefinition

            //在这里，我们可以给该对象的属性注入对应的实例。
            //比如mybatis，就在这里注入了dataSource和sqlSessionFactory，
            // 注意，如果采用definition.getPropertyValues()方式的话，
            // 类似definition.getPropertyValues().add("interfaceType", beanClazz);
            // 则要求在FactoryBean（本应用中即ServiceFactory）提供setter方法，否则会注入失败
            // 如果采用definition.getConstructorArgumentValues()，
            // 则FactoryBean中需要提供包含该属性的构造方法，否则会注入失败
            definition.instanceSupplier

            //注意，这里的BeanClass是生成Bean实例的工厂，不是Bean本身。
            // FactoryBean是一种特殊的Bean，其返回的对象不是指定类的一个实例，
            // 其返回的是该工厂Bean的getObject方法所返回的对象。
            definition.beanClass = ServiceFactory::class.java

            //这里采用的是byType方式注入，类似的还有byName等
            definition.autowireMode = GenericBeanDefinition.AUTOWIRE_BY_TYPE
            registry.registerBeanDefinition(beanClazz.simpleName, definition)
        }
    }

    private var metadataReaderFactory: MetadataReaderFactory? = null

    /**
     * 根据包路径获取包及子包下的所有类
     * @param basePackage basePackage
     * @return Set<Class></Class>> Set<Class></Class>>
     */
    private fun scannerPackages(basePackage: String): Set<Class<*>> {
        val set: MutableSet<Class<*>> = LinkedHashSet()
        val packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
            resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN
        try {
            val resources = resourcePatternResolver!!.getResources(packageSearchPath)
            for (resource in resources) {
                if (resource.isReadable) {
                    val metadataReader = metadataReaderFactory!!.getMetadataReader(resource)
                    val className = metadataReader.classMetadata.className
                    var clazz: Class<*>
                    try {
                        clazz = Class.forName(className)
                        set.add(clazz)
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return set
    }

    protected fun resolveBasePackage(basePackage: String?): String {
        return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage))
    }

    @Throws(BeansException::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
    }

    private var resourcePatternResolver: ResourcePatternResolver? = null
    private var applicationContext: ApplicationContext? = null
    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
        metadataReaderFactory = CachingMetadataReaderFactory(resourceLoader)
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    private val environment: Environment
        private get() = applicationContext!!.environment

    companion object {
        private const val DEFAULT_RESOURCE_PATTERN = "**/*.class"
    }
}


/**
 * 接口实例工厂，这里主要是用于提供接口的实例对象
 * @author lichuang
 * @param <T>
</T> */
class ServiceFactory<T>(private val interfaceType: Class<T>) : FactoryBean<T> {
    @Throws(Exception::class)
    override fun getObject(): T {
        //这里主要是创建接口对应的实例，便于注入到spring容器中
        val handler: InvocationHandler = ServiceProxy(interfaceType)
        return Proxy.newProxyInstance(interfaceType.classLoader, arrayOf<Class<*>>(interfaceType), handler) as T
    }

    override fun getObjectType(): Class<T> {
        return interfaceType
    }

    override fun isSingleton(): Boolean {
        return true
    }
}


class ServiceProxy<T>(var intefaceType: Class<T>?) : InvocationHandler {


    @Throws(Throwable::class)
    override operator fun invoke(proxy: Any?, method: Method, args: Array<Any?>): Any {

        println("调用前，参数：{}$args")
        //这里可以得到参数数组和方法等，可以通过反射，注解等，进行结果集的处理
        //mybatis就是在这里获取参数和相关注解，然后根据返回值类型，进行结果集的转换
        val result: Any = args.ToJson()
        println("调用后，结果：{}$result")
        return result
    }
}
