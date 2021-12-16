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
public class DicColumn extends ColumnModel {
    @Override
    public boolean isGeneric() {
        return false;
    }
}
