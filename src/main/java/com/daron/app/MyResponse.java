package com.daron.app;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class MyResponse implements Serializable {

    @XmlElement
    private String responseMessage;

    @XmlElement
    private boolean isError;

    public MyResponse() {
    }

    public MyResponse(String responseMessage, boolean isError) {
        this.responseMessage = responseMessage;
        this.isError = isError;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        isError = isError;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "responseMessage: " + responseMessage + "\nisError: " + isError;
    }
}
