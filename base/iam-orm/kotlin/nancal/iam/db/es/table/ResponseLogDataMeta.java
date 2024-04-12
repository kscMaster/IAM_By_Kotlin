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
public class ResponseLogDataMeta extends EsColumnName {
    private String parentPropertyName;
    ResponseLogDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        

        this.status = EsrUtil.esColumnJoin(this.parentPropertyName, "status");

        this.body = EsrUtil.esColumnJoin(this.parentPropertyName, "body");

        this.header = EsrUtil.esColumnJoin(this.parentPropertyName, "header");
    }
    
    ResponseLogDataMeta(EsColumnName value) {
        this(value.toString());
    }


    /**
     * 响应状态
     */
    @nbcp.db.Cn(value = "响应状态")
    private EsColumnName status = null;
    public EsColumnName getStatus(){
        return status;
    }

    /**
     * 响应体
     */
    @nbcp.db.Cn(value = "响应体")
    private EsColumnName body = null;
    public EsColumnName getBody(){
        return body;
    }

    /**
     * 响应头
     */
    @nbcp.db.Cn(value = "响应头")
    private EsColumnName header = null;
    public EsColumnName getHeader(){
        return header;
    }

    @Override
    public String toString() {
        return EsrUtil.esColumnJoin(this.parentPropertyName).toString();
    }
}

