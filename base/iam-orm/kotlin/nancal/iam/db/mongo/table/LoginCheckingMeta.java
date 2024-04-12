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

public class LoginCheckingMeta extends MongoColumnName{
    private String parentPropertyName;
    public LoginCheckingMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.checkingPeriod = MoerUtil.mongoColumnJoin(this.parentPropertyName, "checkingPeriod");

    this.retryTime = MoerUtil.mongoColumnJoin(this.parentPropertyName, "retryTime");

    this.lockDuration = MoerUtil.mongoColumnJoin(this.parentPropertyName, "lockDuration");

    this.manual = MoerUtil.mongoColumnJoin(this.parentPropertyName, "manual");

    this.accountPolicyDescription = MoerUtil.mongoColumnJoin(this.parentPropertyName, "accountPolicyDescription");
    }
    
    public LoginCheckingMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 登录验证统计周期
     */
    @nbcp.db.Cn(value = "登录验证统计周期")
    private MongoColumnName checkingPeriod = null;
    public MongoColumnName getCheckingPeriod(){
        return checkingPeriod;
    }

    /**
     * 允许登录失败尝试次数
     */
    @nbcp.db.Cn(value = "允许登录失败尝试次数")
    private MongoColumnName retryTime = null;
    public MongoColumnName getRetryTime(){
        return retryTime;
    }

    /**
     * 账户被锁定时持续时间
     */
    @nbcp.db.Cn(value = "账户被锁定时持续时间")
    private MongoColumnName lockDuration = null;
    public MongoColumnName getLockDuration(){
        return lockDuration;
    }

    /**
     * 手动解锁账户
     */
    @nbcp.db.Cn(value = "手动解锁账户")
    private MongoColumnName manual = null;
    public MongoColumnName getManual(){
        return manual;
    }

    /**
     * 账户锁定策略说明
     */
    @nbcp.db.Cn(value = "账户锁定策略说明")
    private MongoColumnName accountPolicyDescription = null;
    public MongoColumnName getAccountPolicyDescription(){
        return accountPolicyDescription;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

