package cloud.agileframework.generator.model.swagger;

import cloud.agileframework.common.util.http.RequestMethod;
import cloud.agileframework.common.util.number.NumberUtil;
import cloud.agileframework.generator.model.ColumnModel;
import cloud.agileframework.generator.model.PrimaryKeyColumn;
import cloud.agileframework.generator.model.TableModel;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Data
public class Swagger {
    private String swagger = "2.0";
    private JSON info = JSON.parseObject("{\n" +
            "    \"description\": \"This is a sample server Petstore server.  You can find out more about     Swagger at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).      For this sample, you can use the api key `special-key` to test the authorization     filters.\",\n" +
            "    \"version\": \"1.0.0\",\n" +
            "    \"title\": \"Swagger Petstore\",\n" +
            "    \"termsOfService\": \"http://swagger.io/terms/\",\n" +
            "    \"contact\": {\n" +
            "      \"email\": \"apiteam@swagger.io\"\n" +
            "    },\n" +
            "    \"license\": {\n" +
            "      \"name\": \"Apache 2.0\",\n" +
            "      \"url\": \"http://www.apache.org/licenses/LICENSE-2.0.html\"\n" +
            "    }\n" +
            "  }");
    private String host = "petstore.swagger.io";
    private String basePath = "/v2";
    private Set<Tags> tags;
    private String[] schemes = new String[]{"https", "http"};
    private Map<String, Map<RequestMethod, SwaggerApi>> paths;
    private JSON securityDefinitions = JSON.parseObject("{\n" +
            "    \"petstore_auth\": {\n" +
            "      \"type\": \"oauth2\",\n" +
            "      \"authorizationUrl\": \"http://petstore.swagger.io/oauth/dialog\",\n" +
            "      \"flow\": \"implicit\",\n" +
            "      \"scopes\": {\n" +
            "        \"write:pets\": \"modify pets in your account\",\n" +
            "        \"read:pets\": \"read your pets\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"api_key\": {\n" +
            "      \"type\": \"apiKey\",\n" +
            "      \"name\": \"api_key\",\n" +
            "      \"in\": \"header\"\n" +
            "    }\n" +
            "  }");
    private Map<String, SwaggerApi.SwaggerProperty> definitions = Maps.newHashMap();

    private JSON externalDocs = JSON.parseObject("{\n" +
            "    \"description\": \"Find out more about Swagger\",\n" +
            "    \"url\": \"http://swagger.io\"\n" +
            "  }");

