package cloud.agileframework.generator.model;

import cloud.agileframework.generator.properties.AnnotationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreateTimeColumn extends ColumnModel {
    public static boolean is(Map<String, Object> column) {
        String columnName = String.valueOf(column.get("COLUMN_NAME")).toLowerCase(Locale.ROOT);
        return "creat_date".equals(columnName)
                || "creat_time".equals(columnName)
                || "create_time".equals(columnName)
                || "create_date".equals(columnName);
    }

    @Override
    public void setColumnName(String columnName) {
        super.setColumnName(columnName);

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

    @Override
    public boolean isGeneric() {
        return false;
    }
}
