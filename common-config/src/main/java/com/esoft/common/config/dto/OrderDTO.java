package com.esoft.common.config.dto;


import com.esoft.common.config.validation.OrderEnumValidValue;

import java.math.BigDecimal;

public class OrderDTO {
    private int id;

    private String code;

    @OrderEnumValidValue(enumClass = OrderCategory.class, message = "Invalid category")
    private String category;

    private int quantity;

    @OrderEnumValidValue(enumClass = OrderServiceName.class, message = "Invalid service name")
    private String serviceName;

    private BigDecimal amount;

    private String description;

    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}