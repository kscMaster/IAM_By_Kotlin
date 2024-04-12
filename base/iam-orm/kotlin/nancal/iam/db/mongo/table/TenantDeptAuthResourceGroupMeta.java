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
 * 租户部门授权资源组
 */
@nbcp.db.Cn(value = "租户部门授权资源组")
public class TenantDeptAuthResourceGroupMeta extends MongoColumnName{
    private String parentPropertyName;
    public TenantDeptAuthResourceGroupMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.id = MoerUtil.mongoColumnJoin(this.parentPropertyName, "_id");

    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");

    this.heredity = MoerUtil.mongoColumnJoin(this.parentPropertyName, "heredity");

    this.isAllow = MoerUtil.mongoColumnJoin(this.parentPropertyName, "isAllow");

    this.code = MoerUtil.mongoColumnJoin(this.parentPropertyName, "code");
    }
    
    public TenantDeptAuthResourceGroupMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName id = null;
    public MongoColumnName getId(){
        return id;
    }

    /**
     * 名称
     */
    @nbcp.db.Cn(value = "名称")
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

    /**
     * 允许/拒绝
     */
    @nbcp.db.Cn(value = "允许/拒绝")
    private MongoColumnName isAllow = null;
    public MongoColumnName getIsAllow(){
        return isAllow;
    }

    /**
     * 编码
     */
    @nbcp.db.Cn(value = "编码")
    private MongoColumnName code = null;
    public MongoColumnName getCode(){
        return code;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

