package nancal.iam.model.vo;


public class ParametersVo {
    //参数名称
    private String paramName;
    //正则表达式
    private String regex;
    //提示信息
    private String message;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
