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


public class MoerUtil{
    public static MongoColumnName mongoColumnJoin(String... args) {
        return new MongoColumnName(Arrays.asList(args).stream().filter (it-> MyHelper.hasValue( it) ).collect(Collectors.joining(".")));
    }
}


