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

@Component("mongo.iam")
@MetaDataGroup(dbType = DatabaseEnum.Mongo, value = "iam")
public class IamGroup implements IDataGroup {
    @Override
    public HashSet<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(sysAppAuthResource);
            add(tenantMenu);
            add(identitySource);
            add(identityTypeList);
            add(industryDict);
            add(identityOauthSource);
            add(sysApplication);
            add(sysAppRole);
            add(sysAuthRule);
            add(sysResourceInfo);
        } };
    }


    /**
     * 应用授权
     */
    public SysAppAuthResourceEntity sysAppAuthResource = new SysAppAuthResourceEntity();

    /**
     * 租户侧菜单
     */
    public TenantMenuEntity tenantMenu = new TenantMenuEntity();

    /**
     * 企业身份源
     */
    public IdentitySourceEntity identitySource = new IdentitySourceEntity();

    /**
     * 企业身份源类型
     */
    public IdentityTypeListEntity identityTypeList = new IdentityTypeListEntity();

    /**
     * 行业字典
     */
    public IndustryDictEntity industryDict = new IndustryDictEntity();

    /**
     * Oauth2.0企业身份源
     */
    public IdentityOauthSourceEntity identityOauthSource = new IdentityOauthSourceEntity();

    /**
     * 应用
     */
    public SysApplicationEntity sysApplication = new SysApplicationEntity();

    /**
     * 应用角色
     */
    public SysAppRoleEntity sysAppRole = new SysAppRoleEntity();

    /**
     * 应用授权规则
     */
    public SysAuthRuleEntity sysAuthRule = new SysAuthRuleEntity();

    /**
     * 应用资源
     */
    public SysResourceInfoEntity sysResourceInfo = new SysResourceInfoEntity();


    /**
     * 应用授权
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "应用授权")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.name", refEntityClass = nancal.iam.db.mongo.entity.SysAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.code", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.name", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.type", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.resource", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.remark", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    public class SysAppAuthResourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.iam.SysAppAuthResource> {
        public String collectionName;
        public String databaseId;
        public SysAppAuthResourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.iam.SysAppAuthResource.class, "sysAppAuthResource", MyHelper.AsString(collectionName,"sysAppAuthResource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SysAppAuthResourceEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 授权主体类型
         */
        @nbcp.db.Cn(value = "授权主体类型") 
        public MongoColumnName type = new MongoColumnName("type");

        /**
         * 授权主体
         */
        @nbcp.db.Cn(value = "授权主体") 
        public IdNameMeta target = new IdNameMeta("target");

        /**
         * 授权
         */
        @nbcp.db.Cn(value = "授权") 
        public AuthResourceInfoMeta auths = new AuthResourceInfoMeta("auths");

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

        public MongoQueryClip<SysAppAuthResourceEntity, nancal.iam.db.mongo.entity.iam.SysAppAuthResource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SysAppAuthResourceEntity, nancal.iam.db.mongo.entity.iam.SysAppAuthResource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SysAppAuthResourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户侧菜单
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "租户侧菜单")
    public class TenantMenuEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.iam.TenantMenu> {
        public String collectionName;
        public String databaseId;
        public TenantMenuEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.iam.TenantMenu.class, "tenantMenu", MyHelper.AsString(collectionName,"tenantMenu"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantMenuEntity(){
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

        public MongoQueryClip<TenantMenuEntity, nancal.iam.db.mongo.entity.iam.TenantMenu> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantMenuEntity, nancal.iam.db.mongo.entity.iam.TenantMenu> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantMenuEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 企业身份源
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.codeName.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "appCode", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.codeName.code", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "enabled", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.sysAppStatus", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.logo.id", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.logo.url", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "enabled", refIdField = "id", idField = "tenantApps.id", nameField = "tenantApps.$.tenantAppStatus", refEntityClass = nancal.iam.db.mongo.entity.TenantApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"name"})})
    @nbcp.db.Cn(value = "企业身份源")
    public class IdentitySourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.IdentitySource> {
        public String collectionName;
        public String databaseId;
        public IdentitySourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.IdentitySource.class, "identitySource", MyHelper.AsString(collectionName,"identitySource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentitySourceEntity(){
            this("","");
        }
        

        /**
         * 主键
         */
        @nbcp.db.Cn(value = "主键") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 显示名称
         */
        @nbcp.db.Cn(value = "显示名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 所属租户
         */
        @nbcp.db.Cn(value = "所属租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 唯一标识符
         */
        @nbcp.db.Cn(value = "唯一标识符") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 创建日期
         */
        @nbcp.db.Cn(value = "创建日期") 
        public MongoColumnName buildAt = new MongoColumnName("buildAt");

        /**
         * LDAP链接
         */
        @nbcp.db.Cn(value = "LDAP链接") 
        public MongoColumnName url = new MongoColumnName("url");

        /**
         * Bind DN
         */
        @nbcp.db.Cn(value = "Bind DN") 
        public MongoColumnName bindDN = new MongoColumnName("bindDN");

        /**
         * Bind DN密码
         */
        @nbcp.db.Cn(value = "Bind DN密码") 
        public MongoColumnName bindDNPassword = new MongoColumnName("bindDNPassword");

        /**
         * Base DN
         */
        @nbcp.db.Cn(value = "Base DN") 
        public MongoColumnName baseDN = new MongoColumnName("baseDN");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        /**
         * 授权的应用
         */
        @nbcp.db.Cn(value = "授权的应用") 
        public TenantIdentitySourceAppMeta tenantApps = new TenantIdentitySourceAppMeta("tenantApps");

        public MongoQueryClip<IdentitySourceEntity, nancal.iam.db.mongo.entity.IdentitySource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentitySourceEntity, nancal.iam.db.mongo.entity.IdentitySource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentitySourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 企业身份源类型
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"name"})})
    @nbcp.db.Cn(value = "企业身份源类型")
    public class IdentityTypeListEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.IdentityTypeList> {
        public String collectionName;
        public String databaseId;
        public IdentityTypeListEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.IdentityTypeList.class, "identityTypeList", MyHelper.AsString(collectionName,"identityTypeList"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentityTypeListEntity(){
            this("","");
        }
        

        /**
         * 主键
         */
        @nbcp.db.Cn(value = "主键") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 所属租户
         */
        @nbcp.db.Cn(value = "所属租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * ldap
         */
        @nbcp.db.Cn(value = "ldap") 
        public MongoColumnName ldap = new MongoColumnName("ldap");

        /**
         * saml
         */
        @nbcp.db.Cn(value = "saml") 
        public MongoColumnName saml = new MongoColumnName("saml");

        /**
         * cas
         */
        @nbcp.db.Cn(value = "cas") 
        public MongoColumnName cas = new MongoColumnName("cas");

        /**
         * oidc
         */
        @nbcp.db.Cn(value = "oidc") 
        public MongoColumnName oidc = new MongoColumnName("oidc");

        /**
         * oauth2.0
         */
        @nbcp.db.Cn(value = "oauth2.0") 
        public MongoColumnName oauth = new MongoColumnName("oauth");

        /**
         * windowsAD
         */
        @nbcp.db.Cn(value = "windowsAD") 
        public MongoColumnName windowsAD = new MongoColumnName("windowsAD");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        public MongoQueryClip<IdentityTypeListEntity, nancal.iam.db.mongo.entity.IdentityTypeList> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentityTypeListEntity, nancal.iam.db.mongo.entity.IdentityTypeList> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentityTypeListEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 行业字典
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "行业字典")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"code"})})
    public class IndustryDictEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.IndustryDict> {
        public String collectionName;
        public String databaseId;
        public IndustryDictEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.IndustryDict.class, "industryDict", MyHelper.AsString(collectionName,"industryDict"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IndustryDictEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 名称
         */
        @nbcp.db.Cn(value = "名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 编码
         */
        @nbcp.db.Cn(value = "编码") 
        public MongoColumnName code = new MongoColumnName("code");

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

        public MongoQueryClip<IndustryDictEntity, nancal.iam.db.mongo.entity.IndustryDict> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IndustryDictEntity, nancal.iam.db.mongo.entity.IndustryDict> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IndustryDictEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<IndustryDictEntity, nancal.iam.db.mongo.entity.IndustryDict> queryByCode(String code) {
            return this.query().where( it-> it.code.match( code )) ;
        }

        public MongoDeleteClip<IndustryDictEntity> deleteByCode(String code) {
            return this.delete().where (it-> it.code.match( code ));
        }

        public MongoUpdateClip<IndustryDictEntity, nancal.iam.db.mongo.entity.IndustryDict> updateByCode(String code) {
            return this.update().where ( it-> it.code.match( code ));
        }

    }

    /**
     * Oauth2.0企业身份源
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.codeName.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "appCode", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.codeName.code", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "enabled", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.sysAppStatus", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.logo.id", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.logo.url", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "enabled", refIdField = "id", idField = "tenantApps.id", nameField = "tenantApps.$.tenantAppStatus", refEntityClass = nancal.iam.db.mongo.entity.TenantApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"name"})})
    @nbcp.db.Cn(value = "Oauth2.0企业身份源")
    public class IdentityOauthSourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ldap.IdentityOauthSource> {
        public String collectionName;
        public String databaseId;
        public IdentityOauthSourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ldap.IdentityOauthSource.class, "identityOauthSource", MyHelper.AsString(collectionName,"identityOauthSource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentityOauthSourceEntity(){
            this("","");
        }
        

        /**
         * 主键
         */
        @nbcp.db.Cn(value = "主键") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 显示名称
         */
        @nbcp.db.Cn(value = "显示名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 所属租户
         */
        @nbcp.db.Cn(value = "所属租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 唯一标识符
         */
        @nbcp.db.Cn(value = "唯一标识符") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 授权url
         */
        @nbcp.db.Cn(value = "授权url") 
        public MongoColumnName url = new MongoColumnName("url");

        /**
         * tokenUrl
         */
        @nbcp.db.Cn(value = "tokenUrl") 
        public MongoColumnName tokenUrl = new MongoColumnName("tokenUrl");

        /**
         * 创建日期
         */
        @nbcp.db.Cn(value = "创建日期") 
        public MongoColumnName buildAt = new MongoColumnName("buildAt");

        /**
         * Bind DN
         */
        @nbcp.db.Cn(value = "Bind DN") 
        public MongoColumnName scope = new MongoColumnName("scope");

        /**
         * client ID
         */
        @nbcp.db.Cn(value = "client ID") 
        public MongoColumnName clientId = new MongoColumnName("clientId");

        /**
         * client Security
         */
        @nbcp.db.Cn(value = "client Security") 
        public MongoColumnName clientSecurity = new MongoColumnName("clientSecurity");

        /**
         * 登录模式
         */
        @nbcp.db.Cn(value = "登录模式") 
        public MongoColumnName loginType = new MongoColumnName("loginType");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        /**
         * 授权的应用
         */
        @nbcp.db.Cn(value = "授权的应用") 
        public TenantIdentitySourceAppMeta tenantApps = new TenantIdentitySourceAppMeta("tenantApps");

        public MongoQueryClip<IdentityOauthSourceEntity, nancal.iam.db.mongo.entity.ldap.IdentityOauthSource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentityOauthSourceEntity, nancal.iam.db.mongo.entity.ldap.IdentityOauthSource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentityOauthSourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "应用")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "industry.id", nameField = "industry.name", refEntityClass = nancal.iam.db.mongo.entity.IndustryDict.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"appCode"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appSourceDbId"})})
    @nbcp.db.RemoveToSysDustbin
    public class SysApplicationEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.SysApplication> {
        public String collectionName;
        public String databaseId;
        public SysApplicationEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.SysApplication.class, "sysApplication", MyHelper.AsString(collectionName,"sysApplication"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SysApplicationEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 中文应用名称
         */
        @nbcp.db.Cn(value = "中文应用名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 数据同步用
         */
        @nbcp.db.Cn(value = "数据同步用") 
        public MongoColumnName appSourceDbId = new MongoColumnName("appSourceDbId");

        /**
         * 版本
         */
        @nbcp.db.Cn(value = "版本") 
        public MongoColumnName version = new MongoColumnName("version");

        /**
         * 版本
         */
        @nbcp.db.Cn(value = "版本") 
        public MongoColumnName protal = new MongoColumnName("protal");

        /**
         * 应用私钥
         */
        @nbcp.db.Cn(value = "应用私钥") 
        public MongoColumnName privateKey = new MongoColumnName("privateKey");

        /**
         * 应用公钥
         */
        @nbcp.db.Cn(value = "应用公钥") 
        public MongoColumnName publicKey = new MongoColumnName("publicKey");

        /**
         * 启用true/禁用false
         */
        @nbcp.db.Cn(value = "启用true/禁用false") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        /**
         * 应用Id
         */
        @nbcp.db.Cn(value = "应用Id") 
        public MongoColumnName appCode = new MongoColumnName("appCode");

        /**
         * 英文应用名称或其他类型名称
         */
        @nbcp.db.Cn(value = "英文应用名称或其他类型名称") 
        public MongoColumnName ename = new MongoColumnName("ename");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * logo
         */
        @nbcp.db.Cn(value = "logo") 
        public IdUrlMeta logo = new IdUrlMeta("logo");

        /**
         * 所属行业
         */
        @nbcp.db.Cn(value = "所属行业") 
        public IdNameMeta industry = new IdNameMeta("industry");

        /**
         * 用户类型
         */
        @nbcp.db.Cn(value = "用户类型") 
        public MongoColumnName userType = new MongoColumnName("userType");

        /**
         * 是否上架
         */
        @nbcp.db.Cn(value = "是否上架") 
        public MongoColumnName isOnLine = new MongoColumnName("isOnLine");

        /**
         * 地址
         */
        @nbcp.db.Cn(value = "地址") 
        public MongoColumnName url = new MongoColumnName("url");

        /**
         * 标签
         */
        @nbcp.db.Cn(value = "标签") 
        public CodeNameMeta lable = new CodeNameMeta("lable");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

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

        public MongoQueryClip<SysApplicationEntity, nancal.iam.db.mongo.entity.SysApplication> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SysApplicationEntity, nancal.iam.db.mongo.entity.SysApplication> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SysApplicationEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<SysApplicationEntity, nancal.iam.db.mongo.entity.SysApplication> queryByAppCode(String appCode) {
            return this.query().where( it-> it.appCode.match( appCode )) ;
        }

        public MongoDeleteClip<SysApplicationEntity> deleteByAppCode(String appCode) {
            return this.delete().where (it-> it.appCode.match( appCode ));
        }

        public MongoUpdateClip<SysApplicationEntity, nancal.iam.db.mongo.entity.SysApplication> updateByAppCode(String appCode) {
            return this.update().where ( it-> it.appCode.match( appCode ));
        }

    }

    /**
     * 应用角色
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "应用角色")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"appInfo.code", "name"})})
    public class SysAppRoleEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.SysAppRole> {
        public String collectionName;
        public String databaseId;
        public SysAppRoleEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.SysAppRole.class, "sysAppRole", MyHelper.AsString(collectionName,"sysAppRole"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SysAppRoleEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 角色名称
         */
        @nbcp.db.Cn(value = "角色名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

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

        public MongoQueryClip<SysAppRoleEntity, nancal.iam.db.mongo.entity.SysAppRole> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SysAppRoleEntity, nancal.iam.db.mongo.entity.SysAppRole> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SysAppRoleEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<SysAppRoleEntity, nancal.iam.db.mongo.entity.SysAppRole> queryByAppInfoCodeName(String appInfoCode,String name) {
            return this.query().where( it-> it.appInfo.getCode().match( appInfoCode )).where( it-> it.name.match( name )) ;
        }

        public MongoDeleteClip<SysAppRoleEntity> deleteByAppInfoCodeName(String appInfoCode,String name) {
            return this.delete().where (it-> it.appInfo.getCode().match( appInfoCode )).where (it-> it.name.match( name ));
        }

        public MongoUpdateClip<SysAppRoleEntity, nancal.iam.db.mongo.entity.SysAppRole> updateByAppInfoCodeName(String appInfoCode,String name) {
            return this.update().where ( it-> it.appInfo.getCode().match( appInfoCode )).where ( it-> it.name.match( name ));
        }

    }

    /**
     * 应用授权规则
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "应用授权规则")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"name"})})
    @nbcp.db.RemoveToSysDustbin
    public class SysAuthRuleEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.SysAuthRule> {
        public String collectionName;
        public String databaseId;
        public SysAuthRuleEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.SysAuthRule.class, "sysAuthRule", MyHelper.AsString(collectionName,"sysAuthRule"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SysAuthRuleEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 属性名
         */
        @nbcp.db.Cn(value = "属性名") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 条件因子名称
         */
        @nbcp.db.Cn(value = "条件因子名称") 
        public MongoColumnName conditionName = new MongoColumnName("conditionName");

        /**
         * 属性KEY
         */
        @nbcp.db.Cn(value = "属性KEY") 
        public MongoColumnName key = new MongoColumnName("key");

        /**
         * 属性类型
         */
        @nbcp.db.Cn(value = "属性类型") 
        public MongoColumnName paramType = new MongoColumnName("paramType");

        /**
         * 运算符类型
         */
        @nbcp.db.Cn(value = "运算符类型") 
        public MongoColumnName ruleType = new MongoColumnName("ruleType");

        /**
         * 运算符
         */
        @nbcp.db.Cn(value = "运算符") 
        public OperatorMeta operators = new OperatorMeta("operators");

        /**
         * 代码块
         */
        @nbcp.db.Cn(value = "代码块") 
        public MongoColumnName codeBlocks = new MongoColumnName("codeBlocks");

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

        public MongoQueryClip<SysAuthRuleEntity, nancal.iam.db.mongo.entity.SysAuthRule> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SysAuthRuleEntity, nancal.iam.db.mongo.entity.SysAuthRule> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SysAuthRuleEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<SysAuthRuleEntity, nancal.iam.db.mongo.entity.SysAuthRule> queryByName(String name) {
            return this.query().where( it-> it.name.match( name )) ;
        }

        public MongoDeleteClip<SysAuthRuleEntity> deleteByName(String name) {
            return this.delete().where (it-> it.name.match( name ));
        }

        public MongoUpdateClip<SysAuthRuleEntity, nancal.iam.db.mongo.entity.SysAuthRule> updateByName(String name) {
            return this.update().where ( it-> it.name.match( name ));
        }

    }

    /**
     * 应用资源
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "iam")
    @nbcp.db.Cn(value = "应用资源")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {})
    public class SysResourceInfoEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.SysResourceInfo> {
        public String collectionName;
        public String databaseId;
        public SysResourceInfoEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.SysResourceInfo.class, "sysResourceInfo", MyHelper.AsString(collectionName,"sysResourceInfo"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SysResourceInfoEntity(){
            this("","");
        }
        

        /**
         * 主键ID
         */
        @nbcp.db.Cn(value = "主键ID") 
        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 应用
         */
        @nbcp.db.Cn(value = "应用") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 详情
         */
        @nbcp.db.Cn(value = "详情") 
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

        public MongoColumnName type = new MongoColumnName("type");

        public MongoColumnName code = new MongoColumnName("code");

        public MongoColumnName resource = new MongoColumnName("resource");

        public MongoColumnName action = new MongoColumnName("action");

        public MongoColumnName dataAccessLevel = new MongoColumnName("dataAccessLevel");

        public MongoQueryClip<SysResourceInfoEntity, nancal.iam.db.mongo.entity.SysResourceInfo> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SysResourceInfoEntity, nancal.iam.db.mongo.entity.SysResourceInfo> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SysResourceInfoEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

}
