package nancal.iam.model

import nancal.iam.db.mongo.*


class DepItem {
    var base: MutableList<BaseServiceEnum> = mutableListOf()
    var services: MutableList<String> = mutableListOf()
    var extenals: MutableList<String> = mutableListOf()
}