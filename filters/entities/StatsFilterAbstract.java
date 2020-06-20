package com.grabstat.utils.filters.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "condition", defaultImpl = StatsFilterRule.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StatsFilterBlock.class, name = "and"),
        @JsonSubTypes.Type(value = StatsFilterBlock.class, name = "or")
})
@JsonIgnoreProperties(value = {"query"})
@Data
public abstract class StatsFilterAbstract {
    public abstract String getQuery();

    public abstract Predicate getPredicate(CriteriaBuilder builder, Class clazz, Path path);
}
