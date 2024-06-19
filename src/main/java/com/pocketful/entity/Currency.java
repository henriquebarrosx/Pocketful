package com.pocketful.entity;

import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Setter
@AllArgsConstructor
public class Currency {
    private float amount;

    public String getValue() {
        Locale brazilLocale = new Locale("pt", "BR");
        DecimalFormat currencyFormat = new DecimalFormat("R$###,###,###,##0.00", new DecimalFormatSymbols(brazilLocale));
        return currencyFormat.format(amount);
    }
}