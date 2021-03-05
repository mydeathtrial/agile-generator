package cloud.agileframework.generator;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.db.DataBaseUtil;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.common.util.properties.PropertiesUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.config.GeneratorConfig;
import cloud.agileframework.generator.model.TableModel;
import cloud.agileframework.generator.properties.GeneratorProperties;
import cloud.agileframework.generator.util.FreemarkerUtil;
import cloud.agileframework.spring.util.BeanUtil;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static org.springframework.context.support.PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME;

/**
 * @author mydeathtrial on 2017/4/20
 */

public class AgileGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AgileGenerator.class);
    private static final String ENTITY_FTL = "Entity.ftl";
    private static final String SERVICE_FTL = "Service.ftl";
    private static final String FILE_EXTENSION = ".java";
    private static DataSourceProperties dataSourceProperties;
    private static GeneratorProperties generator;


    /**
     * 推测生成java文件的包名
     *
     * @param url 生成目标文件存储路径
     * @return 包名
     */
    static String getPackPath(String url) {
        url = parseUrl(url);
        String javaSourceUrl = parseUrl(generator.getJavaSourceUtl());
        if (StringUtil.isEmpty(javaSourceUrl)) {
            String javaPath = File.separator + "java" + File.separator;
            if (!url.contains(javaPath)) {
                return null;
            }
        }
        url = url.substring(url.indexOf(javaSourceUrl) + javaSourceUrl.length());
        if (url.length() > 0) {
            return url.substring(0, url.length() - 1).replaceAll(Matcher.quoteReplacement(File.separator), ".");
        }
        return null;
    }

    /**
     * 取数据库中所有表信息
     *
     * @return 所有表信息
     */
    private static List<Map<String, Object>> getTableInfo() {
        return DataBaseUtil.listTables(dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword(),
                generator.getTableName());
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
     * 统一路径中的斜杠
     *
     * @param str 路径
     * @return 处理后的合法路径
     */
    private static String parseUrl(String str) {
        String url = str.replaceAll("[\\\\/]+", Matcher.quoteReplacement(File.separator));
        if (!url.endsWith(File.separator)) {
            url += File.separator;
        }
        return url;
    }

    /**
     * 生成实体文件
     *
     * @param tableModel 表信息集
     * @throws IOException       异常
     * @throws TemplateException 异常
     */
    private static void generateEntityFile(TableModel tableModel) throws IOException, TemplateException {
        String url = parseUrl(generator.getEntityUrl());
        String fileName = tableModel.getEntityName() + FILE_EXTENSION;
        tableModel.setEntityPackageName(getPackPath(url));
        FreemarkerUtil.generatorProxy(ENTITY_FTL, url, fileName, tableModel, false);
    }

    /**
     * 生成service文件
     *
     * @param tableModel 表信息集
     * @throws IOException       异常
     * @throws TemplateException 异常
     */
    private static void generateServiceFile(TableModel tableModel) throws IOException, TemplateException {
        String url = parseUrl(generator.getServiceUrl());
        String fileName = tableModel.getServiceName() + FILE_EXTENSION;
        tableModel.setServicePackageName(getPackPath(url));
        FreemarkerUtil.generatorProxy(SERVICE_FTL, url, fileName, tableModel, false);
    }

    /**
     * 初始化环境
     */
    public static void init() {
        initSpringContext();
    }

    /**
     * 生成器
     *
     * @param type 生成文件类型
     * @throws IOException       异常
     * @throws TemplateException 异常
     */
    static void generator(TYPE type) throws IOException, TemplateException {
        List<Map<String, Object>> tables = getTableInfo();
        if (tables == null || tables.isEmpty()) {
            throw new RuntimeException("未加载到任何数据库表信息");
        }
        for (Map<String, Object> table : tables) {
            TableModel tableModel = ObjectUtil.to(table, new TypeReference<TableModel>() {
            });
            switch (type) {
                case ENTITY:
                    generateEntityFile(tableModel);
                    break;
                case SERVICE:
                    generateServiceFile(tableModel);
                    break;
                default:
                    generateEntityFile(tableModel);
                    generateServiceFile(tableModel);
            }
        }
    }

    /**
     * 生成文件类型
     */
    enum TYPE {
        /**
         * 实体
         */
        ENTITY,
        /**
         * service
         */
        SERVICE,
        /**
         * 全生成
         */
        DEFAULT
    }

    public static void main(String[] args) {
        try {
            logger.info("【1】开始生成源代码");
            init();
            logger.info("【2】完成配置初始化，开始生成文件...");
            generator(AgileGenerator.TYPE.ENTITY);
            logger.info("【3】完成源代码生成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
