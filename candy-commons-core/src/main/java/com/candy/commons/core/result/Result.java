package com.candy.commons.core.result;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * service返回的对象
     */
    private Map<String, Object> result = new HashMap<String, Object>();

    /**
     * 默认的key
     */
    public static final String DEFAULT_MODEL_KEY = "value";

    /**
     * 当前的key
     */
    private String modelKey = DEFAULT_MODEL_KEY;


    /**
     * 返回码
     */
    private int resultCode = 200;
    private String[] resultCodeParams;
    private String errorMessage;
    private String successMessage;

    /**
     * 带是否成功的构造方法
     *
     * @param success
     */
    public Result(boolean success) {
        this.success = success;
    }

    /**
     * 默认构造方法
     */
    public Result() {
    }

    /**
     * 新增一个返回结果
     *
     * @param obj
     * @return
     */
    public Object addDefaultModel(Object obj) {
        return result.put(DEFAULT_MODEL_KEY, obj);
    }

    /**
     * 新增一个带key的返回结果
     *
     * @param key
     * @param obj
     * @return
     */
    public Object addDefaultModel(String key, Object obj) {
        modelKey = key;
        return result.put(key, obj);
    }

    /**
     * 取出所有的key
     *
     * @return
     */
    public Set<String> keySet() {
        return result.keySet();
    }

    /**
     * 取出整个map对象
     *
     * @return
     */
    public Map<String, Object> getMap() {
        return result;
    }

    /**
     * 取出默认的值
     *
     * @return
     */
    public Object get() {
        return result.get(modelKey);
    }

    /**
     * 取出值
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return result.get(key);
    }

    /**
     * 取出值集合
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Collection values() {
        return result.values();
    }

    /**
     * 返回是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置返回是否成功
     *
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultCode(int resultCode, String... args) {
        this.resultCode = resultCode;
        this.resultCodeParams = args;
    }

    public String[] getResultCodeParams() {
        return resultCodeParams;
    }

    public void setResultCodeParams(String[] resultCodeParams) {
        this.resultCodeParams = resultCodeParams;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }


}
