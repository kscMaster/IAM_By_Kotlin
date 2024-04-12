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
public class CodeNameMeta extends EsColumnName {
    private String parentPropertyName;
    CodeNameMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        

        this.code = EsrUtil.esColumnJoin(this.parentPropertyName, "code");

        this.name = EsrUtil.esColumnJoin(this.parentPropertyName, "name");
    }
    
    CodeNameMeta(EsColumnName value) {
        this(value.toString());
    }


    /**
     * 编码
     */
    @nbcp.db.Cn(value = "编码")
    private EsColumnName code = null;
    public EsColumnName getCode(){
        return code;
    }

    /**
     * 名称
     */
    @nbcp.db.Cn(value = "名称")
    private EsColumnName name = null;
    public EsColumnName getName(){
        return name;
    }

    @Override
    public String toString() {
        return EsrUtil.esColumnJoin(this.parentPropertyName).toString();
    }
}

