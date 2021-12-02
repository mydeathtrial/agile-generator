package cloud.agileframework.generator.model;

import cloud.agileframework.generator.properties.AnnotationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
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
    }
}
