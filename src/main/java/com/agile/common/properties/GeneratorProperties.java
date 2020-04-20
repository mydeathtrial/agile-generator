package com.agile.common.properties;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private Map<String, String> columnType = new HashMap<>();

    private Set<AnnotationType> annotation = Sets.newHashSet(AnnotationType.JPA,AnnotationType.HibernateValidate);

    private List<String> keywords = Lists.newArrayList();

    public String getJavaType(String type) {
        return columnType.get(type);
    }

    /**
     * 注解类型
     */
    public enum AnnotationType {
        /**
         * JPA注解
         */
        JPA,
        HibernateValidate,
        NO
    }

    public GeneratorProperties() {
        columnType.put("bigint", "java.lang.Long");
        columnType.put("bit", "java.lang.Boolean");
        columnType.put("char", "java.lang.String");
        columnType.put("datetime", "java.util.Date");
        columnType.put("time", "java.util.Date");
        columnType.put("date", "java.util.Date");
        columnType.put("mediumtext", "java.lang.String");
        columnType.put("bolb", "byte[]");
        columnType.put("clob", "java.lang.String");
        columnType.put("decimal", "java.lang.Double");
        columnType.put("double", "java.lang.Double");
        columnType.put("float", "java.lang.Float");
        columnType.put("image", "byte[]");
        columnType.put("int", "java.lang.Integer");
        columnType.put("longblob", "java.lang.Byte");
        columnType.put("money", "java.lang.Double");
        columnType.put("nchar", "java.lang.String");
        columnType.put("number", "java.math.BigDecimal");
        columnType.put("numeric", "java.lang.Double");
        columnType.put("nvarchar", "java.lang.String");
        columnType.put("real", "java.lang.Double");
        columnType.put("smallint", "java.lang.Double");
        columnType.put("text", "java.lang.String");
        columnType.put("timestamp", "java.util.Date");
        columnType.put("tinyint", "java.lang.Integer");
        columnType.put("varchar", "java.lang.String");
        columnType.put("varchar2", "java.lang.String");
        columnType.put("tinytext", "java.lang.String");
        columnType.put("longtext", "java.lang.String");
        columnType.put("character", "java.lang.String");

        keywords.add("order");
        keywords.add("dec");
        keywords.add("desc");
        keywords.add("name");
        keywords.add("code");
        keywords.add("status");
        keywords.add("where");
        keywords.add("select");
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

    public Map<String, String> getColumnType() {
        return columnType;
    }

    public void setColumnType(Map<String, String> columnType) {
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
