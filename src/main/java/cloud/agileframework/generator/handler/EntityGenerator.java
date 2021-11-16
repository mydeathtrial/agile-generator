package cloud.agileframework.generator.handler;

import cloud.agileframework.generator.model.TableModel;
import cloud.agileframework.generator.properties.TYPE;
import cloud.agileframework.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;

import java.io.IOException;

public class EntityGenerator extends ByTableGenerator {
    @Override
    public String freemarkerTemplate() {
        return "Entity.ftl";
    }

    @Override
    public String fileExtension() {
        return ".java";
    }

    @Override
    public TYPE type() {
        return TYPE.ENTITY;
    }

    @Override
    public void generateFile(TableModel tableModel) throws TemplateException, IOException {
        String url = parseUrl(generator.getEntityUrl());
        String fileName = tableModel.getEntityName() + fileExtension();
        tableModel.setEntityPackageName(getPackPath(url));
        FreemarkerUtil.generatorProxy(freemarkerTemplate(), url, fileName, tableModel, false);
    }
}
