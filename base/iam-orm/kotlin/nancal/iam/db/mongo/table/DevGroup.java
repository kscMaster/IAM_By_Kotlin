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

@Component("mongo.dev")
@MetaDataGroup(dbType = DatabaseEnum.Mongo, value = "dev")
public class DevGroup implements IDataGroup {
    @Override
    public HashSet<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(dbConnection);
        } };
    }


    /**
     * 数据连接
     */
    public DbConnectionEntity dbConnection = new DbConnectionEntity();


    /**
     * 数据连接
     */
    @nbcp.db.Cn(value = "数据连接")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "dev")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    public class DbConnectionEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.DbConnection> {
        public String collectionName;
        public String databaseId;
        public DbConnectionEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.DbConnection.class, "dbConnection", MyHelper.AsString(collectionName,"dbConnection"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public DbConnectionEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 名称
         */
        @nbcp.db.Cn(value = "名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 数据库类型
         */
        @nbcp.db.Cn(value = "数据库类型") 
        public MongoColumnName type = new MongoColumnName("type");

        /**
         * Ip
         */
        @nbcp.db.Cn(value = "Ip") 
        public MongoColumnName host = new MongoColumnName("host");

        /**
         * 端口
         */
        @nbcp.db.Cn(value = "端口") 
        public MongoColumnName port = new MongoColumnName("port");

        /**
         * 用户名
         */
        @nbcp.db.Cn(value = "用户名") 
        public MongoColumnName userName = new MongoColumnName("userName");

        /**
         * 密码
         */
        @nbcp.db.Cn(value = "密码") 
        public MongoColumnName password = new MongoColumnName("password");

        /**
         * 数据库名
         */
        @nbcp.db.Cn(value = "数据库名") 
        public MongoColumnName dbName = new MongoColumnName("dbName");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
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

        public MongoQueryClip<DbConnectionEntity, nancal.iam.db.mongo.entity.DbConnection> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<DbConnectionEntity, nancal.iam.db.mongo.entity.DbConnection> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<DbConnectionEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

}
