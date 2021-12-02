package cloud.agileframework.generator.model.swagger;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Builder
@Data
public class SwaggerApi {
    private Set<String> tags;
    private String summary;
    @Builder.Default
    private String description = "";
    private String operationId;
    private String[] consumes;
    @Builder.Default
    private String[] produces = {"application/json"};
    private Set<SwaggerApiParameter> parameters;
    private Map<String, ResponseData> responses;

    public enum IN {
        query, body, path, formData, header, cookie
    }

    @Data
    @Builder
    public static class SwaggerApiParameter {
        private IN in;
        private String name;
        private String description;
        @Builder.Default
        private boolean required = false;
        private SwaggerPropertyType type;
        private SwaggerPropertyFormat format;
        @JSONField(name = "default")
        private Object defaults;
        @JSONField(name = "enum")
        private Set<String> enums;
        private SwaggerProperty items;
        private SwaggerCollectionFormat collectionFormat;
        private SwaggerProperty schema;
        private String title;
    }

    @Data
    public static class SwaggerProperty {
        private SwaggerPropertyType type;
        private SwaggerPropertyFormat format;
        private Map<String, SwaggerProperty> properties;
        private Set<String> required;
        private Object example;
        @JSONField(name = "default")
        private Object defaults;
        private String description;
        @JSONField(name = "enum")
        private Set<String> enums;
        private SwaggerProperty items;
        private SwaggerCollectionFormat collectionFormat;
        @JSONField(name = "$ref")
        private String ref;
        private String title;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Map<String, SwaggerProperty> properties;
            private Set<String> required;
            private SwaggerPropertyType type;
            private SwaggerPropertyFormat format;
            private Object defaults;
            private Object example;
            private String description;
            private Set<String> enums;
            private SwaggerProperty items;
            private SwaggerCollectionFormat collectionFormat;
            private String ref;
            private String title;

            public Builder property(String key, SwaggerProperty property) {
                if (properties == null) {
                    this.properties = Maps.newConcurrentMap();
                }
                properties.put(key, property);
                return this;
            }

            public Builder required(String... requireds) {
                if (required == null) {
                    this.required = Sets.newHashSet();
                }
                this.required.addAll(Arrays.asList(requireds));
                return this;
            }

            public Builder type(SwaggerPropertyType type) {
                this.type = type;
                return this;
            }

            public Builder format(SwaggerPropertyFormat format) {
                this.format = format;
                return this;
            }

            public Builder properties(Map<String, SwaggerProperty> properties) {
                if (this.properties == null) {
                    this.properties = Maps.newConcurrentMap();
                }
                this.properties.putAll(properties);
                return this;
            }

            public Builder example(Object example) {
                this.example = example;
                return this;
            }

            public Builder defaults(Object defaults) {
                this.defaults = defaults;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder enums(Set<String> enums) {
                this.enums = enums;
                return this;
            }

            public Builder items(SwaggerProperty items) {
                this.items = items;
                return this;
            }

            public Builder ref(String schema) {
                this.ref = "#/definitions/" + schema;
                return this;
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public SwaggerProperty build() {
                SwaggerProperty a = new SwaggerProperty();
                a.setFormat(format);
                a.setType(type);
                a.setProperties(properties);
                a.setExample(example);
                a.setDefaults(defaults);
                a.setEnums(enums);
                a.setDescription(description);
                a.setCollectionFormat(collectionFormat);
                a.setItems(items);
                a.setRef(ref);
                a.setTitle(title);
                a.setRequired(required);
                return a;
            }
        }
    }

    @Data
    @Builder
    public static class ResponseData {
        private String description;
        private SwaggerProperty schema;
    }
}
