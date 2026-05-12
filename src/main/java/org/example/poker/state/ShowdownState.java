package org.example.poker.state;

import org.example.poker.app.CardVisualizer;
import org.example.poker.logic.HandEvaluator;
import org.example.poker.model.GameMatch;
import org.example.poker.model.Player;

public class ShowdownState implements PokerState {
    @Override
    public void handle(GameMatch context) {
        System.out.println("\n=== [TRẠNG THÁI: SHOWDOWN] ===");
        System.out.println("\n=== KẾT QUẢ CUỐI CÙNG ===");
        
        context.displayCommunityCards();
        System.out.println("-------------------------------------------");

        for (Player p : context.getPlayers()) {
            if (!p.isFolded()) {
                System.out.println("Bài của " + p.getName() + (p.isHuman() ? " (Bạn):" : ":"));
                CardVisualizer.printHand(p.getHand());
            } else {
                System.out.println(p.getName() + " đã Fold.");
            }
        }
        System.out.println("-------------------------------------------");

        Player winner = HandEvaluator.evaluate(context.getPlayers(), context.getCommunityCards());

        if (winner != null) {
            System.out.println("\n>>> NGƯỜI THẮNG CUỘC: " + winner.getName().toUpperCase() + " <<<");
            System.out.println("Số tiền thắng (Pot): $" + context.getTotalPot());
            winner.setBalance(winner.getBalance() + context.getTotalPot());
        } else {
            System.out.println("Không có ai thắng (Mọi người đều Fold).");
        }

        System.out.println("\n=== TỔNG KẾT TÀI KHOẢN ===");
        for (Player p : context.getPlayers()) {
            System.out.println(p.getName() + ": $" + p.getBalance());
        }
        
        // Kết thúc trò chơi
        context.setState(null);
    }
}
