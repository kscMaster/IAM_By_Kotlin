package nancal.iam.db.es.table;

import nbcp.db.*;
import nbcp.db.es.*;
import nbcp.utils.*;
import nbcp.comm.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.*;
import nancal.iam.db.es.entity.*;

//generate auto @2022-08-12 18:03:58
public class DataMeta extends EsColumnName {
    private String parentPropertyName;
    DataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        

        this.action = EsrUtil.esColumnJoin(this.parentPropertyName, "action");

        this.resource = EsrUtil.esColumnJoin(this.parentPropertyName, "resource");

        this.remark = EsrUtil.esColumnJoin(this.parentPropertyName, "remark");

        this.result = EsrUtil.esColumnJoin(this.parentPropertyName, "result");

        this.ip = EsrUtil.esColumnJoin(this.parentPropertyName, "ip");

        this.os = EsrUtil.esColumnJoin(this.parentPropertyName, "os");

        this.browser = EsrUtil.esColumnJoin(this.parentPropertyName, "browser");

        this.city = EsrUtil.esColumnJoin(this.parentPropertyName, "city");

        this.tenant = new IdNameMeta(EsrUtil.esColumnJoin(this.parentPropertyName,"tenant"));

        this.appInfo = new CodeNameMeta(EsrUtil.esColumnJoin(this.parentPropertyName,"appInfo"));

        this.roles = new IdNameMeta(EsrUtil.esColumnJoin(this.parentPropertyName,"roles"));
    }
    
    DataMeta(EsColumnName value) {
        this(value.toString());
    }


    /**
     * 操作类型
     */
    @nbcp.db.Cn(value = "操作类型")
    private EsColumnName action = null;
    public EsColumnName getAction(){
        return action;
    }

    /**
     * 操作资源
     */
    @nbcp.db.Cn(value = "操作资源")
    private EsColumnName resource = null;
    public EsColumnName getResource(){
        return resource;
    }

    /**
     * 详情
     */
    @nbcp.db.Cn(value = "详情")
    private EsColumnName remark = null;
    public EsColumnName getRemark(){
        return remark;
    }

    /**
     * 结果
     */
    @nbcp.db.Cn(value = "结果")
    private EsColumnName result = null;
    public EsColumnName getResult(){
        return result;
    }

    /**
     * 客户端IP地址
     */
    @nbcp.db.Cn(value = "客户端IP地址")
    private EsColumnName ip = null;
    public EsColumnName getIp(){
        return ip;
    }

    /**
     * 操作系统
     */
    @nbcp.db.Cn(value = "操作系统")
    private EsColumnName os = null;
    public EsColumnName getOs(){
        return os;
    }

    /**
     * 浏览器
     */
    @nbcp.db.Cn(value = "浏览器")
    private EsColumnName browser = null;
    public EsColumnName getBrowser(){
        return browser;
    }

    /**
     * 国省市
     */
    @nbcp.db.Cn(value = "国省市")
    private EsColumnName city = null;
    public EsColumnName getCity(){
        return city;
    }

    /**
     * 租户
     */
    @nbcp.db.Cn(value = "租户")
    private IdNameMeta tenant = null;
    public IdNameMeta getTenant(){
        return tenant;
    }

    /**
     * 应用IdName
     */
    @nbcp.db.Cn(value = "应用IdName")
    private CodeNameMeta appInfo = null;
    public CodeNameMeta getAppInfo(){
        return appInfo;
    }

    /**
     * 角色
     */
    @nbcp.db.Cn(value = "角色")
    private IdNameMeta roles = null;
    public IdNameMeta getRoles(){
        return roles;
    }

    @Override
    public String toString() {
        return EsrUtil.esColumnJoin(this.parentPropertyName).toString();
    }
}

