package com.grabstat.utils.filters;

import com.grabstat.utils.filters.entities.*;
import com.grabstat.utils.filters.entities.enums.TypeOperator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Slf4j
public class FilterConfigurator {

    private static Collection stringOperators = Set.of(
            TypeOperator.STRING_EQUALS, TypeOperator.STRING_NOT_EQUALS,
            TypeOperator.STRING_LIKE, TypeOperator.STRING_NOT_LIKE
    );

    public static StatsFilterConfigs filterForClass(Class clazz) throws NoSuchFieldException {
        StatsFilterConfigs statsFilterConfigs = new StatsFilterConfigs();
        HashMap<String, StatsFilterConfig> map = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() != String.class) {
                continue;
            }
            field.setAccessible(true);
            FilterConfig annotation = field.getDeclaredAnnotation(FilterConfig.class);
            if (annotation != null && annotation.ignore()) {
                continue;
            }
            String fieldName = field.getName();
            map.put(fieldName, new StatsFilterConfig(fieldName, "stringHelper", stringOperators, null));
        }
        statsFilterConfigs.setFields(map);
        return statsFilterConfigs;
    }


    public static Class getOriginClassFromDto(Class clazz) {
        FilterConfig annotation = (FilterConfig) clazz.getDeclaredAnnotation(FilterConfig.class);
        if (annotation == null) {
            throw new IllegalArgumentException("No origin class for filtered annotation");
        }
        String className = annotation.origin();
        Class originClass;
        try {
            originClass = Class.forName(className);
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException("Origin class not found by name");
        }
        return originClass;
    }

    public static String getConfigFieldFromClass(Class clazz, String fieldName) {
        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            return null;
        }
        FilterConfig annotation = field.getDeclaredAnnotation(FilterConfig.class);
        if (annotation != null && annotation.ignore()) {
            return null;
        }
        if (annotation != null && !annotation.field().equals("")) {
            return annotation.field();
        }
        return fieldName;
    }

    public static String getDateFieldFromClass(Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            FilterConfig annotation = field.getDeclaredAnnotation(FilterConfig.class);
            if (annotation != null && annotation.isDate()) {
                if (annotation.field().equals("")) {
                    return field.getName();
                }
                return annotation.field();
            }
        }
        return null;
    }

    public static boolean ValidateFilters(Class clazz, StatsFilter filter) {
        return ValidateFilter(clazz, filter.getFilterQuery());
    }

    public static boolean ValidateFilter(Class clazz, StatsFilterAbstract filter) {
        if (filter.getClass() == StatsFilterBlock.class) {
            for (StatsFilterAbstract filters : ((StatsFilterBlock) filter).getRules()) {
                if (!ValidateFilter(clazz, filters)) {
                    return false;
                }
            }
        } else if (filter.getClass() == StatsFilterRule.class) {
            if (getConfigFieldFromClass(clazz, ((StatsFilterRule) filter).getField()) == null) {
                return false;
            }
        }
        return true;
    }
}
