# Admin 
>开发环境

- JDK 1.8
- Intellij IDEA
- Kotlin
- Maven
- Python3
>Intellij IDEA 插件

    Kotlin
    Maven Helper
    Lombok

## 编译

    python install_jar.py
    mvn-jar.cmd -f admin

## nacos

* data-id: admin-api.yml : 装载 application.yml
* data-id: admin-api-win.yml : 装载 application-win.yml

#开发规范
##命名
    扩展方法名，属性名使用大驼峰命名法。
##Jsonp格式
    使用 JsonpMapping 代替 RequestMapping
    Swaager参数注解：
        @ApiParam 默认会使paramType是 body,需要额外提供  注解
        或者使用 @ApiImplicitParams 注解，指定 paramType = "query"

##权限设计

### 服务器端接口权限

* 类或接口使用 OpenAction 注解表示开放接口。
* 类或接口使用 RoleAction("admin",...)表示允许 LoginUserModel.roles 中哪些角色访问接口。
* 可以把角色 理解为 模块，即RoleAction("product")表示 product模块， LoginUserModel.roles=arrayOf("product")表示该用户拥有product模块。
* 角色区分大小写。

### Vue前端权限

    编辑页面，能通过统一的参数，使成为只读页面。如：
    /product/edit/1 是编辑页面
    /product/edit/1?ro=1 是只读页面(readonly),用户可以通过去除ro,使页面出现按钮，所以需要在服务器端接口进行权限的校验。

    前端的权限控制在 router.js 即路由跳转的地方控制。
    
    权限定义如下：
        页面URL，     是否只读，   add,edit,delete
    页面URL中， 星号表示任意一级目录，两个星号表示任意一级目录。如：
        /corpInfo/*
        /corpInfo/**
        优先使用精确匹配，其次是一颗星，最后是两颗星， 每个级别中URL长度最长的优先。
    金维度权限字典：
        permissions:{
                    "/corpInfo/**":["add","edit","delete"]
                    ,...
             },
        menus:{ id:1,name:"基本信息",url:"/corpInfo/index",subMenus:[] }
        //menus权限，即控制菜单的显示，也参与页面的打开权限。
    角色可以拥有权限表示如下：
        permissions:{
             "~/corpInfo/**":["add","edit"]
            ,"!/productInfo/**":["delete"]
            ,...
        },
        menus:"1,2,3,15,29,39,92"
        其中： !叹号表示只读。 ~波浪号表示可操作
        



                      