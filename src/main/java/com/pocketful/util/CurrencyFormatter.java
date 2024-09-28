package com.pocketful.util;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Setter
@AllArgsConstructor
public abstract class CurrencyFormatter {
    public static String apply(BigDecimal amount) {
        Locale brazilLocale = new Locale("pt", "BR");
        DecimalFormat currencyFormat = new DecimalFormat("R$ ###,###,###,##0.00", new DecimalFormatSymbols(brazilLocale));
        return currencyFormat.format(amount);
    }
}