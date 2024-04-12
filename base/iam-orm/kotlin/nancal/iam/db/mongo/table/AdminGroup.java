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

@Component("mongo.admin")
@MetaDataGroup(dbType = DatabaseEnum.Mongo, value = "admin")
public class AdminGroup implements IDataGroup {
    @Override
    public HashSet<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(adminLoginUser);
            add(adminMenu);
            add(adminPermissionApi);
            add(adminPermissionPage);
            add(adminPermissionPageAction);
            add(adminRole);
            add(adminUser);
            add(sysMongoAdminUser);
        } };
    }



    public AdminLoginUserEntity adminLoginUser = new AdminLoginUserEntity();

    /**
     * 菜单
     */
    public AdminMenuEntity adminMenu = new AdminMenuEntity();

    /**
     * 权限接口
     */
    public AdminPermissionApiEntity adminPermissionApi = new AdminPermissionApiEntity();

    /**
     * 权限页面定义
     */
    public AdminPermissionPageEntity adminPermissionPage = new AdminPermissionPageEntity();

    /**
     * 权限页面按钮
     */
    public AdminPermissionPageActionEntity adminPermissionPageAction = new AdminPermissionPageActionEntity();

    /**
     * 后台角色
     */
    public AdminRoleEntity adminRole = new AdminRoleEntity();


    public AdminUserEntity adminUser = new AdminUserEntity();


    public SysMongoAdminUserEntity sysMongoAdminUser = new SysMongoAdminUserEntity();



    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"email"})})
    public class AdminLoginUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminLoginUser> {
        public String collectionName;
        public String databaseId;
        public AdminLoginUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminLoginUser.class, "adminLoginUser", MyHelper.AsString(collectionName,"adminLoginUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminLoginUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 登录出错次数
         */
        @nbcp.db.Cn(value = "登录出错次数") 
        public MongoColumnName errorLoginTimes = new MongoColumnName("errorLoginTimes");

        /**
         * 密码的盐
         */
        @nbcp.db.Cn(value = "密码的盐") 
        public MongoColumnName passwordSalt = new MongoColumnName("passwordSalt");

        /**
         * 用户唯一Id
         */
        @nbcp.db.Cn(value = "用户唯一Id") 
        public MongoColumnName userId = new MongoColumnName("userId");

        /**
         * 登录名
         */
        @nbcp.db.Cn(value = "登录名") 
        public MongoColumnName loginName = new MongoColumnName("loginName");

        /**
         * 登录手机
         */
        @nbcp.db.Cn(value = "登录手机") 
        public MongoColumnName mobile = new MongoColumnName("mobile");

        /**
         * 登录邮箱
         */
        @nbcp.db.Cn(value = "登录邮箱") 
        public MongoColumnName email = new MongoColumnName("email");

        /**
         * 密码
         */
        @nbcp.db.Cn(value = "密码") 
        public MongoColumnName password = new MongoColumnName("password");

        /**
         * 最后登录时间
         */
        @nbcp.db.Cn(value = "最后登录时间") 
        public MongoColumnName lastLoginAt = new MongoColumnName("lastLoginAt");

        /**
         * 是否已锁定
         */
        @nbcp.db.Cn(value = "是否已锁定") 
        public MongoColumnName isLocked = new MongoColumnName("isLocked");

        /**
         * 锁定详情
         */
        @nbcp.db.Cn(value = "锁定详情") 
        public MongoColumnName lockedRemark = new MongoColumnName("lockedRemark");

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

        public MongoQueryClip<AdminLoginUserEntity, nancal.iam.db.mongo.entity.AdminLoginUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminLoginUserEntity, nancal.iam.db.mongo.entity.AdminLoginUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminLoginUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<AdminLoginUserEntity, nancal.iam.db.mongo.entity.AdminLoginUser> queryByLoginName(String loginName) {
            return this.query().where( it-> it.loginName.match( loginName )) ;
        }

        public MongoDeleteClip<AdminLoginUserEntity> deleteByLoginName(String loginName) {
            return this.delete().where (it-> it.loginName.match( loginName ));
        }

        public MongoUpdateClip<AdminLoginUserEntity, nancal.iam.db.mongo.entity.AdminLoginUser> updateByLoginName(String loginName) {
            return this.update().where ( it-> it.loginName.match( loginName ));
        }


        public MongoQueryClip<AdminLoginUserEntity, nancal.iam.db.mongo.entity.AdminLoginUser> queryByUserId(String userId) {
            return this.query().where( it-> it.userId.match( userId )) ;
        }

        public MongoDeleteClip<AdminLoginUserEntity> deleteByUserId(String userId) {
            return this.delete().where (it-> it.userId.match( userId ));
        }

        public MongoUpdateClip<AdminLoginUserEntity, nancal.iam.db.mongo.entity.AdminLoginUser> updateByUserId(String userId) {
            return this.update().where ( it-> it.userId.match( userId ));
        }

    }

    /**
     * 菜单
     */
    @nbcp.db.Cn(value = "菜单")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"})})
    public class AdminMenuEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminMenu> {
        public String collectionName;
        public String databaseId;
        public AdminMenuEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminMenu.class, "adminMenu", MyHelper.AsString(collectionName,"adminMenu"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminMenuEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 菜单名称
         */
        @nbcp.db.Cn(value = "菜单名称") 
        public MongoColumnName name = new MongoColumnName("name");

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

        /**
         * 菜单链接
         */
        @nbcp.db.Cn(value = "菜单链接") 
        public MongoColumnName url = new MongoColumnName("url");

        /**
         * class
         */
        @nbcp.db.Cn(value = "class") 
        public MongoColumnName css = new MongoColumnName("css");

        /**
         * 资源编码
         */
        @nbcp.db.Cn(value = "资源编码") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 排序
         */
        @nbcp.db.Cn(value = "排序") 
        public MongoColumnName sort = new MongoColumnName("sort");

        public IdNameMeta parent = new IdNameMeta("parent");

        public MongoQueryClip<AdminMenuEntity, nancal.iam.db.mongo.entity.AdminMenu> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminMenuEntity, nancal.iam.db.mongo.entity.AdminMenu> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminMenuEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 权限接口
     */
    @nbcp.db.Cn(value = "权限接口")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"})})
    public class AdminPermissionApiEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminPermissionApi> {
        public String collectionName;
        public String databaseId;
        public AdminPermissionApiEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminPermissionApi.class, "adminPermissionApi", MyHelper.AsString(collectionName,"adminPermissionApi"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminPermissionApiEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 接口名称
         */
        @nbcp.db.Cn(value = "接口名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 地址,格式[方法]@/url
         */
        @nbcp.db.Cn(value = "地址,格式[方法]@/url") 
        public MongoColumnName url = new MongoColumnName("url");

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

        public MongoQueryClip<AdminPermissionApiEntity, nancal.iam.db.mongo.entity.AdminPermissionApi> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminPermissionApiEntity, nancal.iam.db.mongo.entity.AdminPermissionApi> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminPermissionApiEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 权限页面定义
     */
    @nbcp.db.Cn(value = "权限页面定义")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"path"})})
    public class AdminPermissionPageEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminPermissionPage> {
        public String collectionName;
        public String databaseId;
        public AdminPermissionPageEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminPermissionPage.class, "adminPermissionPage", MyHelper.AsString(collectionName,"adminPermissionPage"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminPermissionPageEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 页面名称
         */
        @nbcp.db.Cn(value = "页面名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * vue路由
         */
        @nbcp.db.Cn(value = "vue路由") 
        public MongoColumnName path = new MongoColumnName("path");

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

        public MongoQueryClip<AdminPermissionPageEntity, nancal.iam.db.mongo.entity.AdminPermissionPage> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminPermissionPageEntity, nancal.iam.db.mongo.entity.AdminPermissionPage> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminPermissionPageEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 权限页面按钮
     */
    @nbcp.db.Cn(value = "权限页面按钮")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"page.path"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"action"})})
    public class AdminPermissionPageActionEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminPermissionPageAction> {
        public String collectionName;
        public String databaseId;
        public AdminPermissionPageActionEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminPermissionPageAction.class, "adminPermissionPageAction", MyHelper.AsString(collectionName,"adminPermissionPageAction"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminPermissionPageActionEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 按钮名称
         */
        @nbcp.db.Cn(value = "按钮名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 动作定义
         */
        @nbcp.db.Cn(value = "动作定义") 
        public MongoColumnName action = new MongoColumnName("action");

        /**
         * 页面
         */
        @nbcp.db.Cn(value = "页面") 
        public IdNamePathMeta page = new IdNamePathMeta("page");

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

        public MongoQueryClip<AdminPermissionPageActionEntity, nancal.iam.db.mongo.entity.AdminPermissionPageAction> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminPermissionPageActionEntity, nancal.iam.db.mongo.entity.AdminPermissionPageAction> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminPermissionPageActionEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 后台角色
     */
    @nbcp.db.Cn(value = "后台角色")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"})})
    public class AdminRoleEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminRole> {
        public String collectionName;
        public String databaseId;
        public AdminRoleEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminRole.class, "adminRole", MyHelper.AsString(collectionName,"adminRole"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminRoleEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 角色名称
         */
        @nbcp.db.Cn(value = "角色名称") 
        public MongoColumnName name = new MongoColumnName("name");

        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 授权的菜单
         */
        @nbcp.db.Cn(value = "授权的菜单") 
        public MongoColumnName menus = new MongoColumnName("menus");

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

        public MongoQueryClip<AdminRoleEntity, nancal.iam.db.mongo.entity.AdminRole> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminRoleEntity, nancal.iam.db.mongo.entity.AdminRole> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminRoleEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }


    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"email"})})
    public class AdminUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.AdminUser> {
        public String collectionName;
        public String databaseId;
        public AdminUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.AdminUser.class, "adminUser", MyHelper.AsString(collectionName,"adminUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AdminUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 昵称
         */
        @nbcp.db.Cn(value = "昵称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 是否为超级管理员
         */
        @nbcp.db.Cn(value = "是否为超级管理员") 
        public MongoColumnName isAdmin = new MongoColumnName("isAdmin");

        /**
         * 地址
         */
        @nbcp.db.Cn(value = "地址") 
        public MongoColumnName address = new MongoColumnName("address");

        /**
         * 角色
         */
        @nbcp.db.Cn(value = "角色") 
        public IdNameMeta roles = new IdNameMeta("roles");

        /**
         * 发送密码方式
         */
        @nbcp.db.Cn(value = "发送密码方式") 
        public MongoColumnName sendPasswordType = new MongoColumnName("sendPasswordType");

        /**
         * 登录名
         */
        @nbcp.db.Cn(value = "登录名") 
        public MongoColumnName loginName = new MongoColumnName("loginName");

        /**
         * 手机号
         */
        @nbcp.db.Cn(value = "手机号") 
        public MongoColumnName mobile = new MongoColumnName("mobile");

        /**
         * 电子邮件
         */
        @nbcp.db.Cn(value = "电子邮件") 
        public MongoColumnName email = new MongoColumnName("email");

        /**
         * 头像
         */
        @nbcp.db.Cn(value = "头像") 
        public IdUrlMeta logo = new IdUrlMeta("logo");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 身份证
         */
        @nbcp.db.Cn(value = "身份证") 
        public IdentityCardDataMeta identityCard = new IdentityCardDataMeta("identityCard");

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

        public MongoQueryClip<AdminUserEntity, nancal.iam.db.mongo.entity.AdminUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<AdminUserEntity, nancal.iam.db.mongo.entity.AdminUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<AdminUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<AdminUserEntity, nancal.iam.db.mongo.entity.AdminUser> queryByLoginName(String loginName) {
            return this.query().where( it-> it.loginName.match( loginName )) ;
        }

        public MongoDeleteClip<AdminUserEntity> deleteByLoginName(String loginName) {
            return this.delete().where (it-> it.loginName.match( loginName ));
        }

        public MongoUpdateClip<AdminUserEntity, nancal.iam.db.mongo.entity.AdminUser> updateByLoginName(String loginName) {
            return this.update().where ( it-> it.loginName.match( loginName ));
        }

    }


    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "admin")
    @nbcp.db.DbName(value = "system.users")
    public class SysMongoAdminUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.SysMongoAdminUser> {
        public String collectionName;
        public String databaseId;
        public SysMongoAdminUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.SysMongoAdminUser.class, "system.users", MyHelper.AsString(collectionName,"system.users"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SysMongoAdminUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName userId = new MongoColumnName("userId");

        public MongoColumnName user = new MongoColumnName("user");

        public MongoColumnName db = new MongoColumnName("db");

        public MongoCredentialsDataMeta credentials = new MongoCredentialsDataMeta("credentials");

        public MongoRoleDataMeta roles = new MongoRoleDataMeta("roles");

        public MongoQueryClip<SysMongoAdminUserEntity, nancal.iam.db.mongo.entity.SysMongoAdminUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SysMongoAdminUserEntity, nancal.iam.db.mongo.entity.SysMongoAdminUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SysMongoAdminUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

}
