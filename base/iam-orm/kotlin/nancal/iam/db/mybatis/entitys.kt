//package xmz.db.mybatis
//
//import java.util.*
//
///**
// * Created by udi on 2017.3.6.
// */
//
//data class SysAnnex( var id:Int=0, var uid:String="", var localPath:String="", var name:String="", var ext:String="", var size:Int=0, var createAt:Date=Date(), var createBy:Int=0, var checkCode:String="", var errorMsg:String="", var url:String="") {}
//data class SysCity( var id:Int=0, var code:String="", var name:String="", var shortName:String="", var x:Float=0F, var y:Float=0F, var cityId:Int=0, var cityName:String="", var cityPy:String="", var firstLetter:String="", var remark:String="", var createAt:Date=Date(), var createBy:String="", var updateAt:Date=Date(), var updateBy:String="") {}
//data class SCorpUser( var userId:Int=0, var loginName:String="", var passwd:String="", var lastLoginAt:Date=Date(), var errorLoginTimes:Byte=0, var systemTempPasswd:String="", var locked:Boolean=false, var lockedRemark:String="", var corpId:Int=0) {}
//data class Corporation( var id:Int=0, var uid:String="", var name:String="", var address:String="", var createAt:Date=Date(), var phone:String="", var cityId:Int=0, var logoId:Int=0, var detail:String="", var status:Int=0, var locked:Boolean=false, var lockedRemark:String="") {}
//data class SysLoginUser( var userId:Int=0, var loginName:String="", var passwd:String="", var lastLoginAt:Date=Date(), var errorLoginTimes:Byte=0, var systemTempPasswd:String="", var locked:Boolean=false, var lockedRemark:String="") {}
//data class SysRole( var id:Int=0, var name:String="", var remark:String="", var urlPowers:String="", var createAt:Date=Date()) {}
//data class SSecurityUrl( var id:Int=0, var url:String="") {}
//data class CorpUser( var id:Int=0, var uid:String="", var loginName:String="", var name:String="", var logoId:Int=0, var score:Int=0, var birthday:Date=Date(), var idCardNumber:String="", var email:String="", var sex:Byte=0, var mobile:String="", var createAt:Date=Date(), var city:Int=0, var qq:String="", var validateCode:String="") {}
//data class SUserRole( var id:Int=0, var userId:Int=0, var roleId:Int=0, var createAt:Int=0) {}
//
//
//
//
//
