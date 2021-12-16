package cloud.agileframework.generator.handler;

import cloud.agileframework.generator.model.*;
import cloud.agileframework.generator.properties.AnnotationType;
import cloud.agileframework.generator.properties.TYPE;
import cloud.agileframework.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class AgileAbstractBusinessGenerator extends ByTableGenerator {
    @Override
    public String freemarkerTemplate() {
        return "AgileEntity.ftl";
    }

    @Override
    public String fileExtension() {
        return ".java";
    }

    @Override
    public TYPE type() {
        return TYPE.AGILE_CODE;
    }

    @Override
    public void generateFile(TableModel tableModel) throws TemplateException, IOException {
        tableModel.getColumns().removeIf(c -> c instanceof CreateTimeColumn
                || c instanceof UpdateTimeColumn
                || c instanceof CreateUserColumn
                || c instanceof UpdateUserColumn
                || c instanceof DeleteColumn);


        tableModel.getColumns().stream().filter(c -> c instanceof PrimaryKeyColumn).forEach(c -> {
            c.addAnnotation(new GeneratedValue() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return GeneratedValue.class;
                }

                @Override
                public GenerationType strategy() {
                    return GenerationType.AUTO;
                }

                @Override
                public String generator() {
                    return "custom-id";
                }
            }, AnnotationType.JPA, desc -> c.getAnnotationDesc().add(desc));

            c.addAnnotation(new GenericGenerator() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return GenericGenerator.class;
                }

                @Override
                public String name() {
                    return "custom-id";
                }

                @Override
                public String strategy() {
                    return "cloud.agileframework.jpa.dao.IDGenerator";
                }

                @Override
                public Parameter[] parameters() {
                    return new Parameter[0];
                }
            }, AnnotationType.JPA, desc -> c.getAnnotationDesc().add(desc));
            tableModel.setImport(c.getImports());

        });

        tableModel.getAnnotationDesc().remove("@Builder");
        tableModel.addAnnotation(SuperBuilder.class, AnnotationType.LOMBOK, desc -> tableModel.getAnnotationDesc().add(desc));
        tableModel.build();

        String baseUrl = parseUrl(generator.getEntityUrl()) + tableModel.getModelName() + File.separator + tableModel.getMvcPackageName() + File.separator;

        String doUrl = baseUrl + "pojo" + File.separator + "db" + File.separator;
        String doFileName = tableModel.getDoName() + fileExtension();
        tableModel.setDoPackageName(getPackPath(doUrl));
        FreemarkerUtil.generatorProxy("AgileDo.ftl", doUrl, doFileName, tableModel, false);

        String voUrl = baseUrl + "pojo" + File.separator + "vo" + File.separator;
        String inVoFileName = tableModel.getInVoName() + fileExtension();
        tableModel.setVoPackageName(getPackPath(voUrl));
        FreemarkerUtil.generatorProxy("AgileInVo.ftl", voUrl, inVoFileName, tableModel, false);
        String outVoFileName = tableModel.getOutVoName() + fileExtension();
        tableModel.setVoPackageName(getPackPath(voUrl));
        FreemarkerUtil.generatorProxy("AgileOutVo.ftl", voUrl, outVoFileName, tableModel, false);

        String controllerUrl = baseUrl + "controller" + File.separator;
        String controllerFileName = tableModel.getJavaName() + "Controller" + fileExtension();
        tableModel.setControllerPackageName(getPackPath(controllerUrl));
        FreemarkerUtil.generatorProxy("AgileController.ftl", controllerUrl, controllerFileName, tableModel, false);
    }
}

