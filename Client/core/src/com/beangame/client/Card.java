package com.beangame.client;

import com.badlogic.gdx.graphics.Texture;

/* Represents a card only when it is in this player's hand.
 * A card only has a texture to indicate its type and is never updated.
 */
public class Card {

    private int cardVal;
    private Texture texture;

    public Card(int cardVal) {
        this.cardVal = cardVal;
        texture = Assets.beans[cardVal];
    }

    public int getCardVal() {
        return cardVal;
    }

    public Texture getTexture() {
        return texture;
    }

}
