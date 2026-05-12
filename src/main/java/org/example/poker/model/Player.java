package org.example.poker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the poker game.
 */
public class Player {
    private final String name;
    private double balance;
    private final List<Card> hand;
    private boolean folded;
    private final boolean isHuman;
    private double currentBetInRound;

    public Player(String name, double balance, boolean isHuman) {
        this.name = name;
        this.balance = balance;
        this.hand = new ArrayList<>();
        this.folded = false;
        this.isHuman = isHuman;
        this.currentBetInRound = 0;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void resetBet() {
        this.currentBetInRound = 0;
    }

    public double getCurrentBetInRound() {
        return currentBetInRound;
    }

    public void bet(double amount) {
        if (amount > balance) {
            amount = balance;
        }
        balance -= amount;
        currentBetInRound += amount;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
        this.folded = false;
    }

    @Override
    public String toString() {
        return name + " ($" + balance + ")";
    }
}
