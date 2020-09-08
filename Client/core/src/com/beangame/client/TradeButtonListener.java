package com.beangame.client;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TradeButtonListener extends ClickListener {

    public Hand hand;

    public int i;

    public TradeButtonListener(int i) {
        this.i = i;
    }

    public TradeButtonListener(int i, Hand hand) {
        this.i = i;
        this.hand = hand;
    }

}
