<#if (entityPackageName?? && entityPackageName!="")>package ${entityPackageName};

</#if>import java.io.Serializable;
<#list importDesc as import>
import ${import};
</#list>

/**
<#if (remarks?? && remarks!="")> * 描述：${remarks}</#if>
 *
 * @author agile generator
 */
<#list annotationDesc as annotation>
${annotation}
</#list>
public class ${entityName} implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
<#list columns as property>
    <#list property.fieldAnnotationDesc as annotation>
    ${annotation}
    </#list>
    <#if property.defValue??>
    @Builder.Default
    </#if>
    private ${property.javaSimpleTypeName} ${property.javaName}<#if property.defValue??> = <#if property.javaSimpleTypeName=="Boolean" ||  property.javaSimpleTypeName=="boolean"><#if property.defValue=="0">false<#else>true</#if><#else>${property.defValue}</#if></#if>;
</#list>

<#if haveGetMethod><#list columns as property>
    <#if property.annotationDesc??><#list property.annotationDesc as annotation>
    ${annotation}
    </#list></#if>
    public ${property.javaSimpleTypeName} ${property.getMethod}() {
        return ${property.javaName};
    }

</#list></#if>
<#if haveSetMethod><#list columns as property>
    public void ${property.setMethod}(${property.javaSimpleTypeName} ${property.javaName}) {
        this.${property.javaName} = ${property.javaName};
    }

</#list></#if>
    @Override
    public ${entityName} clone() {
        try {
            return (${entityName}) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}
