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
 * 租户部门授权资源
 */
@nbcp.db.Cn(value = "租户部门授权资源")
public class TenantDeptAuthResourceInfoMeta extends MongoColumnName{
    private String parentPropertyName;
    public TenantDeptAuthResourceInfoMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.id = MoerUtil.mongoColumnJoin(this.parentPropertyName, "_id");

    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");

    this.heredity = MoerUtil.mongoColumnJoin(this.parentPropertyName, "heredity");

    this.resourceId = MoerUtil.mongoColumnJoin(this.parentPropertyName, "resourceId");

    this.actionIsAll = MoerUtil.mongoColumnJoin(this.parentPropertyName, "actionIsAll");

    this.resourceIsAll = MoerUtil.mongoColumnJoin(this.parentPropertyName, "resourceIsAll");

    this.isAllow = MoerUtil.mongoColumnJoin(this.parentPropertyName, "isAllow");

    this.rules = new TenantAuthRuleMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "rules"));

    this.type = MoerUtil.mongoColumnJoin(this.parentPropertyName, "type");

    this.code = MoerUtil.mongoColumnJoin(this.parentPropertyName, "code");

    this.resource = MoerUtil.mongoColumnJoin(this.parentPropertyName, "resource");

    this.action = MoerUtil.mongoColumnJoin(this.parentPropertyName, "action");

    this.dataAccessLevel = MoerUtil.mongoColumnJoin(this.parentPropertyName, "dataAccessLevel");
    }
    
    public TenantDeptAuthResourceInfoMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName id = null;
    public MongoColumnName getId(){
        return id;
    }

    private MongoColumnName name = null;
    public MongoColumnName getName(){
        return name;
    }

    /**
     * 子部门继承
     */
    @nbcp.db.Cn(value = "子部门继承")
    private MongoColumnName heredity = null;
    public MongoColumnName getHeredity(){
        return heredity;
    }

    private MongoColumnName resourceId = null;
    public MongoColumnName getResourceId(){
        return resourceId;
    }

    private MongoColumnName actionIsAll = null;
    public MongoColumnName getActionIsAll(){
        return actionIsAll;
    }

    private MongoColumnName resourceIsAll = null;
    public MongoColumnName getResourceIsAll(){
        return resourceIsAll;
    }

    private MongoColumnName isAllow = null;
    public MongoColumnName getIsAllow(){
        return isAllow;
    }

    /**
     * 授权规则
     */
    @nbcp.db.Cn(value = "授权规则")
    private TenantAuthRuleMeta rules = null;
    public TenantAuthRuleMeta getRules(){
        return rules;
    }

    private MongoColumnName type = null;
    public MongoColumnName getType(){
        return type;
    }

    private MongoColumnName code = null;
    public MongoColumnName getCode(){
        return code;
    }

    private MongoColumnName resource = null;
    public MongoColumnName getResource(){
        return resource;
    }

    private MongoColumnName action = null;
    public MongoColumnName getAction(){
        return action;
    }

    private MongoColumnName dataAccessLevel = null;
    public MongoColumnName getDataAccessLevel(){
        return dataAccessLevel;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

