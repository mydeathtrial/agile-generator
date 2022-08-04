package cloud.agileframework.generator.model.config;

import lombok.Data;

@Data
public class PropertyBaseValue {

    private PropertyType type;
    private String value;

    public PropertyBaseValue() {
    }

    public PropertyBaseValue(PropertyType type, String value) {
        this.type = type;
        this.value = value;
    }
}
