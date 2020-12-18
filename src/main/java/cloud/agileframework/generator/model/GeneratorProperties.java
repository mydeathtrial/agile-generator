package cloud.agileframework.generator.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/1/31 9:39
 * 描述： TODO
 * @since 1.0
 */
public class GeneratorProperties {
    /**
     * 实体文件生成到的目录地址
     */
    private String entityUrl;
    /**
     * Service文件生成到的目录地址
     */
    private String serviceUrl;
    /**
     * 测试文件生成到的目录地址
     */
    private String testUrl;
    /**
     * 实体类生成名前缀
     */
    private String entityPrefix = "";
    /**
     * 实体类生成名后缀
     */
    private String entitySuffix = "Entity";
    /**
     * service类生成名前缀
     */
    private String servicePrefix = "";
    /**
     * service类生成名后缀
     */
    private String serviceSuffix = "Service";
    /**
     * 源码包
     */
    private String javaSourceUrl;
    /**
     * 测试文件类生成名前缀
     */
    private String testPrefix = "";
    /**
     * 测试文件类生成名后缀
     */
    private String testSuffix = "Test";
    /**
     * 代码生成器目标表名字,可逗号分隔,可like百分号形式模糊匹配
     */
    private String tableName = "%";
    /**
     * 表名大小写是否敏感
     */
    private boolean isSensitive = false;
    /**
     * 数据库字段类型与java映射规则
     */
    private Map<String, Class<?>> columnType = new HashMap<String, Class<?>>();

    private Set<AnnotationType> annotation = Sets.newHashSet();

