package cloud.agileframework.generator.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrimaryKeyColumn extends ColumnModel{
    public static boolean is(Map<String, Object> column) {
        return Boolean.parseBoolean(String.valueOf(column.get("IS_PRIMARY_KEY")));
    }

    @Override
    public boolean isGeneric() {
        return false;
    }
}
