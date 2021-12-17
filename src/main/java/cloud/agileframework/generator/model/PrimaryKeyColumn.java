package cloud.agileframework.generator.model;

import cloud.agileframework.generator.properties.AnnotationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrimaryKeyColumn extends ColumnModel {
    public static boolean is(Map<String, Object> column) {
        return Boolean.parseBoolean(String.valueOf(column.get("IS_PRIMARY_KEY")));
    }

    @Override
    public boolean isGeneric() {
        return false;
    }

    @Override
    public void build() {
        super.build();
        addAnnotation(Id.class, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
        addAnnotation(new GeneratedValue() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return GeneratedValue.class;
            }

            @Override
            public GenerationType strategy() {
                return GenerationType.AUTO;
            }

            @Override
            public String generator() {
                return "custom-id";
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));

        if (Long.class != getJavaType() && String.class != getJavaType()) {
            return;
        }
        addAnnotation(new GenericGenerator() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return GenericGenerator.class;
            }

            @Override
            public String name() {
                return "custom-id";
            }

            @Override
            public String strategy() {
                if (Long.class == getJavaType()) {
                    return "cloud.agileframework.jpa.dao.IDGenerator";
                }
                if (String.class == getJavaType()) {
                    return "cloud.agileframework.jpa.dao.IDGeneratorToString";
                }
                return null;
            }

            @Override
            public Parameter[] parameters() {
                return new Parameter[0];
            }
        }, AnnotationType.JPA, desc -> getAnnotationDesc().add(desc));
    }
}
