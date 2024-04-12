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

public class TenantIdentitySourceAppMeta extends MongoColumnName{
    private String parentPropertyName;
    public TenantIdentitySourceAppMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.id = MoerUtil.mongoColumnJoin(this.parentPropertyName, "_id");

    this.sysAppId = MoerUtil.mongoColumnJoin(this.parentPropertyName, "sysAppId");

    this.codeName = new CodeNameMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "codeName"));

    this.logo = new IdUrlMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "logo"));

    this.status = MoerUtil.mongoColumnJoin(this.parentPropertyName, "status");

    this.sysAppStatus = MoerUtil.mongoColumnJoin(this.parentPropertyName, "sysAppStatus");

    this.tenantAppStatus = MoerUtil.mongoColumnJoin(this.parentPropertyName, "tenantAppStatus");

    this.isSysDefine = MoerUtil.mongoColumnJoin(this.parentPropertyName, "isSysDefine");
    }
    
    public TenantIdentitySourceAppMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName id = null;
    public MongoColumnName getId(){
        return id;
    }

    private MongoColumnName sysAppId = null;
    public MongoColumnName getSysAppId(){
        return sysAppId;
    }

    private CodeNameMeta codeName = null;
    public CodeNameMeta getCodeName(){
        return codeName;
    }

    private IdUrlMeta logo = null;
    public IdUrlMeta getLogo(){
        return logo;
    }

    private MongoColumnName status = null;
    public MongoColumnName getStatus(){
        return status;
    }

    private MongoColumnName sysAppStatus = null;
    public MongoColumnName getSysAppStatus(){
        return sysAppStatus;
    }

    private MongoColumnName tenantAppStatus = null;
    public MongoColumnName getTenantAppStatus(){
        return tenantAppStatus;
    }

    private MongoColumnName isSysDefine = null;
    public MongoColumnName getIsSysDefine(){
        return isSysDefine;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

