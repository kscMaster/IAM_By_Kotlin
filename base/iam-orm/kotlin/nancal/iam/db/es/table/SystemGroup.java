package nancal.iam.db.es.table;

import nbcp.db.*;
import nbcp.db.es.*;
import nbcp.utils.*;
import nbcp.comm.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.*;
import nancal.iam.db.es.entity.*;

//generate auto @2022-08-12 18:03:58

@Component("es.system")
@MetaDataGroup(dbType =DatabaseEnum.ElasticSearch, value = "system")
public class SystemGroup implements IDataGroup{
    @Override
    public Set<BaseMetaData> getEntities(){
        return new HashSet(){ { 
            add(appLogIndex);
            add(bizLogData);
            add(nginxLogIndex);
            add(orderMain);
            add(productIndex);
        }};
    }

    public AppLogIndexEntity appLogIndex = new AppLogIndexEntity();
    public AppLogIndexEntity appLogIndex(String collectionName){
        return new AppLogIndexEntity(collectionName,"");
    }
    public BizLogDataEntity bizLogData = new BizLogDataEntity();
    public BizLogDataEntity bizLogData(String collectionName){
        return new BizLogDataEntity(collectionName,"");
    }
    public NginxLogIndexEntity nginxLogIndex = new NginxLogIndexEntity();
    public NginxLogIndexEntity nginxLogIndex(String collectionName){
        return new NginxLogIndexEntity(collectionName,"");
    }
    public OrderMainEntity orderMain = new OrderMainEntity();
    public OrderMainEntity orderMain(String collectionName){
        return new OrderMainEntity(collectionName,"");
    }
    public ProductIndexEntity productIndex = new ProductIndexEntity();
    public ProductIndexEntity productIndex(String collectionName){
        return new ProductIndexEntity(collectionName,"");
    }



