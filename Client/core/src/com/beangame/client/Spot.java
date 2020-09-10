package com.beangame.client;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/* A spot represents a space on the player's mat where beans may be planted.
 * The spot retains the type of bean and the number of beans.
 * All information is updated client-side and sent to the server.
 */
public class Spot {

    private int cardVal;

    private float x;
    int spotNum;
    private int cards;
    private int coins1;
    private int coins2;
    private int coins3;
    private int coins4;

    public Spot(float x, int spotNum) {
        this.x = x;
        this.spotNum = spotNum;
        cardVal = -1;
    }

    public int getSpotNum() {
        return spotNum;
    }

    private void setCoins(int coins1, int coins2, int coins3, int coins4) {
        this.coins1 = coins1;
        this.coins2 = coins2;
        this.coins3 = coins3;
        this.coins4 = coins4;
    }

    public boolean isOpen() {
        return cards == 0;
    }

    public void setType(int cardVal) {
        this.cardVal = cardVal;

        switch(cardVal) {
            case 0:
                setCoins(2,4,5,6);
                break;
            case 1:
                setCoins(4,6,8,10);
                break;
            case 2:
                setCoins(3,6,8,9);
                break;
            case 3:
                setCoins(2,2,3,4);
                break;
            case 4:
                setCoins(4,7,10,12);
                break;
            case 5:
                setCoins(2,2,3,100);
                break;
            case 6:
                setCoins(3,5,6,7);
                break;
            case 7:
                setCoins(2,3,4,5);
                break;
            case 8:
                setCoins(2,4,6,7);
                break;
            case 9:
                setCoins(3,5,7,8);
                break;
            case 10:
                setCoins(4,7,9,11);
                break;
        }
    }

    public int getType() {
        return cardVal;
    }

    public int getCards() {
        return cards;
    }

    public void addCard() {
        cards++;
    }

    public void render(SpriteBatch batch) {
        if(cardVal != -1) {
            batch.begin();
            batch.enableBlending();
            for (int i = 0; i < cards; i++) {
                batch.draw(Assets.beans[cardVal], x, 390 - 30 * i, Settings.CARD_WIDTH, Settings.CARD_HEIGHT);
            }
            batch.end();
        }
    }

    public int harvest() {
        int value = 0;
        if(cards >= coins1) {
            value++;
        } if(cards >= coins2) {
            value++;
        } if(cards >= coins3) {
            value++;
        } if(cards >= coins4) {
            value++;
        }
        cards = 0;
        return value;
    }

}
