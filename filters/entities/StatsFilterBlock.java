package com.grabstat.utils.filters.entities;

import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class StatsFilterBlock extends StatsFilterAbstract {

    String condition = "and";

    //@JsonInclude(JsonInclude.Include.NON_NULL)
    List<StatsFilterAbstract> rules = new ArrayList<>();

    @Override
    public String getQuery() {

        String wherePart = rules.stream()
                .map(i -> i.getQuery())
                .collect(Collectors.joining(String.format(" %s ", condition)));

        return String.format("(%s)", wherePart);
    }

    public Predicate getPredicate(CriteriaBuilder builder, Class clazz, Path path) {
        Predicate predicate = null;
        for (StatsFilterAbstract filter : rules) {
            if (predicate == null) {
                predicate = filter.getPredicate(builder, clazz, path);
                continue;
            }
            if (condition.equals("and")) {
                predicate = builder.and(predicate, filter.getPredicate(builder, clazz, path));
            } else {
                predicate = builder.or(predicate, filter.getPredicate(builder, clazz, path));
            }
        }
        return predicate;
    }
}
