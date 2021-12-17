<#if (controllerPackageName?? && controllerPackageName!="")>package ${controllerPackageName};

</#if>
import ${doPackageName}.${doName};
import ${voPackageName}.${inVoName};
import ${voPackageName}.${outVoName};
import cloud.agileframework.abstractbusiness.service.AllBusinessService;
import cloud.agileframework.mvc.annotation.AgileService;
import cloud.agileframework.mvc.annotation.Mapping;
<#list importDesc as import>
import ${import};
</#list>

/**
<#if (remarks?? && remarks!="")> * 描述：${remarks}控制器</#if>
*
* @author agile generator
*/


@AgileService
@Mapping("/api/${modelName}/${javaName}")
public class ${javaName}Service implements AllBusinessService<${doName}, ${inVoName}, ${outVoName}> {

}
