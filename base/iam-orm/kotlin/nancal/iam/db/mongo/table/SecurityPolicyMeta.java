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

public class SecurityPolicyMeta extends MongoColumnName{
    private String parentPropertyName;
    public SecurityPolicyMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.leastCharacters = MoerUtil.mongoColumnJoin(this.parentPropertyName, "leastCharacters");

    this.lowInput = MoerUtil.mongoColumnJoin(this.parentPropertyName, "lowInput");

    this.upInput = MoerUtil.mongoColumnJoin(this.parentPropertyName, "upInput");

    this.specialInput = MoerUtil.mongoColumnJoin(this.parentPropertyName, "specialInput");

    this.numberInput = MoerUtil.mongoColumnJoin(this.parentPropertyName, "numberInput");

    this.leastLenght = MoerUtil.mongoColumnJoin(this.parentPropertyName, "leastLenght");

    this.expires = MoerUtil.mongoColumnJoin(this.parentPropertyName, "expires");

    this.firstLoginUpdatePassword = MoerUtil.mongoColumnJoin(this.parentPropertyName, "firstLoginUpdatePassword");

    this.expiresDays = MoerUtil.mongoColumnJoin(this.parentPropertyName, "expiresDays");

    this.expiresNotice = MoerUtil.mongoColumnJoin(this.parentPropertyName, "expiresNotice");

    this.secretPolicyDescription = MoerUtil.mongoColumnJoin(this.parentPropertyName, "secretPolicyDescription");
    }
    
    public SecurityPolicyMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 密码最少字符
     */
    @nbcp.db.Cn(value = "密码最少字符")
    private MongoColumnName leastCharacters = null;
    public MongoColumnName getLeastCharacters(){
        return leastCharacters;
    }

    /**
     * 小写字符
     */
    @nbcp.db.Cn(value = "小写字符")
    private MongoColumnName lowInput = null;
    public MongoColumnName getLowInput(){
        return lowInput;
    }

    /**
     * 大写字符
     */
    @nbcp.db.Cn(value = "大写字符")
    private MongoColumnName upInput = null;
    public MongoColumnName getUpInput(){
        return upInput;
    }

    /**
     * 特殊字符
     */
    @nbcp.db.Cn(value = "特殊字符")
    private MongoColumnName specialInput = null;
    public MongoColumnName getSpecialInput(){
        return specialInput;
    }

    /**
     * 数字字符
     */
    @nbcp.db.Cn(value = "数字字符")
    private MongoColumnName numberInput = null;
    public MongoColumnName getNumberInput(){
        return numberInput;
    }

    /**
     * 最短长度
     */
    @nbcp.db.Cn(value = "最短长度")
    private MongoColumnName leastLenght = null;
    public MongoColumnName getLeastLenght(){
        return leastLenght;
    }

    /**
     * 强制密码过期
     */
    @nbcp.db.Cn(value = "强制密码过期")
    private MongoColumnName expires = null;
    public MongoColumnName getExpires(){
        return expires;
    }

    /**
     * 首次登录是否修改密码
     */
    @nbcp.db.Cn(value = "首次登录是否修改密码")
    private MongoColumnName firstLoginUpdatePassword = null;
    public MongoColumnName getFirstLoginUpdatePassword(){
        return firstLoginUpdatePassword;
    }

    /**
     * 过期天数
     */
    @nbcp.db.Cn(value = "过期天数")
    private MongoColumnName expiresDays = null;
    public MongoColumnName getExpiresDays(){
        return expiresDays;
    }

    /**
     * 到期通知
     */
    @nbcp.db.Cn(value = "到期通知")
    private MongoColumnName expiresNotice = null;
    public MongoColumnName getExpiresNotice(){
        return expiresNotice;
    }

    /**
     * 密码策略说明
     */
    @nbcp.db.Cn(value = "密码策略说明")
    private MongoColumnName secretPolicyDescription = null;
    public MongoColumnName getSecretPolicyDescription(){
        return secretPolicyDescription;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

