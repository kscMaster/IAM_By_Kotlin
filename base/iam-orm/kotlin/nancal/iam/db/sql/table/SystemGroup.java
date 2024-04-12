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

@Component("sql.system")
@MetaDataGroup(dbType = DatabaseEnum.Sql, value = "system")
public class SystemGroup implements IDataGroup {
    @Override
    public Set<BaseMetaData> getEntities() {
        return new HashSet() {
            {
                add(s_login_user);
                add(s_user);
            }
        };
    }

    public s_login_user_table s_login_user = new s_login_user_table();
    public s_user_table s_user = new s_user_table();


    @nbcp.db.DbEntityGroup(value = "system")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"})})
    public class s_login_user_table extends SqlBaseMetaTable<nancal.iam.db.sql.entity.s_login_user> {
        private String collectionName = "";
        private String datasource = "";

        public s_login_user_table() {
            this("", "");
        }

        public s_login_user_table(String collectionName, String datasource) {
            super(nancal.iam.db.sql.entity.s_login_user.class, "s_login_user", MyHelper.AsString(collectionName, "s_login_user"), datasource);
            this.collectionName = collectionName;
            this.datasource = datasource;
        }


        public SqlColumnName id = new SqlColumnName(DbType.Int, this.getAliaTableName(), "id");
        public SqlColumnName loginName = new SqlColumnName(DbType.String, this.getAliaTableName(), "loginName");
        public SqlColumnName password = new SqlColumnName(DbType.String, this.getAliaTableName(), "password");
        public SqlColumnName lastLoginAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(), "lastLoginAt");
        public SqlColumnName errorCount = new SqlColumnName(DbType.Int, this.getAliaTableName(), "errorCount");
        public SqlColumnName forget_password = new SqlColumnName(DbType.Boolean, this.getAliaTableName(), "forget_password");
        public SqlColumnName updateAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(), "updateAt");
        public SqlColumnName createAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(), "createAt");

        @Override
        public String[] getSpreadColumns() {
            return new String[]{
            };
        }

        @Override
        public String getAutoIncrementKey() {
            return "";
        }

        @Override
        public String[][] getUks() {
            return new String[][]{new String[]{"id"}, new String[]{"loginName"}};
        }

        @Override
        public FkDefine[] getFks() {
            return new FkDefine[]{};
        }


        public SqlQueryClip<s_login_user_table, nancal.iam.db.sql.entity.s_login_user> queryById(int id) {
            return this.query().where(it -> it.id.match(id));
        }

        public SqlDeleteClip<s_login_user_table> deleteById(int id) {
            return this.delete().where(it -> it.id.match(id));
        }

        public SqlUpdateClip<s_login_user_table> updateById(int id) {
            return this.update().where(it -> it.id.match(id));
        }


        public SqlQueryClip<s_login_user_table, nancal.iam.db.sql.entity.s_login_user> queryByLoginName(String loginName) {
            return this.query().where(it -> it.loginName.match(loginName));
        }

        public SqlDeleteClip<s_login_user_table> deleteByLoginName(String loginName) {
            return this.delete().where(it -> it.loginName.match(loginName));
        }

        public SqlUpdateClip<s_login_user_table> updateByLoginName(String loginName) {
            return this.update().where(it -> it.loginName.match(loginName));
        }


        public SqlQueryClip<s_login_user_table, nancal.iam.db.sql.entity.s_login_user> query() {
            return new SqlQueryClip(this);
        }

        public SqlUpdateClip<s_login_user_table> update() {
            return new SqlUpdateClip(this);
        }

        public SqlDeleteClip<s_login_user_table> delete() {
            return new SqlDeleteClip(this);
        }
    }

    @nbcp.db.DbEntityGroup(value = "system")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"loginName"})})
    public class s_user_table extends SqlBaseMetaTable<nancal.iam.db.sql.entity.s_user> {
        private String collectionName = "";
        private String datasource = "";

        public s_user_table() {
            this("", "");
        }

        public s_user_table(String collectionName, String datasource) {
            super(nancal.iam.db.sql.entity.s_user.class, "s_user", MyHelper.AsString(collectionName, "s_user"), datasource);
            this.collectionName = collectionName;
            this.datasource = datasource;
        }


        public SqlColumnName id = new SqlColumnName(DbType.Int, this.getAliaTableName(), "id");
        public SqlColumnName name = new SqlColumnName(DbType.String, this.getAliaTableName(), "name");
        public SqlColumnName loginName = new SqlColumnName(DbType.String, this.getAliaTableName(), "loginName");
        public SqlColumnName code = new SqlColumnName(DbType.String, this.getAliaTableName(), "code");
        public SqlColumnName mobile = new SqlColumnName(DbType.String, this.getAliaTableName(), "mobile");
        public SqlColumnName isChecker = new SqlColumnName(DbType.Boolean, this.getAliaTableName(), "isChecker");
        public SqlColumnName isAdmin = new SqlColumnName(DbType.Boolean, this.getAliaTableName(), "isAdmin");
        public SqlColumnName isDisabled = new SqlColumnName(DbType.Boolean, this.getAliaTableName(), "isDisabled");
        public SqlColumnName token = new SqlColumnName(DbType.String, this.getAliaTableName(), "token");
        public SqlColumnName updateAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(), "updateAt");
        public SqlColumnName createAt = new SqlColumnName(DbType.DateTime, this.getAliaTableName(), "createAt");

        @Override
        public String[] getSpreadColumns() {
            return new String[]{
            };
        }

        @Override
        public String getAutoIncrementKey() {
            return "id";
        }

        @Override
        public String[][] getUks() {
            return new String[][]{new String[]{"id"}};
        }

        @Override
        public FkDefine[] getFks() {
            return new FkDefine[]{};
        }


        public SqlQueryClip<s_user_table, nancal.iam.db.sql.entity.s_user> queryById(int id) {
            return this.query().where(it -> it.id.match(id));
        }

        public SqlDeleteClip<s_user_table> deleteById(int id) {
            return this.delete().where(it -> it.id.match(id));
        }

        public SqlUpdateClip<s_user_table> updateById(int id) {
            return this.update().where(it -> it.id.match(id));
        }


        public SqlQueryClip<s_user_table, nancal.iam.db.sql.entity.s_user> query() {
            return new SqlQueryClip(this);
        }

        public SqlUpdateClip<s_user_table> update() {
            return new SqlUpdateClip(this);
        }

        public SqlDeleteClip<s_user_table> delete() {
            return new SqlDeleteClip(this);
        }
    }
}

