package org.meteoinit.skywatch.model;

public enum OperatorType {
    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUAL("=");

    private final String symbol;

    OperatorType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static OperatorType fromString(String value) {
        switch (value) {
            case ">":
                return GREATER_THAN;
            case "<":
                return LESS_THAN;
            case "=":
                return EQUAL;
            default:
                throw new IllegalArgumentException("Unknown operator: " + value);
        }
    }
}