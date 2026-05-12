package org.example.poker.state;

import org.example.poker.model.GameMatch;

public interface PokerState {
    void handle(GameMatch context);
}
