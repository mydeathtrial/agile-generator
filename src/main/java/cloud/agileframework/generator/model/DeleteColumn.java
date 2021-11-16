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
public class DeleteColumn extends ColumnModel {
    private String deleted = "1";
    private String noDelete = "0";

    public static boolean is(Map<String, Object> column) {
        return "delete".equals(String.valueOf(column.get("COLUMN_NAME")).toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean isGeneric() {
        return false;
    }
}
