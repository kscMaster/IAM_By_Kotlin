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
public class KeyValueStringMeta extends EsColumnName {
    private String parentPropertyName;
    KeyValueStringMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        

        this.key = EsrUtil.esColumnJoin(this.parentPropertyName, "key");

        this.value = EsrUtil.esColumnJoin(this.parentPropertyName, "value");
    }
    
    KeyValueStringMeta(EsColumnName value) {
        this(value.toString());
    }


    /**
     * 键
     */
    @nbcp.db.Cn(value = "键")
    private EsColumnName key = null;
    public EsColumnName getKey(){
        return key;
    }

    /**
     * 值
     */
    @nbcp.db.Cn(value = "值")
    private EsColumnName value = null;
    public EsColumnName getValue(){
        return value;
    }

    @Override
    public String toString() {
        return EsrUtil.esColumnJoin(this.parentPropertyName).toString();
    }
}

