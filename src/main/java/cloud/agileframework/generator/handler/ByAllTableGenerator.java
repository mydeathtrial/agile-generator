package cloud.agileframework.generator.handler;

import cloud.agileframework.generator.model.TableModel;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.List;

public abstract class ByAllTableGenerator extends AbstractGenerator {
    public abstract void generateFile(List<TableModel> tableModel) throws TemplateException, IOException;
}
