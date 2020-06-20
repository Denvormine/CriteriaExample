package com.grabstat.utils.filters.entities;

import com.grabstat.utils.filters.FilterConfigurator;
import com.grabstat.utils.filters.entities.enums.TypeOperator;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Data
public class StatsFilterRule extends StatsFilterAbstract {

    private String field;
    private TypeOperator operator;
    private String value;

    @Override
    public String getQuery() {
        String wherePart = String.format(operator.getText(), field, value);
        return wherePart;
    }

    public Predicate getPredicate(CriteriaBuilder builder, Class clazz, Path path) {
        String fieldPath;
        // Вроде норм
        fieldPath = FilterConfigurator.getConfigFieldFromClass(clazz, field);
        for (String string : fieldPath.split("\\.")) {
            path = path.get(string);
        }
        Predicate predicate;
        switch (operator) {
            case STRING_LIKE:
                predicate = builder.like(path, "%" + value + "%");
                break;
            case STRING_EQUALS:
                predicate = builder.equal(path, value);
                break;
            case STRING_NOT_LIKE:
                predicate = builder.notLike(path, "%" + value + "%");
                break;
            case STRING_NOT_EQUALS:
                predicate = builder.notEqual(path, value);
                break;
            default:
                throw new IllegalArgumentException("Operator is illegal for string");
        }
        return predicate;
    }
}
