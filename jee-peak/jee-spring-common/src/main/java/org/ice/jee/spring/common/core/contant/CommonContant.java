package org.ice.jee.spring.common.core.contant;

/**
 * Created by Jackie on 2017/6/28.
 */
public interface CommonContant {

    interface MessageType{

        String MESSAGE_TYPE_REQUEST = "req";

        String MESSAGE_TYPE_RESPONSE = "rsp";

    }

    enum OrderStatusEnum{

        NORMALSTATU("OS01","正常"),
        ENDSTATU("OS99","结束");

        private String nodeCode;

        private String nodeName;

        private OrderStatusEnum(String code, String name){
            this.nodeName = name;
            this.nodeCode = code;
        }

        public String NodeCode() {
            return nodeCode;
        }

        public String NodeName() {
            return nodeName;
        }
    }



}
