package cloud.agileframework.generator.handler;

import cloud.agileframework.generator.model.TableModel;
import freemarker.template.TemplateException;

import java.io.IOException;

public abstract class ByTableGenerator extends AbstractGenerator {
    public abstract void generateFile(TableModel tableModel) throws TemplateException, IOException;
}
