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
public class ${entityName} implements Serializable {

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

<#if haveEqualsAndHashCodeMethod>
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ${entityName} that = (${entityName}) o;
        return <#list columns as property>Objects.equals(${property.javaName}, that.${property.javaName})
                <#if property_has_next>&& </#if></#list>;
    }

    @Override
    public int hashCode() {
        return Objects.hash(<#list columns as property>${property.javaName}<#if property_has_next>, </#if></#list>);
    }
</#if>
}