    private final String[] keywords = "a,abort,abs,absent,absolute,access,according,action,ada,add,admin,after,aggregate,all,allocate,also,alter,always,analyse,analyze,and,any,are,array,array_agg,array_max_cardinality,as,asc,asensitive,assertion,assignment,asymmetric,at,atomic,attribute,attributes,authorization,avg,backward,base64,before,begin,begin_frame,begin_partition,bernoulli,between,bigint,binary,bit,bit_length,blob,blocked,bom,boolean,both,breadth,by,c,cache,call,called,cardinality,cascade,cascaded,case,cast,catalog,catalog_name,ceil,ceiling,chain,char,character,characteristics,characters,character_length,character_set_catalog,character_set_name,character_set_schema,char_length,check,checkpoint,class,class_origin,clob,close,cluster,coalesce,cobol,collate,collation,collation_catalog,collation_name,collation_schema,collect,column,columns,column_name,command_function,command_function_code,comment,comments,commit,committed,concurrently,condition,condition_number,configuration,connect,connection,connection_name,constraint,constraints,constraint_catalog,constraint_name,constraint_schema,constructor,contains,content,continue,control,conversion,convert,copy,corr,corresponding,cost,count,covar_pop,covar_samp,create,cross,csv,cube,cume_dist,current,current_catalog,current_date,current_default_transform_group,current_path,current_role,current_row,current_schema,current_time,current_timestamp,current_transform_group_for_type,current_user,cursor,cursor_name,cycle,data,database,datalink,date,datetime_interval_code,datetime_interval_precision,day,db,deallocate,dec,decimal,declare,default,defaults,deferrable,deferred,defined,definer,degree,delete,delimiter,delimiters,dense_rank,depth,deref,derived,desc,describe,descriptor,deterministic,diagnostics,dictionary,disable,discard,disconnect,dispatch,distinct,dlnewcopy,dlpreviouscopy,dlurlcomplete,dlurlcompleteonly,dlurlcompletewrite,dlurlpath,dlurlpathonly,dlurlpathwrite,dlurlscheme,dlurlserver,dlvalue,do,document,domain,double,drop,dynamic,dynamic_function,dynamic_function_code,each,element,else,empty,enable,encoding,encrypted,end,end-exec,end_frame,end_partition,enforced,enum,equals,escape,event,every,except,exception,exclude,excluding,exclusive,exec,execute,exists,exp,explain,expression,extension,external,extract,false,family,fetch,file,filter,final,first,first_value,flag,float,floor,following,for,force,foreign,fortran,forward,found,frame_row,free,freeze,from,fs,full,function,functions,fusion,g,general,generated,get,global,go,goto,grant,granted,greatest,group,grouping,groups,handler,having,header,hex,hierarchy,hold,hour,id,identity,if,ignore,ilike,immediate,immediately,immutable,implementation,implicit,import,in,including,increment,indent,index,indexes,indicator,inherit,inherits,initially,inline,inner,inout,input,insensitive,insert,instance,instantiable,instead,int,integer,integrity,intersect,intersection,interval,into,invoker,is,isnull,isolation,join,k,key,key_member,key_type,label,lag,language,large,last,last_value,lateral,lc_collate,lc_ctype,lead,leading,leakproof,least,left,length,level,library,like,like_regex,limit,link,listen,ln,load,local,localtime,localtimestamp,location,locator,lock,lower,m,map,mapping,match,matched,materialized,max,maxvalue,max_cardinality,member,merge,message_length,message_octet_length,message_text,method,min,minute,minvalue,mod,mode,modifies,module,month,more,move,multiset,mumps,name,names,namespace,national,natural,nchar,nclob,nesting,new,next,nfc,nfd,nfkc,nfkd,nil,no,none,normalize,normalized,not,nothing,notify,notnull,nowait,nth_value,ntile,null,nullable,nullif,nulls,number,numeric,object,occurrences_regex,octets,octet_length,of,off,offset,oids,old,on,only,open,operator,option,options,or,order,ordering,ordinality,others,out,outer,output,over,overlaps,overlay,overriding,owned,owner,p,pad,parameter,parameter_mode,parameter_name,parameter_ordinal_position,parameter_specific_catalog,parameter_specific_name,parameter_specific_schema,parser,partial,partition,pascal,passing,passthrough,password,path,percent,percentile_cont,percentile_disc,percent_rank,period,permission,placing,plans,pli,portion,position,position_regex,power,precedes,preceding,precision,prepare,prepared,preserve,primary,prior,privileges,procedural,procedure,program,public,quote,range,rank,read,reads,real,reassign,recheck,recovery,recursive,ref,references,referencing,refresh,regr_avgx,regr_avgy,regr_count,regr_intercept,regr_r2,regr_slope,regr_sxx,regr_sxy,regr_syy,reindex,relative,release,rename,repeatable,replace,replica,requiring,reset,respect,restart,restore,restrict,result,return,returned_cardinality,returned_length,returned_octet_length,returned_sqlstate,returning,returns,revoke,right,role,rollback,rollup,routine,routine_catalog,routine_name,routine_schema,row,rows,row_count,row_number,rule,savepoint,scale,schema,schema_name,scope,scope_catalog,scope_name,scope_schema,scroll,search,second,section,security,select,selective,self,sensitive,sequence,sequences,serializable,server,server_name,session,session_user,set,setof,sets,share,show,similar,simple,size,smallint,snapshot,some,source,space,specific,specifictype,specific_name,sql,sqlcode,sqlerror,sqlexception,sqlstate,sqlwarning,sqrt,stable,standalone,start,state,statement,static,statistics,stddev_pop,stddev_samp,stdin,stdout,storage,strict,strip,structure,style,subclass_origin,submultiset,substring,substring_regex,succeeds,sum,symmetric,sysid,system,system_time,system_user,t,table,tables,tablesample,tablespace,table_name,temp,template,temporary,text,then,ties,time,timestamp,timezone_hour,timezone_minute,to,token,top_level_count,trailing,transaction,transactions_committed,transactions_rolled_back,transaction_active,transform,transforms,translate,translate_regex,translation,treat,trigger,trigger_catalog,trigger_name,trigger_schema,trim,trim_array,true,truncate,trusted,type,types,uescape,unbounded,uncommitted,under,unencrypted,union,unique,unknown,unlink,unlisten,unlogged,unnamed,unnest,until,untyped,update,upper,uri,usage,user,user_defined_type_catalog,user_defined_type_code,user_defined_type_name,user_defined_type_schema,using,vacuum,valid,validate,validator,value,values,value_of,varbinary,varchar,variadic,varying,var_pop,var_samp,verbose,version,versioning,view,volatile,when,whenever,where,whitespace,width_bucket,window,with,within,without,work,wrapper,write,xml,xmlagg,xmlattributes,xmlbinary,xmlcast,xmlcomment,xmlconcat,xmldeclaration,xmldocument,xmlelement,xmlexists,xmlforest,xmliterate,xmlnamespaces,xmlparse,xmlpi,xmlquery,xmlroot,xmlschema,xmlserialize,xmltable,xmltext,xmlvalidate,year,yes,zone".split(",");

