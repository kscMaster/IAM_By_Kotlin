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

public class TenantSettingMeta extends MongoColumnName{
    private String parentPropertyName;
    public TenantSettingMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.protocol = MoerUtil.mongoColumnJoin(this.parentPropertyName, "protocol");

    this.sessionTimeout = MoerUtil.mongoColumnJoin(this.parentPropertyName, "sessionTimeout");

    this.sessionUnit = MoerUtil.mongoColumnJoin(this.parentPropertyName, "sessionUnit");

    this.selfSetting = new SelfSettingMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "selfSetting"));

    this.ldapSetting = new LdapSettingMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "ldapSetting"));

    this.oauthSetting = new ObjectMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "oauthSetting"));

    this.samlSetting = new ObjectMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "samlSetting"));
    }
    
    public TenantSettingMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 认证协议
     */
    @nbcp.db.Cn(value = "认证协议")
    private MongoColumnName protocol = null;
    public MongoColumnName getProtocol(){
        return protocol;
    }

    /**
     * 会话超时策略
     */
    @nbcp.db.Cn(value = "会话超时策略")
    private MongoColumnName sessionTimeout = null;
    public MongoColumnName getSessionTimeout(){
        return sessionTimeout;
    }

    /**
     * 会话超时单位
     */
    @nbcp.db.Cn(value = "会话超时单位")
    private MongoColumnName sessionUnit = null;
    public MongoColumnName getSessionUnit(){
        return sessionUnit;
    }

    /**
     * 配置
     */
    @nbcp.db.Cn(value = "配置")
    private SelfSettingMeta selfSetting = null;
    public SelfSettingMeta getSelfSetting(){
        return selfSetting;
    }

    /**
     * LDAP配置
     */
    @nbcp.db.Cn(value = "LDAP配置")
    private LdapSettingMeta ldapSetting = null;
    public LdapSettingMeta getLdapSetting(){
        return ldapSetting;
    }

    /**
     * OAuth配置
     */
    @nbcp.db.Cn(value = "OAuth配置")
    private ObjectMeta oauthSetting = null;
    public ObjectMeta getOauthSetting(){
        return oauthSetting;
    }

    /**
     * SAML配置
     */
    @nbcp.db.Cn(value = "SAML配置")
    private ObjectMeta samlSetting = null;
    public ObjectMeta getSamlSetting(){
        return samlSetting;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

