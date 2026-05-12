package org.example.poker.state;

import org.example.poker.model.GameMatch;

public class FlopState implements PokerState {
    @Override
    public void handle(GameMatch context) {
        System.out.println("\n=== [TRẠNG THÁI: FLOP] ===");
        context.dealFlop();
        context.displayCommunityCards();
        
        // Reset tiền cược của vòng trước để bắt đầu vòng cược mới
        context.resetPlayersBets();
        context.handleBettingRound();
        
        if (canContinue(context)) {
            context.setState(new TurnState());
        } else {
            context.setState(new ShowdownState());
        }
    }

    private boolean canContinue(GameMatch context) {
        return context.getPlayers().stream().filter(p -> !p.isFolded()).count() > 1;
    }
}
