package cloud.agileframework.generator.model.swagger;

import com.alibaba.fastjson.annotation.JSONField;

public enum SwaggerPropertyFormat {
    @JSONField(name = "int64")
    int64,
    @JSONField(name = "int32")
    int32,
    @JSONField(name = "date-time")
    date_time,
    @JSONField(name = "double")
    doublec,
    @JSONField(name = "float")
    floatc,

    binary
}
