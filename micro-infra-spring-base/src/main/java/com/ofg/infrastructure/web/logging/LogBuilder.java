package com.ofg.infrastructure.web.logging;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import java.util.Map;

class LogBuilder {

    private HttpData httpData;

    private LogBuilder() {}

    static LogBuilder createLogBuilder() {
        return new LogBuilder();
    }

    LogBuilder withHttpData(HttpData httpData) {
        Preconditions.checkArgument(httpData != null, "httpData should not be empty");
        this.httpData = httpData;
        return this;
    }


    String build() {
        Preconditions.checkArgument(httpData != null, "httpData should not be empty");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getMethodLog())
                     .append(getURILog())
                     .append(getHeadersLog())
                     .append(getStatusLog())
                     .append(getBodyLog());
        return stringBuilder.toString();
    }


    private String getBodyLog(){
        StringBuilder stringBuilder = new StringBuilder();
        if(StringUtils.isNotEmpty(httpData.getProcessedContent())){
            stringBuilder.append("body [").
                            append(httpData.getProcessedContent()).
                            append("] ");
        }
        return stringBuilder.toString();
    }

    private String getURILog(){
        StringBuilder stringBuilder = new StringBuilder();
        if(StringUtils.isNotEmpty(httpData.getUrl())){
            stringBuilder.append("url [").
                            append(httpData.getUrl()).
                            append("] ");
        }
        return stringBuilder.toString();
    }

    private String getMethodLog(){
        StringBuilder stringBuilder = new StringBuilder();
        if(StringUtils.isNotEmpty(httpData.getHttpMethod())){
            stringBuilder.append("method [").
                            append(httpData.getHttpMethod()).
                            append("] ");
        }
        return stringBuilder.toString();
    }

    private String getStatusLog(){
        StringBuilder stringBuilder = new StringBuilder();
        if(httpData.getHttpStatus() != 0){
            stringBuilder.append("status [").
                            append(httpData.getHttpStatus()).
                            append("] ");
        }
        return stringBuilder.toString();
    }

    private String getHeadersLog(){
        StringBuilder stringBuilder = new StringBuilder();
        if(httpData.getProcessedHeaders() != null){
            stringBuilder.append("headers [");
            for (Map.Entry<String, String> entry : httpData.getProcessedHeaders().entrySet()) {
                stringBuilder.append(entry.getKey())
                                .append(" : ")
                                .append(entry.getValue())
                                .append(" ; ");
            }
            stringBuilder.append(" ] ");
        }

        return stringBuilder.toString();
    }
}
