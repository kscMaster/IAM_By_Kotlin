package nancal.iam.db.mongo.entity

import nbcp.db.Cn
import nbcp.db.IdName


/**
 * Saas平台部门角色
 */
open class DeptDefine(
    @Cn("是否为主部门")
    var isMain: Boolean = false,
) : IdName()

