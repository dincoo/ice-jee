package org.ice.jee.spring.common.core.exception;

import org.ice.jee.spring.common.core.support.HttpCode;

/**
 * 远程服务异常
 * Created by jaden on 2018/5/30.
 */
public class FacadeServerException extends BaseException{

    public FacadeServerException(){ super(); }

    public FacadeServerException(Throwable ex) {
        super(ex);
    }

    public FacadeServerException(String message) {
        super(message);
    }

    public FacadeServerException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    protected HttpCode getHttpCode() {
        return HttpCode.FACADE_SERVER_FAIL;
    }

}
