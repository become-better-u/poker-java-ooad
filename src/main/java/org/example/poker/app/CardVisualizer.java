package org.example.poker.app;

import org.example.poker.model.Card;
import java.util.List;

public class CardVisualizer {

    public static void printHand(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("[Trống]");
            return;
        }

        String[] lines = {"", "", ""};

        for (Card card : cards) {
            String r = card.getRank().getSymbol();
            String s = card.getSuit().getSymbol();

            // " A♠" or "10♠" - always 3 characters combined
            String content = String.format("%2s%s", r, s);

            lines[0] += "┌───┐ ";
            lines[1] += String.format("│%s│ ", content);
            lines[2] += "└───┘ ";
        }

        for (String line : lines) {
            System.out.println(line);
        }
    }
}
