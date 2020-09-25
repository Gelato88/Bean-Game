package com.beangame.client;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/* Represents a card
 * A card only has a texture to indicate its type and is never updated.
 */
public class Card {

    private int cardVal;
    private TextureRegion texture;

    public Card(int cardVal) {
        this.cardVal = cardVal;
        texture = Assets.beans[cardVal];
    }

    public int getCardVal() {
        return cardVal;
    }

    public TextureRegion getTexture() {
        return texture;
    }

}
