package cloud.agileframework.generator.model;

import cloud.agileframework.common.annotation.Remark;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.properties.AnnotationType;
import cloud.agileframework.generator.properties.GeneratorProperties;
import cloud.agileframework.spring.util.BeanUtil;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author 佟盟
 * 日期 2020/8/00013 13:59
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Setter
@Getter
public class BaseModel implements Serializable {
    private static GeneratorProperties properties = BeanUtil.getBean(GeneratorProperties.class);
    private static DataSourceProperties dataSourceProperties = BeanUtil.getBean(DataSourceProperties.class);

    private Set<Class<?>> imports = Sets.newHashSet();
    private Set<String> importDesc = Sets.newHashSet();
    private Set<Annotation> annotations = Sets.newHashSet();
    private Set<Class<? extends Annotation>> annotationClass = Sets.newHashSet();
    private Set<String> annotationDesc = Sets.newHashSet();

    private String remarks;

    public static String toBlank(String str) {
        return str == null ? "" : str;
    }

    public static String annotationToDesc(Class<? extends Annotation> annotation, Consumer<Class<?>> consumer) {
        consumer.accept(annotation);
        return "@" + annotation.getSimpleName();
    }

    /**
     * 注解转换为用于freemark的描述
     *
     * @param annotation 注解
     * @param consumer   自定义方法
     * @return 用于freemark的描述
     */
    public static String annotationToDesc(Annotation annotation, Consumer<Class<?>> consumer) {
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
                if (returnType != void.class) {
                    consumer.accept(returnType);
                }

                Object value = method.invoke(annotation);
                String key = method.getName();
                String stringValue = annotationPropertyValueToDesc(consumer, returnType, value);

                return String.format("%s = %s", key, stringValue);
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

    /**
     * 把注解的属性值，转换为可直接用于freemark打印的字符串
     *
     * @param consumer                自定义方法
     * @param annotationPropertyType  属性类型
     * @param annotationPropertyValue 属性值
     * @return 可直接用于freemark打印的字符串
     */
    private static String annotationPropertyValueToDesc(Consumer<Class<?>> consumer, Class<?> annotationPropertyType, Object annotationPropertyValue) {
        String stringValue;
        if (annotationPropertyValue instanceof String) {
            stringValue = String.format("\"%s\"", annotationPropertyValue);
        } else if (Annotation.class.isAssignableFrom(annotationPropertyType)) {
            stringValue = String.format("%s",
                    annotationToDesc((Annotation) annotationPropertyValue, consumer));
        } else if (annotationPropertyValue != null && annotationPropertyValue.getClass().isArray()) {
            Class<?> innerClass = TypeReference.extractArray(annotationPropertyValue.getClass());

            int length = Array.getLength(annotationPropertyValue);

            stringValue = String.format("{%s}",
                    IntStream.range(0, length).mapToObj(index -> {
                        Object node = Array.get(annotationPropertyValue, index);
                        return annotationPropertyValueToDesc(consumer, innerClass, node);
                    }).collect(Collectors.joining(", ")));

        } else {
            stringValue = String.format("%s", annotationPropertyType.isEnum() ? annotationPropertyType.getSimpleName() + "." + annotationPropertyValue : annotationPropertyValue);
        }
        return stringValue;
    }

    public static GeneratorProperties getProperties() {
        return properties;
    }

    public static DataSourceProperties getDataSourceProperties() {
        return dataSourceProperties;
    }

    public void setImport(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isArray()) {
                clazz = TypeReference.extractArray(clazz);
                if (clazz == null) {
                    return;
                }
            }
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

    public void setRemarks(String remarks) {
        this.remarks = deleteHiddenCharacter(remarks);
        if (!StringUtil.isEmpty(this.remarks)) {
            setImport(Remark.class);
        }
    }

    public String deleteHiddenCharacter(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("[\\s]+", "");
    }

    public void addAnnotation(Annotation annotation, AnnotationType annotationType, Consumer<String> consumer) {
        if (getProperties().getAnnotation().contains(annotationType)) {
            consumer.accept(annotationToDesc(annotation, this::setImport));
        }
    }

    public void addAnnotation(Class<? extends Annotation> annotation, AnnotationType annotationType, Consumer<String> consumer) {
        if (getProperties().getAnnotation().contains(annotationType)) {
            consumer.accept(annotationToDesc(annotation, this::setImport));
        }
    }
}
