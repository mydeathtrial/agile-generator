package cloud.agileframework.generator.model.swagger;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tags {
    private String name;
    private String description;
}
