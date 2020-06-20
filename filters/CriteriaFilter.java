package com.grabstat.utils.filters;

import com.grabstat.utils.filters.entities.StatsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Component
@Slf4j
public class CriteriaFilter {
    private final EntityManager entityManager;
    private CriteriaBuilder builder;
    private CriteriaQuery query;
    private Root root;

    @Autowired
    public CriteriaFilter(EntityManager entityManager) {
        this.entityManager = entityManager;
        builder = entityManager.getCriteriaBuilder();
    }

    public List<String> getPathValues(Class clazz, String field) {
        Class originClass = FilterConfigurator.getOriginClassFromDto(clazz);
        query = builder.createQuery(originClass);
        root = query.from(originClass);
        field = FilterConfigurator.getConfigFieldFromClass(clazz, field);
        Path path = root;
        for (String string : field.split("\\.")) {
            path = path.get(string);
        }
        TypedQuery typedQuery = entityManager.createQuery(query.select(path).distinct(true));
        return typedQuery.getResultList();
    }

    public TypedQuery getQueryForFilter(Class clazz, StatsFilter filter) {
        if (!FilterConfigurator.ValidateFilters(clazz, filter)) {
            throw new FilterException("Получены некорректные фильтра");
        }
        Class originClass = FilterConfigurator.getOriginClassFromDto(clazz);
        log.info(originClass.getName());
        query = builder.createQuery(originClass);
        root = query.from(originClass);
        Predicate predicate = filter.getFilterQuery().getPredicate(builder, clazz, root);
        String dateField = FilterConfigurator.getDateFieldFromClass(clazz);
        Path path = root;
        if (filter.getDateSince() != null && filter.getDateUntil() != null &&
                dateField != null && !dateField.equals("")) {
            for (String field : dateField.split("\\.")) {
                path = path.get(field);
            }
            if (predicate == null) {
                predicate = builder.and(
                        builder.greaterThanOrEqualTo(path, filter.getDateSince()),
                        builder.lessThanOrEqualTo(path, filter.getDateUntil()));
            } else {
                predicate = builder.and(
                        predicate,
                        builder.greaterThanOrEqualTo(path, filter.getDateSince()),
                        builder.lessThanOrEqualTo(path, filter.getDateUntil()));
            }
        }
        if (predicate != null) {
            query = query.select(root).where(predicate);
        } else {
            query = query.select(root);
        }

        TypedQuery typedQuery = entityManager.createQuery(query);
        return typedQuery;
    }

    /*    public String getQuery(TypedQuery query) {
        String hqlQueryString = query.unwrap(org.hibernate.query.Query.class).getQueryString();
        ASTQueryTranslatorFactory queryTranslatorFactory = new ASTQueryTranslatorFactory();
        SessionImplementor hibernateSession = entityManager.unwrap(SessionImplementor.class);
        QueryTranslator queryTranslator = queryTranslatorFactory.createQueryTranslator("", hqlQueryString, java.util.Collections.EMPTY_MAP, hibernateSession.getFactory(), null);
        queryTranslator.compile(java.util.Collections.EMPTY_MAP, false);
        return queryTranslator.getSQLString();
    }*/

/*    public Predicate getStringExpressionFromFilter(StatsFilterRule rule) {
        Path path = root;
        Boolean ok = false;
        for (String string : rule.getField().split("\\.")) {
            if (!ok) {
                ok = true;
                continue;
            }
            path = path.get(string);
        }
        String value = rule.getValue();
        Predicate predicate;
        switch (rule.getOperator()) {
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
    }*/

/*    public TypedQuery test(Class clazz, StatsFilter filter) {
        query = builder.createQuery(clazz);
        root = query.from(clazz);
        StatsFilterBlock block = (StatsFilterBlock) filter.getFilterQuery();
        ArrayList<Predicate> predicates = new ArrayList<>();
        for (StatsFilterAbstract subFilter : block.getRules()) {
            if (subFilter.getClass() == StatsFilterRule.class) {
                predicates.add(getStringExpressionFromFilter((StatsFilterRule) subFilter));
            } else if (subFilter.getClass() == StatsFilterBlock.class) {

            }
        }
        Predicate finalPredicate = builder.and(predicates.toArray(new Predicate[0]));
        TypedQuery typedQuery = entityManager.createQuery(query.select(root).where(finalPredicate));
        log.info(getQuery(typedQuery));
        return typedQuery;
    }*/
}
