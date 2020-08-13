package ${entityPackageName};

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
<#list imports as import>
import ${import};
</#list>

/**
<#if (remarks?? && remarks!="")> * 描述：${remarks}</#if>
 * @author agile generator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "${tableName}")<#if (remarks?? && remarks!="")>
@Remark("${remarks}")</#if>
public class ${entityName} implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
<#list columns as property>
    <#if property.remarks?? && property.remarks!="">
    @Remark("${property.remarks}")
    </#if>
    <#if property.defValue??>
    @Builder.Default
    </#if>
    private ${property.javaSimpleTypeName} ${property.javaName}<#if property.defValue??> = <#if property.javaSimpleTypeName=="Boolean" ||  property.javaSimpleTypeName=="boolean"><#if property.defValue=="0">false<#else>true</#if><#else>${property.defValue}</#if></#if>;
</#list>

<#list columns as property>
    <#if property.annotations??><#list property.annotations as annotation>
    ${annotation}
    </#list></#if>
    public ${property.javaSimpleTypeName} ${property.getMethod}() {
        return ${property.javaName};
    }

</#list>

    @Override
    public ${entityName} clone() {
        try {
            return (${entityName}) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}
