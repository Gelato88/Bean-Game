package com.beangame.client;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TradedHandListener extends ClickListener {

    public Card c;
    public Button b;

    public TradedHandListener(Card c, Button b) {
        this.c = c;
        this.b = b;
    }

}
