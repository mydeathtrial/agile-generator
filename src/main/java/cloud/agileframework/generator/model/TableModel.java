package cloud.agileframework.generator.model;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.db.DataBaseUtil;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.annotation.Remark;
import cloud.agileframework.generator.properties.AnnotationType;
import cloud.agileframework.spring.util.BeanUtil;
import com.google.common.collect.Sets;
import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/11 14:18
 * 描述： 表模型信息
 * @since 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class TableModel extends BaseModel {
    private String moduleName = BeanUtil.getApplicationContext().getId();
    private String tableCat;
    private String tableName;
    private String selfReferencingColName;
    private String tableSchem;
    private String typeSchem;
    private String typeCat;
    private String tableType;

    private String refGeneration;
    private String typeName;

    private String serviceName;
    private String entityName;
    private String entityCenterLineName;
    private String javaName;
    private String servicePackageName;
    private String entityPackageName;

    private Set<ColumnModel> columns = Sets.newHashSet();

    private boolean haveSetMethod;
    private boolean haveGetMethod;
    private boolean haveEqualsAndHashCodeMethod = true;

    public void setColumn(ColumnModel columns) {
        this.columns.add(columns);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.javaName = StringUtil.toUpperName(tableName);

        List<Map<String, Object>> columnInfos = DataBaseUtil.listColumns(getDataSourceProperties().getUrl(),
                getDataSourceProperties().getUsername(),
                getDataSourceProperties().getPassword(),
                tableName);
        for (Map<String, Object> column : columnInfos) {
            ColumnModel columnModel;

            if (PrimaryKeyColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(PrimaryKeyColumn.class, column);
            } else if (DeleteColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(DeleteColumn.class, column);
            } else if (CreateTimeColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(CreateTimeColumn.class, column);
            } else if (UpdateTimeColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(UpdateTimeColumn.class, column);
            } else if (CreateUserColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(CreateUserColumn.class, column);
            } else if (UpdateUserColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(UpdateUserColumn.class, column);
            } else if (ParentKeyColumn.is(column)) {
                columnModel = ObjectUtil.getObjectFromMap(ParentKeyColumn.class, column);
            } else {
                columnModel = ObjectUtil.getObjectFromMap(ColumnModel.class, column);
            }

            columnModel.build();
            setImport(columnModel.getImports());
            setColumn(columnModel);
        }

        fkHandler(tableName);

        this.serviceName = getProperties().getServicePrefix() + javaName + getProperties().getServiceSuffix();
        this.entityName = getProperties().getEntityPrefix() + javaName + getProperties().getEntitySuffix();
        this.entityCenterLineName = StringUtil.toUnderline(javaName).replace(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.MINUS).toLowerCase();

        if (getProperties().getAnnotation().contains(AnnotationType.JPA) || getProperties().getAnnotation().contains(AnnotationType.VALIDATE)) {
            addAnnotation(Setter.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
            addAnnotation(Builder.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
            addAnnotation(ToString.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
        } else {
            addAnnotation(Data.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
        }

        addAnnotation(AllArgsConstructor.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
        addAnnotation(NoArgsConstructor.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));

        addAnnotation(Entity.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        addAnnotation(new Table() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Table.class;
            }

            @Override
            public String name() {
                return toBlank(getTableName());
            }

            @Override
            public String catalog() {
                return toBlank(getTableCat());
            }

            @Override
            public String schema() {
                return toBlank(getTableSchem());
            }

            @Override
            public UniqueConstraint[] uniqueConstraints() {
                return new UniqueConstraint[0];
            }

            @Override
            public Index[] indexes() {
                return new Index[0];
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));

        this.hibernateAnnotationHandler();

        this.haveSetMethod = !getImports().contains(Setter.class) && !getImports().contains(Data.class);
        this.haveGetMethod = !getImports().contains(Getter.class) && !getImports().contains(Data.class);

        this.setImport(Objects.class);
    }

    /**
     * 处理外键
     *
     * @param tableName 表
     */
    private void fkHandler(String tableName) {
        List<Map<String, Object>> fKeys = DataBaseUtil.listFKeys(getDataSourceProperties().getUrl(),
                getDataSourceProperties().getUsername(),
                getDataSourceProperties().getPassword(),
                tableName);

        Set<String> cache = Sets.newHashSet();

        fKeys.forEach(fk -> {
            FKeyColumn columnModel = ObjectUtil.getObjectFromMap(FKeyColumn.class, fk);
            if (cache.contains(columnModel.getFktableName())) {
                columnModel.setFktableName(columnModel.getFktableName() + 1);
            }
            cache.add(columnModel.getFktableName());
            columnModel.build();
            setImport(columnModel.getImports());
            setColumn(columnModel);
        });
    }

    private void hibernateAnnotationHandler() {
        Set<PrimaryKeyColumn> primaryColumns = columns.stream().filter(c -> c instanceof PrimaryKeyColumn).map(c -> (PrimaryKeyColumn) c).collect(Collectors.toSet());
        Set<DeleteColumn> deleteColumns = columns.stream().filter(c -> c instanceof DeleteColumn).map(c -> (DeleteColumn) c).collect(Collectors.toSet());
        if (!deleteColumns.isEmpty()) {
            addAnnotation(new Where() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Where.class;
                }

                @Override
                public String clause() {
                    return deleteColumns.stream().map(c -> c.getColumnName() + " = " + c.getNoDelete()).collect(Collectors.joining(" and "));
                }
            }, AnnotationType.HIBERNATE, desc -> getAnnotationDesc().add(desc));
            addAnnotation(new SQLDelete() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return SQLDelete.class;
                }

                @Override
                public String sql() {
                    String set = deleteColumns.stream().map(c -> c.getColumnName() + " = " + c.getDeleted()).collect(Collectors.joining(", "));
                    String where = primaryColumns.stream().map(c -> c.getColumnName() + " = ?").collect(Collectors.joining(" and "));
                    return String.format("update %s set %s where %s", getTableName(), set, where);
                }

                @Override
                public boolean callable() {
                    return false;
                }

                @Override
                public ResultCheckStyle check() {
                    return ResultCheckStyle.NONE;
                }
            }, AnnotationType.HIBERNATE, desc -> getAnnotationDesc().add(desc));
        }
    }

    @Override
    public void setRemarks(String remarks) {
        super.setRemarks(remarks);
        addAnnotation(new Remark() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Remark.class;
            }

            @Override
            public String value() {
                return toBlank(getRemarks());
            }
        }, AnnotationType.REMARK, desc -> getAnnotationDesc().add(desc));
    }
}
