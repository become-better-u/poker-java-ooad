package org.example.poker.state;

import org.example.poker.model.GameMatch;

public class PreFlopState implements PokerState {
    @Override
    public void handle(GameMatch context) {
        System.out.println("\n=== [TRẠNG THÁI: PRE-FLOP] ===");
        context.getDeck().shuffle();
        context.dealInitialCards();

        // Thu tiền mù (Blinds)
        context.resetPlayersBets();
        context.postBlinds(50, 100);

        context.handleBettingRound();

        if (canContinue(context)) {
            context.setState(new FlopState());
        } else {
            context.setState(new ShowdownState());
        }
    }

    private boolean canContinue(GameMatch context) {
        return context.getPlayers().stream().filter(p -> !p.isFolded()).count() > 1;
    }
}
