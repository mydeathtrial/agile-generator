package cloud.agileframework.generator.handler;

import cloud.agileframework.generator.model.TableModel;
import cloud.agileframework.generator.properties.TYPE;
import cloud.agileframework.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;

import java.io.IOException;

public class ServiceGenerator extends ByTableGenerator {
    @Override
    public String freemarkerTemplate() {
        return "Service.ftl";
    }

    @Override
    public String fileExtension() {
        return ".java";
    }

    @Override
    public TYPE type() {
        return TYPE.SERVICE;
    }

    @Override
    public void generateFile(TableModel tableModel) throws TemplateException, IOException {
        String url = parseUrl(generator.getServiceUrl());
        String fileName = tableModel.getServiceName() + fileExtension();
        tableModel.setServicePackageName(getPackPath(url));
        FreemarkerUtil.generatorProxy(freemarkerTemplate(), url, fileName, tableModel, false);
    }
}