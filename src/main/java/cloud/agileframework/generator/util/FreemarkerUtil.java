package cloud.agileframework.generator.util;

import cloud.agileframework.generator.AgileGenerator;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author 佟盟 on 2018/6/29
 */
public class FreemarkerUtil {
    private static final Logger logger = LoggerFactory.getLogger(FreemarkerUtil.class);
    private static final Configuration CFG = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    private static final String ENCODER = StandardCharsets.UTF_8.name();

    static {
        initFreemarker();
    }

    private static void initFreemarker() {
        try {
            CFG.setClassForTemplateLoading(AgileGenerator.class, "/cloud/agileframework/generator/template");
            CFG.setDefaultEncoding(ENCODER);
            CFG.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Template getTemplate(String name) {
        try {
            return CFG.getTemplate(name, ENCODER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成器引擎
     *
     * @param templateUri 模板地址
     * @param fileName    文件名
     * @param data        数据
     */
    public static void generatorProxy(String templateUri, String directory, String fileName, Object data, boolean append) throws IOException, TemplateException {
        Template template = getTemplate(templateUri);
        File serviceFileDir = new File(directory);
        if (!serviceFileDir.exists()) {
            boolean f = serviceFileDir.mkdirs();
            if (!f) {
                logger.error(String.format("无法创建代码生成路径：%s", directory));
                return;
            }
        }
        File serviceFile = new File(serviceFileDir.getPath() + File.separator + fileName);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("生成文件[%s]", serviceFile.getAbsoluteFile().getPath()));
        }
        BufferedWriter serviceFileBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile, append), ENCODER));
        assert template != null;
        template.process(data, serviceFileBw);
        serviceFileBw.flush();
        serviceFileBw.close();
    }
}
