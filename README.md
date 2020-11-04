# agile-generator ： 代码生成器
[![freemark](https://img.shields.io/badge/freemark-LATEST-green)](https://img.shields.io/badge/freemark-LATEST-green)
[![maven](https://img.shields.io/badge/build-maven-green)](https://img.shields.io/badge/build-maven-green)
## 它有什么作用

* **持久层POJO生成**

* **自定义数据库类型与java类型映射**

* **支持生成JPA、HibernateValidate、lombok等注解**

-------
## 快速入门
开始你的第一个项目是非常容易的。
可以参照样例原代码地址：`https://gitee.com/agile-framework/agile-generator-demo`

#### 步骤 1: 下载包
您可以从[最新稳定版本]下载包(https://github.com/mydeathtrial/agile-generator/releases).
该包已上传至maven中央仓库，可在pom中直接声明引用

以版本agile-generator-2.0.6.jar为例。
#### 步骤 2: 添加maven依赖
```xml
<!--声明中央仓库-->
<repositories>
    <repository>
        <id>cent</id>
        <url>https://repo1.maven.org/maven2/</url>
    </repository>
</repositories>
<!--声明依赖-->
<dependency>
    <groupId>cloud.agileframework</groupId>
    <artifactId>agile-generator</artifactId>
    <version>2.0.6</version>
</dependency>
```
#### 步骤 3: 开箱即用
```yaml
//配置生成规则
agile:
  generator:
    //实体类生成的前缀
    entity-prefix: ''
    //实体类生成的后缀
    entity-suffix: Entity
    //实体类生成文件存放位置
    entity-url: D:\workspace-agile\agile-generator\src\test\java\com\agile\entity
    //扫描的数据库表名，支持英文逗号分隔与`like + %`方式形式过滤
    table-name: 'sys_%,dictionary_data'
    //生成的注解种类，共4种，JPA（JPA注解）、VALIDATE（hibernate-validate验证注解）、LOMBOK（lombok注解）、REMARK（备注信息注解）
    annotation: LOMBOK,REMARK
    //数据库关键字，当字段名与关键字重叠时，生成器会为其添加``号，生成器已内置部分关键字，更多关键字可以在此添加
    keywords: order,dec,desc
    //字段类型映射，key为数据库类型，value为java类型
    column-type:
      char: java.lang.String
      varchar: java.lang.String

//数据库连接，代码生成器借用spring-boot-starter默认的连接方式，获取数据库连接信息
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dsmc?serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL&autoReconnect=true&allowPublicKeyRetrieval=true
    username: root
    password: 123456
```
运行生成器
代码生成器是通过main方法直接运行触发，可以直接运行`cloud.agileframework.generator.AgileEntityGenerator`

运行后日志
```
20:19:59.250 [main] INFO cloud.agileframework.generator.AgileEntityGenerator - 【1】开始生成源代码
...
20:20:00.722 [main] INFO cloud.agileframework.generator.AgileEntityGenerator - 【2】完成配置初始化，开始生成文件...
...
20:20:09.379 [main] DEBUG cloud.agileframework.generator.util.FreemarkerUtil - 生成文件[D:\workspace-agile\agile-generator\src\test\java\com\agile\entity\UebaStrategyRealtimeTimewindowEntity.java]
20:20:09.411 [main] DEBUG cloud.agileframework.generator.util.FreemarkerUtil - 生成文件[D:\workspace-agile\agile-generator\src\test\java\com\agile\entity\UebaStrategyRealtimeTriggerEntity.java]
20:20:09.442 [main] DEBUG cloud.agileframework.generator.util.FreemarkerUtil - 生成文件[D:\workspace-agile\agile-generator\src\test\java\com\agile\entity\UebaStrategyRepeatEntity.java]
20:20:09.489 [main] DEBUG cloud.agileframework.generator.util.FreemarkerUtil - 生成文件[D:\workspace-agile\agile-generator\src\test\java\com\agile\entity\UebaStrategyTimewindowEntity.java]
20:20:09.504 [main] DEBUG cloud.agileframework.generator.util.FreemarkerUtil - 生成文件[D:\workspace-agile\agile-generator\src\test\java\com\agile\entity\UebaUserWatchEntity.java]
20:20:09.520 [main] INFO cloud.agileframework.generator.AgileEntityGenerator - 【3】完成源代码生成
...
Process finished with exit code 0

```
#####注意
代码生成器是依赖配置文件读取数据库信息，所以运行时需要注意控制台开始打印的加载配置文件记录中是否包含你的
配置文件，配置文件会加载编译路径下的所有properties、yml配置文件，如下：
```
15:26:55.779 [main] INFO cloud.agileframework.generator.AgileGenerator - 【1】开始生成源代码
/cloud/agileframework/generator/template/Entity.ftl
/cloud/agileframework/generator/template/Showdoc.ftl
/cloud/agileframework/generator/template/Test.ftl
/META-INF/additional-spring-configuration-metadata.json
/META-INF/spring.factories
/application.yml
```