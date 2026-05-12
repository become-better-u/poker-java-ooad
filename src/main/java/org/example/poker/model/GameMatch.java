package org.example.poker.model;

import org.example.poker.app.CardVisualizer;
import org.example.poker.state.PokerState;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameMatch {
    private final List<Player> players;
    private final List<Card> communityCards;
    private final Deck deck;
    private PokerState currentState;
    private double totalPot;
    private final Scanner scanner;

    public GameMatch(Scanner scanner) {
        this.players = new ArrayList<>();
        this.communityCards = new ArrayList<>();
        this.deck = new Deck();
        this.totalPot = 0;
        this.scanner = scanner;
    }

    public void addToPot(double amount) {
        this.totalPot += amount;
    }

    public double getTotalPot() {
        return totalPot;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void setState(PokerState state) {
        this.currentState = state;
    }

    public void nextStep() {
        if (currentState != null) {
            currentState.handle(this);
        }
    }

    public void dealInitialCards() {
        System.out.println("Đang chia bài cho người chơi...");
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                Card card = deck.dealCard();
                if (card != null) {
                    player.receiveCard(card);
                }
            }
        }
    }

    public void dealFlop() {
        System.out.println("Đang chia Flop (3 lá bài chung)...");
        deck.dealCard(); // burn card
        for (int i = 0; i < 3; i++) {
            Card card = deck.dealCard();
            if (card != null) {
                addCommunityCard(card);
            }
        }
    }

    public void dealTurn() {
        System.out.println("Đang chia Turn (lá bài chung thứ 4)...");
        deck.dealCard(); // burn card
        Card card = deck.dealCard();
        if (card != null) {
            addCommunityCard(card);
        }
    }

    public void dealRiver() {
        System.out.println("Đang chia River (lá bài chung thứ 5)...");
        deck.dealCard(); // burn card
        Card card = deck.dealCard();
        if (card != null) {
            addCommunityCard(card);
        }
    }

    public void handleBettingRound() {
        // Tìm mức cược cao nhất hiện tại (ví dụ từ tiền mù)
        double currentHighestBet = 0;
        for (Player p : players) {
            if (p.getCurrentBetInRound() > currentHighestBet) {
                currentHighestBet = p.getCurrentBetInRound();
            }
        }
        
        boolean roundFinished = false;

        while (!roundFinished) {
            roundFinished = true;
            for (Player p : players) {
                if (p.isFolded() || p.getBalance() <= 0)
                    continue;

                long activeCount = players.stream().filter(pl -> !pl.isFolded()).count();
                if (activeCount <= 1) {
                    roundFinished = true;
                    break;
                }

                double needToCall = currentHighestBet - p.getCurrentBetInRound();

                if (needToCall > 0 || roundFinished) {
                    if (p.isHuman()) {
                        CardVisualizer.printHand(p.getHand());
                        System.out.println("\nLượt của " + p.getName() + ". Cần theo: $" + needToCall);
                        System.out.println("1. Check/Call  2. Raise  3. Fold  4. All-in");
                        
                        if (scanner.hasNextInt()) {
                            int choice = scanner.nextInt();
                            switch (choice) {
                                case 1:
                                    if (needToCall > 0) {
                                        p.bet(needToCall);
                                        addToPot(needToCall);
                                        System.out.println(p.getName() + " đã Call $" + needToCall);
                                    } else {
                                        System.out.println(p.getName() + " đã Check.");
                                    }
                                    break;
                                case 2:
                                    System.out.print("Nhập số tiền muốn Raise thêm: ");
                                    if (scanner.hasNextDouble()) {
                                        double raiseAmount = scanner.nextDouble();
                                        double totalBet = needToCall + raiseAmount;
                                        p.bet(totalBet);
                                        addToPot(totalBet);
                                        currentHighestBet = p.getCurrentBetInRound();
                                        System.out.println(p.getName() + " đã Raise lên $" + currentHighestBet);
                                        roundFinished = false;
                                    }
                                    break;
                                case 3:
                                    p.setFolded(true);
                                    System.out.println(p.getName() + " đã Fold!");
                                    break;
                                case 4:
                                    double allInAmount = p.getBalance();
                                    p.bet(allInAmount);
                                    addToPot(allInAmount);
                                    if (p.getCurrentBetInRound() > currentHighestBet) {
                                        currentHighestBet = p.getCurrentBetInRound();
                                        roundFinished = false;
                                    }
                                    System.out.println(p.getName() + " đã ALL-IN với $" + allInAmount);
                                    break;
                            }
                        }
                    } else {
                        // Logic đơn giản cho Bot
                        if (needToCall > 0) {
                            p.bet(needToCall);
                            addToPot(needToCall);
                            System.out.println(p.getName() + " (Bot) đã Call $" + needToCall);
                        } else {
                            System.out.println(p.getName() + " (Bot) đã Check.");
                        }
                    }
                }
            }
            for (Player p : players) {
                if (!p.isFolded() && p.getBalance() > 0 && p.getCurrentBetInRound() < currentHighestBet) {
                    roundFinished = false;
                }
            }
        }
    }

    public void postBlinds(double smallBlind, double bigBlind) {
        if (players.size() < 2)
            return;

        // Giả thiết Player 1 là SB, Player 2 là BB
        Player sbPlayer = players.get(1 % players.size());
        Player bbPlayer = players.get(2 % players.size());

        sbPlayer.bet(smallBlind);
        addToPot(smallBlind);
        System.out.println(sbPlayer.getName() + " đã đặt Small Blind: $" + smallBlind);

        bbPlayer.bet(bigBlind);
        addToPot(bigBlind);
        System.out.println(bbPlayer.getName() + " đã đặt Big Blind: $" + bigBlind);
    }

    public void resetPlayersBets() {
        for (Player p : players) {
            p.resetBet();
        }
    }

    public void displayCommunityCards() {
        System.out.println("\nCÁC LÁ BÀI CHUNG (COMMUNITY CARDS):");
        CardVisualizer.printHand(communityCards);
        System.out.println("Pot hiện tại: $" + totalPot);
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public void addCommunityCard(Card card) {
        communityCards.add(card);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public boolean isGameOver() {
        return currentState == null;
    }
}
