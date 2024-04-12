package nancal.iam.db.sql.table;

import nbcp.db.*;
import nbcp.db.sql.*;
import nbcp.db.sql.entity.*;
import nbcp.db.mysql.*;
import nbcp.comm.*;
import nbcp.utils.*;
import java.util.*;
import org.springframework.stereotype.*;
import nancal.iam.db.sql.*;

//generate auto @2022-08-10 18:06:50

@Component("sql.admin")
@MetaDataGroup(dbType = DatabaseEnum.Sql, value = "admin")
public class AdminGroup implements IDataGroup{
    @Override
    public Set<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(admin_login_user);
            add(admin_user);
        } };
    }

    public admin_login_user_table admin_login_user = new admin_login_user_table();
    public admin_user_table admin_user = new admin_user_table();



    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"userId"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"})})
    public class admin_login_user_table extends SqlBaseMetaTable<nancal.iam.db.sql.entity.admin_login_user> {
        private String collectionName = "";
        private String datasource = "";
        
        public admin_login_user_table(){
            this("","");
        }
        public admin_login_user_table(String collectionName, String datasource){
            super(nancal.iam.db.sql.entity.admin_login_user.class, "admin_login_user", MyHelper.AsString(collectionName,"admin_login_user"), datasource);
            this.collectionName = collectionName;
            this.datasource = datasource;
        }
        
        
        public SqlColumnName updateAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(),"updateAt");
        public SqlColumnName userId = new SqlColumnName(DbType.String, this.getAliaTableName(),"userId");
        public SqlColumnName loginName = new SqlColumnName(DbType.String, this.getAliaTableName(),"loginName");
        public SqlColumnName mobile = new SqlColumnName(DbType.String, this.getAliaTableName(),"mobile");
        public SqlColumnName email = new SqlColumnName(DbType.String, this.getAliaTableName(),"email");
        public SqlColumnName password = new SqlColumnName(DbType.String, this.getAliaTableName(),"password");
        public SqlColumnName lastLoginAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(),"lastLoginAt");
        public SqlColumnName authorizeCode = new SqlColumnName(DbType.String, this.getAliaTableName(),"authorizeCode");
        public SqlColumnName token = new SqlColumnName(DbType.String, this.getAliaTableName(),"token");
        public SqlColumnName freshToken = new SqlColumnName(DbType.String, this.getAliaTableName(),"freshToken");
        public SqlColumnName authorizeCodeCreateAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(),"authorizeCodeCreateAt");
        public SqlColumnName isLocked = new SqlColumnName(DbType.Boolean, this.getAliaTableName(),"isLocked");
        public SqlColumnName lockedRemark = new SqlColumnName(DbType.String, this.getAliaTableName(),"lockedRemark");
        public SqlColumnName errorLoginTimes = new SqlColumnName(DbType.Byte, this.getAliaTableName(),"errorLoginTimes");
        public SqlColumnName id = new SqlColumnName(DbType.String, this.getAliaTableName(),"id");
        public SqlColumnName createAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(),"createAt");

        @Override
        public String[] getSpreadColumns() {
            return new String[] { 
            };
        }

        @Override
        public String getAutoIncrementKey() { return ""; }
        @Override
        public String[][] getUks() { return new String[][]{  new String[]{ "id" } , new String[]{ "userId" } , new String[]{ "loginName" }  }; }
        @Override
        public FkDefine[] getFks() { return new FkDefine[]{  }; }


        public SqlQueryClip<admin_login_user_table, nancal.iam.db.sql.entity.admin_login_user> queryById (String id) {
            return this.query().where(it-> it.id.match( id ) );
        }

        public SqlDeleteClip<admin_login_user_table> deleteById (String id) {
            return this.delete().where(it-> it.id.match( id) );
        }

        public SqlUpdateClip<admin_login_user_table> updateById (String id) {
            return this.update().where(it-> it.id.match( id) );
        }


        public SqlQueryClip<admin_login_user_table, nancal.iam.db.sql.entity.admin_login_user> queryByUserId (String userId) {
            return this.query().where(it-> it.userId.match( userId ) );
        }

        public SqlDeleteClip<admin_login_user_table> deleteByUserId (String userId) {
            return this.delete().where(it-> it.userId.match( userId) );
        }

        public SqlUpdateClip<admin_login_user_table> updateByUserId (String userId) {
            return this.update().where(it-> it.userId.match( userId) );
        }


        public SqlQueryClip<admin_login_user_table, nancal.iam.db.sql.entity.admin_login_user> queryByLoginName (String loginName) {
            return this.query().where(it-> it.loginName.match( loginName ) );
        }

        public SqlDeleteClip<admin_login_user_table> deleteByLoginName (String loginName) {
            return this.delete().where(it-> it.loginName.match( loginName) );
        }

        public SqlUpdateClip<admin_login_user_table> updateByLoginName (String loginName) {
            return this.update().where(it-> it.loginName.match( loginName) );
        }


        public SqlQueryClip<admin_login_user_table, nancal.iam.db.sql.entity.admin_login_user> query(){
            return new SqlQueryClip(this);
        }
        
        public SqlUpdateClip<admin_login_user_table> update(){
            return new SqlUpdateClip(this);
        }
        
        public SqlDeleteClip<admin_login_user_table> delete(){
            return new SqlDeleteClip(this);
        }
    }

    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"})})
    public class admin_user_table extends SqlBaseMetaTable<nancal.iam.db.sql.entity.admin_user> {
        private String collectionName = "";
        private String datasource = "";
        
        public admin_user_table(){
            this("","");
        }
        public admin_user_table(String collectionName, String datasource){
            super(nancal.iam.db.sql.entity.admin_user.class, "admin_user", MyHelper.AsString(collectionName,"admin_user"), datasource);
            this.collectionName = collectionName;
            this.datasource = datasource;
        }
        
        
        public SqlColumnName updateAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(),"updateAt");
        public SqlColumnName name = new SqlColumnName(DbType.String, this.getAliaTableName(),"name");
        public SqlColumnName loginName = new SqlColumnName(DbType.String, this.getAliaTableName(),"loginName");
        public SqlColumnName mobile = new SqlColumnName(DbType.String, this.getAliaTableName(),"mobile");
        public SqlColumnName email = new SqlColumnName(DbType.String, this.getAliaTableName(),"email");
        public SqlColumnName remark = new SqlColumnName(DbType.String, this.getAliaTableName(),"remark");
        public SqlColumnName isAdmin = new SqlColumnName(DbType.Boolean, this.getAliaTableName(),"isAdmin");
        public SqlColumnName address = new SqlColumnName(DbType.String, this.getAliaTableName(),"address");
        public SqlColumnName id = new SqlColumnName(DbType.String, this.getAliaTableName(),"id");
        public SqlColumnName createAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(),"createAt");

        @Override
        public String[] getSpreadColumns() {
            return new String[] { 
            };
        }

        @Override
        public String getAutoIncrementKey() { return ""; }
        @Override
        public String[][] getUks() { return new String[][]{  new String[]{ "id" } , new String[]{ "loginName" }  }; }
        @Override
        public FkDefine[] getFks() { return new FkDefine[]{  }; }


        public SqlQueryClip<admin_user_table, nancal.iam.db.sql.entity.admin_user> queryById (String id) {
            return this.query().where(it-> it.id.match( id ) );
        }

        public SqlDeleteClip<admin_user_table> deleteById (String id) {
            return this.delete().where(it-> it.id.match( id) );
        }

        public SqlUpdateClip<admin_user_table> updateById (String id) {
            return this.update().where(it-> it.id.match( id) );
        }


        public SqlQueryClip<admin_user_table, nancal.iam.db.sql.entity.admin_user> queryByLoginName (String loginName) {
            return this.query().where(it-> it.loginName.match( loginName ) );
        }

        public SqlDeleteClip<admin_user_table> deleteByLoginName (String loginName) {
            return this.delete().where(it-> it.loginName.match( loginName) );
        }

        public SqlUpdateClip<admin_user_table> updateByLoginName (String loginName) {
            return this.update().where(it-> it.loginName.match( loginName) );
        }


        public SqlQueryClip<admin_user_table, nancal.iam.db.sql.entity.admin_user> query(){
            return new SqlQueryClip(this);
        }
        
        public SqlUpdateClip<admin_user_table> update(){
            return new SqlUpdateClip(this);
        }
        
        public SqlDeleteClip<admin_user_table> delete(){
            return new SqlDeleteClip(this);
        }
    }
}

