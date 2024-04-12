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


public class EsrMetaMap {
    private String parentPropertyName;
    
    public EsrMetaMap(String... args){
        this.parentPropertyName = Arrays.asList(args).stream().filter (it-> MyHelper.hasValue( it) ).collect(Collectors.joining("."));
    }
    
    public String keys(String... keys) {
        return this.parentPropertyName + "." + Arrays.asList(keys).stream().filter (it-> MyHelper.hasValue( it) ).collect(Collectors.joining("."));
    }
}


