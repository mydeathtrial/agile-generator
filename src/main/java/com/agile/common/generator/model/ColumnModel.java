package com.agile.common.generator.model;

import com.agile.common.annotation.Remark;
import com.agile.common.base.Constant;
import com.agile.common.properties.GeneratorProperties;
import com.agile.common.util.FactoryUtil;
import com.agile.common.util.NumberUtil;
import com.agile.common.util.StringUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/11 14:18
 * 描述： TODO
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class ColumnModel {
    private String tableCat;
    private String bufferLength;
    private String tableName;
    private String columnDef;
    private String scopeCatalog;
    private String tableSchem;
    private String columnName;
    private String remarks;
    private String numPrecRadix;
    private String isAutoincrement;
    private String sqlDataType;
    private String scopeSchema;
    private String isPrimaryKey;
    private String dataType;
    private int columnSize;
    private String scopeTable;
    private String isNullable;
    private String nullable;
    private int decimalDigits;
    private String sqlDatetimeSub;
    private String isGeneratedcolumn;
    private String charOctetLength;
    private String ordinalPosition;
    private String sourceDataType;
    private String typeName;

    private String javaName;
    private String getMethod;
    private String setMethod;
    private Class<?> javaType;
    private String javaTypeName;
    private String javaSimpleTypeName;
    private String defValue;
    private Set<Class<?>> imports = new HashSet<>();
    private Set<String> annotations = new HashSet<>();
    private GeneratorProperties properties = FactoryUtil.getBean(GeneratorProperties.class);

    public void build() {
        StringBuilder temp = new StringBuilder();
        temp.append("name = \"").append(columnName).append("\"");
        if ("0".equals(nullable)) {
            temp.append(", nullable = false");
            if (javaType == String.class) {
                if (Boolean.parseBoolean(isPrimaryKey)) {
                    if (javaType == String.class) {
                        setAnnotation("@NotBlank(message = \"唯一标识不能为空\", groups = {Update.class, Delete.class})", GeneratorProperties.AnnotationType.HibernateValidate);
                        setImport(Update.class, NotBlank.class, Delete.class);
                    }
                } else {
                    setAnnotation(String.format("@NotBlank(message = \"%s不能为空\", groups = {Insert.class, Update.class})", remarks == null ? "" : remarks), GeneratorProperties.AnnotationType.HibernateValidate);
                    setImport(Insert.class, NotBlank.class, Update.class);
                }
            } else {
                if (Boolean.parseBoolean(isPrimaryKey)) {
                    if (javaType == String.class) {
                        setAnnotation("@NotNull(message = \"唯一标识不能为空\", groups = {Update.class, Delete.class})", GeneratorProperties.AnnotationType.HibernateValidate);
                        setImport(Delete.class, NotNull.class, Update.class);
                    }
                } else {
                    setAnnotation(String.format("@NotNull(message = \"%s不能为空\", groups = {Insert.class, Update.class})", remarks == null ? "" : remarks), GeneratorProperties.AnnotationType.HibernateValidate);
                    setImport(Insert.class, Update.class, NotNull.class);
                }
            }
        }
        if (!StringUtil.isEmpty(columnDef)) {
            temp.append(", columnDefinition = \"").append(String.format("%s default %s", typeName, columnDef)).append("\"");
        }
        if (columnSize > 0) {
            temp.append(", length = ").append(columnSize);
            if (javaType == String.class) {
                setImport(Length.class, Insert.class, Update.class);
                setAnnotation(String.format("@Length(max = %s, message = \"最长为%s个字符\", groups = {Insert.class, Update.class})", columnSize, columnSize), GeneratorProperties.AnnotationType.HibernateValidate);
            } else if (javaType == int.class || javaType == Integer.class) {
                setImport(Max.class, Min.class);
                setAnnotation(String.format("@Max(value = %s, groups = {Insert.class, Update.class})", Integer.MAX_VALUE), GeneratorProperties.AnnotationType.HibernateValidate);
                setAnnotation(String.format("@Min(value = %s, groups = {Insert.class, Update.class})", Constant.NumberAbout.ZERO), GeneratorProperties.AnnotationType.HibernateValidate);
            } else if (javaType == long.class || javaType == Long.class) {
                setImport(DecimalMax.class, DecimalMin.class);
                setAnnotation(String.format("@DecimalMax(value = \"%s\", groups = {Insert.class, Update.class})", Long.MAX_VALUE), GeneratorProperties.AnnotationType.HibernateValidate);
                setAnnotation(String.format("@DecimalMin(value = \"%s\", groups = {Insert.class, Update.class})", Constant.NumberAbout.ZERO), GeneratorProperties.AnnotationType.HibernateValidate);
            }
        }
        if ("creatDate".equals(javaName) || "creatTime".equals(javaName) || "createTime".equals(javaName) || "createDate".equals(javaName)) {
            temp.append(", updatable = false");
            setImport(Past.class);
            setAnnotation("@Past", GeneratorProperties.AnnotationType.HibernateValidate);
        }
        setAnnotation(String.format("@Column(%s)", temp), GeneratorProperties.AnnotationType.JPA);

        if (Boolean.parseBoolean(isPrimaryKey)) {
            setAnnotation("@Id", GeneratorProperties.AnnotationType.JPA);
        } else {
            if ("byte[]".equals(javaTypeName) || "java.sql.Blob".equals(javaTypeName) || "java.sql.Clob".equals(javaTypeName)) {
                if ("java.sql.Blob".equals(javaTypeName) || "java.sql.Clob".equals(javaTypeName)) {
                    setAnnotation("@Lob", GeneratorProperties.AnnotationType.JPA);
                    setImport(Lob.class, FetchType.class);
                }
                setAnnotation("@Basic(fetch = FetchType.LAZY)", GeneratorProperties.AnnotationType.JPA);
            } else {
                setAnnotation("@Basic", GeneratorProperties.AnnotationType.JPA);
            }
        }
    }


    public void setColumnName(String columnName) {
        columnName = deleteHiddenCharacter(columnName);
        if (properties.getKeywords().contains(columnName)) {
            this.columnName = String.format("`%s`", columnName);
        } else {
            this.columnName = columnName;
        }

        if (properties.isSensitive()) {
            this.javaName = StringUtil.toLowerName(columnName);
        } else {
            this.javaName = StringUtil.toLowerName(columnName.toLowerCase());
        }

        javaName = javaName.replaceAll(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.BLANK);

        if ("updateTime".equals(javaName) || "updateDate".equals(javaName)) {
            setAnnotation("@Temporal(TemporalType.TIMESTAMP)", GeneratorProperties.AnnotationType.JPA);
            setAnnotation("@UpdateTimestamp", GeneratorProperties.AnnotationType.JPA);
            setImport(UpdateTimestamp.class, Temporal.class, TemporalType.class);
        }

        if ("creatDate".equals(javaName) || "creatTime".equals(javaName) || "createTime".equals(javaName) || "createDate".equals(javaName)) {
            setAnnotation("@Temporal(TemporalType.TIMESTAMP)", GeneratorProperties.AnnotationType.JPA);
            setAnnotation("@CreationTimestamp", GeneratorProperties.AnnotationType.JPA);
            setImport(CreationTimestamp.class, Temporal.class, TemporalType.class);
        }
        setMethod(javaName);
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        if ("TIMESTAMP".equals(typeName) || "DATE".equals(typeName) || "TIME".equals(typeName)) {
            setAnnotation(String.format("@Temporal(TemporalType.%s)", typeName), GeneratorProperties.AnnotationType.JPA);
            setImport(Temporal.class, TemporalType.class);
        }

        this.javaTypeName = properties.getJavaType(typeName.split("[\\s]+")[0].toLowerCase());

        try {
            this.javaType = Class.forName(javaTypeName);
            this.javaSimpleTypeName = javaType.getSimpleName();
            setImport(javaType);
        } catch (ClassNotFoundException ignored) {

        }
    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
        setImport(Id.class);
    }

    private void setMethod(String name) {
        if (boolean.class == javaType) {
            this.getMethod = "is" + StringUtil.toUpperName(name);
        } else {
            this.getMethod = "get" + StringUtil.toUpperName(name);
        }
        this.setMethod = "set" + StringUtil.toUpperName(name);

    }

    public void setIsAutoincrement(String isAutoincrement) {
        this.isAutoincrement = isAutoincrement;
        if ("YES".equals(isAutoincrement)) {
            setImport(GenerationType.class, GeneratedValue.class);
            setAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)", GeneratorProperties.AnnotationType.JPA);
        }
    }

    public void setColumnDef(String columnDef) {
        this.columnDef = deleteHiddenCharacter(columnDef);
        if (this.columnDef == null || "null".equals(columnDef.toLowerCase())) {
            return;
        }

        if (Double.class == javaType) {
            defValue = NumberUtil.isNumber(columnDef) ? Double.valueOf(columnDef).toString() : null;
        } else if (String.class == javaType || Char.class == javaType) {
            defValue = String.format("\"%s\"", columnDef.replaceAll(Constant.RegularAbout.UP_COMMA, ""));
        } else if ("CURRENT_TIMESTAMP".equals(columnDef)) {
            if (Date.class == javaType || java.sql.Date.class == javaType) {
                defValue = "new Date()";
            } else if (Time.class == javaType) {
                defValue = "new Time(System.currentTimeMillis())";
            } else if (Timestamp.class == javaType) {
                defValue = "new Timestamp(System.currentTimeMillis())";
            } else if (long.class == javaType) {
                defValue = "System.currentTimeMillis()";
            }
        }
    }

    public void setRemarks(String remarks) {
        this.remarks = deleteHiddenCharacter(remarks);
        if (!StringUtil.isEmpty(remarks)) {
            setImport(Remark.class);
        }
    }

    public void setImport(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.getPackage().getName().startsWith("java.lang")) {
                continue;
            }
            this.imports.add(clazz);
        }
    }

    private void setAnnotation(String annotation, GeneratorProperties.AnnotationType annotationType) {
        if (!properties.getAnnotation().contains(GeneratorProperties.AnnotationType.NO) && properties.getAnnotation().contains(annotationType)) {
            this.annotations.add(annotation);
        }
    }

    public void setTableCat(String tableCat) {
        this.tableCat = tableCat;
    }

    public void setBufferLength(String bufferLength) {
        this.bufferLength = bufferLength;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setScopeCatalog(String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
    }

    public void setTableSchem(String tableSchem) {
        this.tableSchem = tableSchem;
    }

    public void setNumPrecRadix(String numPrecRadix) {
        this.numPrecRadix = numPrecRadix;
    }

    public void setSqlDataType(String sqlDataType) {
        this.sqlDataType = sqlDataType;
    }

    public void setScopeSchema(String scopeSchema) {
        this.scopeSchema = scopeSchema;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public void setScopeTable(String scopeTable) {
        this.scopeTable = scopeTable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public void setSqlDatetimeSub(String sqlDatetimeSub) {
        this.sqlDatetimeSub = sqlDatetimeSub;
    }

    public void setIsGeneratedcolumn(String isGeneratedcolumn) {
        this.isGeneratedcolumn = isGeneratedcolumn;
    }

    public void setCharOctetLength(String charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    public void setOrdinalPosition(String ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public void setSourceDataType(String sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public void setGetMethod(String getMethod) {
        this.getMethod = getMethod;
    }

    public void setSetMethod(String setMethod) {
        this.setMethod = setMethod;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public void setJavaSimpleTypeName(String javaSimpleTypeName) {
        this.javaSimpleTypeName = javaSimpleTypeName;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    public void setImports(Set<Class<?>> imports) {
        this.imports = imports;
    }

    public void setAnnotations(Set<String> annotations) {
        this.annotations = annotations;
    }

    public void setProperties(GeneratorProperties properties) {
        this.properties = properties;
    }

    private String deleteHiddenCharacter(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("[\\s]+", "");
    }

}
