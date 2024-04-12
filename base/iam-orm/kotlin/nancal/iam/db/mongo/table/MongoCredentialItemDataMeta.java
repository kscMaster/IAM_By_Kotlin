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

public class MongoCredentialItemDataMeta extends MongoColumnName{
    private String parentPropertyName;
    public MongoCredentialItemDataMeta(String parentPropertyName) {
        this.parentPropertyName = parentPropertyName;
        
    this.iterationCount = MoerUtil.mongoColumnJoin(this.parentPropertyName, "iterationCount");

    this.salt = MoerUtil.mongoColumnJoin(this.parentPropertyName, "salt");

    this.storedKey = MoerUtil.mongoColumnJoin(this.parentPropertyName, "storedKey");

    this.serverKey = MoerUtil.mongoColumnJoin(this.parentPropertyName, "serverKey");
    }
    
    public MongoCredentialItemDataMeta(MongoColumnName value) {
        this(value.toString());
    }

    private MongoColumnName iterationCount = null;
    public MongoColumnName getIterationCount(){
        return iterationCount;
    }

    private MongoColumnName salt = null;
    public MongoColumnName getSalt(){
        return salt;
    }

    private MongoColumnName storedKey = null;
    public MongoColumnName getStoredKey(){
        return storedKey;
    }

    private MongoColumnName serverKey = null;
    public MongoColumnName getServerKey(){
        return serverKey;
    }
    @Override 
    public String toString() {
        return MoerUtil.mongoColumnJoin(this.parentPropertyName).toString();
    }
}

