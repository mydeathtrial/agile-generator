package cloud.agileframework.generator.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ParentKeyColumn extends ColumnModel {
    public static boolean is(Map<String, Object> column) {
        String columnName = String.valueOf(column.get("COLUMN_NAME")).toLowerCase(Locale.ROOT);
        return "parent_id".equals(columnName);
    }

    @Override
    public boolean isGeneric() {
        return false;
    }
}
