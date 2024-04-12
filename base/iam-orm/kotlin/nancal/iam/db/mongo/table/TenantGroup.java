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

@Component("mongo.tenant")
@MetaDataGroup(dbType = DatabaseEnum.Mongo, value = "tenant")
public class TenantGroup implements IDataGroup {
    @Override
    public HashSet<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(excelErrorJob);
            add(excelJob);
            add(identitySyncData);
            add(identitySyncJob);
            add(identitySyncJobLog);
            add(identitySyncRiskData);
            add(socialIdentitySourceConfig);
            add(tenantWeChatLoginUser);
            add(tenantAuthUpdateDetail);
            add(tenantAuthUpdateDetailLastTime);
            add(tenantStandardDeptAuthResource);
            add(tenantStandardRoleAuthResource);
            add(tenantStandardUserAuthResource);
            add(tenantStandardUserGroupAuthResource);
            add(excelDeportmentErrorJob);
            add(excelDeportmentSuccessJob);
            add(tenantAdminRole);
            add(tenantGroupDict);
            add(tenantResourceGroup);
            add(tenant);
            add(tenantAdminLoginUser);
            add(tenantAdminUser);
            add(tenantAppAuthResourceInfo);
            add(tenantAppExtendFieldDataSourceDict);
            add(tenantApplication);
            add(tenantApplicationFieldExtend);
            add(tenantAppRole);
            add(tenantAuthResourceGroup);
            add(tenantAuthRules);
            add(tenantDepartmentInfo);
            add(tenantDepartmentInfoFieldExtend);
            add(tenantDutyDict);
            add(tenantExtendFieldDataSourceDict);
            add(tenantLoginUser);
            add(tenantResourceInfo);
            add(tenantSecretSet);
            add(tenantUser);
            add(tenantUserFieldExtend);
            add(tenantUserGroup);
            add(tenantUserLeave);
        } };
    }


    /**
     * 导入数据(失败数据) (动态库)
     */
    public ExcelErrorJobEntity excelErrorJob = new ExcelErrorJobEntity();

    /**
     * 导入数据(失败数据) (动态库)
     */
    public ExcelErrorJobEntity excelErrorJob(String tenantId) {
        return new ExcelErrorJobEntity("",tenantId);
    }

    /**
     * 导入数据(成功数据) (动态库)
     */
    public ExcelJobEntity excelJob = new ExcelJobEntity();

    /**
     * 导入数据(成功数据) (动态库)
     */
    public ExcelJobEntity excelJob(String tenantId) {
        return new ExcelJobEntity("",tenantId);
    }

    /**
     * 同步任务数据
     */
    public IdentitySyncDataEntity identitySyncData = new IdentitySyncDataEntity();

    /**
     * 身份源同步任务
     */
    public IdentitySyncJobEntity identitySyncJob = new IdentitySyncJobEntity();

    /**
     * 同步任务记录
     */
    public IdentitySyncJobLogEntity identitySyncJobLog = new IdentitySyncJobLogEntity();

    /**
     * 风险数据
     */
    public IdentitySyncRiskDataEntity identitySyncRiskData = new IdentitySyncRiskDataEntity();

    /**
     * 社会身份源
     */
    public SocialIdentitySourceConfigEntity socialIdentitySourceConfig = new SocialIdentitySourceConfigEntity();

    /**
     * 微信身份源
     */
    public TenantWeChatLoginUserEntity tenantWeChatLoginUser = new TenantWeChatLoginUserEntity();

    /**
     * 应用授权同步详情记录表 (动态库)
     */
    public TenantAuthUpdateDetailEntity tenantAuthUpdateDetail = new TenantAuthUpdateDetailEntity();

    /**
     * 应用授权同步详情记录表 (动态库)
     */
    public TenantAuthUpdateDetailEntity tenantAuthUpdateDetail(String tenantId) {
        return new TenantAuthUpdateDetailEntity("",tenantId);
    }

    /**
     * 最后同步标准表时间
     */
    public TenantAuthUpdateDetailLastTimeEntity tenantAuthUpdateDetailLastTime = new TenantAuthUpdateDetailLastTimeEntity();

    /**
     * 应用部门授权标准表 (动态库)
     */
    public TenantStandardDeptAuthResourceEntity tenantStandardDeptAuthResource = new TenantStandardDeptAuthResourceEntity();

    /**
     * 应用部门授权标准表 (动态库)
     */
    public TenantStandardDeptAuthResourceEntity tenantStandardDeptAuthResource(String tenantId) {
        return new TenantStandardDeptAuthResourceEntity("",tenantId);
    }

    /**
     * 应用角色授权标准表 (动态库)
     */
    public TenantStandardRoleAuthResourceEntity tenantStandardRoleAuthResource = new TenantStandardRoleAuthResourceEntity();

    /**
     * 应用角色授权标准表 (动态库)
     */
    public TenantStandardRoleAuthResourceEntity tenantStandardRoleAuthResource(String tenantId) {
        return new TenantStandardRoleAuthResourceEntity("",tenantId);
    }

    /**
     * 应用用户授权标准表 (动态库)
     */
    public TenantStandardUserAuthResourceEntity tenantStandardUserAuthResource = new TenantStandardUserAuthResourceEntity();

    /**
     * 应用用户授权标准表 (动态库)
     */
    public TenantStandardUserAuthResourceEntity tenantStandardUserAuthResource(String tenantId) {
        return new TenantStandardUserAuthResourceEntity("",tenantId);
    }

    /**
     * 应用组授权标准表 (动态库)
     */
    public TenantStandardUserGroupAuthResourceEntity tenantStandardUserGroupAuthResource = new TenantStandardUserGroupAuthResourceEntity();

    /**
     * 应用组授权标准表 (动态库)
     */
    public TenantStandardUserGroupAuthResourceEntity tenantStandardUserGroupAuthResource(String tenantId) {
        return new TenantStandardUserGroupAuthResourceEntity("",tenantId);
    }

    /**
     * 部门导入数据(失败数据) (动态库)
     */
    public ExcelDeportmentErrorJobEntity excelDeportmentErrorJob = new ExcelDeportmentErrorJobEntity();

    /**
     * 部门导入数据(失败数据) (动态库)
     */
    public ExcelDeportmentErrorJobEntity excelDeportmentErrorJob(String tenantId) {
        return new ExcelDeportmentErrorJobEntity("",tenantId);
    }

    /**
     * 部门导入数据(成功数据) (动态库)
     */
    public ExcelDeportmentSuccessJobEntity excelDeportmentSuccessJob = new ExcelDeportmentSuccessJobEntity();

    /**
     * 部门导入数据(成功数据) (动态库)
     */
    public ExcelDeportmentSuccessJobEntity excelDeportmentSuccessJob(String tenantId) {
        return new ExcelDeportmentSuccessJobEntity("",tenantId);
    }

    /**
     * 租户管理员角色
     */
    public TenantAdminRoleEntity tenantAdminRole = new TenantAdminRoleEntity();

    /**
     * 数据字典 (动态库)
     */
    public TenantGroupDictEntity tenantGroupDict = new TenantGroupDictEntity();

    /**
     * 数据字典 (动态库)
     */
    public TenantGroupDictEntity tenantGroupDict(String tenantId) {
        return new TenantGroupDictEntity("",tenantId);
    }

    /**
     * 资源组 (动态库)
     */
    public TenantResourceGroupEntity tenantResourceGroup = new TenantResourceGroupEntity();

    /**
     * 资源组 (动态库)
     */
    public TenantResourceGroupEntity tenantResourceGroup(String tenantId) {
        return new TenantResourceGroupEntity("",tenantId);
    }

    /**
     * 租户
     */
    public TenantEntity tenant = new TenantEntity();

    /**
     * 租户管理员账号（废弃）
     */
    public TenantAdminLoginUserEntity tenantAdminLoginUser = new TenantAdminLoginUserEntity();

    /**
     * 租户管理员用户（废弃） (动态库)
     */
    public TenantAdminUserEntity tenantAdminUser = new TenantAdminUserEntity();

    /**
     * 租户管理员用户（废弃） (动态库)
     */
    public TenantAdminUserEntity tenantAdminUser(String tenantId) {
        return new TenantAdminUserEntity("",tenantId);
    }

    /**
     * 应用授权 (动态库)
     */
    public TenantAppAuthResourceInfoEntity tenantAppAuthResourceInfo = new TenantAppAuthResourceInfoEntity();

    /**
     * 应用授权 (动态库)
     */
    public TenantAppAuthResourceInfoEntity tenantAppAuthResourceInfo(String tenantId) {
        return new TenantAppAuthResourceInfoEntity("",tenantId);
    }

    /**
     * 应用扩展字段数据源字典 (动态库)
     */
    public TenantAppExtendFieldDataSourceDictEntity tenantAppExtendFieldDataSourceDict = new TenantAppExtendFieldDataSourceDictEntity();

    /**
     * 应用扩展字段数据源字典 (动态库)
     */
    public TenantAppExtendFieldDataSourceDictEntity tenantAppExtendFieldDataSourceDict(String tenantId) {
        return new TenantAppExtendFieldDataSourceDictEntity("",tenantId);
    }

    /**
     * 租户应用 (动态库)
     */
    public TenantApplicationEntity tenantApplication = new TenantApplicationEntity();

    /**
     * 租户应用 (动态库)
     */
    public TenantApplicationEntity tenantApplication(String tenantId) {
        return new TenantApplicationEntity("",tenantId);
    }

    /**
     * 应用自定义字段 (动态库)
     */
    public TenantApplicationFieldExtendEntity tenantApplicationFieldExtend = new TenantApplicationFieldExtendEntity();

    /**
     * 应用自定义字段 (动态库)
     */
    public TenantApplicationFieldExtendEntity tenantApplicationFieldExtend(String tenantId) {
        return new TenantApplicationFieldExtendEntity("",tenantId);
    }

    /**
     * 租户应用角色 (动态库)
     */
    public TenantAppRoleEntity tenantAppRole = new TenantAppRoleEntity();

    /**
     * 租户应用角色 (动态库)
     */
    public TenantAppRoleEntity tenantAppRole(String tenantId) {
        return new TenantAppRoleEntity("",tenantId);
    }

    /**
     * 租户资源组授权 (动态库)
     */
    public TenantAuthResourceGroupEntity tenantAuthResourceGroup = new TenantAuthResourceGroupEntity();

    /**
     * 租户资源组授权 (动态库)
     */
    public TenantAuthResourceGroupEntity tenantAuthResourceGroup(String tenantId) {
        return new TenantAuthResourceGroupEntity("",tenantId);
    }

    /**
     * 租户应用授权规则 (动态库)
     */
    public TenantAuthRulesEntity tenantAuthRules = new TenantAuthRulesEntity();

    /**
     * 租户应用授权规则 (动态库)
     */
    public TenantAuthRulesEntity tenantAuthRules(String tenantId) {
        return new TenantAuthRulesEntity("",tenantId);
    }

    /**
     * 部门 (动态库)
     */
    public TenantDepartmentInfoEntity tenantDepartmentInfo = new TenantDepartmentInfoEntity();

    /**
     * 部门 (动态库)
     */
    public TenantDepartmentInfoEntity tenantDepartmentInfo(String tenantId) {
        return new TenantDepartmentInfoEntity("",tenantId);
    }

    /**
     * 部门自定义字段 (动态库)
     */
    public TenantDepartmentInfoFieldExtendEntity tenantDepartmentInfoFieldExtend = new TenantDepartmentInfoFieldExtendEntity();

    /**
     * 部门自定义字段 (动态库)
     */
    public TenantDepartmentInfoFieldExtendEntity tenantDepartmentInfoFieldExtend(String tenantId) {
        return new TenantDepartmentInfoFieldExtendEntity("",tenantId);
    }

    /**
     * 岗位字典 (动态库)
     */
    public TenantDutyDictEntity tenantDutyDict = new TenantDutyDictEntity();

    /**
     * 岗位字典 (动态库)
     */
    public TenantDutyDictEntity tenantDutyDict(String tenantId) {
        return new TenantDutyDictEntity("",tenantId);
    }

    /**
     * 扩展字段数据源字典 (动态库)
     */
    public TenantExtendFieldDataSourceDictEntity tenantExtendFieldDataSourceDict = new TenantExtendFieldDataSourceDictEntity();

    /**
     * 扩展字段数据源字典 (动态库)
     */
    public TenantExtendFieldDataSourceDictEntity tenantExtendFieldDataSourceDict(String tenantId) {
        return new TenantExtendFieldDataSourceDictEntity("",tenantId);
    }

    /**
     * 用户登录表
     */
    public TenantLoginUserEntity tenantLoginUser = new TenantLoginUserEntity();

    /**
     * 租户应用资源 (动态库)
     */
    public TenantResourceInfoEntity tenantResourceInfo = new TenantResourceInfoEntity();

    /**
     * 租户应用资源 (动态库)
     */
    public TenantResourceInfoEntity tenantResourceInfo(String tenantId) {
        return new TenantResourceInfoEntity("",tenantId);
    }

    /**
     * 租户设置
     */
    public TenantSecretSetEntity tenantSecretSet = new TenantSecretSetEntity();

    /**
     * 租户用户 (动态库)
     */
    public TenantUserEntity tenantUser = new TenantUserEntity();

    /**
     * 租户用户 (动态库)
     */
    public TenantUserEntity tenantUser(String tenantId) {
        return new TenantUserEntity("",tenantId);
    }

    /**
     * 租户用户自定义字段 (动态库)
     */
    public TenantUserFieldExtendEntity tenantUserFieldExtend = new TenantUserFieldExtendEntity();

    /**
     * 租户用户自定义字段 (动态库)
     */
    public TenantUserFieldExtendEntity tenantUserFieldExtend(String tenantId) {
        return new TenantUserFieldExtendEntity("",tenantId);
    }

    /**
     * 租户用户组 (动态库)
     */
    public TenantUserGroupEntity tenantUserGroup = new TenantUserGroupEntity();

    /**
     * 租户用户组 (动态库)
     */
    public TenantUserGroupEntity tenantUserGroup(String tenantId) {
        return new TenantUserGroupEntity("",tenantId);
    }

    /**
     * 租户用户离职表 (动态库)
     */
    public TenantUserLeaveEntity tenantUserLeave = new TenantUserLeaveEntity();

    /**
     * 租户用户离职表 (动态库)
     */
    public TenantUserLeaveEntity tenantUserLeave(String tenantId) {
        return new TenantUserLeaveEntity("",tenantId);
    }


    /**
     * 导入数据(失败数据) (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "导入数据(失败数据)")
    @nbcp.db.VarDatabase(value = "tenant.id")
    public class ExcelErrorJobEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ExcelErrorJob> {
        public String collectionName;
        public String databaseId;
        public ExcelErrorJobEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ExcelErrorJob.class, "excelErrorJob", MyHelper.AsString(collectionName,"excelErrorJob"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public ExcelErrorJobEntity(){
            this("","");
        }
        

        /**
         * id
         */
        @nbcp.db.Cn(value = "id") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 姓名
         */
        @nbcp.db.Cn(value = "姓名") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * jobId
         */
        @nbcp.db.Cn(value = "jobId") 
        public MongoColumnName jobId = new MongoColumnName("jobId");

        /**
         * 行号
         */
        @nbcp.db.Cn(value = "行号") 
        public MongoColumnName rowNumber = new MongoColumnName("rowNumber");

        /**
         * 部门
         */
        @nbcp.db.Cn(value = "部门") 
        public MongoColumnName depts = new MongoColumnName("depts");

        /**
         * 职务
         */
        @nbcp.db.Cn(value = "职务") 
        public MongoColumnName duty = new MongoColumnName("duty");

        /**
         * 用户名
         */
        @nbcp.db.Cn(value = "用户名") 
        public MongoColumnName loginName = new MongoColumnName("loginName");

        /**
         * 手机号
         */
        @nbcp.db.Cn(value = "手机号") 
        public MongoColumnName mobile = new MongoColumnName("mobile");

        /**
         * 邮箱
         */
        @nbcp.db.Cn(value = "邮箱") 
        public MongoColumnName email = new MongoColumnName("email");

        /**
         * 失败原因
         */
        @nbcp.db.Cn(value = "失败原因") 
        public MongoColumnName reason = new MongoColumnName("reason");

        public MongoQueryClip<ExcelErrorJobEntity, nancal.iam.db.mongo.entity.ExcelErrorJob> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<ExcelErrorJobEntity, nancal.iam.db.mongo.entity.ExcelErrorJob> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<ExcelErrorJobEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 导入数据(成功数据) (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "导入数据(成功数据)")
    @nbcp.db.VarDatabase(value = "tenant.id")
    public class ExcelJobEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ExcelJob> {
        public String collectionName;
        public String databaseId;
        public ExcelJobEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ExcelJob.class, "excelJob", MyHelper.AsString(collectionName,"excelJob"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public ExcelJobEntity(){
            this("","");
        }
        

        /**
         * id
         */
        @nbcp.db.Cn(value = "id") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 姓名
         */
        @nbcp.db.Cn(value = "姓名") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * jobId
         */
        @nbcp.db.Cn(value = "jobId") 
        public MongoColumnName jobId = new MongoColumnName("jobId");

        /**
         * 部门
         */
        @nbcp.db.Cn(value = "部门") 
        public MongoColumnName depts = new MongoColumnName("depts");

        /**
         * 职务
         */
        @nbcp.db.Cn(value = "职务") 
        public MongoColumnName duty = new MongoColumnName("duty");

        /**
         * 用户名
         */
        @nbcp.db.Cn(value = "用户名") 
        public MongoColumnName loginName = new MongoColumnName("loginName");

        /**
         * 手机号
         */
        @nbcp.db.Cn(value = "手机号") 
        public MongoColumnName mobile = new MongoColumnName("mobile");

        /**
         * 邮箱
         */
        @nbcp.db.Cn(value = "邮箱") 
        public MongoColumnName email = new MongoColumnName("email");

        public MongoQueryClip<ExcelJobEntity, nancal.iam.db.mongo.entity.ExcelJob> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<ExcelJobEntity, nancal.iam.db.mongo.entity.ExcelJob> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<ExcelJobEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 同步任务数据
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "同步任务数据")
    public class IdentitySyncDataEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ldap.IdentitySyncData> {
        public String collectionName;
        public String databaseId;
        public IdentitySyncDataEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ldap.IdentitySyncData.class, "identitySyncData", MyHelper.AsString(collectionName,"identitySyncData"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentitySyncDataEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 任务ID
         */
        @nbcp.db.Cn(value = "任务ID") 
        public MongoColumnName jobId = new MongoColumnName("jobId");

        /**
         * 任务日志ID
         */
        @nbcp.db.Cn(value = "任务日志ID") 
        public MongoColumnName jobLogId = new MongoColumnName("jobLogId");

        /**
         * 主体类型
         */
        @nbcp.db.Cn(value = "主体类型") 
        public MongoColumnName objectType = new MongoColumnName("objectType");

        /**
         * 主体数据标识
         */
        @nbcp.db.Cn(value = "主体数据标识") 
        public IdNameMeta objectData = new IdNameMeta("objectData");

        /**
         * 同步类型
         */
        @nbcp.db.Cn(value = "同步类型") 
        public MongoColumnName syncType = new MongoColumnName("syncType");

        /**
         * 执行结果
         */
        @nbcp.db.Cn(value = "执行结果") 
        public MongoColumnName result = new MongoColumnName("result");

        /**
         * 错误信息
         */
        @nbcp.db.Cn(value = "错误信息") 
        public MongoColumnName msg = new MongoColumnName("msg");

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

        public MongoQueryClip<IdentitySyncDataEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncData> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentitySyncDataEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncData> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentitySyncDataEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 身份源同步任务
     */
    @nbcp.db.Cn(value = "身份源同步任务")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityGroup(value = "tenant")
    public class IdentitySyncJobEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ldap.IdentitySyncJob> {
        public String collectionName;
        public String databaseId;
        public IdentitySyncJobEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ldap.IdentitySyncJob.class, "identitySyncJob", MyHelper.AsString(collectionName,"identitySyncJob"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentitySyncJobEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 身份源
         */
        @nbcp.db.Cn(value = "身份源") 
        public MongoColumnName identitySource = new MongoColumnName("identitySource");

        /**
         * 同步模式
         */
        @nbcp.db.Cn(value = "同步模式") 
        public MongoColumnName schema = new MongoColumnName("schema");

        /**
         * 任务状态
         */
        @nbcp.db.Cn(value = "任务状态") 
        public MongoColumnName status = new MongoColumnName("status");

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

        public MongoQueryClip<IdentitySyncJobEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncJob> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentitySyncJobEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncJob> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentitySyncJobEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 同步任务记录
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "同步任务记录")
    public class IdentitySyncJobLogEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ldap.IdentitySyncJobLog> {
        public String collectionName;
        public String databaseId;
        public IdentitySyncJobLogEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ldap.IdentitySyncJobLog.class, "identitySyncJobLog", MyHelper.AsString(collectionName,"identitySyncJobLog"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentitySyncJobLogEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 身份源
         */
        @nbcp.db.Cn(value = "身份源") 
        public MongoColumnName identitySource = new MongoColumnName("identitySource");

        /**
         * 任务ID
         */
        @nbcp.db.Cn(value = "任务ID") 
        public MongoColumnName jobId = new MongoColumnName("jobId");

        /**
         * 成功条数
         */
        @nbcp.db.Cn(value = "成功条数") 
        public MongoColumnName successNumber = new MongoColumnName("successNumber");

        /**
         * 错误条数
         */
        @nbcp.db.Cn(value = "错误条数") 
        public MongoColumnName errorNumber = new MongoColumnName("errorNumber");

        /**
         * 错误信息
         */
        @nbcp.db.Cn(value = "错误信息") 
        public MongoColumnName msg = new MongoColumnName("msg");

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

        public MongoQueryClip<IdentitySyncJobLogEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncJobLog> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentitySyncJobLogEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncJobLog> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentitySyncJobLogEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 风险数据
     */
    @nbcp.db.Cn(value = "风险数据")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityGroup(value = "tenant")
    public class IdentitySyncRiskDataEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.ldap.IdentitySyncRiskData> {
        public String collectionName;
        public String databaseId;
        public IdentitySyncRiskDataEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.ldap.IdentitySyncRiskData.class, "identitySyncRiskData", MyHelper.AsString(collectionName,"identitySyncRiskData"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public IdentitySyncRiskDataEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 身份源
         */
        @nbcp.db.Cn(value = "身份源") 
        public MongoColumnName identitySource = new MongoColumnName("identitySource");

        /**
         * 主体类型
         */
        @nbcp.db.Cn(value = "主体类型") 
        public MongoColumnName objectType = new MongoColumnName("objectType");

        /**
         * 主体数据标识
         */
        @nbcp.db.Cn(value = "主体数据标识") 
        public IdNameMeta objectData = new IdNameMeta("objectData");

        /**
         * 同步类型
         */
        @nbcp.db.Cn(value = "同步类型") 
        public MongoColumnName syncType = new MongoColumnName("syncType");

        /**
         * 执行状态
         */
        @nbcp.db.Cn(value = "执行状态") 
        public MongoColumnName status = new MongoColumnName("status");

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

        public MongoQueryClip<IdentitySyncRiskDataEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncRiskData> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<IdentitySyncRiskDataEntity, nancal.iam.db.mongo.entity.ldap.IdentitySyncRiskData> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<IdentitySyncRiskDataEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 社会身份源
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.codeName.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "appCode", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.codeName.code", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "enabled", refIdField = "id", idField = "tenantApps.sysAppId", nameField = "tenantApps.$.sysAppStatus", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "enabled", refIdField = "id", idField = "tenantApps.id", nameField = "tenantApps.$.tenantAppStatus", refEntityClass = nancal.iam.db.mongo.entity.TenantApplication.class)})
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "社会身份源")
    public class SocialIdentitySourceConfigEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.socialIdentitySource.SocialIdentitySourceConfig> {
        public String collectionName;
        public String databaseId;
        public SocialIdentitySourceConfigEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.socialIdentitySource.SocialIdentitySourceConfig.class, "socialIdentitySourceConfig", MyHelper.AsString(collectionName,"socialIdentitySourceConfig"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public SocialIdentitySourceConfigEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 显示名称
         */
        @nbcp.db.Cn(value = "显示名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 应用登录方式  weixin:WeChatOfficialAccount(微信公众号扫码关注登录)weixin:WeChatApplet(小程序扫码登录)
         */
        @nbcp.db.Cn(value = "应用登录方式  weixin:WeChatOfficialAccount(微信公众号扫码关注登录)weixin:WeChatApplet(小程序扫码登录)") 
        public MongoColumnName loginType = new MongoColumnName("loginType");

        /**
         * 社会化身份源类型
         */
        @nbcp.db.Cn(value = "社会化身份源类型") 
        public MongoColumnName socialType = new MongoColumnName("socialType");

        /**
         * 身份源连接的唯一标识 租户下唯一
         */
        @nbcp.db.Cn(value = "身份源连接的唯一标识 租户下唯一") 
        public MongoColumnName identitySourceLinkId = new MongoColumnName("identitySourceLinkId");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 配置settings
         */
        @nbcp.db.Cn(value = "配置settings") 
        public MongoColumnName settings = new MongoColumnName("settings");

        /**
         * 配置是否启用
         */
        @nbcp.db.Cn(value = "配置是否启用") 
        public MongoColumnName configStatus = new MongoColumnName("configStatus");

        /**
         * 租户应用
         */
        @nbcp.db.Cn(value = "租户应用") 
        public TenantIdentitySourceAppMeta tenantApps = new TenantIdentitySourceAppMeta("tenantApps");

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

        public MongoQueryClip<SocialIdentitySourceConfigEntity, nancal.iam.db.mongo.entity.socialIdentitySource.SocialIdentitySourceConfig> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<SocialIdentitySourceConfigEntity, nancal.iam.db.mongo.entity.socialIdentitySource.SocialIdentitySourceConfig> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<SocialIdentitySourceConfigEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 微信身份源
     */
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "微信身份源")
    public class TenantWeChatLoginUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.socialIdentitySource.TenantWeChatLoginUser> {
        public String collectionName;
        public String databaseId;
        public TenantWeChatLoginUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.socialIdentitySource.TenantWeChatLoginUser.class, "tenantWeChatLoginUser", MyHelper.AsString(collectionName,"tenantWeChatLoginUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantWeChatLoginUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 用户id
         */
        @nbcp.db.Cn(value = "用户id") 
        public MongoColumnName userId = new MongoColumnName("userId");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 微信公众号openId
         */
        @nbcp.db.Cn(value = "微信公众号openId") 
        public MongoColumnName wxOpenId = new MongoColumnName("wxOpenId");

        /**
         * 微信小程序openId
         */
        @nbcp.db.Cn(value = "微信小程序openId") 
        public MongoColumnName wxAppOpenId = new MongoColumnName("wxAppOpenId");

        /**
         * 社会化身份源唯一标识
         */
        @nbcp.db.Cn(value = "社会化身份源唯一标识") 
        public MongoColumnName identitySourceLinkId = new MongoColumnName("identitySourceLinkId");

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

        public MongoQueryClip<TenantWeChatLoginUserEntity, nancal.iam.db.mongo.entity.socialIdentitySource.TenantWeChatLoginUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantWeChatLoginUserEntity, nancal.iam.db.mongo.entity.socialIdentitySource.TenantWeChatLoginUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantWeChatLoginUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用授权同步详情记录表 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用授权同步详情记录表")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.code", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.type", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.resource", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.action", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.remark", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"createAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    public class TenantAuthUpdateDetailEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail> {
        public String collectionName;
        public String databaseId;
        public TenantAuthUpdateDetailEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail.class, "tenantAuthUpdateDetail", MyHelper.AsString(collectionName,"tenantAuthUpdateDetail"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAuthUpdateDetailEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 授权数据ID
         */
        @nbcp.db.Cn(value = "授权数据ID") 
        public MongoColumnName authId = new MongoColumnName("authId");

        /**
         * 授权主体类型
         */
        @nbcp.db.Cn(value = "授权主体类型") 
        public MongoColumnName actionType = new MongoColumnName("actionType");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

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
         * 子部门是否授权，只在type=Dept时用
         */
        @nbcp.db.Cn(value = "子部门是否授权，只在type=Dept时用") 
        public MongoColumnName childDeptsAll = new MongoColumnName("childDeptsAll");

        /**
         * 是否系统默认
         */
        @nbcp.db.Cn(value = "是否系统默认") 
        public MongoColumnName isSysDefine = new MongoColumnName("isSysDefine");

        /**
         * 系统默认ID
         */
        @nbcp.db.Cn(value = "系统默认ID") 
        public MongoColumnName sysId = new MongoColumnName("sysId");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

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

        public MongoQueryClip<TenantAuthUpdateDetailEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAuthUpdateDetailEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetail> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAuthUpdateDetailEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 最后同步标准表时间
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "最后同步标准表时间")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"createAt"})})
    public class TenantAuthUpdateDetailLastTimeEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetailLastTime> {
        public String collectionName;
        public String databaseId;
        public TenantAuthUpdateDetailLastTimeEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetailLastTime.class, "tenantAuthUpdateDetailLastTime", MyHelper.AsString(collectionName,"tenantAuthUpdateDetailLastTime"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAuthUpdateDetailLastTimeEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

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

        public MongoQueryClip<TenantAuthUpdateDetailLastTimeEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetailLastTime> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAuthUpdateDetailLastTimeEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantAuthUpdateDetailLastTime> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAuthUpdateDetailLastTimeEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用部门授权标准表 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用部门授权标准表")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.type", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.code", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.resource", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.action", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "dept.id", nameField = "dept.name", refEntityClass = nancal.iam.db.mongo.entity.TenantDepartmentInfo.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantStandardDeptAuthResourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardDeptAuthResource> {
        public String collectionName;
        public String databaseId;
        public TenantStandardDeptAuthResourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardDeptAuthResource.class, "tenantStandardDeptAuthResource", MyHelper.AsString(collectionName,"tenantStandardDeptAuthResource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantStandardDeptAuthResourceEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 授权主体
         */
        @nbcp.db.Cn(value = "授权主体") 
        public IdNameMeta dept = new IdNameMeta("dept");

        /**
         * 授权的资源
         */
        @nbcp.db.Cn(value = "授权的资源") 
        public TenantDeptAuthResourceInfoMeta resources = new TenantDeptAuthResourceInfoMeta("resources");

        /**
         * 授权的资源组
         */
        @nbcp.db.Cn(value = "授权的资源组") 
        public TenantDeptAuthResourceGroupMeta resourceGroups = new TenantDeptAuthResourceGroupMeta("resourceGroups");

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

        public MongoQueryClip<TenantStandardDeptAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardDeptAuthResource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantStandardDeptAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardDeptAuthResource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantStandardDeptAuthResourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用角色授权标准表 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用角色授权标准表")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.type", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.code", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.resource", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.action", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "role.id", nameField = "role.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantStandardRoleAuthResourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardRoleAuthResource> {
        public String collectionName;
        public String databaseId;
        public TenantStandardRoleAuthResourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardRoleAuthResource.class, "tenantStandardRoleAuthResource", MyHelper.AsString(collectionName,"tenantStandardRoleAuthResource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantStandardRoleAuthResourceEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 授权主体
         */
        @nbcp.db.Cn(value = "授权主体") 
        public IdNameMeta role = new IdNameMeta("role");

        /**
         * 授权的资源
         */
        @nbcp.db.Cn(value = "授权的资源") 
        public AuthResourceInfoMeta resources = new AuthResourceInfoMeta("resources");

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

        public MongoQueryClip<TenantStandardRoleAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardRoleAuthResource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantStandardRoleAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardRoleAuthResource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantStandardRoleAuthResourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用用户授权标准表 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用用户授权标准表")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.type", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.code", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.resource", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.action", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "user.id", nameField = "user.name", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantStandardUserAuthResourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserAuthResource> {
        public String collectionName;
        public String databaseId;
        public TenantStandardUserAuthResourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserAuthResource.class, "tenantStandardUserAuthResource", MyHelper.AsString(collectionName,"tenantStandardUserAuthResource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantStandardUserAuthResourceEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 授权主体
         */
        @nbcp.db.Cn(value = "授权主体") 
        public IdNameMeta user = new IdNameMeta("user");

        /**
         * 授权的资源
         */
        @nbcp.db.Cn(value = "授权的资源") 
        public AuthResourceInfoMeta resources = new AuthResourceInfoMeta("resources");

        /**
         * 授权的资源组
         */
        @nbcp.db.Cn(value = "授权的资源组") 
        public AuthResourceGroupMeta resourceGroups = new AuthResourceGroupMeta("resourceGroups");

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

        public MongoQueryClip<TenantStandardUserAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserAuthResource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantStandardUserAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserAuthResource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantStandardUserAuthResourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用组授权标准表 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用组授权标准表")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.type", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.code", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.resource", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "sources.id", nameField = "sources.$.action", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "group.id", nameField = "group.name", refEntityClass = nancal.iam.db.mongo.entity.TenantUserGroup.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantStandardUserGroupAuthResourceEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserGroupAuthResource> {
        public String collectionName;
        public String databaseId;
        public TenantStandardUserGroupAuthResourceEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserGroupAuthResource.class, "tenantStandardUserGroupAuthResource", MyHelper.AsString(collectionName,"tenantStandardUserGroupAuthResource"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantStandardUserGroupAuthResourceEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 授权主体
         */
        @nbcp.db.Cn(value = "授权主体") 
        public IdNameMeta group = new IdNameMeta("group");

        /**
         * 授权的资源
         */
        @nbcp.db.Cn(value = "授权的资源") 
        public AuthResourceInfoMeta resources = new AuthResourceInfoMeta("resources");

        /**
         * 授权的资源组
         */
        @nbcp.db.Cn(value = "授权的资源组") 
        public AuthResourceGroupMeta resourceGroups = new AuthResourceGroupMeta("resourceGroups");

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

        public MongoQueryClip<TenantStandardUserGroupAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserGroupAuthResource> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantStandardUserGroupAuthResourceEntity, nancal.iam.db.mongo.entity.tenant.authsource.TenantStandardUserGroupAuthResource> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantStandardUserGroupAuthResourceEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 部门导入数据(失败数据) (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "部门导入数据(失败数据)")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    public class ExcelDeportmentErrorJobEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.ExcelDeportmentErrorJob> {
        public String collectionName;
        public String databaseId;
        public ExcelDeportmentErrorJobEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.ExcelDeportmentErrorJob.class, "excelDeportmentErrorJob", MyHelper.AsString(collectionName,"excelDeportmentErrorJob"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public ExcelDeportmentErrorJobEntity(){
            this("","");
        }
        

        /**
         * id
         */
        @nbcp.db.Cn(value = "id") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * tenant
         */
        @nbcp.db.Cn(value = "tenant") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * jobId
         */
        @nbcp.db.Cn(value = "jobId") 
        public MongoColumnName jobId = new MongoColumnName("jobId");

        /**
         * 行号
         */
        @nbcp.db.Cn(value = "行号") 
        public MongoColumnName rowNumber = new MongoColumnName("rowNumber");

        /**
         * 部门路径
         */
        @nbcp.db.Cn(value = "部门路径") 
        public MongoColumnName path = new MongoColumnName("path");

        /**
         * 失败原因
         */
        @nbcp.db.Cn(value = "失败原因") 
        public MongoColumnName reason = new MongoColumnName("reason");

        /**
         * 失败部分
         */
        @nbcp.db.Cn(value = "失败部分") 
        public MongoColumnName failDep = new MongoColumnName("failDep");

        public MongoQueryClip<ExcelDeportmentErrorJobEntity, nancal.iam.db.mongo.entity.tenant.ExcelDeportmentErrorJob> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<ExcelDeportmentErrorJobEntity, nancal.iam.db.mongo.entity.tenant.ExcelDeportmentErrorJob> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<ExcelDeportmentErrorJobEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 部门导入数据(成功数据) (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "部门导入数据(成功数据)")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    public class ExcelDeportmentSuccessJobEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.ExcelDeportmentSuccessJob> {
        public String collectionName;
        public String databaseId;
        public ExcelDeportmentSuccessJobEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.ExcelDeportmentSuccessJob.class, "excelDeportmentSuccessJob", MyHelper.AsString(collectionName,"excelDeportmentSuccessJob"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public ExcelDeportmentSuccessJobEntity(){
            this("","");
        }
        

        /**
         * id
         */
        @nbcp.db.Cn(value = "id") 
        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * tenant
         */
        @nbcp.db.Cn(value = "tenant") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * jobId
         */
        @nbcp.db.Cn(value = "jobId") 
        public MongoColumnName jobId = new MongoColumnName("jobId");

        /**
         * 部门路径
         */
        @nbcp.db.Cn(value = "部门路径") 
        public MongoColumnName path = new MongoColumnName("path");

        public MongoQueryClip<ExcelDeportmentSuccessJobEntity, nancal.iam.db.mongo.entity.tenant.ExcelDeportmentSuccessJob> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<ExcelDeportmentSuccessJobEntity, nancal.iam.db.mongo.entity.tenant.ExcelDeportmentSuccessJob> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<ExcelDeportmentSuccessJobEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户管理员角色
     */
    @nbcp.db.Cn(value = "租户管理员角色")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"code"})})
    public class TenantAdminRoleEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.TenantAdminRole> {
        public String collectionName;
        public String databaseId;
        public TenantAdminRoleEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.TenantAdminRole.class, "tenantAdminRole", MyHelper.AsString(collectionName,"tenantAdminRole"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAdminRoleEntity(){
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

        public MongoQueryClip<TenantAdminRoleEntity, nancal.iam.db.mongo.entity.tenant.TenantAdminRole> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAdminRoleEntity, nancal.iam.db.mongo.entity.tenant.TenantAdminRole> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAdminRoleEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantAdminRoleEntity, nancal.iam.db.mongo.entity.tenant.TenantAdminRole> queryByCode(TenantAdminTypeEnum code) {
            return this.query().where( it-> it.code.match( code )) ;
        }

        public MongoDeleteClip<TenantAdminRoleEntity> deleteByCode(TenantAdminTypeEnum code) {
            return this.delete().where (it-> it.code.match( code ));
        }

        public MongoUpdateClip<TenantAdminRoleEntity, nancal.iam.db.mongo.entity.tenant.TenantAdminRole> updateByCode(TenantAdminTypeEnum code) {
            return this.update().where ( it-> it.code.match( code ));
        }

    }

    /**
     * 数据字典 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "数据字典")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"})})
    @nbcp.db.RemoveToSysDustbin
    public class TenantGroupDictEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.TenantGroupDict> {
        public String collectionName;
        public String databaseId;
        public TenantGroupDictEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.TenantGroupDict.class, "tenantGroupDict", MyHelper.AsString(collectionName,"tenantGroupDict"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantGroupDictEntity(){
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
         * 说明
         */
        @nbcp.db.Cn(value = "说明") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 类型
         */
        @nbcp.db.Cn(value = "类型") 
        public MongoColumnName group = new MongoColumnName("group");

        /**
         * 顺序
         */
        @nbcp.db.Cn(value = "顺序") 
        public MongoColumnName number = new MongoColumnName("number");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

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

        public MongoQueryClip<TenantGroupDictEntity, nancal.iam.db.mongo.entity.tenant.TenantGroupDict> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantGroupDictEntity, nancal.iam.db.mongo.entity.tenant.TenantGroupDict> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantGroupDictEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 资源组 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "资源组")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    public class TenantResourceGroupEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.tenant.TenantResourceGroup> {
        public String collectionName;
        public String databaseId;
        public TenantResourceGroupEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.tenant.TenantResourceGroup.class, "tenantResourceGroup", MyHelper.AsString(collectionName,"tenantResourceGroup"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantResourceGroupEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 组名
         */
        @nbcp.db.Cn(value = "组名") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 组编码
         */
        @nbcp.db.Cn(value = "组编码") 
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

        public MongoQueryClip<TenantResourceGroupEntity, nancal.iam.db.mongo.entity.tenant.TenantResourceGroup> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantResourceGroupEntity, nancal.iam.db.mongo.entity.tenant.TenantResourceGroup> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantResourceGroupEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "industry.id", nameField = "industry.name", refEntityClass = nancal.iam.db.mongo.entity.IndustryDict.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "apps.id", nameField = "apps.$.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"name"})})
    @nbcp.db.cache.RedisCacheDefine(value = {"id"})
    @nbcp.db.Cn(value = "租户")
    public class TenantEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.Tenant> {
        public String collectionName;
        public String databaseId;
        public TenantEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.Tenant.class, "tenant", MyHelper.AsString(collectionName,"tenant"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 组织名称
         */
        @nbcp.db.Cn(value = "组织名称") 
        public MongoColumnName name = new MongoColumnName("name");

        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 成立日期
         */
        @nbcp.db.Cn(value = "成立日期") 
        public MongoColumnName buildAt = new MongoColumnName("buildAt");

        /**
         * 行业
         */
        @nbcp.db.Cn(value = "行业") 
        public IdNameMeta industry = new IdNameMeta("industry");

        /**
         * 联系人
         */
        @nbcp.db.Cn(value = "联系人") 
        public MongoColumnName concatName = new MongoColumnName("concatName");

        /**
         * 联系电话
         */
        @nbcp.db.Cn(value = "联系电话") 
        public MongoColumnName concatPhone = new MongoColumnName("concatPhone");

        /**
         * 邮箱
         */
        @nbcp.db.Cn(value = "邮箱") 
        public MongoColumnName email = new MongoColumnName("email");

        /**
         * 企业地址
         */
        @nbcp.db.Cn(value = "企业地址") 
        public MongoColumnName address = new MongoColumnName("address");

        /**
         * 企业简介
         */
        @nbcp.db.Cn(value = "企业简介") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 企业密钥
         */
        @nbcp.db.Cn(value = "企业密钥") 
        public MongoColumnName secret = new MongoColumnName("secret");

        /**
         * 动态数据库地址
         */
        @nbcp.db.Cn(value = "动态数据库地址") 
        public MongoColumnName aloneDbConnection = new MongoColumnName("aloneDbConnection");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        /**
         * 网站地址
         */
        @nbcp.db.Cn(value = "网站地址") 
        public MongoColumnName siteUrl = new MongoColumnName("siteUrl");

        /**
         * 网站备案号
         */
        @nbcp.db.Cn(value = "网站备案号") 
        public MongoColumnName siteNumber = new MongoColumnName("siteNumber");

        /**
         * 所在城市
         */
        @nbcp.db.Cn(value = "所在城市") 
        public CityCodeNameMeta city = new CityCodeNameMeta("city");

        /**
         * 营业执照
         */
        @nbcp.db.Cn(value = "营业执照") 
        public BusinessLicenseDataMeta businessLicense = new BusinessLicenseDataMeta("businessLicense");

        /**
         * 徽标
         */
        @nbcp.db.Cn(value = "徽标") 
        public IdUrlMeta logo = new IdUrlMeta("logo");

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

        public MongoQueryClip<TenantEntity, nancal.iam.db.mongo.entity.Tenant> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantEntity, nancal.iam.db.mongo.entity.Tenant> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户管理员账号（废弃）
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "loginName", refEntityClass = nancal.iam.db.mongo.entity.TenantAdminUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "mobile", refEntityClass = nancal.iam.db.mongo.entity.TenantAdminUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "email", refEntityClass = nancal.iam.db.mongo.entity.TenantAdminUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "enabled", refEntityClass = nancal.iam.db.mongo.entity.TenantAdminUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"userId"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"email"})})
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "租户管理员账号（废弃）")
    public class TenantAdminLoginUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAdminLoginUser> {
        public String collectionName;
        public String databaseId;
        public TenantAdminLoginUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAdminLoginUser.class, "tenantAdminLoginUser", MyHelper.AsString(collectionName,"tenantAdminLoginUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAdminLoginUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 是否可用
         */
        @nbcp.db.Cn(value = "是否可用") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        /**
         * 登录错误次数
         */
        @nbcp.db.Cn(value = "登录错误次数") 
        public MongoColumnName errorLoginTimes = new MongoColumnName("errorLoginTimes");

        /**
         * 是否遗忘密码
         */
        @nbcp.db.Cn(value = "是否遗忘密码") 
        public MongoColumnName forget_password = new MongoColumnName("forget_password");

        /**
         * 上次更新密码时间
         */
        @nbcp.db.Cn(value = "上次更新密码时间") 
        public MongoColumnName lastUpdatePwd = new MongoColumnName("lastUpdatePwd");

        /**
         * 是否第一次登录
         */
        @nbcp.db.Cn(value = "是否第一次登录") 
        public MongoColumnName isFirstLogin = new MongoColumnName("isFirstLogin");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

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

        public MongoQueryClip<TenantAdminLoginUserEntity, nancal.iam.db.mongo.entity.TenantAdminLoginUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAdminLoginUserEntity, nancal.iam.db.mongo.entity.TenantAdminLoginUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAdminLoginUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantAdminLoginUserEntity, nancal.iam.db.mongo.entity.TenantAdminLoginUser> queryByLoginName(String loginName) {
            return this.query().where( it-> it.loginName.match( loginName )) ;
        }

        public MongoDeleteClip<TenantAdminLoginUserEntity> deleteByLoginName(String loginName) {
            return this.delete().where (it-> it.loginName.match( loginName ));
        }

        public MongoUpdateClip<TenantAdminLoginUserEntity, nancal.iam.db.mongo.entity.TenantAdminLoginUser> updateByLoginName(String loginName) {
            return this.update().where ( it-> it.loginName.match( loginName ));
        }


        public MongoQueryClip<TenantAdminLoginUserEntity, nancal.iam.db.mongo.entity.TenantAdminLoginUser> queryByUserId(String userId) {
            return this.query().where( it-> it.userId.match( userId )) ;
        }

        public MongoDeleteClip<TenantAdminLoginUserEntity> deleteByUserId(String userId) {
            return this.delete().where (it-> it.userId.match( userId ));
        }

        public MongoUpdateClip<TenantAdminLoginUserEntity, nancal.iam.db.mongo.entity.TenantAdminLoginUser> updateByUserId(String userId) {
            return this.update().where ( it-> it.userId.match( userId ));
        }

    }

    /**
     * 租户管理员用户（废弃） (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"email"})})
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.SortNumber(step = 10, field = "sort", groupBy = "")
    @nbcp.db.cache.RedisCacheDefine(value = {"id"})
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "租户管理员用户（废弃）")
    public class TenantAdminUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAdminUser> {
        public String collectionName;
        public String databaseId;
        public TenantAdminUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAdminUser.class, "tenantAdminUser", MyHelper.AsString(collectionName,"tenantAdminUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAdminUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 昵称
         */
        @nbcp.db.Cn(value = "昵称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 工号
         */
        @nbcp.db.Cn(value = "工号") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 发送密码方式
         */
        @nbcp.db.Cn(value = "发送密码方式") 
        public MongoColumnName sendPasswordType = new MongoColumnName("sendPasswordType");

        /**
         * 是否发送密码
         */
        @nbcp.db.Cn(value = "是否发送密码") 
        public MongoColumnName isSendPassword = new MongoColumnName("isSendPassword");

        public MongoColumnName sort = new MongoColumnName("sort");

        /**
         * 管理员类型
         */
        @nbcp.db.Cn(value = "管理员类型") 
        public MongoColumnName userAdminType = new MongoColumnName("userAdminType");

        public MongoColumnName enabled = new MongoColumnName("enabled");

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

        public MongoQueryClip<TenantAdminUserEntity, nancal.iam.db.mongo.entity.TenantAdminUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAdminUserEntity, nancal.iam.db.mongo.entity.TenantAdminUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAdminUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantAdminUserEntity, nancal.iam.db.mongo.entity.TenantAdminUser> queryByLoginName(String loginName) {
            return this.query().where( it-> it.loginName.match( loginName )) ;
        }

        public MongoDeleteClip<TenantAdminUserEntity> deleteByLoginName(String loginName) {
            return this.delete().where (it-> it.loginName.match( loginName ));
        }

        public MongoUpdateClip<TenantAdminUserEntity, nancal.iam.db.mongo.entity.TenantAdminUser> updateByLoginName(String loginName) {
            return this.update().where ( it-> it.loginName.match( loginName ));
        }

    }

    /**
     * 应用授权 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用授权")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.code", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.type", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.resource", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "auths.resourceId", nameField = "auths.$.remark", refEntityClass = nancal.iam.db.mongo.entity.TenantResourceInfo.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"type"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantAppAuthResourceInfoEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo> {
        public String collectionName;
        public String databaseId;
        public TenantAppAuthResourceInfoEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo.class, "tenantAppAuthResourceInfo", MyHelper.AsString(collectionName,"tenantAppAuthResourceInfo"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAppAuthResourceInfoEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 应用IdName
         */
        @nbcp.db.Cn(value = "应用IdName") 
        public CodeNameMeta appInfo = new CodeNameMeta("appInfo");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

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
         * 子部门是否授权，只在type=Dept时用
         */
        @nbcp.db.Cn(value = "子部门是否授权，只在type=Dept时用") 
        public MongoColumnName childDeptsAll = new MongoColumnName("childDeptsAll");

        /**
         * 是否系统默认
         */
        @nbcp.db.Cn(value = "是否系统默认") 
        public MongoColumnName isSysDefine = new MongoColumnName("isSysDefine");

        /**
         * 系统默认ID
         */
        @nbcp.db.Cn(value = "系统默认ID") 
        public MongoColumnName sysId = new MongoColumnName("sysId");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

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

        public MongoQueryClip<TenantAppAuthResourceInfoEntity, nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAppAuthResourceInfoEntity, nancal.iam.db.mongo.entity.TenantAppAuthResourceInfo> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAppAuthResourceInfoEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用扩展字段数据源字典 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用扩展字段数据源字典")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantAppExtendFieldDataSourceDictEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAppExtendFieldDataSourceDict> {
        public String collectionName;
        public String databaseId;
        public TenantAppExtendFieldDataSourceDictEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAppExtendFieldDataSourceDict.class, "tenantAppExtendFieldDataSourceDict", MyHelper.AsString(collectionName,"tenantAppExtendFieldDataSourceDict"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAppExtendFieldDataSourceDictEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 名称
         */
        @nbcp.db.Cn(value = "名称") 
        public MongoColumnName name = new MongoColumnName("name");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 编码
         */
        @nbcp.db.Cn(value = "编码") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 数据源
         */
        @nbcp.db.Cn(value = "数据源") 
        public MongoColumnName dataSource = new MongoColumnName("dataSource");

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

        public MongoQueryClip<TenantAppExtendFieldDataSourceDictEntity, nancal.iam.db.mongo.entity.TenantAppExtendFieldDataSourceDict> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAppExtendFieldDataSourceDictEntity, nancal.iam.db.mongo.entity.TenantAppExtendFieldDataSourceDict> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAppExtendFieldDataSourceDictEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户应用 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = true, unique = false, value = {"tenant.id"})})
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "remark", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "logo", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "industry", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "userType", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "url", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "ename", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appCode", nameField = "lable", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.Cn(value = "租户应用")
    public class TenantApplicationEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantApplication> {
        public String collectionName;
        public String databaseId;
        public TenantApplicationEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantApplication.class, "tenantApplication", MyHelper.AsString(collectionName,"tenantApplication"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantApplicationEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 中文应用名称
         */
        @nbcp.db.Cn(value = "中文应用名称") 
        public MongoColumnName name = new MongoColumnName("name");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 是否是开放系统
         */
        @nbcp.db.Cn(value = "是否是开放系统") 
        public MongoColumnName isOpen = new MongoColumnName("isOpen");

        /**
         * 启用true/禁用false
         */
        @nbcp.db.Cn(value = "启用true/禁用false") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        /**
         * 是否系统默认
         */
        @nbcp.db.Cn(value = "是否系统默认") 
        public MongoColumnName isSysDefine = new MongoColumnName("isSysDefine");

        /**
         * 系统默认ID
         */
        @nbcp.db.Cn(value = "系统默认ID") 
        public MongoColumnName sysId = new MongoColumnName("sysId");

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

        public MongoQueryClip<TenantApplicationEntity, nancal.iam.db.mongo.entity.TenantApplication> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantApplicationEntity, nancal.iam.db.mongo.entity.TenantApplication> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantApplicationEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 应用自定义字段 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "应用自定义字段")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantApplicationFieldExtendEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantApplicationFieldExtend> {
        public String collectionName;
        public String databaseId;
        public TenantApplicationFieldExtendEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantApplicationFieldExtend.class, "tenantApplicationFieldExtend", MyHelper.AsString(collectionName,"tenantApplicationFieldExtend"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantApplicationFieldExtendEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 字段name
         */
        @nbcp.db.Cn(value = "字段name") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 应用id
         */
        @nbcp.db.Cn(value = "应用id") 
        public MongoColumnName appCode = new MongoColumnName("appCode");

        /**
         * 字段code
         */
        @nbcp.db.Cn(value = "字段code") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 字段类型
         */
        @nbcp.db.Cn(value = "字段类型") 
        public MongoColumnName fieldType = new MongoColumnName("fieldType");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 字典项
         */
        @nbcp.db.Cn(value = "字典项") 
        public MongoColumnName dataSource = new MongoColumnName("dataSource");

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

        public MongoQueryClip<TenantApplicationFieldExtendEntity, nancal.iam.db.mongo.entity.TenantApplicationFieldExtend> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantApplicationFieldExtendEntity, nancal.iam.db.mongo.entity.TenantApplicationFieldExtend> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantApplicationFieldExtendEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户应用角色 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "租户应用角色")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.TenantApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"appInfo.code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"tenant.id", "appInfo.code", "name"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantAppRoleEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAppRole> {
        public String collectionName;
        public String databaseId;
        public TenantAppRoleEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAppRole.class, "tenantAppRole", MyHelper.AsString(collectionName,"tenantAppRole"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAppRoleEntity(){
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
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 是否系统默认
         */
        @nbcp.db.Cn(value = "是否系统默认") 
        public MongoColumnName isSysDefine = new MongoColumnName("isSysDefine");

        /**
         * 系统默认ID
         */
        @nbcp.db.Cn(value = "系统默认ID") 
        public MongoColumnName sysId = new MongoColumnName("sysId");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        /**
         * 不允许被删除(true：隐藏删除按钮；false：不隐藏删除按钮)
         */
        @nbcp.db.Cn(value = "不允许被删除(true：隐藏删除按钮；false：不隐藏删除按钮)") 
        public MongoColumnName notAllowDeleted = new MongoColumnName("notAllowDeleted");

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

        public MongoQueryClip<TenantAppRoleEntity, nancal.iam.db.mongo.entity.TenantAppRole> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAppRoleEntity, nancal.iam.db.mongo.entity.TenantAppRole> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAppRoleEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantAppRoleEntity, nancal.iam.db.mongo.entity.TenantAppRole> queryByTenantIdAppInfoCodeName(String tenantId,String appInfoCode,String name) {
            return this.query().where( it-> it.tenant.getId().match( tenantId )).where( it-> it.appInfo.getCode().match( appInfoCode )).where( it-> it.name.match( name )) ;
        }

        public MongoDeleteClip<TenantAppRoleEntity> deleteByTenantIdAppInfoCodeName(String tenantId,String appInfoCode,String name) {
            return this.delete().where (it-> it.tenant.getId().match( tenantId )).where (it-> it.appInfo.getCode().match( appInfoCode )).where (it-> it.name.match( name ));
        }

        public MongoUpdateClip<TenantAppRoleEntity, nancal.iam.db.mongo.entity.TenantAppRole> updateByTenantIdAppInfoCodeName(String tenantId,String appInfoCode,String name) {
            return this.update().where ( it-> it.tenant.getId().match( tenantId )).where ( it-> it.appInfo.getCode().match( appInfoCode )).where ( it-> it.name.match( name ));
        }

    }

    /**
     * 租户资源组授权 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "租户资源组授权")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantUserGroup.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "target.id", nameField = "target.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantDepartmentInfo.class)})
    public class TenantAuthResourceGroupEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAuthResourceGroup> {
        public String collectionName;
        public String databaseId;
        public TenantAuthResourceGroupEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAuthResourceGroup.class, "tenantAuthResourceGroup", MyHelper.AsString(collectionName,"tenantAuthResourceGroup"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAuthResourceGroupEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

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
         * 授权资源组
         */
        @nbcp.db.Cn(value = "授权资源组") 
        public AuthResourceGroupMeta auths = new AuthResourceGroupMeta("auths");

        /**
         * 子部门是否授权，只在type=Dept时用
         */
        @nbcp.db.Cn(value = "子部门是否授权，只在type=Dept时用") 
        public MongoColumnName heredity = new MongoColumnName("heredity");

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

        public MongoQueryClip<TenantAuthResourceGroupEntity, nancal.iam.db.mongo.entity.TenantAuthResourceGroup> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAuthResourceGroupEntity, nancal.iam.db.mongo.entity.TenantAuthResourceGroup> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAuthResourceGroupEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户应用授权规则 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "租户应用授权规则")
    public class TenantAuthRulesEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantAuthRules> {
        public String collectionName;
        public String databaseId;
        public TenantAuthRulesEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantAuthRules.class, "tenantAuthRules", MyHelper.AsString(collectionName,"tenantAuthRules"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantAuthRulesEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 条件因子名称
         */
        @nbcp.db.Cn(value = "条件因子名称") 
        public MongoColumnName conditionName = new MongoColumnName("conditionName");

        public MongoColumnName key = new MongoColumnName("key");

        /**
         * 是否系统默认
         */
        @nbcp.db.Cn(value = "是否系统默认") 
        public MongoColumnName isSysDefine = new MongoColumnName("isSysDefine");

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

        public MongoQueryClip<TenantAuthRulesEntity, nancal.iam.db.mongo.entity.TenantAuthRules> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantAuthRulesEntity, nancal.iam.db.mongo.entity.TenantAuthRules> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantAuthRulesEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 部门 (动态库)
     */
    @nbcp.db.Cn(value = "部门")
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "parent.id", nameField = "parent.name", refEntityClass = nancal.iam.db.mongo.entity.TenantDepartmentInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "roles.id", nameField = "roles.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "manager.id", nameField = "manager.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "allowApps.id", nameField = "allowApps.$.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "denyApps.id", nameField = "denyApps.$.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.SortNumber(step = 10, field = "sort", groupBy = "parent.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantDepartmentInfoEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantDepartmentInfo> {
        public String collectionName;
        public String databaseId;
        public TenantDepartmentInfoEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantDepartmentInfo.class, "tenantDepartmentInfo", MyHelper.AsString(collectionName,"tenantDepartmentInfo"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantDepartmentInfoEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 部门名称
         */
        @nbcp.db.Cn(value = "部门名称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 所属公司
         */
        @nbcp.db.Cn(value = "所属公司") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 上级部门
         */
        @nbcp.db.Cn(value = "上级部门") 
        public IdNameMeta parent = new IdNameMeta("parent");

        /**
         * 部门总人数
         */
        @nbcp.db.Cn(value = "部门总人数") 
        public MongoColumnName userCount = new MongoColumnName("userCount");

        /**
         * 角色
         */
        @nbcp.db.Cn(value = "角色") 
        public IdNameMeta roles = new IdNameMeta("roles");

        /**
         * 部门负责人
         */
        @nbcp.db.Cn(value = "部门负责人") 
        public IdNameMeta manager = new IdNameMeta("manager");

        /**
         * 电话
         */
        @nbcp.db.Cn(value = "电话") 
        public MongoColumnName phone = new MongoColumnName("phone");

        public CodeNameMeta allowApps = new CodeNameMeta("allowApps");

        public CodeNameMeta denyApps = new CodeNameMeta("denyApps");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        public MongoColumnName sort = new MongoColumnName("sort");

        /**
         * 层级
         */
        @nbcp.db.Cn(value = "层级") 
        public MongoColumnName level = new MongoColumnName("level");

        /**
         * 身份源
         */
        @nbcp.db.Cn(value = "身份源") 
        public MongoColumnName identitySource = new MongoColumnName("identitySource");

        /**
         * distinguishedName
         */
        @nbcp.db.Cn(value = "distinguishedName") 
        public MongoColumnName distinguishedName = new MongoColumnName("distinguishedName");

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

        public MongoQueryClip<TenantDepartmentInfoEntity, nancal.iam.db.mongo.entity.TenantDepartmentInfo> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantDepartmentInfoEntity, nancal.iam.db.mongo.entity.TenantDepartmentInfo> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantDepartmentInfoEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 部门自定义字段 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "部门自定义字段")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantDepartmentInfoFieldExtendEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantDepartmentInfoFieldExtend> {
        public String collectionName;
        public String databaseId;
        public TenantDepartmentInfoFieldExtendEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantDepartmentInfoFieldExtend.class, "tenantDepartmentInfoFieldExtend", MyHelper.AsString(collectionName,"tenantDepartmentInfoFieldExtend"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantDepartmentInfoFieldExtendEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 字段name
         */
        @nbcp.db.Cn(value = "字段name") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 字段code
         */
        @nbcp.db.Cn(value = "字段code") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 字段类型
         */
        @nbcp.db.Cn(value = "字段类型") 
        public MongoColumnName fieldType = new MongoColumnName("fieldType");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 字典项
         */
        @nbcp.db.Cn(value = "字典项") 
        public MongoColumnName dataSource = new MongoColumnName("dataSource");

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

        public MongoQueryClip<TenantDepartmentInfoFieldExtendEntity, nancal.iam.db.mongo.entity.TenantDepartmentInfoFieldExtend> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantDepartmentInfoFieldExtendEntity, nancal.iam.db.mongo.entity.TenantDepartmentInfoFieldExtend> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantDepartmentInfoFieldExtendEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 岗位字典 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "岗位字典")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"})})
    @nbcp.db.RemoveToSysDustbin
    public class TenantDutyDictEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantDutyDict> {
        public String collectionName;
        public String databaseId;
        public TenantDutyDictEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantDutyDict.class, "tenantDutyDict", MyHelper.AsString(collectionName,"tenantDutyDict"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantDutyDictEntity(){
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
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

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

        public MongoQueryClip<TenantDutyDictEntity, nancal.iam.db.mongo.entity.TenantDutyDict> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantDutyDictEntity, nancal.iam.db.mongo.entity.TenantDutyDict> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantDutyDictEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 扩展字段数据源字典 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "扩展字段数据源字典")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"})})
    @nbcp.db.RemoveToSysDustbin
    public class TenantExtendFieldDataSourceDictEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantExtendFieldDataSourceDict> {
        public String collectionName;
        public String databaseId;
        public TenantExtendFieldDataSourceDictEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantExtendFieldDataSourceDict.class, "tenantExtendFieldDataSourceDict", MyHelper.AsString(collectionName,"tenantExtendFieldDataSourceDict"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantExtendFieldDataSourceDictEntity(){
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
         * 数据源
         */
        @nbcp.db.Cn(value = "数据源") 
        public MongoColumnName dataSource = new MongoColumnName("dataSource");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

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

        public MongoQueryClip<TenantExtendFieldDataSourceDictEntity, nancal.iam.db.mongo.entity.TenantExtendFieldDataSourceDict> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantExtendFieldDataSourceDictEntity, nancal.iam.db.mongo.entity.TenantExtendFieldDataSourceDict> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantExtendFieldDataSourceDictEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 用户登录表
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "loginName", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "mobile", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "email", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "id", idField = "userId", nameField = "enabled", refEntityClass = nancal.iam.db.mongo.entity.TenantUser.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"userId"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id", "mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id", "email"})})
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.Cn(value = "用户登录表")
    public class TenantLoginUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantLoginUser> {
        public String collectionName;
        public String databaseId;
        public TenantLoginUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantLoginUser.class, "tenantLoginUser", MyHelper.AsString(collectionName,"tenantLoginUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantLoginUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 是否可用
         */
        @nbcp.db.Cn(value = "是否可用") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 登录错误次数
         */
        @nbcp.db.Cn(value = "登录错误次数") 
        public MongoColumnName errorLoginTimes = new MongoColumnName("errorLoginTimes");

        /**
         * 是否遗忘密码
         */
        @nbcp.db.Cn(value = "是否遗忘密码") 
        public MongoColumnName forget_password = new MongoColumnName("forget_password");

        /**
         * 上次修改密码时间
         */
        @nbcp.db.Cn(value = "上次修改密码时间") 
        public MongoColumnName lastUpdatePwdAt = new MongoColumnName("lastUpdatePwdAt");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        /**
         * 是否第一次登录
         */
        @nbcp.db.Cn(value = "是否第一次登录") 
        public MongoColumnName isFirstLogin = new MongoColumnName("isFirstLogin");

        /**
         * 密码的盐
         */
        @nbcp.db.Cn(value = "密码的盐") 
        public MongoColumnName passwordSalt = new MongoColumnName("passwordSalt");

        /**
         * 窗口期密码提醒次数-自动发送
         */
        @nbcp.db.Cn(value = "窗口期密码提醒次数-自动发送") 
        public MongoColumnName autoRemindPwdTimes = new MongoColumnName("autoRemindPwdTimes");

        /**
         * 窗口期密码提醒次数-手动动发送
         */
        @nbcp.db.Cn(value = "窗口期密码提醒次数-手动动发送") 
        public MongoColumnName manualRemindPwdTimes = new MongoColumnName("manualRemindPwdTimes");

        /**
         * 过期密码提醒次数-自动发送
         */
        @nbcp.db.Cn(value = "过期密码提醒次数-自动发送") 
        public MongoColumnName autoExpirePwdTimes = new MongoColumnName("autoExpirePwdTimes");

        /**
         * 过期密码提醒次数-手动发送
         */
        @nbcp.db.Cn(value = "过期密码提醒次数-手动发送") 
        public MongoColumnName manualExpirePwdTimes = new MongoColumnName("manualExpirePwdTimes");

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

        public MongoQueryClip<TenantLoginUserEntity, nancal.iam.db.mongo.entity.TenantLoginUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantLoginUserEntity, nancal.iam.db.mongo.entity.TenantLoginUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantLoginUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantLoginUserEntity, nancal.iam.db.mongo.entity.TenantLoginUser> queryByLoginName(String loginName) {
            return this.query().where( it-> it.loginName.match( loginName )) ;
        }

        public MongoDeleteClip<TenantLoginUserEntity> deleteByLoginName(String loginName) {
            return this.delete().where (it-> it.loginName.match( loginName ));
        }

        public MongoUpdateClip<TenantLoginUserEntity, nancal.iam.db.mongo.entity.TenantLoginUser> updateByLoginName(String loginName) {
            return this.update().where ( it-> it.loginName.match( loginName ));
        }


        public MongoQueryClip<TenantLoginUserEntity, nancal.iam.db.mongo.entity.TenantLoginUser> queryByUserId(String userId) {
            return this.query().where( it-> it.userId.match( userId )) ;
        }

        public MongoDeleteClip<TenantLoginUserEntity> deleteByUserId(String userId) {
            return this.delete().where (it-> it.userId.match( userId ));
        }

        public MongoUpdateClip<TenantLoginUserEntity, nancal.iam.db.mongo.entity.TenantLoginUser> updateByUserId(String userId) {
            return this.update().where ( it-> it.userId.match( userId ));
        }

    }

    /**
     * 租户应用资源 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "租户应用资源")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "name", refIdField = "appCode", idField = "appInfo.code", nameField = "appInfo.name", refEntityClass = nancal.iam.db.mongo.entity.TenantApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "id", nameField = "type", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "id", nameField = "name", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "id", nameField = "code", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "id", nameField = "resource", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "id", nameField = "action", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "id", nameField = "remark", refEntityClass = nancal.iam.db.mongo.entity.SysResourceInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "groups.id", nameField = "groups.$.name", refEntityClass = nancal.iam.db.mongo.entity.tenant.TenantResourceGroup.class)})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityIndexes(value = {})
    public class TenantResourceInfoEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantResourceInfo> {
        public String collectionName;
        public String databaseId;
        public TenantResourceInfoEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantResourceInfo.class, "tenantResourceInfo", MyHelper.AsString(collectionName,"tenantResourceInfo"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantResourceInfoEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public MongoColumnName name = new MongoColumnName("name");

        public IdNameMeta tenant = new IdNameMeta("tenant");

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
         * 是否系统默认
         */
        @nbcp.db.Cn(value = "是否系统默认") 
        public MongoColumnName isSysDefine = new MongoColumnName("isSysDefine");

        /**
         * 系统默认ID
         */
        @nbcp.db.Cn(value = "系统默认ID") 
        public MongoColumnName sysId = new MongoColumnName("sysId");

        /**
         * 资源组
         */
        @nbcp.db.Cn(value = "资源组") 
        public IdNameMeta groups = new IdNameMeta("groups");

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
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        public MongoColumnName type = new MongoColumnName("type");

        public MongoColumnName code = new MongoColumnName("code");

        public MongoColumnName resource = new MongoColumnName("resource");

        public MongoColumnName action = new MongoColumnName("action");

        public MongoColumnName dataAccessLevel = new MongoColumnName("dataAccessLevel");

        public MongoQueryClip<TenantResourceInfoEntity, nancal.iam.db.mongo.entity.TenantResourceInfo> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantResourceInfoEntity, nancal.iam.db.mongo.entity.TenantResourceInfo> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantResourceInfoEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户设置
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.RemoveToSysDustbin
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"tenant.id"})})
    @nbcp.db.Cn(value = "租户设置")
    public class TenantSecretSetEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantSecretSet> {
        public String collectionName;
        public String databaseId;
        public TenantSecretSetEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantSecretSet.class, "tenantSecretSet", MyHelper.AsString(collectionName,"tenantSecretSet"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantSecretSetEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 系统私钥
         */
        @nbcp.db.Cn(value = "系统私钥") 
        public MongoColumnName sysPrivateSecret = new MongoColumnName("sysPrivateSecret");

        /**
         * 企业公钥
         */
        @nbcp.db.Cn(value = "企业公钥") 
        public MongoColumnName publicSecret = new MongoColumnName("publicSecret");

        /**
         * 安全设置
         */
        @nbcp.db.Cn(value = "安全设置") 
        public TenantSettingMeta setting = new TenantSettingMeta("setting");

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

        public MongoQueryClip<TenantSecretSetEntity, nancal.iam.db.mongo.entity.TenantSecretSet> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantSecretSetEntity, nancal.iam.db.mongo.entity.TenantSecretSet> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantSecretSetEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantSecretSetEntity, nancal.iam.db.mongo.entity.TenantSecretSet> queryByTenantId(String tenantId) {
            return this.query().where( it-> it.tenant.getId().match( tenantId )) ;
        }

        public MongoDeleteClip<TenantSecretSetEntity> deleteByTenantId(String tenantId) {
            return this.delete().where (it-> it.tenant.getId().match( tenantId ));
        }

        public MongoUpdateClip<TenantSecretSetEntity, nancal.iam.db.mongo.entity.TenantSecretSet> updateByTenantId(String tenantId) {
            return this.update().where ( it-> it.tenant.getId().match( tenantId ));
        }

    }

    /**
     * 租户用户 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "租户用户")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "depts.id", nameField = "depts.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantDepartmentInfo.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "roles.id", nameField = "roles.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "groups.id", nameField = "groups.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantUserGroup.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "duty.id", nameField = "duty.name", refEntityClass = nancal.iam.db.mongo.entity.TenantDutyDict.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "personnelSecret.id", nameField = "personnelSecret.name", refEntityClass = nancal.iam.db.mongo.entity.tenant.TenantGroupDict.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "personnelSecret.id", nameField = "personnelSecret.code", refEntityClass = nancal.iam.db.mongo.entity.tenant.TenantGroupDict.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"code"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = true, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"email"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.SortNumber(step = 10, field = "sort", groupBy = "")
    @nbcp.db.cache.RedisCacheDefine(value = {"id"})
    public class TenantUserEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantUser> {
        public String collectionName;
        public String databaseId;
        public TenantUserEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantUser.class, "tenantUser", MyHelper.AsString(collectionName,"tenantUser"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantUserEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 昵称
         */
        @nbcp.db.Cn(value = "昵称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 地址
         */
        @nbcp.db.Cn(value = "地址") 
        public MongoColumnName address = new MongoColumnName("address");

        /**
         * 工号
         */
        @nbcp.db.Cn(value = "工号") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 部门
         */
        @nbcp.db.Cn(value = "部门") 
        public DeptDefineMeta depts = new DeptDefineMeta("depts");

        /**
         * 角色
         */
        @nbcp.db.Cn(value = "角色") 
        public IdNameMeta roles = new IdNameMeta("roles");

        /**
         * 用户组
         */
        @nbcp.db.Cn(value = "用户组") 
        public IdNameMeta groups = new IdNameMeta("groups");

        /**
         * 岗位
         */
        @nbcp.db.Cn(value = "岗位") 
        public IdNameMeta duty = new IdNameMeta("duty");

        /**
         * 发送密码方式
         */
        @nbcp.db.Cn(value = "发送密码方式") 
        public MongoColumnName sendPasswordType = new MongoColumnName("sendPasswordType");

        /**
         * 是否发送密码
         */
        @nbcp.db.Cn(value = "是否发送密码") 
        public MongoColumnName isSendPassword = new MongoColumnName("isSendPassword");

        /**
         * 入职时间
         */
        @nbcp.db.Cn(value = "入职时间") 
        public MongoColumnName goJobTime = new MongoColumnName("goJobTime");

        /**
         * 员工类型
         */
        @nbcp.db.Cn(value = "员工类型") 
        public MongoColumnName employeeType = new MongoColumnName("employeeType");

        /**
         * 员工状态
         */
        @nbcp.db.Cn(value = "员工状态") 
        public MongoColumnName employeeStatus = new MongoColumnName("employeeStatus");

        /**
         * 上级领导
         */
        @nbcp.db.Cn(value = "上级领导") 
        public IdNameMeta leader = new IdNameMeta("leader");

        /**
         * 排序
         */
        @nbcp.db.Cn(value = "排序") 
        public MongoColumnName sort = new MongoColumnName("sort");

        /**
         * 允许应用
         */
        @nbcp.db.Cn(value = "允许应用") 
        public CodeNameMeta allowApps = new CodeNameMeta("allowApps");

        /**
         * 拒绝应用
         */
        @nbcp.db.Cn(value = "拒绝应用") 
        public CodeNameMeta denyApps = new CodeNameMeta("denyApps");

        /**
         * 启用/停用
         */
        @nbcp.db.Cn(value = "启用/停用") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        /**
         * 身份源
         */
        @nbcp.db.Cn(value = "身份源") 
        public MongoColumnName identitySource = new MongoColumnName("identitySource");

        /**
         * AD中的标识字段DN
         */
        @nbcp.db.Cn(value = "AD中的标识字段DN") 
        public MongoColumnName distinguishedName = new MongoColumnName("distinguishedName");

        /**
         * 管理员类型
         */
        @nbcp.db.Cn(value = "管理员类型") 
        public MongoColumnName adminType = new MongoColumnName("adminType");

        /**
         * 人员密级
         */
        @nbcp.db.Cn(value = "人员密级") 
        public MongoColumnName personClassified = new MongoColumnName("personClassified");

        /**
         * 人员密级新字段
         */
        @nbcp.db.Cn(value = "人员密级新字段") 
        public IdCodeNameMeta personnelSecret = new IdCodeNameMeta("personnelSecret");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

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

        public MongoQueryClip<TenantUserEntity, nancal.iam.db.mongo.entity.TenantUser> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantUserEntity, nancal.iam.db.mongo.entity.TenantUser> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantUserEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

        public MongoQueryClip<TenantUserEntity, nancal.iam.db.mongo.entity.TenantUser> queryByLoginName(String loginName) {
            return this.query().where( it-> it.loginName.match( loginName )) ;
        }

        public MongoDeleteClip<TenantUserEntity> deleteByLoginName(String loginName) {
            return this.delete().where (it-> it.loginName.match( loginName ));
        }

        public MongoUpdateClip<TenantUserEntity, nancal.iam.db.mongo.entity.TenantUser> updateByLoginName(String loginName) {
            return this.update().where ( it-> it.loginName.match( loginName ));
        }

    }

    /**
     * 租户用户自定义字段 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "租户用户自定义字段")
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantUserFieldExtendEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantUserFieldExtend> {
        public String collectionName;
        public String databaseId;
        public TenantUserFieldExtendEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantUserFieldExtend.class, "tenantUserFieldExtend", MyHelper.AsString(collectionName,"tenantUserFieldExtend"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantUserFieldExtendEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 字段name
         */
        @nbcp.db.Cn(value = "字段name") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 字段code
         */
        @nbcp.db.Cn(value = "字段code") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 字段类型
         */
        @nbcp.db.Cn(value = "字段类型") 
        public MongoColumnName fieldType = new MongoColumnName("fieldType");

        /**
         * 备注
         */
        @nbcp.db.Cn(value = "备注") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 字典项
         */
        @nbcp.db.Cn(value = "字典项") 
        public MongoColumnName dataSource = new MongoColumnName("dataSource");

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

        public MongoQueryClip<TenantUserFieldExtendEntity, nancal.iam.db.mongo.entity.TenantUserFieldExtend> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantUserFieldExtendEntity, nancal.iam.db.mongo.entity.TenantUserFieldExtend> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantUserFieldExtendEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户用户组 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "租户用户组")
    @nbcp.db.DbEntityFieldRefs(value = {@nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "tenant.id", nameField = "tenant.name", refEntityClass = nancal.iam.db.mongo.entity.Tenant.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "roles.id", nameField = "roles.$.name", refEntityClass = nancal.iam.db.mongo.entity.TenantAppRole.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "allowApps.id", nameField = "allowApps.$.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class), @nbcp.db.DbEntityFieldRef(refNameField = "", refIdField = "", idField = "denyApps.id", nameField = "denyApps.$.name", refEntityClass = nancal.iam.db.mongo.entity.SysApplication.class)})
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"updateAt"}), @nbcp.db.DbEntityIndex(cacheable = true, unique = false, value = {"tenant.id"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"name"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.RemoveToSysDustbin
    public class TenantUserGroupEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantUserGroup> {
        public String collectionName;
        public String databaseId;
        public TenantUserGroupEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantUserGroup.class, "tenantUserGroup", MyHelper.AsString(collectionName,"tenantUserGroup"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantUserGroupEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 组名
         */
        @nbcp.db.Cn(value = "组名") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 组详情
         */
        @nbcp.db.Cn(value = "组详情") 
        public MongoColumnName remark = new MongoColumnName("remark");

        /**
         * 是否冻结组
         */
        @nbcp.db.Cn(value = "是否冻结组") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        public CodeNameMeta allowApps = new CodeNameMeta("allowApps");

        public CodeNameMeta denyApps = new CodeNameMeta("denyApps");

        /**
         * 角色
         */
        @nbcp.db.Cn(value = "角色") 
        public IdNameMeta roles = new IdNameMeta("roles");

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

        public MongoQueryClip<TenantUserGroupEntity, nancal.iam.db.mongo.entity.TenantUserGroup> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantUserGroupEntity, nancal.iam.db.mongo.entity.TenantUserGroup> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantUserGroupEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

    /**
     * 租户用户离职表 (动态库)
     */
    @org.springframework.data.mongodb.core.mapping.Document(language = "", collection = "", collation = "", value = "")
    @nbcp.db.DbEntityGroup(value = "tenant")
    @nbcp.db.Cn(value = "租户用户离职表")
    @nbcp.db.DbEntityIndexes(value = {@nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"loginName"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"mobile"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"email"}), @nbcp.db.DbEntityIndex(cacheable = false, unique = false, value = {"name"})})
    @nbcp.db.VarDatabase(value = "tenant.id")
    @nbcp.db.cache.RedisCacheDefine(value = {"id"})
    public class TenantUserLeaveEntity
        extends MongoBaseMetaCollection<nancal.iam.db.mongo.entity.TenantUserLeave> {
        public String collectionName;
        public String databaseId;
        public TenantUserLeaveEntity(String collectionName,String databaseId){
            super(nancal.iam.db.mongo.entity.TenantUserLeave.class, "tenantUserLeave", MyHelper.AsString(collectionName,"tenantUserLeave"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public TenantUserLeaveEntity(){
            this("","");
        }
        

        public MongoColumnName id = new MongoColumnName("_id");

        /**
         * 昵称
         */
        @nbcp.db.Cn(value = "昵称") 
        public MongoColumnName name = new MongoColumnName("name");

        /**
         * 地址
         */
        @nbcp.db.Cn(value = "地址") 
        public MongoColumnName address = new MongoColumnName("address");

        /**
         * 工号
         */
        @nbcp.db.Cn(value = "工号") 
        public MongoColumnName code = new MongoColumnName("code");

        /**
         * 租户
         */
        @nbcp.db.Cn(value = "租户") 
        public IdNameMeta tenant = new IdNameMeta("tenant");

        /**
         * 部门
         */
        @nbcp.db.Cn(value = "部门") 
        public DeptDefineMeta depts = new DeptDefineMeta("depts");

        /**
         * 角色
         */
        @nbcp.db.Cn(value = "角色") 
        public IdNameMeta roles = new IdNameMeta("roles");

        /**
         * 用户组
         */
        @nbcp.db.Cn(value = "用户组") 
        public IdNameMeta groups = new IdNameMeta("groups");

        /**
         * 岗位
         */
        @nbcp.db.Cn(value = "岗位") 
        public IdNameMeta duty = new IdNameMeta("duty");

        /**
         * 发送密码方式
         */
        @nbcp.db.Cn(value = "发送密码方式") 
        public MongoColumnName sendPasswordType = new MongoColumnName("sendPasswordType");

        /**
         * 是否发送密码
         */
        @nbcp.db.Cn(value = "是否发送密码") 
        public MongoColumnName isSendPassword = new MongoColumnName("isSendPassword");

        /**
         * 入职时间
         */
        @nbcp.db.Cn(value = "入职时间") 
        public MongoColumnName goJobTime = new MongoColumnName("goJobTime");

        /**
         * 员工类型
         */
        @nbcp.db.Cn(value = "员工类型") 
        public MongoColumnName employeeType = new MongoColumnName("employeeType");

        /**
         * 员工状态
         */
        @nbcp.db.Cn(value = "员工状态") 
        public MongoColumnName employeeStatus = new MongoColumnName("employeeStatus");

        /**
         * 上级领导
         */
        @nbcp.db.Cn(value = "上级领导") 
        public IdNameMeta leader = new IdNameMeta("leader");

        /**
         * 排序
         */
        @nbcp.db.Cn(value = "排序") 
        public MongoColumnName sort = new MongoColumnName("sort");

        /**
         * 允许应用
         */
        @nbcp.db.Cn(value = "允许应用") 
        public CodeNameMeta allowApps = new CodeNameMeta("allowApps");

        /**
         * 拒绝应用
         */
        @nbcp.db.Cn(value = "拒绝应用") 
        public CodeNameMeta denyApps = new CodeNameMeta("denyApps");

        /**
         * 启用/停用
         */
        @nbcp.db.Cn(value = "启用/停用") 
        public MongoColumnName enabled = new MongoColumnName("enabled");

        /**
         * 身份源
         */
        @nbcp.db.Cn(value = "身份源") 
        public MongoColumnName identitySource = new MongoColumnName("identitySource");

        /**
         * AD中的标识字段DN
         */
        @nbcp.db.Cn(value = "AD中的标识字段DN") 
        public MongoColumnName distinguishedName = new MongoColumnName("distinguishedName");

        /**
         * 管理员类型
         */
        @nbcp.db.Cn(value = "管理员类型") 
        public MongoColumnName adminType = new MongoColumnName("adminType");

        /**
         * 人员密级
         */
        @nbcp.db.Cn(value = "人员密级") 
        public MongoColumnName personClassified = new MongoColumnName("personClassified");

        /**
         * 是否逻辑删除(false:不删除、true:删除)
         */
        @nbcp.db.Cn(value = "是否逻辑删除(false:不删除、true:删除)") 
        public MongoColumnName isDeleted = new MongoColumnName("isDeleted");

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

        public MongoQueryClip<TenantUserLeaveEntity, nancal.iam.db.mongo.entity.TenantUserLeave> query(){
            return new MongoQueryClip(this);
        }
        
        public MongoUpdateClip<TenantUserLeaveEntity, nancal.iam.db.mongo.entity.TenantUserLeave> update(){
            return new MongoUpdateClip(this);
        }
        
        public MongoDeleteClip<TenantUserLeaveEntity> delete(){
            return new MongoDeleteClip(this);
        }
        

    }

}
