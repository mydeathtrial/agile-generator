package com.agile.common.generator;

import com.agile.common.properties.GeneratorProperties;
import com.agile.common.util.ApiUtil;
import com.agile.common.util.FactoryUtil;
import freemarker.template.TemplateException;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mydeathtrial on 2017/5/10.
 */
public class AgileTestGenerator {
    private static GeneratorProperties generator;

    public static void main(String[] args) throws IOException, TemplateException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        AgileGenerator.init();
        generator = FactoryUtil.getBean(GeneratorProperties.class);
        creatTest("./src/main/java/com/agile/mvc/service");
    }

    private static void creatTest(String path) throws IOException, TemplateException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        File directory = new File(path);
        String packageName = AgileGenerator.getPackPath(path);
        if (directory.isDirectory()) {
            String[] files = directory.list();
            if (files == null) {
                return;
            }
            for (String fileName : files) {
                if (!"java".equals(StringUtils.getFilenameExtension(fileName))) {
                    creatTest(path + "/" + fileName);
                } else {
                    //分割获取类名
                    String serviceName = fileName.replaceAll(".java$", "");
                    String entityName = null;
                    Map<String, Object> data = new HashMap<>();
                    List<Map<String, String>> methodList = new ArrayList<>();
                    List<String> propertyList = new ArrayList<>();
                    //循环处理各个类
                    try {
                        Class<?> clazz = Class.forName(packageName + "." + serviceName);
                        ApiUtil.registerApiMapping(serviceName, clazz.newInstance());

//                        Method[] methods = clazz.getDeclaredMethods();
//                        Method[] methods1 = clazz.getMethods();
//                        for (Method method : methods) {
//
//                            if (!ArrayUtil.contains(methods1, method)) {
//                                continue;
//                            }
//                            String methodName = method.getName();
//                            Map<String, String> map = new HashMap<>();
//                            map.put("methodName", methodName);
//                            map.put("url", "/api/" + serviceName + "/" + methodName);
//                            methodList.add(map);
//                        }
//
//
//                        if (serviceName.startsWith(generator.getServicePrefix())) {
//                            entityName = serviceName.replaceFirst(generator.getServicePrefix(), "");
//                        }
//                        if (serviceName.endsWith(generator.getServiceSuffix())) {
//                            assert entityName != null;
//                            entityName = entityName.replaceAll(generator.getServiceSuffix() + "$", "");
//                        }
//                        if (!StringUtil.isEmpty(generator.getEntityPrefix())) {
//                            entityName = generator.getEntityPrefix() + entityName;
//                        }
//                        if (!StringUtil.isEmpty(generator.getEntitySuffix())) {
//                            entityName = entityName + generator.getEntitySuffix();
//                        }
//                        try {
//                            Class<?> entity = Class.forName("com.agile.mvc.entity." + entityName);
//                            Method[] entityMethods = entity.getMethods();
//                            for (Method entityMethod : entityMethods) {
//                                String entityMethodName = entityMethod.getName();
//                                if (entityMethodName.startsWith("set")) {
//                                    propertyList.add(StringUtil.toLowerName(entityMethodName));
//                                }
//                            }
//                        } catch (Exception ignored) {
//                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                    //数据装填
//                    data.put("className", serviceName);
//                    data.put("methodList", methodList);
//                    data.put("propertyList", propertyList);
//
//                    //生成器
//                    String testFileName = serviceName + "Test.java";
//                    FreemarkerUtil.generatorProxy("Test.ftl", "./test/main/java/com/agile/mvc/service/", testFileName, data, false);
                }
            }

        }
        System.exit(0);
    }


}
