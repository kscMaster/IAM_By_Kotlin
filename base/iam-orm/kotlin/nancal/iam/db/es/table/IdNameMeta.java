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
public class IdNameMeta extends EsColumnName {
    private String parentPropertyName;
    IdNameMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        

        this.id = EsrUtil.esColumnJoin(this.parentPropertyName, "_id");

        this.name = EsrUtil.esColumnJoin(this.parentPropertyName, "name");
    }
    
    IdNameMeta(EsColumnName value) {
        this(value.toString());
    }


    private EsColumnName id = null;
    public EsColumnName getId(){
        return id;
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