    @nbcp.db.DbName(value = "app-log")
    @nbcp.db.DbEntityGroup(value = "system")
    @nbcp.db.DbDefines(value = {@nbcp.db.DbDefine(fieldName = "logFile", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "className", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "content", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}")})
    public class AppLogIndexEntity extends EsBaseMetaEntity<nancal.iam.db.es.entity.AppLogIndex> {
        public String collectionName;
        public String databaseId;

        public AppLogIndexEntity(String collectionName,String databaseId){
            super(nancal.iam.db.es.entity.AppLogIndex.class, "app-log", MyHelper.AsString(collectionName,"app-log"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public AppLogIndexEntity(){
            this("","");
        }
        
            
        public EsColumnName visitAt = new EsColumnName("visitAt");
        public EsColumnName logFile = new EsColumnName("logFile");
        public EsColumnName level = new EsColumnName("level");
        public EsColumnName group = new EsColumnName("group");
        public EsColumnName requestId = new EsColumnName("requestId");
        public EsColumnName className = new EsColumnName("className");
        public EsColumnName line = new EsColumnName("line");
        public EsColumnName content = new EsColumnName("content");
        public EsColumnName id = new EsColumnName("_id");
        public EsColumnName createAt = new EsColumnName("createAt");
        public EsColumnName updateAt = new EsColumnName("updateAt");


        public EsQueryClip<AppLogIndexEntity, nancal.iam.db.es.entity.AppLogIndex> query(){
            return new EsQueryClip(this);
        }
        
        public EsUpdateClip<AppLogIndexEntity, nancal.iam.db.es.entity.AppLogIndex> update(){
            return new EsUpdateClip(this);
        }
        
        public EsDeleteClip<AppLogIndexEntity> delete(){
            return new EsDeleteClip(this);
        }
    }


    @nbcp.db.DbName(value = "biz-log")
    @nbcp.db.DbEntityGroup(value = "system")
    @nbcp.db.DbDefines(value = {@nbcp.db.DbDefine(fieldName = "id", define = "{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}"), @nbcp.db.DbDefine(fieldName = "module", define = "{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}"), @nbcp.db.DbDefine(fieldName = "data", define = "{\"properties\":{\"action\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"appInfo\":{\"properties\":{\"code\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}},\"browser\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"city\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"ip\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"os\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"remark\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"resource\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"result\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"roles\":{\"properties\":{\"id\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}},\"tenant\":{\"properties\":{\"id\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"name\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}}}}"), @nbcp.db.DbDefine(fieldName = "msg", define = "{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}"), @nbcp.db.DbDefine(fieldName = "creator", define = "{\"properties\":{\"id\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}},\"name\":{\"type\":\"keyword\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}}"), @nbcp.db.DbDefine(fieldName = "createAt", define = "{\"type\":\"date\"}")})
    public class BizLogDataEntity extends EsBaseMetaEntity<nancal.iam.db.es.entity.BizLogData> {
        public String collectionName;
        public String databaseId;

        public BizLogDataEntity(String collectionName,String databaseId){
            super(nancal.iam.db.es.entity.BizLogData.class, "biz-log", MyHelper.AsString(collectionName,"biz-log"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public BizLogDataEntity(){
            this("","");
        }
        
            
        public EsColumnName id = new EsColumnName("_id");
        public EsColumnName module = new EsColumnName("module");
        public DataMeta data = new DataMeta("data");
        public EsColumnName msg = new EsColumnName("msg");
        public RequestLogDataMeta request = new RequestLogDataMeta("request");
        public ResponseLogDataMeta response = new ResponseLogDataMeta("response");
        public IdNameMeta creator = new IdNameMeta("creator");
        public EsColumnName createAt = new EsColumnName("createAt");
        public EsColumnName status = new EsColumnName("status");


        public EsQueryClip<BizLogDataEntity, nancal.iam.db.es.entity.BizLogData> query(){
            return new EsQueryClip(this);
        }
        
        public EsUpdateClip<BizLogDataEntity, nancal.iam.db.es.entity.BizLogData> update(){
            return new EsUpdateClip(this);
        }
        
        public EsDeleteClip<BizLogDataEntity> delete(){
            return new EsDeleteClip(this);
        }
    }


    @nbcp.db.DbName(value = "ngin-log")
    @nbcp.db.DbEntityGroup(value = "system")
    @nbcp.db.DbDefines(value = {@nbcp.db.DbDefine(fieldName = "url", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "referer", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "agent", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}")})
    public class NginxLogIndexEntity extends EsBaseMetaEntity<nancal.iam.db.es.entity.NginxLogIndex> {
        public String collectionName;
        public String databaseId;

        public NginxLogIndexEntity(String collectionName,String databaseId){
            super(nancal.iam.db.es.entity.NginxLogIndex.class, "ngin-log", MyHelper.AsString(collectionName,"ngin-log"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public NginxLogIndexEntity(){
            this("","");
        }
        
            
        public EsColumnName ip = new EsColumnName("ip");
        public EsColumnName visitAt = new EsColumnName("visitAt");
        public EsColumnName method = new EsColumnName("method");
        public EsColumnName url = new EsColumnName("url");
        public EsColumnName referer = new EsColumnName("referer");
        public EsColumnName status = new EsColumnName("status");
        public EsColumnName agent = new EsColumnName("agent");
        public EsColumnName id = new EsColumnName("_id");
        public EsColumnName createAt = new EsColumnName("createAt");
        public EsColumnName updateAt = new EsColumnName("updateAt");


        public EsQueryClip<NginxLogIndexEntity, nancal.iam.db.es.entity.NginxLogIndex> query(){
            return new EsQueryClip(this);
        }
        
        public EsUpdateClip<NginxLogIndexEntity, nancal.iam.db.es.entity.NginxLogIndex> update(){
            return new EsUpdateClip(this);
        }
        
        public EsDeleteClip<NginxLogIndexEntity> delete(){
            return new EsDeleteClip(this);
        }
    }


    @nbcp.db.DbName(value = "order_main")
    @nbcp.db.DbEntityGroup(value = "system")
    public class OrderMainEntity extends EsBaseMetaEntity<nancal.iam.db.es.entity.OrderMain> {
        public String collectionName;
        public String databaseId;

        public OrderMainEntity(String collectionName,String databaseId){
            super(nancal.iam.db.es.entity.OrderMain.class, "order-main", MyHelper.AsString(collectionName,"order-main"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public OrderMainEntity(){
            this("","");
        }
        
            
        public EsColumnName accountState = new EsColumnName("accountState");
        public EsColumnName activePayStartTime = new EsColumnName("activePayStartTime");
        public EsColumnName activePayTime = new EsColumnName("activePayTime");
        public EsColumnName appraiseState = new EsColumnName("appraiseState");
        public EsColumnName autarky = new EsColumnName("autarky");
        public EsColumnName cashBackMoney = new EsColumnName("cashBackMoney");
        public EsColumnName changeState = new EsColumnName("changeState");
        public EsColumnName copartnerRatio = new EsColumnName("copartnerRatio");
        public EsColumnName couponFreeMoney = new EsColumnName("couponFreeMoney");
        public EsColumnName createOpeTime = new EsColumnName("createOpeTime");
        public EsColumnName createOper = new EsColumnName("createOper");
        public EsColumnName customerName = new EsColumnName("customerName");
        public EsColumnName customerUuid = new EsColumnName("customerUuid");
        public EsColumnName delFlag = new EsColumnName("delFlag");
        public EsColumnName delayDays = new EsColumnName("delayDays");
        public EsColumnName distributionMoney = new EsColumnName("distributionMoney");
        public EsColumnName distributionRatio = new EsColumnName("distributionRatio");
        public EsColumnName distributorState = new EsColumnName("distributorState");
        public EsColumnName endTime = new EsColumnName("endTime");
        public EsColumnName freight = new EsColumnName("freight");
        public EsColumnName fullReduceFreeMoney = new EsColumnName("fullReduceFreeMoney");
        public EsColumnName integral = new EsColumnName("integral");
        public EsColumnName invoiceEmail = new EsColumnName("invoiceEmail");
        public EsColumnName invoiceState = new EsColumnName("invoiceState");
        public EsColumnName oneProductDiscount = new EsColumnName("oneProductDiscount");
        public EsColumnName opeTime = new EsColumnName("opeTime");
        public EsColumnName oper = new EsColumnName("oper");
        public EsColumnName orderDetailJson = new EsColumnName("orderDetailJson");
        public EsColumnName orderFreePrice = new EsColumnName("orderFreePrice");
        public EsColumnName orderGroupUuid = new EsColumnName("orderGroupUuid");
        public EsColumnName orderId = new EsColumnName("orderId");
        public EsColumnName orderState = new EsColumnName("orderState");
        public EsColumnName orderType = new EsColumnName("orderType");
        public EsColumnName payPrice = new EsColumnName("payPrice");
        public EsColumnName payPriceStr = new EsColumnName("payPriceStr");
        public EsColumnName payRatio = new EsColumnName("payRatio");
        public EsColumnName payState = new EsColumnName("payState");
        public EsColumnName permanentDel = new EsColumnName("permanentDel");
        public EsColumnName platCouponFreeMoney = new EsColumnName("platCouponFreeMoney");
        public EsColumnName refundMoney = new EsColumnName("refundMoney");
        public EsColumnName refundState = new EsColumnName("refundState");
        public EsColumnName returnState = new EsColumnName("returnState");
        public EsColumnName searchQuery = new EsColumnName("searchQuery");
        public EsColumnName sendCouponState = new EsColumnName("sendCouponState");
        public EsColumnName sendPointsState = new EsColumnName("sendPointsState");
        public EsColumnName serviceFee = new EsColumnName("serviceFee");
        public EsColumnName serviceFeeRatio = new EsColumnName("serviceFeeRatio");
        public EsColumnName shipType = new EsColumnName("shipType");
        public EsColumnName shopState = new EsColumnName("shopState");
        public EsColumnName startTime = new EsColumnName("startTime");
        public EsColumnName storeDiscount = new EsColumnName("storeDiscount");
        public EsColumnName storeName = new EsColumnName("storeName");
        public EsColumnName storeSplitRatio = new EsColumnName("storeSplitRatio");
        public EsColumnName storeUuid = new EsColumnName("storeUuid");
        public EsColumnName sunState = new EsColumnName("sunState");
        public EsColumnName tempFreight = new EsColumnName("tempFreight");
        public EsColumnName totalFreePrice = new EsColumnName("totalFreePrice");
        public EsColumnName totalPrice = new EsColumnName("totalPrice");
        public EsColumnName uuid = new EsColumnName("uuid");
        public EsColumnName version = new EsColumnName("version");
        public EsColumnName payType = new EsColumnName("payType");
        public EsColumnName payTime = new EsColumnName("payTime");
        public EsColumnName sendTime = new EsColumnName("sendTime");
        public EsColumnName receiveTime = new EsColumnName("receiveTime");
        public EsColumnName id = new EsColumnName("_id");
        public EsColumnName createAt = new EsColumnName("createAt");
        public EsColumnName updateAt = new EsColumnName("updateAt");


        public EsQueryClip<OrderMainEntity, nancal.iam.db.es.entity.OrderMain> query(){
            return new EsQueryClip(this);
        }
        
        public EsUpdateClip<OrderMainEntity, nancal.iam.db.es.entity.OrderMain> update(){
            return new EsUpdateClip(this);
        }
        
        public EsDeleteClip<OrderMainEntity> delete(){
            return new EsDeleteClip(this);
        }
    }


    @nbcp.db.DbName(value = "product")
    @nbcp.db.DbEntityGroup(value = "system")
    @nbcp.db.DbDefines(value = {@nbcp.db.DbDefine(fieldName = "name", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "slogan", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "detail", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "remark", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "skuDefines.key", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}"), @nbcp.db.DbDefine(fieldName = "skuDefines.value", define = "{\"type\":\"text\",\"index\":\"true\",\"boost\":\"1\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}")})
    public class ProductIndexEntity extends EsBaseMetaEntity<nancal.iam.db.es.entity.ProductIndex> {
        public String collectionName;
        public String databaseId;

        public ProductIndexEntity(String collectionName,String databaseId){
            super(nancal.iam.db.es.entity.ProductIndex.class, "product", MyHelper.AsString(collectionName,"product"), databaseId);
        
            this.collectionName = collectionName;
            this.databaseId = databaseId;
        }
        
        public ProductIndexEntity(){
            this("","");
        }
        
            
        public EsColumnName name = new EsColumnName("name");
        public IdNameMeta tenant = new IdNameMeta("tenant");
        public EsColumnName slogan = new EsColumnName("slogan");
        public IdCodeNameMeta brandPath = new IdCodeNameMeta("brandPath");
        public IdCodeNameMeta corpCategoryPath = new IdCodeNameMeta("corpCategoryPath");
        public IdCodeNameMeta categoryPath = new IdCodeNameMeta("categoryPath");
        public EsColumnName detail = new EsColumnName("detail");
        public EsColumnName remark = new EsColumnName("remark");
        public EsColumnName status = new EsColumnName("status");
        public EsColumnName guidePrice = new EsColumnName("guidePrice");
        public KeyValueStringMeta skuDefines = new KeyValueStringMeta("skuDefines");
        public EsColumnName id = new EsColumnName("_id");
        public EsColumnName createAt = new EsColumnName("createAt");
        public EsColumnName updateAt = new EsColumnName("updateAt");


        public EsQueryClip<ProductIndexEntity, nancal.iam.db.es.entity.ProductIndex> query(){
            return new EsQueryClip(this);
        }
        
        public EsUpdateClip<ProductIndexEntity, nancal.iam.db.es.entity.ProductIndex> update(){
            return new EsUpdateClip(this);
        }
        
        public EsDeleteClip<ProductIndexEntity> delete(){
            return new EsDeleteClip(this);
        }
    }

}
