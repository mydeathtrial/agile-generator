package com.agile.mvc.service;

import com.agile.mvc.controller.MainControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author agile gennerator
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ${className}Test extends MainControllerTest {
<#list methodList as method>
    @Test
    public void ${method.methodName}() throws Exception {
    this.setUrl("${method.url}");
    this.setMethod("post");

    <#list propertyList as property>
        this.setParameter("${property}", "");
    </#list>

    this.processor();
    }

</#list>

}