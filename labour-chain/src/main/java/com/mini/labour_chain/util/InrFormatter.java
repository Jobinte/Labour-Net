package com.mini.labour_chain.util;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Component
public class InrFormatter {
    private static final Locale IN_LOCALE = new Locale("en", "IN");

    public String fmt(Number amount) {
        if (amount == null) return "";
        NumberFormat nf = NumberFormat.getCurrencyInstance(IN_LOCALE);
        return nf.format(amount.doubleValue());
    }
}
