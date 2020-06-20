package com.grabstat.utils.filters.entities;

import lombok.Data;

import java.util.Map;

@Data
public class StatsFilterConfigs {
    Map<String, StatsFilterConfig> fields;
}
