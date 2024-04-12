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

public class SelfSettingMeta extends MongoColumnName{
    private String parentPropertyName;
    public SelfSettingMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.loginChecking = new LoginCheckingMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "loginChecking"));

    this.securityPolicy = new SecurityPolicyMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "securityPolicy"));
    }
    
    public SelfSettingMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 登录认证
     */
    @nbcp.db.Cn(value = "登录认证")
    private LoginCheckingMeta loginChecking = null;
    public LoginCheckingMeta getLoginChecking(){
        return loginChecking;
    }

    /**
     * 密码策略
     */
    @nbcp.db.Cn(value = "密码策略")
    private SecurityPolicyMeta securityPolicy = null;
    public SecurityPolicyMeta getSecurityPolicy(){
        return securityPolicy;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

