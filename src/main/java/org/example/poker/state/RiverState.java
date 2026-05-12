package org.example.poker.state;

import org.example.poker.model.GameMatch;

public class RiverState implements PokerState {
    @Override
    public void handle(GameMatch context) {
        System.out.println("\n=== [TRẠNG THÁI: RIVER] ===");
        context.dealRiver();
        context.displayCommunityCards();
        
        // Reset tiền cược của vòng trước
        context.resetPlayersBets();
        context.handleBettingRound();
        
        context.setState(new ShowdownState());
    }
}
