package com.grabstat.utils.filters.entities.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeOperator {
    @JsonProperty("=")
    EQUALS("%s = %s"),

    @JsonProperty("!=")
    NOT_EQUALS("%s != %s"),

    @JsonProperty("равно")
    STRING_EQUALS("%s = '%s'"),

    @JsonProperty("не равно")
    STRING_NOT_EQUALS("%s != '%s'"),//%Rostov%

    @JsonProperty("содержит")
    STRING_LIKE("%s like '%%%s%%'"),

    @JsonProperty("не содержит")
    STRING_NOT_LIKE("%s not like '%%%s%%'"),//%Rostov%

    @JsonProperty("<=")
    LESS_OR_EQUALS("%s <= %s"),

    @JsonProperty("in")
    IN("%s in %s");

    private String text;

    TypeOperator(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
