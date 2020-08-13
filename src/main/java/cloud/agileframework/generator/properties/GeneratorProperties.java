package cloud.agileframework.generator.properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/1/31 9:39
 * 描述： TODO
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.generator")
public class GeneratorProperties {
    /**
     * 实体文件生成到的目录地址
     */
    private String entityUrl;
    /**
     * Service文件生成到的目录地址
     */
    private String serviceUrl;
    /**
     * 测试文件生成到的目录地址
     */
    private String testUrl;
    /**
     * 实体类生成名前缀
     */
    private String entityPrefix = "";
    /**
     * 实体类生成名后缀
     */
    private String entitySuffix = "Entity";
    /**
     * service类生成名前缀
     */
    private String servicePrefix = "";
    /**
     * service类生成名后缀
     */
    private String serviceSuffix = "Service";
    /**
     * 测试文件类生成名前缀
     */
    private String testPrefix = "";
    /**
     * 测试文件类生成名后缀
     */
    private String testSuffix = "Test";
    /**
     * 代码生成器目标表名字,可逗号分隔,可like百分号形式模糊匹配
     */
    private String tableName = "%";
    /**
     * 表名大小写是否敏感
     */
    private boolean isSensitive = false;
    /**
     * 数据库字段类型与java映射规则
     */
    private Map<String, Class<?>> columnType = new HashMap<>();

    private Set<AnnotationType> annotation = Sets.newHashSet();

    private List<String> keywords = Lists.newArrayList();

    public Class<?> getJavaType(String type) {
        return columnType.get(type);
    }

    public GeneratorProperties() {
        columnType.put("bigint", Long.class);
        columnType.put("bit", Boolean.class);
        columnType.put("char", String.class);
        columnType.put("datetime", Date.class);
        columnType.put("time", Date.class);
        columnType.put("date", Date.class);
        columnType.put("mediumtext", String.class);
        columnType.put("bolb", byte[].class);
        columnType.put("clob", String.class);
        columnType.put("decimal", Double.class);
        columnType.put("double", Double.class);
        columnType.put("float", Float.class);
        columnType.put("image", byte[].class);
        columnType.put("int", Integer.class);
        columnType.put("longblob", Byte.class);
        columnType.put("money", Double.class);
        columnType.put("nchar", String.class);
        columnType.put("number", BigDecimal.class);
        columnType.put("numeric", Double.class);
        columnType.put("nvarchar", String.class);
        columnType.put("real", Double.class);
        columnType.put("smallint", Double.class);
        columnType.put("text", String.class);
        columnType.put("timestamp", Date.class);
        columnType.put("tinyint", Integer.class);
        columnType.put("varchar", String.class);
        columnType.put("varchar2", String.class);
        columnType.put("tinytext", String.class);
        columnType.put("longtext", String.class);
        columnType.put("character", String.class);

        keywords.add("order");
        keywords.add("dec");
        keywords.add("desc");
        keywords.add("name");
        keywords.add("code");
        keywords.add("status");
        keywords.add("where");
        keywords.add("select");
        keywords.add("mode");
    }

    public String getEntityUrl() {
        return entityUrl;
    }

    public void setEntityUrl(String entityUrl) {
        this.entityUrl = entityUrl;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    public String getEntitySuffix() {
        return entitySuffix;
    }

    public void setEntitySuffix(String entitySuffix) {
        this.entitySuffix = entitySuffix;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public String getServiceSuffix() {
        return serviceSuffix;
    }

    public void setServiceSuffix(String serviceSuffix) {
        this.serviceSuffix = serviceSuffix;
    }

    public String getTestPrefix() {
        return testPrefix;
    }

    public void setTestPrefix(String testPrefix) {
        this.testPrefix = testPrefix;
    }

    public String getTestSuffix() {
        return testSuffix;
    }

    public void setTestSuffix(String testSuffix) {
        this.testSuffix = testSuffix;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isSensitive() {
        return isSensitive;
    }

    public void setSensitive(boolean sensitive) {
        isSensitive = sensitive;
    }

    public Map<String, Class<?>> getColumnType() {
        return columnType;
    }

    public void setColumnType(Map<String, Class<?>> columnType) {
        this.columnType = columnType;
    }

    public Set<AnnotationType> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Set<AnnotationType> annotation) {
        this.annotation = annotation;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
