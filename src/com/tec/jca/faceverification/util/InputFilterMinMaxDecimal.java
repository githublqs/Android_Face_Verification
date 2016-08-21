package com.tec.jca.faceverification.util;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMaxDecimal implements InputFilter {

    private double min, max;

    public InputFilterMinMaxDecimal(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMaxDecimal(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {   
        try {
        	double input = Double.parseDouble(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }     
        return "";
    }

    private boolean isInRange( double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}