package cloud.agileframework.generator.model;

import cloud.agileframework.generator.properties.AnnotationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UpdateTimeColumn extends ColumnModel {
    public static boolean is(Map<String, Object> column) {
        String columnName = String.valueOf(column.get("COLUMN_NAME")).toLowerCase(Locale.ROOT);
        return "update_time".equals(columnName) || "update_date".equals(columnName);
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
        addAnnotation(UpdateTimestamp.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
    }

    @Override
    public boolean isGeneric() {
        return false;
    }
}
