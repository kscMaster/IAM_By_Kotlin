db = db.getSiblingDB("iam");
var tenantId = ObjectId("61deabfcf0dfe8182d67dc20");
var needDeleteTables = [

    "tenantAdminLoginUser",
    "tenantAdminUser",
    "tenantApplication",
    "tenantDepartmentInfo",
    "tenantUserGroup",
    "tenantUser",
    "tenantUserFieldExtend",
    "tenantDepartmentInfoFieldExtend",
    "tenantApplicationFieldExtend",
    "tenantSecretSet",
    "tenantResourceInfo",
    "tenantLoginUser",
    "tenantExtendFieldDataSourceDict",
    "tenantDutyDict",
    "tenantAppRole",
    "tenantAppExtendFieldDataSourceDict",
    "tenantAppAuthResourceInfo",


]
needDeleteTables.forEach(
    function (tableName) {
            print(tableName);
            db.getCollection(tableName).update(
                {'tenant._id': tenantId},{$set:{'isDeleted':true}},{multi :true}
            );
    }
)
//删除租户
db.getCollection("tenant").update(
    {'_id': tenantId}, {$set:{'isDeleted':true}}
);


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

