package cloud.agileframework.generator.model.swagger;

import cloud.agileframework.common.util.http.RequestMethod;
import cloud.agileframework.common.util.number.NumberUtil;
import cloud.agileframework.generator.model.ColumnModel;
import cloud.agileframework.generator.model.ParentKeyColumn;
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
    private Map<String, SwaggerApi.SwaggerProperty> definitions = Maps.newHashMap();

    private JSON externalDocs = JSON.parseObject("{\n" +
            "    \"description\": \"Find out more about Swagger\",\n" +
            "    \"url\": \"http://swagger.io\"\n" +
            "  }");

    public Swagger(List<TableModel> tableModels) {
        definitions.put("HttpStatus", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).example("OK").enums(Arrays.stream(HttpStatus.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet())).build());
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
        deleteApi(t, map);
        result.put("/api/" + t.getJavaName(), map);

        Map<RequestMethod, SwaggerApi> map1 = Maps.newConcurrentMap();
        findByIdApi(t, map1);
        result.put("/api/" + t.getJavaName() + "/{id}", map1);

        Map<RequestMethod, SwaggerApi> map2 = Maps.newConcurrentMap();
        pageApi(t, map2);
        result.put("/api/" + t.getJavaName() + "/page", map2);

        Map<RequestMethod, SwaggerApi> map3 = Maps.newConcurrentMap();
        importApi(t, map3);
        exportApi(t, map3);
        result.put("/api/" + t.getJavaName() + "/store", map3);

        Optional<ColumnModel> isTree = t.getColumns().stream().filter(c -> c instanceof ParentKeyColumn).findAny();
        if (isTree.isPresent()) {
            Map<RequestMethod, SwaggerApi> map4 = Maps.newConcurrentMap();
            treeApi(t, map4);
            result.put("/api/" + t.getJavaName() + "/tree", map4);
        }

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
                .parameters(createParameter(SwaggerApi.SwaggerApiParameter.builder()
                        .in(SwaggerApi.IN.query)
                        .name("id")
                        .required(true)
                        .type(SwaggerPropertyType.array)
                        .description("唯一标识")
                        .items(SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).build())
                        .build())
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
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
                .parameters(createParameter(SwaggerApi.SwaggerApiParameter.builder()
                        .in(SwaggerApi.IN.path)
                        .name("id")
                        .required(true)
                        .type(SwaggerPropertyType.string)
                        .build())
                )
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .title("查询结果")
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
                    .title(c.getRemarks())
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
    private void treeApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {

        SwaggerApi.ResponseData value = SwaggerApi.ResponseData.builder()
                .description("操作成功")
                .schema(properties(SwaggerApi.SwaggerProperty.builder()
                        .type(SwaggerPropertyType.object)
                        .title("查询结果")
                        .property("id", SwaggerApi.SwaggerProperty.builder().title("唯一标识").type(SwaggerPropertyType.string).build())
                        .property("parentId", SwaggerApi.SwaggerProperty.builder().title("父级唯一标识").type(SwaggerPropertyType.string).build())
                        .property("children", SwaggerApi.SwaggerProperty.builder().title("子节点").type(SwaggerPropertyType.array).items(
                                SwaggerApi.SwaggerProperty.builder()
                                        .property("id", SwaggerApi.SwaggerProperty.builder().title("唯一标识").type(SwaggerPropertyType.string).build())
                                        .property("parentId", SwaggerApi.SwaggerProperty.builder().title("父级唯一标识").type(SwaggerPropertyType.string).build())
                                        .property("children", SwaggerApi.SwaggerProperty.builder().title("子节点").type(SwaggerPropertyType.array).items(SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.object).build()).build())
                                        .properties(t.getColumns()
                                                .stream().filter(ColumnModel::isGeneric)
                                                .collect(getColumnModelMapCollector())).build()).build())
                        .properties(t.getColumns().stream().filter(ColumnModel::isGeneric)
                                .collect(getColumnModelMapCollector()))
                        .build()))
                .build();

        map.put(RequestMethod.POST, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .description(t.getRemarks() + "树形")
                .summary(t.getRemarks() + "树形")
                .operationId("tree_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(createParameter())
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", value);
                }})
                .build());
    }

    /**
     * 分页查询
     *
     * @param t   表信息
     * @param map api容器
     */
    private void pageApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {

        SwaggerApi.SwaggerProperty.Builder build = SwaggerApi.SwaggerProperty.builder()
                .type(SwaggerPropertyType.object)
                .property("pageSize", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.integer).defaults(10).format(SwaggerPropertyFormat.int32).title("每页大小").build())
                .property("pageNum", SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.integer).defaults(1).format(SwaggerPropertyFormat.int32).title("第几页").build());

        t.getColumns()
                .stream().filter(ColumnModel::isGeneric)
                .forEach(c -> build.property(c.getJavaName(),
                                SwaggerApi.SwaggerProperty.builder()
                                        .type(swaggerPropertyType(c))
                                        .format(swaggerPropertyFormat(c))
                                        .title(c.getRemarks())
                                        .build())
                        .build());

        SwaggerApi.SwaggerApiParameter swaggerApiParameter = SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.body)
                .name("请求参数")
                .required(true)
                .schema(build.build())
                .build();

        map.put(RequestMethod.POST, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .description(t.getRemarks() + "分页")
                .summary(t.getRemarks() + "分页")
                .operationId("page_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(createParameter(swaggerApiParameter))
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .title("查询结果")
                                    .property("total", SwaggerApi.SwaggerProperty.builder().title("总条数").type(SwaggerPropertyType.integer).format(SwaggerPropertyFormat.int32).defaults(20).build())
                                    .property("context", SwaggerApi.SwaggerProperty.builder().title("当前页面数据").type(SwaggerPropertyType.array).items(
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
     * 分页查询
     *
     * @param t   表信息
     * @param map api容器
     */
    private void importApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {

        SwaggerApi.SwaggerApiParameter swaggerApiParameter = SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.formData)
                .description("导入文件")
                .type(SwaggerPropertyType.file)
                .name("请求参数")
                .required(true)
                .build();

        map.put(RequestMethod.POST, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .description(t.getRemarks() + "批量导入")
                .summary(t.getRemarks() + "批量导入")
                .operationId("import_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(createParameter(swaggerApiParameter))
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
                            .schema(properties(null))
                            .build());
                }})
                .build());
    }

    /**
     * 分页查询
     *
     * @param t   表信息
     * @param map api容器
     */
    private void exportApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        map.put(RequestMethod.GET, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("导出" + t.getRemarks())
                .operationId("export_" + t.getJavaName())
                .parameters(createParameter(SwaggerApi.SwaggerApiParameter.builder()
                        .in(SwaggerApi.IN.query)
                        .name("id")
                        .required(true)
                        .type(SwaggerPropertyType.array)
                        .description("唯一标识")
                        .items(SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).build())
                        .build())
                )
                .description("当导出数量小于1000条数据时，直接提供浏览器文件下载；\n" +
                        "当导出数量超过1000条数据时,系统自动将导出文件转至下载中心，并返回响应提示")
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
                            .schema(properties(null))
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

        SwaggerApi.SwaggerProperty.Builder build = SwaggerApi.SwaggerProperty.builder()
                .type(SwaggerPropertyType.object);

        t.getColumns()
                .stream().filter(ColumnModel::isGeneric)
                .forEach(c -> build.property(c.getJavaName(),
                                SwaggerApi.SwaggerProperty.builder()
                                        .type(swaggerPropertyType(c))
                                        .format(swaggerPropertyFormat(c))
                                        .title(c.getRemarks())
                                        .build())
                        .build());

        SwaggerApi.SwaggerApiParameter swaggerApiParameter = SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.body)
                .name("请求参数")
                .required(true)
                .schema(build.build())
                .build();


        //新增
        map.put(RequestMethod.POST, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("新建" + t.getRemarks())
                .operationId("add_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(createParameter(swaggerApiParameter))
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .title("新增后的数据")
                                    .properties(t.getColumns()
                                            .stream()
                                            .collect(getColumnModelMapCollector()))
                                    .build()))
                            .build());
                }})
                .build());
    }

    /**
     * 批量修改
     *
     * @param t   表信息
     * @param map api容器
     */
    private void updateApi(TableModel t, Map<RequestMethod, SwaggerApi> map) {
        SwaggerApi.SwaggerProperty.Builder build = SwaggerApi.SwaggerProperty.builder()
                .type(SwaggerPropertyType.object);

        t.getColumns()
                .stream().filter(c -> c.isGeneric() || c instanceof PrimaryKeyColumn)
                .forEach(c -> build.property(c.getJavaName(),
                                SwaggerApi.SwaggerProperty.builder()
                                        .type(swaggerPropertyType(c))
                                        .format(swaggerPropertyFormat(c))
                                        .title(c.getRemarks())
                                        .build())
                        .build());

        build.property("id",
                SwaggerApi.SwaggerProperty.builder()
                        .type(SwaggerPropertyType.array)
                        .items(SwaggerApi.SwaggerProperty.builder().type(SwaggerPropertyType.string).build())
                        .title("唯一标识")
                        .description("当为数组时，视为批量修改。该参数与主键参数至少有一个为非空")
                        .build());

        SwaggerApi.SwaggerApiParameter swaggerApiParameter = SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.body)
                .name("请求参数")
                .required(true)
                .schema(build.build())
                .build();

        map.put(RequestMethod.PUT, SwaggerApi.builder()
                .tags(Sets.newHashSet(t.getRemarks()))
                .summary("修改" + t.getRemarks())
                .operationId("update_" + t.getJavaName())
                .consumes(new String[]{"multipart/form-data"})
                .parameters(createParameter(swaggerApiParameter))
                .responses(new HashMap<String, SwaggerApi.ResponseData>() {{
                    put("200", SwaggerApi.ResponseData.builder()
                            .description("操作成功")
                            .schema(properties(SwaggerApi.SwaggerProperty.builder()
                                    .type(SwaggerPropertyType.object)
                                    .title("更新后的数据")
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
                .title("响应头")
                .type(SwaggerPropertyType.object)
                .property("ip", SwaggerApi.SwaggerProperty.builder().title("服务器IP").type(SwaggerPropertyType.string).example("11.66.77.116").build())
                .property("code", SwaggerApi.SwaggerProperty.builder().title("业务代码").description("6位业务代码，第一位（0代表成功，1代表失败，2代表错误，3代表警告）。第二、三位代表业务领域编号。后三位代表实际业务服务").type(SwaggerPropertyType.string).example("000000").build())
                .property("msg", SwaggerApi.SwaggerProperty.builder().title("响应信息").description("可用于前端冒泡提示框").type(SwaggerPropertyType.string).example("服务执行成功").build())
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
        } else if (Boolean.class == c.getJavaType()) {
            type = SwaggerPropertyType.booleanc;
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
        } else if (Double.class == c.getJavaType()) {
            format = SwaggerPropertyFormat.doublec;
        } else if (Float.class == c.getJavaType()) {
            format = SwaggerPropertyFormat.floatc;
        } else if (Date.class.isAssignableFrom(c.getJavaType())) {
            format = SwaggerPropertyFormat.date_time;
        } else {
            format = null;
        }
        return format;
    }

    private Set<SwaggerApi.SwaggerApiParameter> createParameter(SwaggerApi.SwaggerApiParameter... parameters) {
        HashSet<SwaggerApi.SwaggerApiParameter> parameterSet;
        if (parameters != null) {
            parameterSet = Sets.newHashSet(parameters);
        } else {
            parameterSet = Sets.newHashSet();
        }

        parameterSet.add(SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.header)
                .name("token")
                .required(true)
                .description("身份会话令牌")
                .type(SwaggerPropertyType.string)
                .build());
        parameterSet.add(SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.header)
                .name("replayToken")
                .required(true)
                .description("防重放令牌：RSA加密(时间戳+_SPLIT_+8位随机数)")
                .type(SwaggerPropertyType.string)
                .build());
        parameterSet.add(SwaggerApi.SwaggerApiParameter.builder()
                .in(SwaggerApi.IN.header)
                .name("falsifyToken")
                .required(true)
                .description("防篡改令牌：RSA加密(body参数)")
                .type(SwaggerPropertyType.string)
                .build());
        return parameterSet;
    }
}
