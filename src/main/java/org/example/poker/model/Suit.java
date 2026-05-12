package org.example.poker.model;

/**
 * Represents the four suits in a standard deck of cards.
 */
public enum Suit {
    SPADES("Spades", "♠"),
    CLUBS("Clubs", "♣"),
    DIAMONDS("Diamonds", "♦"),
    HEARTS("Hearts", "♥");

    private final String displayName;
    private final String symbol;

    Suit(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
