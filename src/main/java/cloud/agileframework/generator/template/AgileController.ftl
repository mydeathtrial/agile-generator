<#if (controllerPackageName?? && controllerPackageName!="")>package ${controllerPackageName};

</#if>
import cloud.agileframework.abstractbusiness.controller.AllBusinessController;
import ${doPackageName}.${doName};
import ${voPackageName}.${inVoName};
import ${voPackageName}.${outVoName};
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
<#list importDesc as import>
import ${import};
</#list>

/**
<#if (remarks?? && remarks!="")> * 描述：${remarks}控制器</#if>
*
* @author agile generator
*/


@Controller
@RequestMapping("/api/${modelName}/${javaName}")
public class ${javaName}Controller implements AllBusinessController<${doName}, ${inVoName}, ${outVoName}> {

}
