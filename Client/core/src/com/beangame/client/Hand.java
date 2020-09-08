package com.beangame.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Hand {

    private ArrayList<Card> cards;

    private int[] held;

    public Hand() {
        held = new int[Assets.beans.length];
        for(int i = 0; i < held.length; i++) {
            held[i] = 0;
        }
        cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void incrementCard(int index, int delta) {
        held[index] += delta;
    }

    public int getCardNum(int index) {
        return held[index];
    }

    public void removeCardType(int cardVal) {
        for(int i = 0; i < cards.size(); i++) {
            if(cards.get(i).getCardVal() == cardVal) {
                cards.remove(i);
                held[cardVal]--;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if(cards.size() > 0) {
            batch.begin();
            batch.enableBlending();
            float startX = Settings.RES_WIDTH / 2 - 400 + (cards.size() - 1) * 40;
            for(int i = cards.size() - 1; i > 0; i--) {
                batch.draw(cards.get(i).getTexture(), startX - 40 * i, 50);
            }
            batch.draw(cards.get(0).getTexture(), Settings.RES_WIDTH / 2 + 200, 50);
            batch.end();
        }
    }

}
