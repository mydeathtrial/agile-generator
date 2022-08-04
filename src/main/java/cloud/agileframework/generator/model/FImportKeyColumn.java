package cloud.agileframework.generator.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@NoArgsConstructor
public class FImportKeyColumn extends ColumnModel {
    private String pkName;
    private String fktableSchem;
    private String deferrability;
    private String deleteRule;
    private String pkcolumnName;
    private String pktableCat;
    private String fkName;
    private String fktableName;
    private String fktableCat;
    private String pktableSchem;
    private String pktableName;
    private String updateRule;
    private String keySeq;
    private String fkcolumnName;

    public static boolean is(Map<String, Object> column, Set<FImportKeyColumn> fImportKeyColumns) {
        if (fImportKeyColumns == null) {
            return false;
        }
        String columnName = String.valueOf(column.get("COLUMN_NAME")).toLowerCase(Locale.ROOT);
        return fImportKeyColumns.stream().anyMatch(c -> Objects.equals(c.getFkcolumnName(), columnName));
    }


    @Override
    public boolean isGeneric() {
        return false;
    }
}
