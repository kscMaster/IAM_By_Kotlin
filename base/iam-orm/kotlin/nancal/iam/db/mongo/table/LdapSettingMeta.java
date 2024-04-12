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

public class LdapSettingMeta extends MongoColumnName{
    private String parentPropertyName;
    public LdapSettingMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.utls = MoerUtil.mongoColumnJoin(this.parentPropertyName, "utls");

    this.base = MoerUtil.mongoColumnJoin(this.parentPropertyName, "base");

    this.username = MoerUtil.mongoColumnJoin(this.parentPropertyName, "username");

    this.password = MoerUtil.mongoColumnJoin(this.parentPropertyName, "password");

    this.mailSuffix = MoerUtil.mongoColumnJoin(this.parentPropertyName, "mailSuffix");
    }
    
    public LdapSettingMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 地址
     */
    @nbcp.db.Cn(value = "地址")
    private MongoColumnName utls = null;
    public MongoColumnName getUtls(){
        return utls;
    }

    /**
     * base dn
     */
    @nbcp.db.Cn(value = "base dn")
    private MongoColumnName base = null;
    public MongoColumnName getBase(){
        return base;
    }

    /**
     * 用户名
     */
    @nbcp.db.Cn(value = "用户名")
    private MongoColumnName username = null;
    public MongoColumnName getUsername(){
        return username;
    }

    /**
     * 密码
     */
    @nbcp.db.Cn(value = "密码")
    private MongoColumnName password = null;
    public MongoColumnName getPassword(){
        return password;
    }

    /**
     * 邮箱后缀
     */
    @nbcp.db.Cn(value = "邮箱后缀")
    private MongoColumnName mailSuffix = null;
    public MongoColumnName getMailSuffix(){
        return mailSuffix;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

