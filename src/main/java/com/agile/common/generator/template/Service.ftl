package ${servicePackageName};

import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import ${entityPackageName}.${entityName};

/**
 * @author agile generator
 */
@Mapping("/api/${moduleName}")
@Service
public class ${serviceName} extends BusinessService<${entityName}> {

    @Validate(beanClass = ${entityName}.class, validateGroups = Insert.class)
    @Mapping(value = "/${entityCenterLineName}", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @Validate(beanClass = ${entityName}.class, validateGroups = Delete.class)
    @Mapping(path = "/${entityCenterLineName}/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @Validate(beanClass = ${entityName}.class, validateGroups = Update.class)
    @Mapping(value = "/${entityCenterLineName}", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @Mapping(path = "/${entityCenterLineName}/list/query", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @Mapping(path = "/${entityCenterLineName}/query", method = RequestMethod.POST)
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @Mapping(path = "/${entityCenterLineName}/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() throws NoSuchIDException {
        return super.queryById();
    }
}
