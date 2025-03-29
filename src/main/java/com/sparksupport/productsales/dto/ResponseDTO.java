package com.sparksupport.productsales.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseDTO {
    private Object data;
    private Integer status;
    private Boolean success;
    private String error;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ResponseDTO(Object data, Integer status, Boolean success, String error) {
        this.data = data;
        this.status = status;
        this.success = success;
        this.error = error;
    }
}
