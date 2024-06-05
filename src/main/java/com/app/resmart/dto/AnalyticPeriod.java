package com.app.resmart.dto;

import java.time.Month;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticPeriod {
    private Map<Month, Integer> countOfOrder;
    private Map<Month, Float> earnedMoney;
    private Map<Month, Integer> countOfClients;
}
