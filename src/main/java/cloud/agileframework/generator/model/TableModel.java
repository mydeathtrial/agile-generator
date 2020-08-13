package cloud.agileframework.generator.model;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.db.DataBaseUtil;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.annotation.Remark;
import cloud.agileframework.generator.properties.AnnotationType;
import cloud.agileframework.spring.util.spring.BeanUtil;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            ColumnModel columnModel = ObjectUtil.getObjectFromMap(ColumnModel.class, column);
            columnModel.build();
            setImport(columnModel.getImports());
            setColumn(columnModel);
        }

        this.serviceName = getProperties().getServicePrefix() + javaName + getProperties().getServiceSuffix();
        this.entityName = getProperties().getEntityPrefix() + javaName + getProperties().getEntitySuffix();
        this.entityCenterLineName = StringUtil.toUnderline(javaName).replace(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.MINUS).toLowerCase();

        if (getProperties().getAnnotation().contains(AnnotationType.JPA) || getProperties().getAnnotation().contains(AnnotationType.VALIDATE)) {
            addAnnotation(Setter.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
            addAnnotation(Builder.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
            addAnnotation(EqualsAndHashCode.class, AnnotationType.LOMBOK, desc -> getAnnotationDesc().add(desc));
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

        this.haveSetMethod = !getImports().contains(Setter.class) && !getImports().contains(Data.class);
        this.haveGetMethod = !getImports().contains(Getter.class) && !getImports().contains(Data.class);
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
