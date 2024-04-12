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

public class EsrUtil{
    public static EsColumnName esColumnJoin(String... args) {
        return new EsColumnName(Arrays.asList(args).stream().filter (it-> MyHelper.hasValue( it) ).collect(Collectors.joining(".")));
    }
}