    public Class<?> getJavaType(String type) {
        return columnType.get(type);
    }

    public GeneratorProperties() {
        columnType.put("bigint", Long.class);
        columnType.put("bit", Boolean.class);
        columnType.put("char", String.class);
        columnType.put("datetime", Date.class);
        columnType.put("time", Date.class);
        columnType.put("date", Date.class);
        columnType.put("mediumtext", String.class);
        columnType.put("bolb", byte[].class);
        columnType.put("clob", String.class);
        columnType.put("decimal", Double.class);
        columnType.put("double", Double.class);
        columnType.put("float", Float.class);
        columnType.put("image", byte[].class);
        columnType.put("int", Integer.class);
        columnType.put("longblob", Byte.class);
        columnType.put("money", Double.class);
        columnType.put("nchar", String.class);
        columnType.put("number", BigDecimal.class);
        columnType.put("numeric", Double.class);
        columnType.put("nvarchar", String.class);
        columnType.put("real", Double.class);
        columnType.put("smallint", Double.class);
        columnType.put("text", String.class);
        columnType.put("timestamp", Date.class);
        columnType.put("tinyint", Integer.class);
        columnType.put("varchar", String.class);
        columnType.put("varchar2", String.class);
        columnType.put("tinytext", String.class);
        columnType.put("longtext", String.class);
        columnType.put("character", String.class);

//        keywords.add("order");
//        keywords.add("dec");
//        keywords.add("desc");
//        keywords.add("name");
//        keywords.add("code");
//        keywords.add("status");
//        keywords.add("where");
//        keywords.add("select");
//        keywords.add("mode");
    }

    public String getEntityUrl() {
        return entityUrl;
    }

    public void setEntityUrl(String entityUrl) {
        this.entityUrl = entityUrl;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getEntityPrefix() {
        return entityPrefix;
    }

    public void setEntityPrefix(String entityPrefix) {
        this.entityPrefix = entityPrefix;
    }

    public String getEntitySuffix() {
        return entitySuffix;
    }

    public void setEntitySuffix(String entitySuffix) {
        this.entitySuffix = entitySuffix;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public String getServiceSuffix() {
        return serviceSuffix;
    }

    public void setServiceSuffix(String serviceSuffix) {
        this.serviceSuffix = serviceSuffix;
    }

    public String getTestPrefix() {
        return testPrefix;
    }

    public void setTestPrefix(String testPrefix) {
        this.testPrefix = testPrefix;
    }

    public String getTestSuffix() {
        return testSuffix;
    }

    public void setTestSuffix(String testSuffix) {
        this.testSuffix = testSuffix;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isSensitive() {
        return isSensitive;
    }

    public void setSensitive(boolean sensitive) {
        isSensitive = sensitive;
    }

    public Map<String, Class<?>> getColumnType() {
        return columnType;
    }

    public void setColumnType(Map<String, Class<?>> columnType) {
        this.columnType = columnType;
    }

    public Set<AnnotationType> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Set<AnnotationType> annotation) {
        this.annotation = annotation;
    }

    public String[] getKeywords() {
        return keywords;
    }

//    public void setKeywords(List<String> keywords) {
//        this.keywords = keywords;
//    }

    public String getJavaSourceUrl() {
        return javaSourceUrl;
    }

    public void setJavaSourceUrl(String javaSourceUrl) {
        this.javaSourceUrl = javaSourceUrl;
    }
}
