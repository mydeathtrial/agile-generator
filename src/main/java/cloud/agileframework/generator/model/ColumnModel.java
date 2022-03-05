package cloud.agileframework.generator.model;

import cloud.agileframework.common.annotation.Remark;
import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.model.config.PropertyConfig;
import cloud.agileframework.generator.properties.AnnotationType;
import cloud.agileframework.validate.group.Insert;
import cloud.agileframework.validate.group.Update;
import com.google.common.collect.Sets;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.Payload;
import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
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
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ColumnModel extends BaseModel {
    private final Set<String> fieldAnnotationDesc = Sets.newHashSet();
    private final Set<String> dicAnnotationDesc = Sets.newHashSet();
    private PropertyConfig propertyConfig;
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
    private boolean notNull = false;
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
                return !isNotNull();
            }

            @Override
            public boolean insertable() {
                return !(ColumnModel.this instanceof DeleteColumn);
            }

            @Override
            public boolean updatable() {
                return !(ColumnModel.this instanceof DeleteColumn)
                        && !(ColumnModel.this instanceof CreateTimeColumn);
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
                if (getColumnSize() > 0 && !getJavaType().isAssignableFrom(Date.class)) {
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

        validateHandler();

        if (!(ColumnModel.this instanceof PrimaryKeyColumn)) {
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

            @Override
            public boolean ignoreCompare() {
                return false;
            }

            @Override
            public boolean excelHead() {
                return true;
            }

            @Override
            public int sort() {
                return 0;
            }
        }, AnnotationType.REMARK, desc -> getFieldAnnotationDesc().add(desc));
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
        notNull = "0".equals(nullable);
    }

    /**
     * 参数验证
     */
    private void validateHandler() {
        if (columnSize <= 0) {
            return;
        }
        if (notNull && javaType == String.class && !Boolean.parseBoolean(isPrimaryKey)) {
            addAnnotation(new NotBlank() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return NotBlank.class;
                }

                @Override
                public String message() {
                    return toBlank(getRemarks()) + "不能为空字符";
                }

                @Override
                public Class<?>[] groups() {
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[0];
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));
        }
        if (notNull && javaType != String.class && !Boolean.parseBoolean(isPrimaryKey)) {
            addAnnotation(new NotNull() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return NotNull.class;
                }

                @Override
                public String message() {
                    return toBlank(getRemarks()) + "不能为Null";
                }

                @Override
                public Class<?>[] groups() {
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[]{};
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));
        }
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
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[0];
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));
            return;
        }
        if (javaType == int.class || javaType == Integer.class) {
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
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[0];
                }

                @Override
                public long value() {
                    double a = (Math.pow(10, getColumnSize()) - 1);
                    if (a > Integer.MAX_VALUE) {
                        return Integer.MAX_VALUE;
                    }
                    return (long) a;
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));

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
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[0];
                }

                @Override
                public long value() {
                    return 0;
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));
            return;
        }
        if ((javaType == long.class || javaType == Long.class)
                && !(this instanceof PrimaryKeyColumn)
                && !(this instanceof ParentKeyColumn)
                && !(this instanceof FExportKeyColumn)
                && !(this instanceof FImportKeyColumn)
        ) {
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
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[0];
                }

                @Override
                public String value() {
                    return BigDecimal.valueOf(Math.pow(10, getColumnSize() - 1)).add(BigDecimal.valueOf(-1)).toString();
                }

                @Override
                public boolean inclusive() {
                    return true;
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));

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
                    return new Class[]{Insert.class, Update.class};
                }

                @Override
                public Class<? extends Payload>[] payload() {
                    return new Class[0];
                }

                @Override
                public String value() {
                    return "-" + (BigDecimal.valueOf(Math.pow(10, getColumnSize() - 2)).add(BigDecimal.valueOf(-1)));
                }

                @Override
                public boolean inclusive() {
                    return true;
                }
            }, AnnotationType.VALIDATE, desc -> getValidateAnnotationDesc().add(desc));
        }
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
        setMethod(javaName);
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

        this.javaType = getProperties().getJavaType(typeName.split("[\\s]+")[0].toLowerCase());

        if (javaType == null) {
            this.javaType = String.class;
        }
        this.javaTypeName = javaType.getName();
        this.javaSimpleTypeName = javaType.getSimpleName();
        setImport(javaType);
    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public void setMethod(String name) {
        this.getMethod = "get" + StringUtil.toUpperName(name);
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
        addAnnotation(Builder.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
        setImport(javaType);
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public void setJavaSimpleTypeName(String javaSimpleTypeName) {
        this.javaSimpleTypeName = javaSimpleTypeName;
    }

    /**
     * 根据字段信息判断是不是该类型数据
     *
     * @return true 是
     */
    public boolean isGeneric() {
        return true;
    }

    public void setPropertyConfig(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
    }
}
