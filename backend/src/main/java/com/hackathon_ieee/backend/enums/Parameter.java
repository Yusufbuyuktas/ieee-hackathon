package com.hackathon_ieee.backend.enums;

/**
 * Arkoc (2014) ve Aydin ve ark. (2026) makalelerinde ölçülen 9 element.
 * Yeni bir element eklenirse buraya ve ThresholdConfig'e (varsa esik degeriyle) eklenmeli.
 */
public enum Parameter {
    ARSENIC("As"),
    COPPER("Cu"),
    IRON("Fe"),
    ZINC("Zn"),
    CHROMIUM("Cr"),
    CADMIUM("Cd"),
    LEAD("Pb"),
    NICKEL("Ni"),
    MANGANESE("Mn");

    private final String symbol;

    Parameter(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
