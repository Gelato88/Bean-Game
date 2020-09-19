package com.beangame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

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

    public void render(SpriteBatch batch, float mouseX, float mouseY) {
        if(cards.size() > 0) {
            batch.begin();
            batch.enableBlending();
            float startX = Settings.RES_WIDTH / 2 - 400 + (cards.size() - 1) * 40;
            for(int i = cards.size() - 1; i > 0; i--) {
                batch.draw(cards.get(i).getTexture(), startX - 40 * i, 30, Settings.CARD_WIDTH, Settings.CARD_HEIGHT);
            }
            batch.draw(cards.get(0).getTexture(), Settings.RES_WIDTH / 2 + 200, 30, Settings.CARD_WIDTH, Settings.CARD_HEIGHT);
            if(Gdx.input.isKeyPressed(Settings.KEY_ZOOM)) {
                if(mouseY > 30 && mouseY < 30 + Settings.CARD_HEIGHT) {
                    for (int i = cards.size()-1; i > 1; i--) {
                        if(mouseX > startX - 40*i && mouseX < startX - 40*i + Settings.CARD_WIDTH) {
                            batch.draw(cards.get(i).getTexture(), Settings.RES_WIDTH/2 - Settings.CARD_WIDTH*2/2, Settings.RES_HEIGHT/2 - Settings.CARD_HEIGHT/2, Settings.CARD_WIDTH*2, Settings.CARD_HEIGHT*2);
                        }
                    }
                    if(mouseX > startX - 40 && mouseX < startX - 40 + Settings.CARD_WIDTH) {
                        batch.draw(cards.get(1).getTexture(), Settings.RES_WIDTH/2 - Settings.CARD_WIDTH*2/2, Settings.RES_HEIGHT/2 - Settings.CARD_HEIGHT/2, Settings.CARD_WIDTH*2, Settings.CARD_HEIGHT*2);
                    }
                    if(mouseX > Settings.RES_WIDTH/2+200 && mouseX < Settings.RES_WIDTH/2+200 + Settings.CARD_WIDTH) {
                        batch.draw(cards.get(0).getTexture(), Settings.RES_WIDTH/2 - Settings.CARD_WIDTH*2/2, Settings.RES_HEIGHT/2 - Settings.CARD_HEIGHT/2, Settings.CARD_WIDTH*2, Settings.CARD_HEIGHT*2);
                    }
                }
            }
            batch.end();
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getCardNum(int index) {
        return held[index];
    }

    /*
     * Adds a card to the tracker
     */
    public void incrementCard(int index, int delta) {
        held[index] += delta;
    }

    /*
     * Removes a specified type of card from the hand
     * TODO: needs to allow player to choose which card if multiple instances
     */
    public boolean removeCardType(int cardVal) {
        for(int i = 0; i < cards.size(); i++) {
            if(cards.get(i).getCardVal() == cardVal) {
                cards.remove(i);
                held[cardVal]--;
                return true;
            }
        }
        System.out.println("Card not found");
        return false;
    }
}
