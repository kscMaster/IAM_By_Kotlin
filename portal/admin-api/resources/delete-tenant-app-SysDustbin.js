db = db.getSiblingDB("iam");
/**
 * 根据租户id删除租户的所有数据 （伪删除）
 */
var tenantIds = [
    ObjectId("61c5383a79f64528c1bce254"),
    ObjectId("61c53d8a79f64528c1bce264"),
    ObjectId("61c540cf79f64528c1bce274"),
    ObjectId("61c5782d8a5e370a58a6755b"),
    ObjectId("61c6c377f3c6823b172784b9"),
    ObjectId("61cb10d91156a21652698001")
]

tenantIds.forEach(
    function (tenantId) {


        /**
         * 1.删除租户数据 tenant
         */
        db.getCollection("tenant").find({"_id": tenantId}).forEach(
            function (tenant) {
                //1.1复制数据到 sysDustbin
                db.getCollection("sysDustbin").insert(
                    {
                        'table': "tenant",
                        "remark": "",
                        "creator": {
                            "_id": "",
                            "name": ""
                        },
                        "data": [
                            tenant
                        ],
                        "createAt": new Date()
                    }
                );
                //1.2删除租户数据
                printjson("删除 tenant")
                db.getCollection("tenant").deleteMany({"_id": tenantId});


            });
        /**
         * 2.删除租户管理员登录数据 tenantAdminLoginUser
         */
        var tenantAdminLoginUserdata = db.getCollection("tenantAdminLoginUser").find({"tenant._id": tenantId})
        if (tenantAdminLoginUserdata.hasNext()) {
            //插入租户管理员登录表回收站数据
            var resTenantAdminLoginUser = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantAdminLoginUser",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

//2.1复制数据到 sysDustbin
            db.getCollection("tenantAdminLoginUser").find({"tenant._id": tenantId}).forEach(
                function (tenantAdminLoginUser) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantAdminLoginUser.insertedId}, {$push: {"data": tenantAdminLoginUser}}
                    );
                });
//2.2删除租户管理员数据
            printjson("删除 tenantAdminLoginUser")
            db.getCollection("tenantAdminLoginUser").deleteMany({"tenant._id": tenantId});
        }


        /**
         * 3.删除租户管理员数据 tenantAdminUser
         */
        var tenantAdminUserdata = db.getCollection("tenantAdminUser").find({"tenant._id": tenantId})
        if (tenantAdminUserdata.hasNext()) {
            //插入租户管理员回收站数据
            var resTenantAdminUser = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantAdminUser",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );
//3.1复制数据到 sysDustbin
            db.getCollection("tenantAdminUser").find({"tenant._id": tenantId}).forEach(
                function (tenantAdminUser) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantAdminUser.insertedId}, {$push: {"data": tenantAdminUser}}
                    );
                });
//3.2删除租户管理员数据
            printjson("删除 tenantAdminUser")
            db.getCollection("tenantAdminUser").deleteMany({"tenant._id": tenantId});

        }


        /**
         * 4.删除租户应用数据 tenantApplication
         */

        var tenantApplicationdata = db.getCollection("tenantApplication").find({"tenant._id": tenantId})
        if (tenantApplicationdata.hasNext()) {
            //插入租户应用回收站数据
            var resTenantApplication = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantApplication",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );
//4.1复制数据到 sysDustbin
            db.getCollection("tenantApplication").find({"tenant._id": tenantId}).forEach(
                function (tenantApplication) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantApplication.insertedId}, {$push: {"data": tenantApplication}}
                    );
                });
//4.2删除租户应用数据
            printjson("删除 tenantApplication")
            db.getCollection("tenantApplication").deleteMany({"tenant._id": tenantId});
        }


        /**
         * 5.删除租户部门数据 tenantDepartmentInfo
         */
        var tenantDepartmentInfodata = db.getCollection("tenantDepartmentInfo").find({"tenant._id": tenantId})
        if (tenantDepartmentInfodata.hasNext()) {
            //插入租户部门回收站数据
            var resTenantDepartmentInfo = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantDepartmentInfo",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );
//5.1复制数据到 sysDustbin
            db.getCollection("tenantDepartmentInfo").find({"tenant._id": tenantId}).forEach(
                function (tenantDepartmentInfo) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantDepartmentInfo.insertedId}, {$push: {"data": tenantDepartmentInfo}}
                    );
                });
//5.2删除租户部门数据
            printjson("删除 tenantDepartmentInfo")
            db.getCollection("tenantDepartmentInfo").deleteMany({"tenant._id": tenantId});

        }


        /**
         * 6.删除租户用户组数据 tenantUserGroup
         */
        var tenantUserGroupdata = db.getCollection("tenantUserGroup").find({"tenant._id": tenantId})
        if (tenantUserGroupdata.hasNext()) {
            //插入租户用户组回收站数据
            var resTenantUserGroup = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantUserGroup",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );
//6.1复制数据到 sysDustbin
            db.getCollection("tenantUserGroup").find({"tenant._id": tenantId}).forEach(
                function (tenantUserGroup) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantUserGroup.insertedId}, {$push: {"data": tenantUserGroup}}
                    );
                });
