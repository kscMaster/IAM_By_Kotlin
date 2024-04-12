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
public class RequestLogDataMeta extends EsColumnName {
    private String parentPropertyName;
    RequestLogDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        

        this.url = EsrUtil.esColumnJoin(this.parentPropertyName, "url");

        this.method = EsrUtil.esColumnJoin(this.parentPropertyName, "method");

        this.body = EsrUtil.esColumnJoin(this.parentPropertyName, "body");

        this.header = EsrUtil.esColumnJoin(this.parentPropertyName, "header");
    }
    
    RequestLogDataMeta(EsColumnName value) {
        this(value.toString());
    }


    /**
     * 请求地址
     */
    @nbcp.db.Cn(value = "请求地址")
    private EsColumnName url = null;
    public EsColumnName getUrl(){
        return url;
    }

    /**
     * 请求方法
     */
    @nbcp.db.Cn(value = "请求方法")
    private EsColumnName method = null;
    public EsColumnName getMethod(){
        return method;
    }

    /**
     * 请求体
     */
    @nbcp.db.Cn(value = "请求体")
    private EsColumnName body = null;
    public EsColumnName getBody(){
        return body;
    }

    /**
     * 请求头
     */
    @nbcp.db.Cn(value = "请求头")
    private EsColumnName header = null;
    public EsColumnName getHeader(){
        return header;
    }

    @Override
    public String toString() {
        return EsrUtil.esColumnJoin(this.parentPropertyName).toString();
    }
}

