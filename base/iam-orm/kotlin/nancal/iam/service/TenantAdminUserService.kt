package nancal.iam.service

import nbcp.comm.*
import nbcp.db.CodeName
import nbcp.db.IdName
import nancal.iam.db.mongo.*
import nbcp.db.mongo.*
import nancal.iam.db.mongo.entity.*
import nancal.iam.db.mongo.extend.*
import org.springframework.stereotype.Service

@Service
class TenantAdminUserService {
    fun getMyMenus(userId: String): ListResult<String> {
        var user = mor.tenant.tenantUser.queryById(userId).toEntity().must().elseThrow { "找不到用户" }

        if (user.adminType == TenantAdminTypeEnum.Super) {
            return ListResult<String>().withValue(user.adminType);
        }

        var role = mor.tenant.tenantAdminRole.queryByCode(user.adminType).toEntity();

        if (role == null || !role.menus.any()) {
            return ListResult.error("", -1)
        }

        return ListResult.of(role.menus).withValue(user.adminType)
    }
}
