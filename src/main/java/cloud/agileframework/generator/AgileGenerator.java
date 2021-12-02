package cloud.agileframework.generator;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.db.DataBaseUtil;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.common.util.properties.PropertiesUtil;
import cloud.agileframework.generator.config.GeneratorConfig;
import cloud.agileframework.generator.handler.ByAllTableGenerator;
import cloud.agileframework.generator.handler.ByTableGenerator;
import cloud.agileframework.generator.model.TableModel;
import cloud.agileframework.generator.properties.GeneratorProperties;
import cloud.agileframework.spring.util.BeanUtil;
import com.google.common.collect.Lists;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.context.support.PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * @author mydeathtrial on 2017/4/20
 */

public class AgileGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AgileGenerator.class);
    private static DataSourceProperties dataSourceProperties;
    private static GeneratorProperties generator;

    /**
     * 取数据库中所有表信息
     *
     * @return 所有表信息
     */
    private static List<Map<String, Object>> getTableInfo(String tables) {
        return DataBaseUtil.listTables(dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword(),
                tables);
    }

    /**
     * 初始化spring容器
     */
    private static void initSpringContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        BeanUtil.setApplicationContext(context);
        StandardEnvironment environment = new StandardEnvironment();
        PropertySource<?> localPropertySource = new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, PropertiesUtil.getProperties());
        environment.getPropertySources().addLast(localPropertySource);

        context.setEnvironment(environment);
        context.register(GeneratorConfig.class);
        context.refresh();

        dataSourceProperties = BeanUtil.getBean(DataSourceProperties.class);
        generator = BeanUtil.getBean(GeneratorProperties.class);
    }

    /**
     * 初始化环境
     */
    public static void init() {
        initSpringContext();
    }

    /**
     * 生成器
     */
    static void generator() {
        String tableNames = generator.getTableName();
        if (tableNames.contains(";")) {
            Arrays.stream(tableNames.split(";")).forEach(AgileGenerator::generator);
        }

        generator(tableNames);
    }

    private static void generator(String tableNames) {
        List<Map<String, Object>> tables = getTableInfo(tableNames);
        if (tables == null || tables.isEmpty()) {
            throw new RuntimeException("未加载到任何数据库表信息");
        }
        List<TableModel> allTableModel = Lists.newArrayList();
        for (Map<String, Object> table : tables) {
            TableModel tableModel = ObjectUtil.to(table, new TypeReference<TableModel>() {
            });

            BeanUtil.getApplicationContext()
                    .getBeanProvider(ByTableGenerator.class)
                    .stream().filter(g -> g.is(generator.getTypes()))
                    .forEach(g -> {
                        try {
                            g.generateFile(tableModel);
                            allTableModel.add(tableModel);
                        } catch (TemplateException | IOException e) {
                            e.printStackTrace();
                        }
                    });

        }

        BeanUtil.getApplicationContext()
                .getBeanProvider(ByAllTableGenerator.class)
                .stream().filter(g -> g.is(generator.getTypes()))
                .forEach(g -> {
                    try {
                        g.generateFile(allTableModel);
                    } catch (TemplateException | IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void main(String[] args) {
        try {
            logger.info("【1】开始生成源代码");
            init();
            logger.info("【2】完成配置初始化，开始生成文件...");
            generator();
            logger.info("【3】完成源代码生成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
