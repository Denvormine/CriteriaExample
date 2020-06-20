package com.grabstat.utils.filters.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grabstat.facebook.controllers.web.DTO.DeSerializers.CustomLocalDateDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StatsFilter {
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate dateSince;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate dateUntil;
    private StatsFilterAbstract filterQuery;
    private StatsFilterConfigs config;
}
