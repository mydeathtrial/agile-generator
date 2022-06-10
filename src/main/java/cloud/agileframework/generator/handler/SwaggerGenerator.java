package cloud.agileframework.generator.handler;

import cloud.agileframework.common.util.file.FileUtil;
import cloud.agileframework.common.util.http.RequestMethod;
import cloud.agileframework.generator.model.TableModel;
import cloud.agileframework.generator.model.swagger.Swagger;
import cloud.agileframework.generator.model.swagger.SwaggerPropertyType;
import cloud.agileframework.generator.properties.TYPE;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class SwaggerGenerator extends ByAllTableGenerator {
    @Override
    public String freemarkerTemplate() {
        return "swagger.ftl";
    }

    @Override
    public String fileExtension() {
        return ".json";
    }

    @Override
    public TYPE type() {
        return TYPE.SWAGGER;
    }

    @Override
    public void generateFile(List<TableModel> tableModels) throws IOException {
        String url = FileUtil.parseFilePath(generator.getEntityUrl());
        String fileName = "swagger_" + System.currentTimeMillis() + fileExtension();
        Swagger swagger = new Swagger(tableModels);

        SerializeConfig s = SerializeConfig.getGlobalInstance();
        s.put(RequestMethod.class, new ObjectSerializer() {
            @Override
            public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) {
                jsonSerializer.write(((RequestMethod) o).name().toLowerCase(Locale.ROOT));
            }
        });
        s.put(SwaggerPropertyType.class, new ObjectSerializer() {
            @Override
            public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) {
                if (o == SwaggerPropertyType.booleanc) {
                    jsonSerializer.write("boolean");
                    return;
                }
                jsonSerializer.write(((SwaggerPropertyType) o).name().toLowerCase(Locale.ROOT));
            }
        });
        FileUtils.writeByteArrayToFile(new File(url + fileName), JSON.toJSONBytes(swagger, SerializeConfig.getGlobalInstance()), false);
    }
}
