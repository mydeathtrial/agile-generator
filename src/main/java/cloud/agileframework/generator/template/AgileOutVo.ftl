<#if (voPackageName?? && voPackageName!="")>package ${voPackageName};

</#if>
import cloud.agileframework.abstractbusiness.pojo.vo.IBaseOutParamVo;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
<#list importDesc as import>
    import ${import};
</#list>

/**
<#if (remarks?? && remarks!="")> * 描述：${remarks}出参</#if>
*
* @author agile generator
*/
@EqualsAndHashCode
@Data
@Builder
public class ${outVoName} implements IBaseOutParamVo {

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
    private ${property.javaSimpleTypeName} ${property.javaName}<#if property.defValue??> = <#if property.javaSimpleTypeName=="Boolean" ||  property.javaSimpleTypeName=="boolean"><#if property.defValue=="0">false<#else>true</#if><#else>${property.defValue}</#if></#if>;
</#list>
<#list newColumns as property>
    <#if (property.remarks?? && property.remarks!="")>
        /**
        * 描述：${property.remarks}
        */
    </#if>
    <#list property.fieldAnnotationDesc as annotation>
        ${annotation}
    </#list>
    <#list property.dicAnnotationDesc as annotation>
        ${annotation}
    </#list>
    private ${property.javaSimpleTypeName} ${property.javaName}<#if property.defValue??> = <#if property.javaSimpleTypeName=="Boolean" ||  property.javaSimpleTypeName=="boolean"><#if property.defValue=="0">false<#else>true</#if><#else>${property.defValue}</#if></#if>;
</#list>
}