    public Swagger(List<TableModel> tableModels) {
        definitions.put("HttpStatus", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).enums(Arrays.stream(HttpStatus.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet())).build());
        tags = tableModels.parallelStream()
                .map(t -> Tags.builder()
                        .name(t.getRemarks())
                        .description("管理" + t.getRemarks())
                        .build())
                .collect(Collectors.toSet());
        paths = tableModels.stream()
                .map(this::createApis)
                .reduce((a, all) -> {
                    all.putAll(a);
                    return all;
                }).orElse(Maps.newHashMap());
    }

    private Map<String, Map<RequestMethod, SwaggerApi>> createApis(TableModel t) {
        Map<String, Map<RequestMethod, SwaggerApi>> result = Maps.newConcurrentMap();

        Map<RequestMethod, SwaggerApi> map = Maps.newConcurrentMap();
        addApi(t, map);
        updateApi(t, map);
        result.put("/api/" + t.getJavaName(), map);

        Map<RequestMethod, SwaggerApi> map1 = Maps.newConcurrentMap();
        deleteApi(t, map1);
        findByIdApi(t, map1);
        result.put("/api/" + t.getJavaName() + "/{id}", map1);

        Map<RequestMethod, SwaggerApi> map2 = Maps.newConcurrentMap();
        pageApi(t, map2);
        result.put("/api/" + t.getJavaName() + "/page", map2);

        return result;
    }

    /**
     * 删除
     *
     * @param t   表信息
     * @param map api容器
     */
    private void deleteApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        map.put(RequestMethod.DELETE, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("删除" + t.getRemarks())
                .operationId("delete_" + t.getJavaName())
                .parameters(Sets.newHashSet(SwaggerApi.SwaggerApiParameter.builder()
                        .in(SwaggerApi.IN.path)
                        .name("id")
                        .required(true)
                        .type(SwaggerPropertyType.string)
                        .description("唯一标识")
                        .build())
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("服务成功")
                            .schema(properties(null))
                            .build());
                }})
                .build());
    }

    /**
     * 根据主键查询
     *
     * @param t   表信息
     * @param map api容器
     */
    private void findByIdApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        map.put(RequestMethod.GET, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("根据主键查询" + t.getRemarks())
                .operationId("findById_" + t.getJavaName())
                .parameters(Sets.newHashSet(SwaggerApi.SwaggerApiParameter.builder()
                        .in(SwaggerApi.IN.path)
                        .name("id")
                        .required(true)
                        .type(SwaggerPropertyType.string)
                        .description("唯一标识")
                        .build())
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("服务成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .properties(t.getColumns()
                                            .stream()
                                            .collect(getColumnModelMapCollector()))
                                    .build()))
                            .build());
                }})
                .build());
    }

    private Collector<ColumnModel, ?, Map<String, SwaggerApi.SwaggerProperty>> getColumnModelMapCollector() {
        return Collectors.toMap(ColumnModel::getJavaName, c -> {
            SwaggerApi.SwaggerProperty v = SwaggerApi.SwaggerProperty.builder()
                    .type(swaggerPropertyType(c))
                    .description(c.getRemarks())
                    .format(swaggerPropertyFormat(c))
                    .build();
            if (SwaggerPropertyType.array == v.getType()) {
                v.setItems(SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).build());
            }
            return v;
        });
    }

    /**
     * 分页查询
     *
     * @param t   表信息
     * @param map api容器
     */
    private void pageApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        Set<SwaggerApi.SwaggerApiParameter> parameters = t.getColumns()
                .stream().filter(ColumnModel::isGeneric)
                .map(c -> SwaggerApi.SwaggerApiParameter.builder()
                        .in(SwaggerApi.IN.formData)
                        .name(c.getJavaName())
                        .format(swaggerPropertyFormat(c))
                        .required("0".equals(c.getNullable()))
                        .type(swaggerPropertyType(c))
                        .description(c.getRemarks())
                        .build()).collect(Collectors.toSet());

        parameters.add(SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.formData)
                .name("pageSize")
                .format(SwaggerPropertyFormat.int32)
                .defaults(10)
                .type(SwaggerPropertyType.integer)
                .description("每页大小")
                .build());
        parameters.add(SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.formData)
                .name("pageNum")
                .format(SwaggerPropertyFormat.int32)
                .defaults(1)
                .type(SwaggerPropertyType.integer)
                .description("第几页")
                .build());
        map.put(RequestMethod.POST, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .description(t.getRemarks() + "分页")
                .summary(t.getRemarks() + "分页")
                .operationId("page_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(parameters
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("服务成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .property("total", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.integer).format(SwaggerPropertyFormat.int32).defaults(20).build())
                                    .property("context", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.array).items(
                                            SwaggerApi.SwaggerProperty.builder()
                                                    .type(SwaggerPropertyType.object)
                                                    .properties(t.getColumns()
                                                            .stream()
                                                            .collect(getColumnModelMapCollector()))
                                                    .build()
                                    ).build())
                                    .build()))
                            .build());
                }})
                .build());
    }

    /**
     * 新增
     *
     * @param t   表信息
     * @param map api容器
     */
    private void addApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        //新增
        map.put(RequestMethod.POST, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("新建" + t.getRemarks())
                .operationId("add_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(t.getColumns()
                        .stream().filter(ColumnModel::isGeneric)
                        .map(c -> SwaggerApi.SwaggerApiParameter.builder()
                                .in(SwaggerApi.IN.formData)
                                .name(c.getJavaName())
                                .format(swaggerPropertyFormat(c))
                                .required("0".equals(c.getNullable()))
                                .type(swaggerPropertyType(c))
                                .description(c.getRemarks())
                                .build()).collect(Collectors.toSet())
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("服务成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .properties(t.getColumns()
                                            .stream()
                                            .collect(getColumnModelMapCollector()))
                                    .build()))
                            .build());
                }})
                .build());
    }

    /**
     * 修改
     *
     * @param t   表信息
     * @param map api容器
     */
    private void updateApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        map.put(RequestMethod.PUT, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("修改" + t.getRemarks())
                .operationId("update_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(t.getColumns()
                        .stream().filter(c -> c.isGeneric() || c instanceof PrimaryKeyColumn)
                        .map(c -> SwaggerApi.SwaggerApiParameter.builder()
                                .in(SwaggerApi.IN.formData)
                                .name(c.getJavaName())
                                .format(swaggerPropertyFormat(c))
                                .required("0".equals(c.getNullable()))
                                .type(swaggerPropertyType(c))
                                .description(c.getRemarks())
                                .build()).collect(Collectors.toSet())
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("服务成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .properties(t.getColumns()
                                            .stream()
                                            .collect(getColumnModelMapCollector()))
                                    .build()))
                            .build());
                }})
                .build());
    }

    /**
     * 根据表信息生成swagger参数信息
     *
     * @return swagger参数信息
     */
    private SwaggerApi.SwaggerProperty properties(SwaggerApi.SwaggerProperty swaggerProperty) {
        SwaggerApi.SwaggerProperty.Builder propertyBuilder = SwaggerApi.SwaggerProperty.builder();

        propertyBuilder.type(SwaggerPropertyType.object);
        propertyBuilder.property("head", SwaggerApi.SwaggerProperty.builder()
                .type(SwaggerPropertyType.object)
                .property("ip", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).defaults("11.66.77.116").build())
                .property("code", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).defaults("000000").build())
                .property("msg", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).defaults("服务执行成功").build())
                .property("status", SwaggerApi.SwaggerProperty.builder().ref("HttpStatus").build())
                .build());
        if (swaggerProperty != null) {
            propertyBuilder.property("result", swaggerProperty);
        }

        return propertyBuilder.build();
    }


    private SwaggerPropertyType swaggerPropertyType(ColumnModel c) {
        SwaggerPropertyType type;
        if (NumberUtil.isNumber(c.getJavaType()) && Long.class != c.getJavaType()) {
            type = SwaggerPropertyType.integer;
        } else if (c.getJavaType().isArray() || Collection.class.isAssignableFrom(c.getJavaType())) {
            type = SwaggerPropertyType.array;
        } else {
            type = SwaggerPropertyType.string;
        }
        return type;
    }

    private SwaggerPropertyFormat swaggerPropertyFormat(ColumnModel c) {
        SwaggerPropertyFormat format;
        if (Long.class == c.getJavaType()) {
            format = SwaggerPropertyFormat.int64;
        } else if (Integer.class == c.getJavaType()) {
            format = SwaggerPropertyFormat.int32;
        } else if (Date.class.isAssignableFrom(c.getJavaType())) {
            format = SwaggerPropertyFormat.date_time;
        } else {
            format = null;
        }
        return format;
    }
}
