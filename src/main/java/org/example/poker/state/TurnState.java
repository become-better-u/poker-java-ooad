package org.example.poker.state;

import org.example.poker.model.GameMatch;

public class TurnState implements PokerState {
    @Override
    public void handle(GameMatch context) {
        System.out.println("\n=== [TRẠNG THÁI: TURN] ===");
        context.dealTurn();
        context.displayCommunityCards();
        
        // Reset tiền cược của vòng trước
        context.resetPlayersBets();
        context.handleBettingRound();
        
        if (canContinue(context)) {
            context.setState(new RiverState());
        } else {
            context.setState(new ShowdownState());
        }
    }

    private boolean canContinue(GameMatch context) {
        return context.getPlayers().stream().filter(p -> !p.isFolded()).count() > 1;
    }
}
