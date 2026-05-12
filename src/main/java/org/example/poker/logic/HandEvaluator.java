package org.example.poker.logic;

import org.example.poker.model.Card;
import org.example.poker.model.Player;
import org.example.poker.model.Suit;

import java.util.*;
import java.util.stream.Collectors;

public class HandEvaluator {

    public static Player evaluate(List<Player> players, List<Card> communityCards) {
        Player winner = null;
        double maxScore = -1;

        for (Player p : players) {
            if (p.isFolded()) {
                continue;
            }

            List<Card> allCards = new ArrayList<>(p.getHand());
            allCards.addAll(communityCards);

            double score = calculateScore(allCards);
            System.out.println(p.getName() + ": " + getHandName(score));

            if (score > maxScore) {
                maxScore = score;
                winner = p;
            }
        }
        return winner;
    }

    private static double calculateScore(List<Card> cards) {
        // 1. Prepare data
        List<Integer> values = cards.stream()
                .map(c -> c.getRank().getValue())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        Map<Integer, Integer> countsMap = new HashMap<>();
        for (int v : values) {
            countsMap.put(v, countsMap.getOrDefault(v, 0) + 1);
        }

        // 2. Check hand ranks from high to low
        boolean isFlush = checkFlush(cards);
        int straightHighCard = checkStraight(values);

        // Priority order (Each level is separated by 1,000,000 points)
        if (isFlush && straightHighCard > 0) {
            return 8000000 + straightHighCard; // Straight Flush
        }
        if (countsMap.containsValue(4)) {
            return 7000000 + getHighCardByCount(countsMap, 4); // Four of a Kind
        }
        if (countsMap.containsValue(3) && countsMap.containsValue(2)) {
            return 6000000 + getHighCardByCount(countsMap, 3); // Full House
        }
        if (isFlush) {
            return 5000000 + values.get(0); // Flush
        }
        if (straightHighCard > 0) {
            return 4000000 + straightHighCard; // Straight
        }
        if (countsMap.containsValue(3)) {
            return 3000000 + getHighCardByCount(countsMap, 3); // Three of a Kind
        }

        long numPairs = countsMap.values().stream().filter(v -> v == 2).count();
        if (numPairs >= 2) {
            return 2000000 + getHighCardByCount(countsMap, 2); // Two Pair
        }
        if (numPairs == 1) {
            return 1000000 + getHighCardByCount(countsMap, 2); // One Pair
        }

        return values.get(0); // High Card
    }

    private static int checkStraight(List<Integer> values) {
        // Remove duplicates and sort in descending order
        List<Integer> distinctValues = values.stream().distinct().sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        if (distinctValues.size() < 5) {
            return 0;
        }

        for (int i = 0; i <= distinctValues.size() - 5; i++) {
            if (distinctValues.get(i) - distinctValues.get(i + 4) == 4) {
                return distinctValues.get(i);
            }
        }
        // Special case: A-2-3-4-5 Straight
        if (distinctValues.contains(14) && distinctValues.contains(2) && distinctValues.contains(3) &&
                distinctValues.contains(4) && distinctValues.contains(5)) {
            return 5;
        }

        return 0;
    }

    private static boolean checkFlush(List<Card> cards) {
        Map<Suit, Integer> suitCounts = new HashMap<>();
        for (Card c : cards) {
            suitCounts.put(c.getSuit(), suitCounts.getOrDefault(c.getSuit(), 0) + 1);
        }
        return suitCounts.values().stream().anyMatch(count -> count >= 5);
    }

    private static int getHighCardByCount(Map<Integer, Integer> counts, int count) {
        return counts.entrySet().stream()
                .filter(e -> e.getValue() == count)
                .map(Map.Entry::getKey)
                .max(Integer::compare).orElse(0);
    }

    public static String getHandName(double score) {
        if (score >= 8000000) return "Sảnh Thùng (Straight Flush)";
        if (score >= 7000000) return "Tứ Quý (Four of a Kind)";
        if (score >= 6000000) return "Cù Lũ (Full House)";
        if (score >= 5000000) return "Thùng (Flush)";
        if (score >= 4000000) return "Sảnh (Straight)";
        if (score >= 3000000) return "Xám Cô (Three of a Kind)";
        if (score >= 2000000) return "Hai Đôi (Two Pair)";
        if (score >= 1000000) return "Một Đôi (One Pair)";
        return "Mậu Thầu (High Card)";
    }
}