package nancal.iam.db.mongo.table;

import nbcp.db.*;
import nbcp.db.mongo.*;
import nbcp.utils.*;
import nbcp.comm.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import nancal.iam.db.mongo.*;

//generate auto @2022-08-11 11:01:01


public class MoerMetaMap {
    private String parentPropertyName;
    
    public MoerMetaMap(String... args){
        this.parentPropertyName = Arrays.asList(args).stream().filter (it-> MyHelper.hasValue( it) ).collect(Collectors.joining("."));
    }
    
    public String keys(String... keys) {
        return this.parentPropertyName + "." + Arrays.asList(keys).stream().filter (it-> MyHelper.hasValue( it) ).collect(Collectors.joining("."));
    }
}


