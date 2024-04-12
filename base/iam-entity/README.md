# nbcp-entity项目介绍
    是品致信产品的实体层。包含以下实体：
        MongoDB
        Redis
        

## 依赖
    nbcp 和 dev8 两个Java项目都依赖 pzx_entity
    
# 使用方式
   
## linux
    在当前目录下执行： install_jar.sh
   
## windows
    在当前目录下执行：
    python install_jar.py

# Mongo实体添加流程
    1. 在 nbcp.db.mongo.entity 包下添加 Mongo实体。
    2. 实体要求使用 @Document注解，和 DbEntityGroup 注解. 必须继承 IMongoDocument (关键是 id 字段)
    3. 执行 test/dev/tool.TestGenerateMoer 方法.
    4. mor 中添加该实例.
    5. 执行 install_jar.py 打包
    6. 签入代码。

## 实体规范
- 金额的单位是分， 数据类型是Int
- 时间：数据类型是 LocalDate , LocalDateTime (Long是备选，目前不用)
- id：主键，ObjectId格式的 String类型.（MySql用自定义短Id，为了分布式）
- name：名称
- code：编码
- remark：备注
- createAt： 创建时间
- createBy: 创建人

## 引用实体
引用实体是 data class,可序列化,不能被继承.

比如:订单表使用产品信息,企业信息,购买者的信息.

* 大部分情况,可以使用 IdName 表示企业信息
* 产品信息需要 Id,Name,Url, 可以单独建一个类.
* 购买者要引用更多的字段, 可以建立 引用数据实体 WxUserInfoModel . 引用实体做为轻量级卡片,被其它实体引用. WxAppUser 对外提借供 toWxUserInfo 方法.
    注意: 不能让 WxAppUser 继承 WxUserInfoModel, 因为如果这样, order.createBy:WxUserInfoModel = WxAppUser()的时候, createBy会是 WxAppUser 的信息.
* 

## Mongo实体
    所有Mongo实体，都有 @Document 注解,IMongoDocument接口 @Id var id:String 属性。
    内嵌实体没有 @Document 注解.

# mor使用语法

## Mongo实体查询语法：
    var list = mor.组名.实体名.query()
       .where{it.列名  match 值}
       .where{it.列名  match_like 值}
       .where{it.列名  match_ne 值}
       .where{it.列名  match_gte 值}
       .select{it.实体名.列名}
       .select{it.实体名.列名}
       .limit(跳过行数,获取行数)
       .orderBy(是否正序，mor.组名.实体名.列名)
       .toList(返回的实体类型)
    
    其中：
        查询实体的where条件，使用 and 进行连接。 
        match 表示相等 
        match_like 表示使用正则表达式的方式进行模糊匹配
        match_ne 表示不相等 
        match_gte 表示大于等于
        match_lte 表示小于等于
        
    或者：
    var entity = mor.组件.实体名.findById( Id字符串 或 ObjectId类型)
    或者:
    var entity = mor.组件.实体名.FindFirst( where条件 )
    
## Mongo更新
    var n = mor.组名.实体名.update()
           .where( where条件 ) 
           .set{it.列名 , 值}
           .unset(mor.组名.实体名.列名 )
           .push(mor.组名.实体名.数组的列名 , 添加到数组的值 )
           .pull(mor.组名.实体名.数组的列名 , 删除数组的条件 )
           .exec();
    
    其中:
        unset 表示把该列置为 null
        push是向数组添加一项.
        pull是从数组中移除一项.
    
        也可以使用 mor.affectRowCount 表示影响的行数,来替代变量n
    或者:
    var n = mor.组件.实体名.updateById( Id字符串 或 ObjectId类型 )
   
## Mongo删除
    var n = mor.组件.实体名.deleteById( Id字符串 或 ObjectId类型 )
    
    
    或者:
    var n = mor.组件.实体名.delete( where条件 , where条件 )
    
## Mongo添加:
     mor.组件.实体名.insert( 实体 )
     mor.组件.实体名.insertAll( 实体集合 )

## 注意事项:
    - 不要使用 save 方法，save方法有两个问题：
        1. 需要先查后保存。 
        2.并发问题。保存时别人可能已经修改了数据。


# mysql 表
```sql

CREATE SCHEMA `shop` DEFAULT CHARACTER SET utf8mb4 ;

```

* s_city
```sql

CREATE TABLE  `s_city` (
  `code` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL DEFAULT '',
  `fullName` VARCHAR(45) NOT NULL DEFAULT '',
  `level` INT NOT NULL DEFAULT 0,
  `lng` FLOAT NOT NULL DEFAULT 0,
  `lat` FLOAT NOT NULL DEFAULT 0,
  `pinyin` VARCHAR(45) NOT NULL DEFAULT '',
  `telCode` VARCHAR(45) NOT NULL DEFAULT '',
  `postCode` VARCHAR(45) NOT NULL DEFAULT '',
  `pcode` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`code`));

INSERT INTO  `s_city` (`code`, `name`, `fullName`, `level`, `lng`, `lat`, `pinyin`, `telCode`, `postCode`, `pcode`) VALUES ('110', '北京', '北京市', 0, 0,0, 'beijing', '110', '110',0);


CREATE TABLE `s_corp` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `city_code` int(11) DEFAULT NULL,
  `remark` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `f_city_idx` (`city_code`),
  CONSTRAINT `f_city` FOREIGN KEY (`city_code`) REFERENCES `s_city` (`code`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `s_user` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `corp_id` int(11) DEFAULT NULL,
  `remark` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `f_corp_idx` (`corp_id`),
  CONSTRAINT `f_corp` FOREIGN KEY (`corp_id`) REFERENCES `s_corp` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


```


# 生成实体

```sql

 

SELECT
concat('data class  ' , TABLE_NAME , '(', GROUP_CONCAT( 

   concat ( '\r\n    var ' , Column_Name , ': ' , 
	   	case 
		   when  data_type = 'int' then 'Int' 
		   when data_type = 'varchar' then 'String'
		   when data_type = 'date' then 'LocalDate'
		   when data_type = 'datetime' then 'LocalDateTime'
		   when data_type = 'bit' then 'Boolean'
		   else data_type
	    end 
     , ' =' , 
	   	case 
		   when  data_type = 'int' then '0' 
		   when data_type = 'varchar' then '""'
		    when data_type = 'date' then 'LocalDate.now()'
		   when data_type = 'datetime' then 'LocalDateTime.now()'
		   when data_type = 'bit' then 'false'
		   else ''
	    end 
	)

 ) , '\r\n) '  ) as txt
FROM INFORMATION_SCHEMA. COLUMNS
where table_schema = 'dev8' and table_name   like 'u$_%' escape '$'
group by table_name

```