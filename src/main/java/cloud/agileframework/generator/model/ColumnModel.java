package cloud.agileframework.generator.model;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.annotation.Remark;
import cloud.agileframework.generator.properties.AnnotationType;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Payload;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/11 14:18
 * 描述： 字段信息
 * @since 1.0
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ColumnModel extends BaseModel {
    private String tableCat;
    private String bufferLength;
    private String tableName;
    private String columnDef;
    private String scopeCatalog;
    private String tableSchem;
    private String columnName;
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

    private final Set<String> fieldAnnotationDesc = Sets.newHashSet();

    public void build() {
        addAnnotation(new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Column.class;
            }

            @Override
            public String name() {
                return getColumnName();
            }

            @Override
            public boolean unique() {
                return false;
            }

            @Override
            public boolean nullable() {
                return !"0".equals(getNullable());
            }

            @Override
            public boolean insertable() {
                return true;
            }

            @Override
            public boolean updatable() {
                boolean update = "creatDate".equals(javaName) || "creatTime".equals(javaName) || "createTime".equals(javaName) || "createDate".equals(javaName);
                return !update;
            }

            @Override
            public String columnDefinition() {
                if (!StringUtils.isEmpty(getColumnDef())) {
                    return String.format("%s default %s", getTypeName(), getColumnDef());
                }
                return "";
            }

            @Override
            public String table() {
                return "";
            }

            @Override
            public int length() {
                if (getColumnSize() > 0) {
                    return getColumnSize();
                }
                return 255;
            }

            @Override
            public int precision() {
                return 0;
            }

            @Override
            public int scale() {
                return 0;
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));

        if ("0".equals(nullable)) {
            if (javaType == String.class) {
                addAnnotation(new NotBlank() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return NotBlank.class;
                    }

                    @Override
                    public String message() {
                        return Boolean.parseBoolean(isPrimaryKey) ? "唯一标识不能为空" : toBlank(getRemarks()) + "不能为空";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));
            } else {
                addAnnotation(new NotNull() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return NotNull.class;
                    }

                    @Override
                    public String message() {
                        return Boolean.parseBoolean(isPrimaryKey) ? "唯一标识不能为空" : toBlank(getRemarks()) + "不能为空";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));
            }
        }

        if (columnSize > 0) {
            if (javaType == String.class) {
                addAnnotation(new Length() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Length.class;
                    }

                    @Override
                    public int min() {
                        return 0;
                    }

                    @Override
                    public int max() {
                        return columnSize;
                    }

                    @Override
                    public String message() {
                        return "最长为" + max() + "个字符";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));
            } else if (javaType == int.class || javaType == Integer.class) {
                addAnnotation(new Max() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Max.class;
                    }

                    @Override
                    public String message() {
                        return "{javax.validation.constraints.Max.message}";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }

                    @Override
                    public long value() {
                        return Integer.MAX_VALUE;
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));

                addAnnotation(new Min() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Min.class;
                    }

                    @Override
                    public String message() {
                        return "{javax.validation.constraints.Min.message}";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }

                    @Override
                    public long value() {
                        return 0;
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));

            } else if (javaType == long.class || javaType == Long.class) {
                addAnnotation(new DecimalMax() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return DecimalMax.class;
                    }

                    @Override
                    public String message() {
                        return "{javax.validation.constraints.DecimalMax.message}";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }

                    @Override
                    public String value() {
                        return Long.toString(Long.MAX_VALUE);
                    }

                    @Override
                    public boolean inclusive() {
                        return true;
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));

                addAnnotation(new DecimalMin() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return DecimalMin.class;
                    }

                    @Override
                    public String message() {
                        return "{javax.validation.constraints.DecimalMin.message}";
                    }

                    @Override
                    public Class<?>[] groups() {
                        return new Class[0];
                    }

                    @Override
                    public Class<? extends Payload>[] payload() {
                        return new Class[0];
                    }

                    @Override
                    public String value() {
                        return Long.toString(Long.MAX_VALUE);
                    }

                    @Override
                    public boolean inclusive() {
                        return true;
                    }
                }, AnnotationType.VALIDATE, desc -> getAnnotationDesc().add(desc));
            }
        }

        if (Boolean.parseBoolean(isPrimaryKey)) {
            addAnnotation(Id.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        } else {
            if ("byte[]".equals(javaTypeName) ||
                    "java.sql.Blob".equals(javaTypeName) ||
                    "java.sql.Clob".equals(javaTypeName)) {
                if ("java.sql.Blob".equals(javaTypeName) || "java.sql.Clob".equals(javaTypeName)) {
                    addAnnotation(Lob.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
                }
                addAnnotation(new Basic() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Basic.class;
                    }

                    @Override
                    public FetchType fetch() {
                        return FetchType.LAZY;
                    }

                    @Override
                    public boolean optional() {
                        return true;
                    }
                }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
            } else {
                addAnnotation(Basic.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
            }
        }

        addAnnotation(new Remark() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Remark.class;
            }

            @Override
            public String value() {
                return toBlank(getRemarks());
            }
        }, AnnotationType.REMARK, desc -> getFieldAnnotationDesc().add(desc));

        setMethod(javaName);
    }


    public void setColumnName(String columnName) {
        columnName = deleteHiddenCharacter(columnName);
        if (getProperties().getKeywords().contains(columnName)) {
            this.columnName = String.format("`%s`", columnName);
        } else {
            this.columnName = columnName;
        }

        if (getProperties().isSensitive()) {
            this.javaName = StringUtil.toLowerName(columnName);
        } else {
            this.javaName = StringUtil.toLowerName(columnName.toLowerCase());
        }

        javaName = javaName.replace(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.BLANK);

        if ("updateTime".equals(javaName) || "updateDate".equals(javaName)) {

            addAnnotation(new Temporal() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Temporal.class;
                }

                @Override
                public TemporalType value() {
                    return TemporalType.TIMESTAMP;
                }
            }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
            addAnnotation(UpdateTimestamp.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        }

        if ("creatDate".equals(javaName) || "creatTime".equals(javaName) || "createTime".equals(javaName) || "createDate".equals(javaName)) {
            addAnnotation(new Temporal() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Temporal.class;
                }

                @Override
                public TemporalType value() {
                    return TemporalType.TIMESTAMP;
                }
            }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
            addAnnotation(CreationTimestamp.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        }
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
        if ("TIMESTAMP".equals(typeName) || "DATE".equals(typeName) || "TIME".equals(typeName)) {
            addAnnotation(new Temporal() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Temporal.class;
                }

                @Override
                public TemporalType value() {
                    return TemporalType.valueOf(typeName);
                }
            }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        }

        this.javaTypeName = getProperties().getJavaType(typeName.split("[\\s]+")[0].toLowerCase());

        if (this.javaTypeName != null) {
            try {
                this.javaType = Class.forName(javaTypeName);
                this.javaSimpleTypeName = javaType.getSimpleName();
                setImport(javaType);
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (javaType == null) {
            this.javaType = String.class;
        }
        if (javaSimpleTypeName == null) {
            this.javaSimpleTypeName = String.class.getSimpleName();
        }

    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
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
            addAnnotation(new GeneratedValue() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return GeneratedValue.class;
                }

                @Override
                public GenerationType strategy() {
                    return GenerationType.IDENTITY;
                }

                @Override
                public String generator() {
                    return "";
                }
            }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        }
    }

    public void setColumnDef(String columnDef) {
        this.columnDef = deleteHiddenCharacter(columnDef);
        if (this.columnDef == null || "null".equalsIgnoreCase(columnDef)) {
            return;
        }

        if (Double.class == javaType) {
            defValue = NumberUtils.isCreatable(columnDef) ? Double.valueOf(columnDef).toString() : null;
        } else if (String.class == javaType || char.class == javaType) {
            defValue = String.format("\"%s\"", columnDef.replace(Constant.RegularAbout.UP_COMMA, ""));
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


}
