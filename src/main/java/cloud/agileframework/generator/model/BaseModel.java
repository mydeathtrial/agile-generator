package cloud.agileframework.generator.model;

import com.google.common.collect.Sets;
import com.intellij.database.model.DasObject;
import com.intellij.database.util.DasUtil;
import lombok.Data;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * 日期 2020-12-16 15:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
public class BaseModel {
    /**
     * 导入包
     */
    private Set<Class<?>> imports = Sets.newHashSet();
    /**
     * 导入包描绘
     */
    private Set<String> importDesc = Sets.newHashSet();

    /**
     * 注解描绘
     */
    private Set<String> annotationDesc = Sets.newHashSet();

    /**
     * 名字
     */
    private String name;

    /**
     * 备注
     */
    private String remarks;
    private String tableCat;
    private String schema;
    private GeneratorProperties config;

    public BaseModel() {
    }

    public BaseModel(DasObject dasObject, GeneratorProperties config) {
        this.config = config;
        this.remarks = dasObject.getComment();
        this.name = dasObject.getName();
        this.tableCat = DasUtil.getCatalog(dasObject);
        this.schema = DasUtil.getSchema(dasObject);
    }


    public static String toString(Class<? extends Annotation> annotation, Consumer<Class<?>> consumer) {
        consumer.accept(annotation);
        return "@" + annotation.getSimpleName();
    }

    public static String toBlank(String str) {
        return str == null ? "" : str;
    }

    public static String toString(Annotation annotation, Consumer<Class<?>> consumer) {
        Class<? extends Annotation> type = annotation.annotationType();
        consumer.accept(type);
        Method[] methods = type.getDeclaredMethods();
        String inner = Arrays.stream(methods).filter(method -> {
            try {
                return !new EqualsBuilder().append(method.getDefaultValue(), method.invoke(annotation)).isEquals();
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
            return false;
        }).map(method -> {
            try {
                Class<?> returnType = method.getReturnType();
                consumer.accept(returnType);
                Object value = method.invoke(annotation);
                String key = method.getName();
                if (value instanceof String) {
                    return String.format("%s = \"%s\"", key, value);
                } else {
                    return String.format("%s = %s", key, returnType.isEnum() ? returnType.getSimpleName() + "." + value : value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.joining(", "));
        if (StringUtils.isEmpty(inner)) {
            return "@" + type.getSimpleName();
        } else {
            return String.format("@%s(%s)", type.getSimpleName(), inner);
        }
    }

    public void setImport(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isPrimitive() || clazz.getPackage().getName().startsWith("java.lang")) {
                continue;
            }
            this.imports.add(clazz);
            this.importDesc.add(String.format("%s.%s", clazz.getPackage().getName(), clazz.getSimpleName()));
        }
    }

    public void setImport(Set<Class<?>> classes) {
        if (classes == null) {
            return;
        }
        for (Class<?> clazz : classes) {
            setImport(clazz);
        }
    }

    public String deleteHiddenCharacter(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("[\\s]+", "");
    }

    public void addAnnotation(Annotation annotation, AnnotationType annotationType, Consumer<String> consumer) {
        if (config.getAnnotation().contains(annotationType)) {
            consumer.accept(toString(annotation, this::setImport));
        }
    }

    public void addAnnotation(Class<? extends Annotation> annotation, AnnotationType annotationType, Consumer<String> consumer) {
        if (config.getAnnotation().contains(annotationType)) {
            consumer.accept(toString(annotation, this::setImport));
        }
    }

}
