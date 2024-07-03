package com.pocketful.entity;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Setter
@AllArgsConstructor
public class Currency {
    private float amount;

    public String getValue() {
        Locale brazilLocale = new Locale("pt", "BR");
        DecimalFormat currencyFormat = new DecimalFormat("R$ ###,###,###,##0.00", new DecimalFormatSymbols(brazilLocale));
        return currencyFormat.format(amount);
    }
}