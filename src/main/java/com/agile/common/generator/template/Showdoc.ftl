

**简要描述：**

- ${desc}

**请求URL：**
<#list url as param>
- ` ${param} `
</#list>

**请求方式：**
- ${method}

**参数：**

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
<#if requestParams??>
<#list requestParams as param>
|${param.name} |<#if param.nullable>true<#else>false</#if>  |${param.type} |${param.desc}   |
</#list>
<#else>
|无 |无  |无 |无   |
</#if>

**返回示例**

```
<#if response??>${response}</#if>
```

**返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
<#if responseParams??>
<#list responseParams as param>
|${param.name} |${param.type}   |${param.desc}  |
</#list>
<#else>
|无 |无 |无 |
</#if>
**备注**

- 更多返回错误代码请看首页的错误代码描述


