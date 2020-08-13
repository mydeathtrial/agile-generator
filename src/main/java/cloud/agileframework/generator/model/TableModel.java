package cloud.agileframework.generator.model;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.db.DataBaseUtil;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.annotation.Remark;
import cloud.agileframework.generator.properties.GeneratorProperties;
import cloud.agileframework.spring.util.spring.BeanUtil;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.HashSet;
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
public class TableModel {
    private String moduleName = BeanUtil.getApplicationContext().getId();
    private String tableCat;
    private String tableName;
    private String selfReferencingColName;
    private String tableSchem;
    private String typeSchem;
    private String typeCat;
    private String tableType;
    private String remarks;
    private String refGeneration;
    private String typeName;

    private Set<ColumnModel> columns = Sets.newHashSet();
    private Set<String> imports = new HashSet<>();
    private String serviceName;
    private String entityName;
    private String entityCenterLineName;
    private String javaName;
    private String servicePackageName;
    private String entityPackageName;

    private GeneratorProperties properties = BeanUtil.getBean(GeneratorProperties.class);
    private static DataSourceProperties dataSourceProperties;

    public void setColumn(ColumnModel columns) {
        this.columns.add(columns);
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks.replaceAll("[\\s]+", " ");
        if (!StringUtils.isEmpty(remarks)) {
            setImport(Remark.class);
        }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.javaName = StringUtil.toUpperName(tableName);

        List<Map<String, Object>> columnInfos = DataBaseUtil.listColumns(dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword(),
                tableName);
        for (Map<String, Object> column : columnInfos) {
            ColumnModel columnModel = ObjectUtil.getObjectFromMap(ColumnModel.class, column);
            columnModel.build();
            setImport(columnModel.getImports());
            setColumn(columnModel);
        }

        this.serviceName = properties.getServicePrefix() + javaName + properties.getServiceSuffix();
        this.entityName = properties.getEntityPrefix() + javaName + properties.getEntitySuffix();
        this.entityCenterLineName = StringUtil.toUnderline(javaName).replace(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.MINUS).toLowerCase();
    }

    public void setImport(Set<Class<?>> classes) {
        if (classes == null) {
            return;
        }
        for (Class<?> clazz : classes) {
            setImport(clazz);
        }
    }

    public void setImport(Class<?> clazz) {
        if (clazz.getPackage().getName().startsWith("java.lang")) {
            return;
        }
        this.imports.add(String.format("%s.%s", clazz.getPackage().getName(), clazz.getSimpleName()));
    }

    public static void setDbInfo(DataSourceProperties dataSourceProperties) {
        TableModel.dataSourceProperties = dataSourceProperties;
    }
}
