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

public class ObjectMeta extends MongoColumnName{
    private String parentPropertyName;
    public ObjectMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    }
    
    public ObjectMeta(MongoColumnName value) {
        this(value.toString());
    }

    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

