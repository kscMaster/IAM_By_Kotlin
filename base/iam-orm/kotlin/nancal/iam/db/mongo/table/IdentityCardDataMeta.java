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
 * 身份证信息
 */
@nbcp.db.Cn(value = "身份证信息")
public class IdentityCardDataMeta extends MongoColumnName{
    private String parentPropertyName;
    public IdentityCardDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.name = MoerUtil.mongoColumnJoin(this.parentPropertyName, "name");

    this.photo = new IdUrlMeta(MoerUtil.mongoColumnJoin(this.parentPropertyName, "photo"));

    this.number = MoerUtil.mongoColumnJoin(this.parentPropertyName, "number");

    this.sex = MoerUtil.mongoColumnJoin(this.parentPropertyName, "sex");

    this.birthday = MoerUtil.mongoColumnJoin(this.parentPropertyName, "birthday");

    this.location = MoerUtil.mongoColumnJoin(this.parentPropertyName, "location");
    }
    
    public IdentityCardDataMeta(MongoColumnName value) {
        this(value.toString());
    }

    /**
     * 姓名
     */
    @nbcp.db.Cn(value = "姓名")
    private MongoColumnName name = null;
    public MongoColumnName getName(){
        return name;
    }

    /**
     * 头像
     */
    @nbcp.db.Cn(value = "头像")
    private IdUrlMeta photo = null;
    public IdUrlMeta getPhoto(){
        return photo;
    }

    /**
     * 身份证号
     */
    @nbcp.db.Cn(value = "身份证号")
    private MongoColumnName number = null;
    public MongoColumnName getNumber(){
        return number;
    }

    /**
     * 性别
     */
    @nbcp.db.Cn(value = "性别")
    private MongoColumnName sex = null;
    public MongoColumnName getSex(){
        return sex;
    }

    /**
     * 生日
     */
    @nbcp.db.Cn(value = "生日")
    private MongoColumnName birthday = null;
    public MongoColumnName getBirthday(){
        return birthday;
    }

    /**
     * 身份证地址
     */
    @nbcp.db.Cn(value = "身份证地址")
    private MongoColumnName location = null;
    public MongoColumnName getLocation(){
        return location;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

