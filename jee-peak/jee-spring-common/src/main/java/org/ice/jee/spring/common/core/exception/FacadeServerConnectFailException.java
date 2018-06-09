package org.ice.jee.spring.common.core.exception;

import org.ice.jee.spring.common.core.support.HttpCode;

/**
 * 远程服务超时异常
 * Created by jaden on 2018/5/30.
 */
public class FacadeServerConnectFailException extends BaseException {


    public FacadeServerConnectFailException(){ super(); }

    public FacadeServerConnectFailException(Throwable ex) {
        super(ex);
    }

    public FacadeServerConnectFailException(String message) {
        super(message);
    }

    public FacadeServerConnectFailException(String message, Throwable ex) {
        super(message, ex);
    }


    @Override
    protected HttpCode getHttpCode() {
        return HttpCode.FACADE_SERVER_CONNECT_FAIL;
    }
}