//6.2删除租户用户组
            printjson("删除 tenantUserGroup")
            db.getCollection("tenantUserGroup").deleteMany({"tenant._id": tenantId});
        }


        /**
         * 7.删除租户用户数据 tenantUser
         */
        var tenantUserdata = db.getCollection("tenantUser").find({"tenant._id": tenantId})
        if (tenantUserdata.hasNext()) {

            var resTenantUser = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantUser",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );
//7.1复制数据到 sysDustbin
            db.getCollection("tenantUser").find({"tenant._id": tenantId}).forEach(
                function (tenantUser) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantUser.insertedId}, {$push: {"data": tenantUser}}
                    );
                });
//7.2删除租户用户
            printjson("删除 tenantUser")
            db.getCollection("tenantUser").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 8.删除租户用户扩展字段 tenantUserFieldExtend
         */
        var tenantUserFieldExtenddata = db.getCollection("tenantUserFieldExtend").find({"tenant._id": tenantId})
        if (tenantUserFieldExtenddata.hasNext()) {

            var resTenantUserFieldExtend = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantUserFieldExtend",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantUserFieldExtend").find({"tenant._id": tenantId}).forEach(
                function (tenantUserFieldExtend) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantUserFieldExtend.insertedId}, {$push: {"data": tenantUserFieldExtend}}
                    );
                });

            printjson("删除 tenantUserFieldExtend")
            db.getCollection("tenantUserFieldExtend").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 9.删除租户部门扩展字段 tenantDepartmentInfoFieldExtend
         */
        var tenantDepartmentInfoFieldExtenddata = db.getCollection("tenantDepartmentInfoFieldExtend").find({"tenant._id": tenantId})
        if (tenantDepartmentInfoFieldExtenddata.hasNext()) {

            var resTenantDepartmentInfoFieldExtend = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantDepartmentInfoFieldExtend",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantDepartmentInfoFieldExtend").find({"tenant._id": tenantId}).forEach(
                function (tenantDepartmentInfoFieldExtend) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantDepartmentInfoFieldExtend.insertedId}, {$push: {"data": tenantDepartmentInfoFieldExtend}}
                    );
                });

            printjson("删除 tenantDepartmentInfoFieldExtend")
            db.getCollection("tenantDepartmentInfoFieldExtend").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 10.删除租户应用扩展字段 tenantApplicationFieldExtend
         */
        var tenantApplicationFieldExtenddata = db.getCollection("tenantApplicationFieldExtend").find({"tenant._id": tenantId})
        if (tenantApplicationFieldExtenddata.hasNext()) {

            var resTenantApplicationFieldExtend = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantApplicationFieldExtend",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantApplicationFieldExtend").find({"tenant._id": tenantId}).forEach(
                function (tenantApplicationFieldExtend) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantApplicationFieldExtend.insertedId}, {$push: {"data": tenantApplicationFieldExtend}}
                    );
                });

            printjson("删除 tenantApplicationFieldExtend")
            db.getCollection("tenantApplicationFieldExtend").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 11.删除租户私密设置 tenantSecretSet
         */
        var tenantSecretSetdata = db.getCollection("tenantSecretSet").find({"tenant._id": tenantId})
        if (tenantSecretSetdata.hasNext()) {

            var resTenantSecretSet = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantSecretSet",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantSecretSet").find({"tenant._id": tenantId}).forEach(
                function (tenantSecretSet) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantSecretSet.insertedId}, {$push: {"data": tenantSecretSet}}
                    );
                });
            printjson("删除 tenantSecretSet")
            db.getCollection("tenantSecretSet").deleteMany({"tenant._id": tenantId});
        }
        /**
         * 12.删除租户资源 tenantResourceInfo
         */
        var tenantResourceInfodata = db.getCollection("tenantResourceInfo").find({"tenant._id": tenantId})
        if (tenantResourceInfodata.hasNext()) {

            var resTenantResourceInfodata = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantResourceInfo",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantResourceInfo").find({"tenant._id": tenantId}).forEach(
                function (tenantResourceInfo) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantResourceInfodata.insertedId}, {$push: {"data": tenantResourceInfo}}
                    );
                });

            printjson("删除 tenantResourceInfo")
            db.getCollection("tenantResourceInfo").deleteMany({"tenant._id": tenantId});
        }
        /**
         * 13.删除租户登录用户 tenantLoginUser
         */
        var tenantLoginUserdata = db.getCollection("tenantLoginUser").find({"tenant._id": tenantId})
        if (tenantLoginUserdata.hasNext()) {

            var resTenantLoginUser = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantLoginUser",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantLoginUser").find({"tenant._id": tenantId}).forEach(
                function (tenantLoginUser) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantLoginUser.insertedId}, {$push: {"data": tenantLoginUser}}
                    );
                });

            printjson("删除 tenantLoginUser")
            db.getCollection("tenantLoginUser").deleteMany({"tenant._id": tenantId});
        }
        /**
         * 14.删除租户扩展字段数据源表 tenantExtendFieldDataSourceDict
         */
        var tenantExtendFieldDataSourceDictdata = db.getCollection("tenantExtendFieldDataSourceDict").find({"tenant._id": tenantId})
        if (tenantExtendFieldDataSourceDictdata.hasNext()) {

            var resTenantExtendFieldDataSourceDict = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantExtendFieldDataSourceDict",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantExtendFieldDataSourceDict").find({"tenant._id": tenantId}).forEach(
                function (tenantExtendFieldDataSourceDict) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantExtendFieldDataSourceDict.insertedId}, {$push: {"data": tenantExtendFieldDataSourceDict}}
                    );
                });

            printjson("删除 tenantExtendFieldDataSourceDict")
            db.getCollection("tenantExtendFieldDataSourceDict").deleteMany({"tenant._id": tenantId});
        }
        /**
         * 15.删除租户职责 tenantDutyDict
         */
        var tenantDutyDictdata = db.getCollection("tenantDutyDict").find({"tenant._id": tenantId})
        if (tenantDutyDictdata.hasNext()) {

            var restenantDutyDict = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantDutyDict",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantDutyDict").find({"tenant._id": tenantId}).forEach(
                function (tenantDutyDict) {
                    db.getCollection("sysDustbin").update(
                        {'_id': restenantDutyDict.insertedId}, {$push: {"data": tenantDutyDict}}
                    );
                });

            printjson("删除 tenantDutyDict")
            db.getCollection("tenantDutyDict").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 16.删除租户tenantAppRole tenantAppRole
         */
        var tenantAppRoledata = db.getCollection("tenantAppRole").find({"tenant._id": tenantId})
        if (tenantAppRoledata.hasNext()) {

            var resTenantAppRole = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantAppRole",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantAppRole").find({"tenant._id": tenantId}).forEach(
                function (tenantAppRole) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantAppRole.insertedId}, {$push: {"data": tenantAppRole}}
                    );
                });

            printjson("删除 tenantAppRole")
            db.getCollection("tenantAppRole").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 16.删除租户应用扩展字段数据源表 tenantAppExtendFieldDataSourceDict
         */
        var tenantAppExtendFieldDataSourceDictdata = db.getCollection("tenantAppExtendFieldDataSourceDict").find({"tenant._id": tenantId})
        if (tenantAppExtendFieldDataSourceDictdata.hasNext()) {

            var resTenantAppExtendFieldDataSourceDict = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantAppExtendFieldDataSourceDict",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantAppExtendFieldDataSourceDict").find({"tenant._id": tenantId}).forEach(
                function (tenantAppExtendFieldDataSourceDict) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantAppExtendFieldDataSourceDict.insertedId}, {$push: {"data": tenantAppExtendFieldDataSourceDict}}
                    );
                });

            printjson("删除 tenantAppExtendFieldDataSourceDict")
            db.getCollection("tenantAppExtendFieldDataSourceDict").deleteMany({"tenant._id": tenantId});
        }

        /**
         * 18.删除租户授权数据 tenantAppAuthResourceInfo
         */
        var tenantAppAuthResourceInfodata = db.getCollection("tenantAppAuthResourceInfo").find({"tenant._id": tenantId})
        if (tenantAppAuthResourceInfodata.hasNext()) {

            var resTenantAppAuthResourceInfo = db.getCollection("sysDustbin").insertOne(
                {
                    'table': "tenantAppAuthResourceInfo",
                    "remark": "",
                    "creator": {
                        "_id": "",
                        "name": ""
                    },
                    "data": [],
                    "createAt": new Date()
                }
            );

            db.getCollection("tenantAppAuthResourceInfo").find({"tenant._id": tenantId}).forEach(
                function (tenantAppAuthResourceInfo) {
                    db.getCollection("sysDustbin").update(
                        {'_id': resTenantAppAuthResourceInfo.insertedId}, {$push: {"data": tenantAppAuthResourceInfo}}
                    );
                });

            printjson("删除 tenantAppAuthResourceInfo")
            db.getCollection("tenantAppAuthResourceInfo").deleteMany({"tenant._id": tenantId});
        }


    }
)


//查数据
//db = db.getSiblingDB("iam");
//db.getCollection("sysDustbin").find( { "data" : {$elemMatch: { "_id":ObjectId("61c1c6c66a9bd029df7d2de5")} }})

//删除数据
// db = db.getSiblingDB("iam");
//db.getCollection("sysDustbin").remove({ "data" : {$elemMatch: { "_id":ObjectId("61c1c6c66a9bd029df7d2de5")} }})
//查询打印
/*
     db = db.getSiblingDB("iam");
    var ss=db.getCollection("sysDustbin").find({"_id":ObjectId("61dd3cbbd055c7e5b7794bd4")}).forEach(
    ss=>
    printjson(ss)
    )
    */

//查询  接返回值
/*db = db.getSiblingDB("iam");
var data=db.getCollection("tenantApplication").find({"tenant._id": ObjectId("61cbe7cc6142fc69b0c94725")})
while (data.hasNext()) {
    printjson(data.next());

}*/

