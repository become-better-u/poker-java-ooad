package org.example.poker.app;

import org.example.poker.model.GameMatch;
import org.example.poker.model.Player;
import org.example.poker.state.PreFlopState;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Sử dụng try-with-resources để quản lý tài nguyên duy nhất tại entry point
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Mô phỏng trò chơi Poker (State Pattern) ===");

            System.out.print("Nhập tên của bạn: ");
            String playerName = scanner.nextLine();
            if (playerName.trim().isEmpty()) {
                playerName = "Người chơi";
            }

            // 1. Khởi tạo trận đấu và truyền scanner vào
            GameMatch match = new GameMatch(scanner);
            match.addPlayer(new Player(playerName, 5000, true));
            match.addPlayer(new Player("Bot 1", 5000, false));
            match.addPlayer(new Player("Bot 2", 5000, false));
            match.addPlayer(new Player("Bot 3", 5000, false));

            System.out.println("Danh sách người chơi: " + match.getPlayers());

            // 2. Bắt đầu với trạng thái đầu tiên
            match.setState(new PreFlopState());

            // 3. Vòng lặp điều khiển trạng thái
            while (!match.isGameOver()) {
                match.nextStep();
            }

            System.out.println("\nCảm ơn bạn đã chơi Poker!");
        } 
    }
}
