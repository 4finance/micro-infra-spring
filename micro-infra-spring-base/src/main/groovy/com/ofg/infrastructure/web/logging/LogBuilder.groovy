package com.ofg.infrastructure.web.logging

import groovy.transform.CompileStatic

@CompileStatic
class LogBuilder {

    private HttpData httpData

    private LogBuilder() {
    }

    static LogBuilder createLogBuilder() {
        return new LogBuilder()
    }

    LogBuilder withHttpData(HttpData httpData) {
        assert httpData, 'httpData should not be empty'
        this.httpData = httpData
        return this
    }


    String build() {
        assert httpData, 'httpData should not be empty'
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(getMethodLog())
                     .append(getURILog())
                     .append(getHeadersLog())
                     .append(getStatusLog())
                     .append(getBodyLog())
        return stringBuilder.toString()
    }


    private String getBodyLog(){
        StringBuilder stringBuilder = new StringBuilder()
        if(httpData.content){
            stringBuilder.append('body [' + httpData.content + '] ')
        }
        return stringBuilder.toString()
    }

    private String getURILog(){
        StringBuilder stringBuilder = new StringBuilder()
        if(httpData.url){
            stringBuilder.append('url [' + httpData.url + '] ')
        }
        return stringBuilder.toString()
    }

    private String getMethodLog(){
        StringBuilder stringBuilder = new StringBuilder()
        if(httpData.httpMethod){
            stringBuilder.append('method [' + httpData.httpMethod + '] ')
        }
        return stringBuilder.toString()
    }

    private String getStatusLog(){
        StringBuilder stringBuilder = new StringBuilder()
        if(httpData.httpStatus){
            stringBuilder.append('status [' + httpData.httpStatus + '] ')
        }
        return stringBuilder.toString()
    }

    private String getHeadersLog(){
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append('headers [')
        if(httpData.headers){
            httpData.headers.each {String key, String value -> stringBuilder.append(key)
                                                       .append(' : ')
                                                       .append(value)
                                                       .append(' ; ')}
        }

        return stringBuilder.append(' ] ').toString()
    }
}
