package nancal.iam.aspect;

//import com.alibaba.fastjson.JSONObject;
import nancal.iam.model.vo.ParametersVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//代表该类是一个切面的处理类
@Aspect
@Component
public class CheckObjectAspect {

    //只要加这个注解，它就执行下面的方法
    @Around(value = "@annotation(nancal.iam.aspect.CheckObjects)")
    public Object checkObjectAroundAdvice(ProceedingJoinPoint target) throws Throwable {

        //拿到目标方法的参数
        Object[] args = target.getArgs();

        //获得目标方法上的注解
        MethodSignature signature = (MethodSignature) target.getSignature();
        Method targetMethod = signature.getMethod();
        CheckObjects annotation = targetMethod.getAnnotation(CheckObjects.class);
        for (CheckObject checkObject : annotation.value()) {

            String path = checkObject.path();
            String reg = checkObject.reg();


        }


//        String js = annotation.js();
//        String params = null;
//        //拿到目标方法的参数
//        Object[] args = target.getArgs();
//        //这里头保存的是前端传过来的参数
//        Map<String, String> map = new HashMap<>();
//        //把前端传来的参数都封装到这个map中
//        for (Object arg : args) {
//            if(!(arg instanceof HttpServletRequest)){
//                //把这个对象转成map
//                Map<String, Object> parseObject = MapUtil.newHashMap();
//                BeanUtil.copyProperties(arg, parseObject);
//                Set<Map.Entry<String, Object>> entrySet = parseObject.entrySet();
//                for (Map.Entry<String, Object> entry : entrySet) {
//                    map.put(entry.getKey(), entry.getValue()+"");
//                }
//            }
//        }
        //拿到注解里头的参数
        //这里头保存的是注解里头的参数
//        List<ParametersVo> parametersVoList = new ArrayList<>();
//        String[] paramValues = params.split(";");
//        for (int i = 0; i < paramValues.length; i++) {
//            //根据注解参数规则,分割
//            //类似这种格式id:[0-9]{1,5}:id必须小于5位数;name:^[\u4e00-\u9fa5]{1,6}$:姓名必须是1到6位汉字;
//            String[] pms = paramValues[i].split(":");
//            ParametersVo parametersVo = new ParametersVo();
//            if (pms != null && pms.length > 0) {
//                //设置参数名称,比如id
//                parametersVo.setParamName(pms[0]);
//                //设置正则,[0-9]{1,5}
//                parametersVo.setRegex(pms[1]);
//                //设置提示语id必须小于5位数
//                parametersVo.setMessage(pms[2]);
//                //将这个对象封装到集合
//                parametersVoList.add(parametersVo);
//            }
//        }
//        if (!CollectionUtils.isEmpty(parametersVoList) && hasFiled(map, parametersVoList)) {
//            for (ParametersVo parametersVo : parametersVoList) {
//                //返回ture,说明前端传过来的数据包含了这个属性
//                if (map.containsKey(parametersVo.getParamName())) {
//                    //遍历前端封装的map,来进行正则校验
//                    Set<Map.Entry<String, String>> entries = map.entrySet();
//                    for (Map.Entry<String, String> entry : entries) {
//                        //判断前端传过来的参数,是不是和后端校验加的参数相同
//                        if (parametersVo.getParamName().equals(entry.getKey())) {
//                            //相同,根据自己加的注解配置,看看这个字段是否可为null
//                            if (parametersVo.getRegex().contains("-or-")) {
//                                //为null,进行一波参数校验
//                                String regex = parametersVo.getRegex();
//                                String[] split = regex.split("-or-");
//                                if (split != null && split.length > 0 && "null".equals(split[0])&&!"null".equals(map.get(parametersVo.getParamName()))) {
//                                    String reg = split[1];
//                                    if (!match(reg, entry.getValue())) {
//                                        //参数校验不通过
//                                        throw new RuntimeException(parametersVo.getMessage());
//                                    }
//                                }
//                            } else if (!match(parametersVo.getRegex(), entry.getValue())) {
//                                //字段不可以为null,也得进行校验
//                                System.out.println("字段不可以为null,也得进行校验");
//                                throw new RuntimeException(parametersVo.getMessage());
//                            }
//                        }
//                    }
//                } else {
//                    //否则,则没有传这个字段。但是后端是必填项
//                    if (!parametersVo.getRegex().contains("-or-")) {
//                        //这个字段可以为空
//                        //根据自己设置的参数来前端有木有
//                        if (!"null".equals(map.get(parametersVo.getParamName()))) {
//                            System.out.println("根据自己设置的参数来前端有木有");
//                            throw new RuntimeException(parametersVo.getMessage());
//                        }
//                    }
//                    //不是必填项，正常执行就可以
//                }
//            }
//        }
        return target.proceed();
    }


    /**
     * 根据正则校验
     * @param regex
     * @param obj
     * @return
     */
    private boolean match(String regex, Object obj) {
        System.out.println("正则表达式:" + regex);
        System.out.println("校验字符串:" + obj + "");
        if ("null".equals(regex)) {
            return true;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(obj + "");
        System.out.println("校验结果:" + matcher.matches());
        return matcher.matches();
    }

    /**
     * 判断前端传过来的参数是不是和后端接受参数项对应
     *
     * @param paramMap         前端传来的参数项
     * @param parametersVoList 添加注解里头的参数
     * @return
     */
    private boolean hasFiled(Map<String, String> paramMap, List<ParametersVo> parametersVoList) {
        if (!CollectionUtils.isEmpty(parametersVoList)) {
            for (ParametersVo parametersVo : parametersVoList) {
                //获取到自己注解对应的参数正则
                String regex = parametersVo.getRegex();
                //-or-表示这个参数可以为null
                if (!paramMap.containsKey(parametersVo.getParamName()) && !regex.contains("-or-")) {
                    throw new RuntimeException("参数个数或参数名称有问题");
                }
            }
        }
        return true;
    }

}
/**
 * 思路:先拿到对象参数
 * 封装成个map
 * 拿到注解中参数、封装个集合
 * 进行匹配
 *
 * 问题:拿到的参数对象是一个复杂对象,类似:{"name":"张三","age":25,{"like":"篮球","type":"运动"}}
 *
 **/