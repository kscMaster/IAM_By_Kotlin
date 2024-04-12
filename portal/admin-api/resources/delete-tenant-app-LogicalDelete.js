db = db.getSiblingDB("iam");
var tenantId = ObjectId("61deabfcf0dfe8182d67dc20");
var appCode = "zao-23e9339qlurs514w";

/**
 * 删除 授权给部门 用户组  用户的角色
 */

var needDeleteAppRoleTables = [
    "tenantUserGroup",
    "tenantUser",
    "tenantDepartmentInfo"
]
db.getCollection("tenantAppRole").find({"tenant._id": tenantId,"appInfo.code":appCode}).forEach(
    function(role){
        printjson(role);
        needDeleteAppRoleTables.forEach(
            function (tableName) {
                print(tableName);
                db.getCollection(tableName).update(
                    {'tenant._id':tenantId},{$pull:{roles:{"_id":role._id}}},{multi :true}
                );
            }
        )


    }
)


/**
 * 逻辑删除
 */
var needDeleteTables = [
    "tenantApplication",
    "tenantResourceInfo",
    "tenantAppRole",
    "tenantAppAuthResourceInfo"
]
needDeleteTables.forEach(
    function (tableName) {
        print(tableName);
        if(tableName=="tenantApplication"){
            db.getCollection(tableName).update(
                {'tenant._id': tenantId},{$set:{'isDeleted':true}},{multi :true}
            );
        }else{
            db.getCollection(tableName).update(
                {'tenant._id': tenantId,"appInfo.code":appCode},{$set:{'isDeleted':true}},{multi :true}
            );
        }

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
        print(tableName);
        db.getCollection(tableName).update(
            {'tenant._id':tenantId},{$pull:{allowApps:{"code":appCode}}},{multi :true}
        );
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

