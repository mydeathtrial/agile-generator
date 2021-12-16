<#if (voPackageName?? && voPackageName!="")>package ${voPackageName};

</#if>
import cloud.agileframework.abstractbusiness.pojo.vo.BaseInParamVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
<#list importDesc as import>
import ${import};
</#list>

/**
<#if (remarks?? && remarks!="")> * 描述：${remarks}入参</#if>
*
* @author agile generator
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ${inVoName} extends BaseInParamVo {

    private static final long serialVersionUID = 1L;
<#list columns as property>
    <#if (property.remarks?? && property.remarks!="")>
    /**
    * 描述：${property.remarks}
    */
    </#if>
    <#list property.fieldAnnotationDesc as annotation>
    ${annotation}
    </#list>
    <#if property.validateAnnotationDesc??><#list property.validateAnnotationDesc as annotation>
    ${annotation}
    </#list></#if>
    private ${property.javaSimpleTypeName} ${property.javaName}<#if property.defValue??> = <#if property.javaSimpleTypeName=="Boolean" ||  property.javaSimpleTypeName=="boolean"><#if property.defValue=="0">false<#else>true</#if><#else>${property.defValue}</#if></#if>;
</#list>
}
