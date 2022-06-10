package cloud.agileframework.generator.handler;

import cloud.agileframework.common.util.file.FileUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.generator.properties.GeneratorProperties;
import cloud.agileframework.generator.properties.TYPE;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.regex.Matcher;

public abstract class AbstractGenerator {
    @Autowired
    GeneratorProperties generator;

    /**
     * 推测生成java文件的包名
     *
     * @param url 生成目标文件存储路径
     * @return 包名
     */
    String getPackPath(String url) {
        url = FileUtil.parseFilePath(url);
        String javaSourceUrl = FileUtil.parseFilePath(generator.getJavaSourceUrl());
        if (StringUtil.isEmpty(javaSourceUrl)) {
            String javaPath = File.separator + "java" + File.separator;
            if (!url.contains(javaPath)) {
                return null;
            }
        }
        url = url.substring(url.indexOf(javaSourceUrl) + javaSourceUrl.length());
        if (url.length() > 0) {
            return url.substring(0, url.length() - 1).replaceAll(Matcher.quoteReplacement(File.separator), ".");
        }
        return null;
    }

    /**
     * 统一路径中的斜杠
     *
     * @param str 路径
     * @return 处理后的合法路径
     */
    String parseFilePath(String str) {
        String url = str.replaceAll("[\\\\/]+", Matcher.quoteReplacement(File.separator));
        if (!url.endsWith(File.separator)) {
            url += File.separator;
        }
        return url;
    }

    /**
     * 判断是否需要执行
     * @param types 生成类型
     * @return true生成
     */
    public boolean is(TYPE[] types) {
        return ArrayUtils.contains(types, type());
    }

    /**
     * 取模板
     *
     * @return 模板
     */
    public abstract String freemarkerTemplate();

    /**
     * 生成的文件扩展名
     *
     * @return 文件扩展名
     */
    public abstract String fileExtension();

    /**
     * 生成的文件扩展名
     *
     * @return 文件扩展名
     */
    public abstract TYPE type();
}
