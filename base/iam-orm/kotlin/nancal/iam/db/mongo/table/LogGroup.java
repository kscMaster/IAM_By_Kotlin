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

@Component("mongo.log")
@MetaDataGroup(dbType = DatabaseEnum.Mongo, value = "log")
public class LogGroup implements IDataGroup {
    @Override
    public HashSet<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(corpLoginLog);
            add(mobileCodeLog);
            add(mqLog);
            add(smsLog);
            add(traceLog);
        } };
    }



    public CorpLoginLogEntity corpLoginLog = new CorpLoginLogEntity();


    public MobileCodeLogEntity mobileCodeLog = new MobileCodeLogEntity();


    public MqLogEntity mqLog = new MqLogEntity();


    public SmsLogEntity smsLog = new SmsLogEntity();


    public TraceLogEntity traceLog = new TraceLogEntity();



    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "log")
    public class CorpLoginLogEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.CorpLoginLog> {
        public String collectionName;
        public String databaseId;
        public CorpLoginLogEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.CorpLoginLog.class, "corpLoginLog", MyHelper.AsString(collectionName,"corpLoginLog"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public CorpLoginLogEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName loginName = new MongoColumnName("loginName");

        public MongoColumnName password = new MongoColumnName("password");

        public MongoColumnName app = new MongoColumnName("app");

        public MongoColumnName clientIp = new MongoColumnName("clientIp");

        public MongoColumnName client = new MongoColumnName("client");

        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 创建时间
         */
        @nbcp.db.Cn(value = "创建时间") 
        public MongoColumnName createAt = new MongoColumnName("createAt");

        /**
         * 更新时间
         */
        @nbcp.db.Cn(value = "更新时间") 
        public MongoColumnName updateAt = new MongoColumnName("updateAt");

        public MongoQueryClip<CorpLoginLogEntity, nancal.iam.db.mongo.entity.CorpLoginLog> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<CorpLoginLogEntity, nancal.iam.db.mongo.entity.CorpLoginLog> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<CorpLoginLogEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }


    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "log")
    public class MobileCodeLogEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.MobileCodeLog> {
        public String collectionName;
        public String databaseId;
        public MobileCodeLogEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.MobileCodeLog.class, "mobileCodeLog", MyHelper.AsString(collectionName,"mobileCodeLog"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public MobileCodeLogEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName module = new MongoColumnName("module");

        public MongoColumnName mobile = new MongoColumnName("mobile");

        public MongoColumnName templateCode = new MongoColumnName("templateCode");

        public MongoColumnName param = new MongoColumnName("param");

        public MongoColumnName bizId = new MongoColumnName("bizId");

        public MongoColumnName errorMessage = new MongoColumnName("errorMessage");

        public MongoColumnName sentAt = new MongoColumnName("sentAt");

        public MongoColumnName arrivedAt = new MongoColumnName("arrivedAt");

        /**
         * 创建时间
         */
        @nbcp.db.Cn(value = "创建时间") 
        public MongoColumnName createAt = new MongoColumnName("createAt");

        /**
         * 更新时间
         */
        @nbcp.db.Cn(value = "更新时间") 
        public MongoColumnName updateAt = new MongoColumnName("updateAt");

        public MongoQueryClip<MobileCodeLogEntity, nancal.iam.db.mongo.entity.MobileCodeLog> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<MobileCodeLogEntity, nancal.iam.db.mongo.entity.MobileCodeLog> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<MobileCodeLogEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }


    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "log")
    public class MqLogEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.MqLog> {
        public String collectionName;
        public String databaseId;
        public MqLogEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.MqLog.class, "mqLog", MyHelper.AsString(collectionName,"mqLog"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public MqLogEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName name = new MongoColumnName("name");

        public MongoColumnName arrivedAt = new MongoColumnName("arrivedAt");

        public MongoColumnName body = new MongoColumnName("body");

        public MongoColumnName sendErrorMessage = new MongoColumnName("sendErrorMessage");

        public MongoColumnName isDone = new MongoColumnName("isDone");

        public MongoColumnName consumeAt = new MongoColumnName("consumeAt");

        public MongoColumnName result = new MongoColumnName("result");

        /**
         * 创建时间
         */
        @nbcp.db.Cn(value = "创建时间") 
        public MongoColumnName createAt = new MongoColumnName("createAt");

        /**
         * 更新时间
         */
        @nbcp.db.Cn(value = "更新时间") 
        public MongoColumnName updateAt = new MongoColumnName("updateAt");

        public MongoQueryClip<MqLogEntity, nancal.iam.db.mongo.entity.MqLog> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<MqLogEntity, nancal.iam.db.mongo.entity.MqLog> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<MqLogEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }


    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "log")
    public class SmsLogEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.SmsLog> {
        public String collectionName;
        public String databaseId;
        public SmsLogEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.SmsLog.class, "smsLog", MyHelper.AsString(collectionName,"smsLog"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SmsLogEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName mobile = new MongoColumnName("mobile");

        public MongoColumnName validateCode = new MongoColumnName("validateCode");

        public MongoColumnName clientIp = new MongoColumnName("clientIp");

        public MongoColumnName used = new MongoColumnName("used");

        public MongoColumnName usedAt = new MongoColumnName("usedAt");

        /**
         * 创建时间
         */
        @nbcp.db.Cn(value = "创建时间") 
        public MongoColumnName createAt = new MongoColumnName("createAt");

        /**
         * 更新时间
         */
        @nbcp.db.Cn(value = "更新时间") 
        public MongoColumnName updateAt = new MongoColumnName("updateAt");

        public MongoQueryClip<SmsLogEntity, nancal.iam.db.mongo.entity.SmsLog> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SmsLogEntity, nancal.iam.db.mongo.entity.SmsLog> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SmsLogEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }


    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "log")
    public class TraceLogEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TraceLog> {
        public String collectionName;
        public String databaseId;
        public TraceLogEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TraceLog.class, "traceLog", MyHelper.AsString(collectionName,"traceLog"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TraceLogEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName method = new MongoColumnName("method");

        public MongoColumnName url = new MongoColumnName("url");

        public MongoColumnName header = new MongoColumnName("header");

        public MongoColumnName body = new MongoColumnName("body");

        public MongoColumnName remark = new MongoColumnName("remark");

        public IdNameMeta user = new IdNameMeta("user");

        /**
         * 创建时间
         */
        @nbcp.db.Cn(value = "创建时间") 
        public MongoColumnName createAt = new MongoColumnName("createAt");

        /**
         * 更新时间
         */
        @nbcp.db.Cn(value = "更新时间") 
        public MongoColumnName updateAt = new MongoColumnName("updateAt");

        public MongoQueryClip<TraceLogEntity, nancal.iam.db.mongo.entity.TraceLog> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TraceLogEntity, nancal.iam.db.mongo.entity.TraceLog> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TraceLogEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

}
