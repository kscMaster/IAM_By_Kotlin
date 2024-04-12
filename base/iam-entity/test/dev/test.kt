package dev

import nancal.iam.TestBase
import nbcp.comm.FromJson
import nbcp.comm.ToJson
import nancal.iam.db.mongo.entity.*
import org.junit.jupiter.api.Test


class test : TestBase() {

    @Test
    fun test1() {
        var role = SysAppRole();
        var resource = ResourceBaseInfo()

        println(role.ToJson())
        println(role.ToJson().FromJson<SysAppRole>())
    }
}