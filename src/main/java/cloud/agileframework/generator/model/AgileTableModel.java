package cloud.agileframework.generator.model;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.string.StringUtil;
import com.google.common.collect.Sets;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.DasTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
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

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author 佟盟
 * 日期 2020-12-16 15:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgileTableModel extends BaseModel{

    private String entityName;
    private String entityCenterLineName;
    private String javaName;
    private String entityPackageName;
    private Set<AgileColumnModel> columns = Sets.newHashSet();
    private String url;

    private boolean haveSetMethod;
    private boolean haveGetMethod;

    public AgileTableModel(DasTable dasObject, GeneratorProperties config) {
        super(dasObject,config);

        parseColumn(dasObject, config);
        this.javaName = StringUtil.toUpperName(getName());
        this.entityName = config.getEntityPrefix() + javaName + config.getEntitySuffix();
        this.entityCenterLineName = StringUtil.toUnderline(javaName).replace(Constant.RegularAbout.UNDER_LINE, Constant.RegularAbout.MINUS).toLowerCase();
        this.entityPackageName = getPackPath(config.getEntityUrl());
        this.url = parseUrl(config.getEntityUrl());

        if (config.getAnnotation().contains(AnnotationType.JPA) || config.getAnnotation().contains(AnnotationType.VALIDATE)) {
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
                return toBlank(getName());
            }

            @Override
            public String catalog() {
                return toBlank(getTableCat());
            }

            @Override
            public String schema() {
                return toBlank(getSchema());
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

    /**
     * 推测生成java文件的包名
     *
     * @param url 生成目标文件存储路径
     * @return 包名
     */
    private String getPackPath(String url) {
        url = parseUrl(url);
        GeneratorProperties config = getConfig();
        String javaSourceUrl = config.getJavaSourceUrl();
        javaSourceUrl = parseUrl(javaSourceUrl);

        String packPath = url.substring(javaSourceUrl.length()).replaceAll(Matcher.quoteReplacement(File.separator), ".");
        if (packPath.isEmpty()) {
            return null;
        }
        return packPath;
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

    private void parseColumn(DasObject dasObject, GeneratorProperties config) {
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dasObject);
        for(DasColumn dasColumn : columns){
            AgileColumnModel columnModel = new AgileColumnModel(dasColumn, config);
            setImport(columnModel.getImports());
            setColumn(columnModel);
        }
    }

    public void setColumn(AgileColumnModel column) {
        this.columns.add(column);
    }
}
