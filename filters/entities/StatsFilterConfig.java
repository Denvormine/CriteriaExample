package com.grabstat.utils.filters.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grabstat.utils.filters.entities.enums.TypeOperator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class StatsFilterConfig {
    String name;
    String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Collection<TypeOperator> operators;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Collection<String> options;
}
