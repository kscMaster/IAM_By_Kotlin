//package nancal.iam.base.config
//
//import org.springframework.boot.context.properties.ConfigurationProperties
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.stereotype.Component
//import org.mybatis.spring.SqlSessionTemplate
//import org.apache.ibatis.session.SqlSessionFactory
//import org.mybatis.spring.SqlSessionFactoryBean
//import org.springframework.jdbc.datasource.DataSourceTransactionManager
//import org.springframework.transaction.PlatformTransactionManager
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.transaction.annotation.TransactionManagementConfigurer
//import org.springframework.transaction.annotation.EnableTransactionManagement
//import javax.sql.DataSource
//import org.mybatis.spring.mapper.MapperScannerConfigurer
//import org.springframework.boot.autoconfigure.AutoConfigureAfter
//import QueryInterceptor
//import UpdateInterceptor
//
//
//@Component
//@ConfigurationProperties("spring.datasource.dbcp2")
//class MySqlConfig {
//
//    var driverClassName = "com.mysql.jdbc.Driver"
//    var username = ""
//    var password = ""
//    var url = ""
//    var maxTotal = 128;
//    var maxIdel = 128;
//    var maxWaitMillis = 180000L
//
//    @Bean
//    fun dataSource(): BasicDataSource {
//        println("BasicDataSource inited: ${url}")
//        val dataSource = BasicDataSource()
//        dataSource.driverClassName = driverClassName
//        dataSource.url = url
//        dataSource.username = username
//        dataSource.password = password
//        dataSource.maxTotal = maxTotal
//        dataSource.maxIdle = maxIdel
//        dataSource.maxWaitMillis = maxWaitMillis
//        dataSource.setValidationQuery("SELECT 1")
//        dataSource.testOnBorrow = true
//        return dataSource
//    }
//
//}
//
//
//@Component
//@AutoConfigureAfter(MySqlConfig::class)
//class MyBatisSessionConfig {
//    @Bean
//    fun mapperScannerConfigurer(): MapperScannerConfigurer {
//        val mapperScannerConfigurer = MapperScannerConfigurer()
//        //获取之前注入的beanName为sqlSessionFactory的对象
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory")
//        //指定xml配置文件的路径
//        mapperScannerConfigurer.setBasePackage("nbcp.db.mybatis.mapper")
//        return mapperScannerConfigurer
//    }
//}
//
//
//@Configuration
////加上这个注解，使得支持事务
//@EnableTransactionManagement
//open class MyBatisConfig : TransactionManagementConfigurer {
//
//    @Autowired
//    private var dataSource: DataSource? = null
//
//    override fun annotationDrivenTransactionManager(): PlatformTransactionManager {
//        return DataSourceTransactionManager(dataSource!!)
//    }
//
//    @Bean(name = arrayOf("sqlSessionFactory"))
//    open fun sqlSessionFactoryBean(): SqlSessionFactory? {
//        val bean = SqlSessionFactoryBean()
//        bean.setDataSource(dataSource)
//
//        var config = org.apache.ibatis.session.Configuration()
//        config.isCacheEnabled = true
//
////        config.addCache(RedisCacheMyBatis())
//
//        config.addInterceptor(QueryInterceptor())
//        config.addInterceptor(UpdateInterceptor())
//
//        bean.setConfiguration(config)
//        try {
//            return bean.`object`
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw RuntimeException(e)
//        }
//
//    }
//
//    @Bean
//    open fun sqlSessionTemplate(sqlSessionFactory: SqlSessionFactory): SqlSessionTemplate {
//        return SqlSessionTemplate(sqlSessionFactory)
//    }
//}