package org.ice.jee.spring.web.core.base;

import java.io.Serializable;

import org.ice.jee.spring.common.core.contant.CommonContant;
import org.ice.jee.spring.common.core.util.OnlyCodeGenerator;

/**
 * API接口请求响应
 * Created by Jackie on 2017/6/28.
 */
public class Parameter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * api调用唯一ID
     */
    private final String uniqueId = "["+ OnlyCodeGenerator.distriId() +"]";

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 服务版本号
     */
    private String version;

    /**
     * 请求参数对象
     */
    private Object requestData;

    /**
     * 响应参数对象
     */
    private Object responseData;

    /**
     * 请求/响应类型
     */
    private String messageType = CommonContant.MessageType.MESSAGE_TYPE_REQUEST;

    /**
     * 请求时间
     */
    private long reqTime;

    /**
     * 响应时间
     */
    private long rspTime;

    public String getUniqueId() {
        return uniqueId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getReqTime() {
        return reqTime;
    }

    public void setReqTime(long reqTime) {
        this.reqTime = reqTime;
    }

    public long getRspTime() {
        return rspTime;
    }

    public void setRspTime(long rspTime) {
        this.rspTime = rspTime;
    }
}
