package cloud.agileframework.generator.model.config;

import lombok.Data;

@Data
public class PropertyConfig {
    private PropertyBaseValue value;

    public void setValue(PropertyBaseValue value) {
        if (PropertyType.dic == value.getType()) {
            this.value = new PropertyDicValue(value.getType(), value.getValue());
            return;
        }
        this.value = value;
    }
}
