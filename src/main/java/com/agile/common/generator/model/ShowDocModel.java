package com.agile.common.generator.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
public class ShowDocModel {
    private String module;
    private String desc;
    private Set<String> url;
    private String method;
    private String request;
    private String response;
    private Set<Param> requestParams;
    private Set<Param> responseParams;

    /**
     * 参数
     */
    @Data
    public static class Param {
        private String name;
        private boolean nullable = false;
        private String type;
        private String desc;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
