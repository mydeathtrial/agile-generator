package cloud.agileframework.generator.config;

import cloud.agileframework.generator.handler.EntityGenerator;
import cloud.agileframework.generator.handler.ServiceGenerator;
import cloud.agileframework.generator.handler.SwaggerGenerator;
import cloud.agileframework.generator.properties.GeneratorProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/11 13:52
 * 描述： TODO
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(value = {GeneratorProperties.class, DataSourceProperties.class})
public class GeneratorConfig {
    @Bean
    public EntityGenerator entityGenerator(){
        return new EntityGenerator();
    }
    @Bean
    public ServiceGenerator serviceGenerator(){
        return new ServiceGenerator();
    }
    @Bean
    public SwaggerGenerator swaggerGenerator(){
        return new SwaggerGenerator();
    }
}
