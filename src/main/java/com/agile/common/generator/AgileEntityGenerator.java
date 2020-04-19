package com.agile.common.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mydeathtrial on 2017/4/20
 */
public class AgileEntityGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AgileEntityGenerator.class);

    public static void main(String[] args) {
        try {
            logger.info("【1】开始生成源代码");
            AgileGenerator.init();
            logger.info("【2】完成配置初始化，开始生成文件...");
            AgileGenerator.generator(AgileGenerator.TYPE.ENTITY);
            logger.info("【3】完成源代码生成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}
