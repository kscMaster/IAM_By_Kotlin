package nancal.iam.db.mongo.table;

import nbcp.db.*;
import nbcp.db.mongo.*;
import nbcp.utils.*;
import nbcp.comm.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import nancal.iam.db.mongo.*;

//generate auto @2022-08-11 11:01:01
/**
 * 营业执照信息
 */
@nbcp.db.Cn(value = "营业执照信息")
public class BusinessLicenseDataMeta extends MongoColumnName{
    private String parentPropertyName;
    public BusinessLicenseDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");

    this.code = MoerUtil.mongoColumnJoin(this.parentPropertyName, "code");

    this.legalPerson = MoerUtil.mongoColumnJoin(this.parentPropertyName, "legalPerson");

    this.type = MoerUtil.mongoColumnJoin(this.parentPropertyName, "type");

    this.businessScope = MoerUtil.mongoColumnJoin(this.parentPropertyName, "businessScope");

    this.registeredCapital = MoerUtil.mongoColumnJoin(this.parentPropertyName, "registeredCapital");

    this.buildAt = MoerUtil.mongoColumnJoin(this.parentPropertyName, "buildAt");

    this.businessTerm = MoerUtil.mongoColumnJoin(this.parentPropertyName, "businessTerm");

    this.location = MoerUtil.mongoColumnJoin(this.parentPropertyName, "location");

    this.registeOrganization = MoerUtil.mongoColumnJoin(this.parentPropertyName, "registeOrganization");

    this.registeAt = MoerUtil.mongoColumnJoin(this.parentPropertyName, "registeAt");
    }
    
    public BusinessLicenseDataMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 企业名称
     */
    @nbcp.db.Cn(value = "企业名称")
    private MongoColumnName name = null;
    public MongoColumnName getName(){
        return name;
    }

    /**
     * 统一社会信用代码
     */
    @nbcp.db.Cn(value = "统一社会信用代码")
    private MongoColumnName code = null;
    public MongoColumnName getCode(){
        return code;
    }

    /**
     * 法人
     */
    @nbcp.db.Cn(value = "法人")
    private MongoColumnName legalPerson = null;
    public MongoColumnName getLegalPerson(){
        return legalPerson;
    }

    /**
     * 类型
     */
    @nbcp.db.Cn(value = "类型")
    private MongoColumnName type = null;
    public MongoColumnName getType(){
        return type;
    }

    /**
     * 经营范围
     */
    @nbcp.db.Cn(value = "经营范围")
    private MongoColumnName businessScope = null;
    public MongoColumnName getBusinessScope(){
        return businessScope;
    }

    /**
     * 注册资本
     */
    @nbcp.db.Cn(value = "注册资本")
    private MongoColumnName registeredCapital = null;
    public MongoColumnName getRegisteredCapital(){
        return registeredCapital;
    }

    /**
     * 成立日期
     */
    @nbcp.db.Cn(value = "成立日期")
    private MongoColumnName buildAt = null;
    public MongoColumnName getBuildAt(){
        return buildAt;
    }

    /**
     * 营业期限
     */
    @nbcp.db.Cn(value = "营业期限")
    private MongoColumnName businessTerm = null;
    public MongoColumnName getBusinessTerm(){
        return businessTerm;
    }

    /**
     * 住所
     */
    @nbcp.db.Cn(value = "住所")
    private MongoColumnName location = null;
    public MongoColumnName getLocation(){
        return location;
    }

    /**
     * 登记机关
     */
    @nbcp.db.Cn(value = "登记机关")
    private MongoColumnName registeOrganization = null;
    public MongoColumnName getRegisteOrganization(){
        return registeOrganization;
    }

    /**
     * 注册时间
     */
    @nbcp.db.Cn(value = "注册时间")
    private MongoColumnName registeAt = null;
    public MongoColumnName getRegisteAt(){
        return registeAt;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

