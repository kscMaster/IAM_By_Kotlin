
# 实体定义文档

## 使用的枚举

#### MobileCodeModuleEnum
* None : 无
* Registe : 注册
* ChangeMobile : 更换手机
* ForgetPassword : 找回密码
* BindBankCard : 绑定银行卡

#### NotifyTypeEnum
* NeedPay
* Payed

#### UserSexEnum
* Male : 男
* Female : 女

#### AppClientEnum
* Android
* Ios



## 关于嵌入的实体（非集合实体，是集合引用到的实体）


#### IdName
| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| id | String |   |
| name | String |   |

#### CodeName
| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| code | String |   |
| name | String |   |

#### IdUrl
| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| id | String |   |
| img256FullUrl | String |   |
| fullUrl | String |   |
| url | String |   |


### System 模块




#### 集合 mobileCodeLog

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| module | MobileCodeModuleEnum |   |
| mobile | String |   |
| code | String |   |
| bizId | String |   |
| errorMessage | String |   |
| isUsed | boolean |   |
| createAt | LocalDateTime |   |
| sentAt | LocalDateTime |   |
| arrivedAt | LocalDateTime |   |
| usedAt | LocalDateTime |   |
| id | String |   |


#### 集合 mqLog

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| name | String |   |
| createAt | LocalDateTime |   |
| body | String |   |
| sendErrorMessage | String |   |
| isDone | boolean |   |
| consumeAt | LocalDateTime |   |
| result | String |   |
| id | String |   |


#### 集合 notifyMessage

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| type | NotifyTypeEnum |   |
| title | String |   |
| content | String |   |
| isRead | boolean |   |
| extDbId | String |   |
| user | IdName |   |
| isPushed | boolean |   |
| createAt | LocalDateTime |   |
| readAt | LocalDateTime |   |
| id | String |   |


#### 集合 sysAnnex

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| name | String |   |
| ext | String |   |
| size | int |   |
| checkCode | String |   |
| imgWidth | int |   |
| imgHeight | int |   |
| url | String |   |
| createBy | IdName |   |
| corp | IdName |   |
| errorMsg | String |   |
| createAt | LocalDateTime |   |
| id | String |   |


#### 集合 sysCity

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| code | int |   |
| name | String |   |
| fullName | String |   |
| level | int |   |
| lng | float |   |
| lat | float |   |
| pinyin | String |   |
| telCode | String |   |
| postCode | String |   |
| pcode | int |   |
| id | String |   |


#### 集合 sysCorporation

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| name | String |   |
| city | CodeName |   |
| address | String |   |
| phone | String |   |
| weixin | String |   |
| industry | IdName |   |
| logo | IdUrl |   |
| qualifications | List |   |
| images | List |   |
| detail | String |   |
| createAt | LocalDateTime |   |
| updateAt | LocalDateTime |   |
| isLocked | boolean |   |
| lockedRemark | String |   |
| id | String |   |


#### 集合 sysLog

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| msg | String |   |
| creatAt | LocalDateTime |   |
| createBy | String |   |
| type | String |   |
| clientIp | String |   |
| module | String |   |
| remark | String |   |
| id | String |   |


#### 集合 sysLoginLog

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| loginName | String |   |
| password | String |   |
| app | String |   |
| clientIp | String |   |
| createAt | LocalDateTime |   |
| client | String |   |
| remark | String |   |
| id | String |   |


#### 集合 sysLoginUser

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| loginName | String |   |
| password | String |   |
| lastLoginAt | LocalDateTime |   |
| errorLoginTimes | byte |   |
| isLocked | boolean |   |
| forget_password | boolean |   |
| lockedRemark | String |   |
| id | String |   |


#### 集合 sysUser

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| loginName | String |   |
| name | String |   |
| logo | IdUrl |   |
| mobile | String |   |
| email | String |   |
| birthday | LocalDate |   |
| recvAddress | String |   |
| sex | UserSexEnum |   |
| qq | String |   |
| deviceId | String |   |
| regionId | String |   |
| corp | IdName |   |
| isLeader | boolean |   |
| isAdmin | boolean |   |
| lastLoginAt | LocalDateTime |   |
| token | String |   |
| createAt | LocalDateTime |   |
| updateAt | LocalDateTime |   |
| id | String |   |


#### 集合 traceLog

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| traceId | String |   |
| host | String |   |
| type | String |   |
| url | String |   |
| method | String |   |
| header | String |   |
| body | String |   |
| createBy | String |   |
| createAt | LocalDateTime |   |
| clientIp | String |   |
| id | String |   |


#### 集合 versionData

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| client | AppClientEnum |   |
| version | String |   |
| remark | String |   |
| url | IdUrl |   |
| mustUpdate | boolean |   |
| publishAt | LocalDateTime |   |
| id | String |   |


}

### Shop 模块




#### 集合 wxFormId

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| openid | String |   |
| createAt | LocalDateTime |   |
| formId | String |   |
| prePayId | String |   |
| used | boolean |   |
| usedAt | LocalDateTime |   |
| id | String |   |


}

### Admin 模块




#### 集合 adminLoginUser

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| loginName | String |   |
| password | String |   |
| lastLoginAt | LocalDateTime |   |
| errorLoginTimes | byte |   |
| isLocked | boolean |   |
| lockedRemark | String |   |
| id | String |   |


#### 集合 adminUser

| 字段名  | 数据类型 | 备注  |
| --- | --- | --- |
| loginName | String |   |
| name | String |   |
| logo | IdUrl |   |
| mobile | String |   |
| sex | UserSexEnum |   |
| qq | String |   |
| token | String |   |
| createAt | LocalDateTime |   |
| updateAt | LocalDateTime |   |
| id | String |   |


