db = db.getSiblingDB("iam");
var appCode = "zao-23e9339qlurs514w";

/**
 * 删除 授权给部门 用户组  用户的角色
 */

var needDeleteAppRoleTables = [
    "tenantUserGroup",
    "tenantUser",
    "tenantDepartmentInfo"
]
db.getCollection("tenantAppRole").find({"appInfo.code":appCode}).forEach(
    function(role){
        printjson(role);
        needDeleteAppRoleTables.forEach(
            function (tableName) {
                printjson("移除授权给的角色" + "【"+tableName+"】");
                db.getCollection(tableName).update(
                    {},{$pull:{roles:{"_id":role._id}}},{multi :true}
                );
            }
        )


    }
)
/**
 * 删除租户应用 授权给部门 用户组  用户的应用
 */
var needDeleteAppTables = [
    "tenantUserGroup",
    "tenantUser",
    "tenantDepartmentInfo"
]
needDeleteAppTables.forEach(
    function (tableName) {
        printjson("移除授权给的应用" + "【"+tableName+"】");
        db.getCollection(tableName).update(
            {},{$pull:{allowApps:{"code":appCode}}},{multi :true}
        );
    }
)


/**
 * 逻辑删除租户应用
 */
var needDeleteTables = [
    "tenantApplication",
    "tenantResourceInfo",
    "tenantAppRole",
    "tenantAppAuthResourceInfo"
]

needDeleteTables.forEach(
    function(tableName){
        if(tableName=="tenantApplication"){
            var data=db.getCollection(tableName).find({"appCode":appCode})
            if (data.hasNext()) {
                //插入回收站数据
                var res = db.getCollection("sysDustbin").insertOne(
                    {
                        'table': tableName,
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
                db.getCollection(tableName).find({"appCode":appCode}).forEach(
                    function (ent) {
                        db.getCollection("sysDustbin").update(
                            {'_id': res.insertedId}, {$push: {"data": ent}}
                        );
                    });
                //2.2删除数据
                printjson("移除租户应用" + "【"+tableName+"】");
                db.getCollection(tableName).deleteMany({"appCode":appCode});
            }
        }else{
            var data=db.getCollection(tableName).find({"appInfo.code":appCode})
            if (data.hasNext()) {
                //插入回收站数据
                var res = db.getCollection("sysDustbin").insertOne(
                    {
                        'table': tableName,
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
                db.getCollection(tableName).find({"appInfo.code":appCode}).forEach(
                    function (ent) {
                        db.getCollection("sysDustbin").update(
                            {'_id': res.insertedId}, {$push: {"data": ent}}
                        );
                    });
                //2.2删除数据
                printjson("删除租户应用下的数据" + "【"+tableName+"】");
                db.getCollection(tableName).deleteMany({"appInfo.code":appCode});
            }

        }


    }
)

/**
 * 删除boss侧应用
 */
var needDeleteBossAppTables = [
    "sysApplication",
    "sysAppRole",
    "sysAppAuthResource"

]


needDeleteBossAppTables.forEach(
    function (tableName) {
        print(tableName);
        if(tableName=="sysApplication"){
            var data=db.getCollection(tableName).find({"appCode":appCode})
            if (data.hasNext()) {
                //插入回收站数据
                var res = db.getCollection("sysDustbin").insertOne(
                    {
                        'table': tableName,
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
                db.getCollection(tableName).find({"appCode":appCode}).forEach(
                    function (ent) {
                        db.getCollection("sysDustbin").update(
                            {'_id': res.insertedId}, {$push: {"data": ent}}
                        );
                    });
                //2.2删除数据
                printjson("删除BOSS侧应用" + "【"+tableName+"】");
                db.getCollection(tableName).deleteMany({"appCode":appCode});
            }


        }else{
            var data=db.getCollection(tableName).find({"appInfo.code":appCode})
            if (data.hasNext()) {
                //插入回收站数据
                var res = db.getCollection("sysDustbin").insertOne(
                    {
                        'table': tableName,
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
                db.getCollection(tableName).find({"appInfo.code":appCode}).forEach(
                    function (ent) {
                        db.getCollection("sysDustbin").update(
                            {'_id': res.insertedId}, {$push: {"data": ent}}
                        );
                    });
                //2.2删除数据
                printjson("删除BOSS侧应用关联的其他数据" + "【"+tableName+"】");
                db.getCollection(tableName).deleteMany({"appInfo.code":appCode});
            }
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

