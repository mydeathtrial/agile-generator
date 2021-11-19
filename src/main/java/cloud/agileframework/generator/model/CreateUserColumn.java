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
public class CreateUserColumn extends ColumnModel {
    public static boolean is(Map<String, Object> column) {
        String columnName = String.valueOf(column.get("COLUMN_NAME")).toLowerCase(Locale.ROOT);
        return "create_user".equals(columnName)
                || "create_user_id".equals(columnName);
    }
}
