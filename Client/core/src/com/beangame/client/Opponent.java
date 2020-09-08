package com.beangame.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/* Each opponent will be allocated a square on the screen for relevant information to be displayed.
 * The information includes: coins, cards in hand, beans planted
 * All information in the opponent class will be updated from server-related input
 */
public class Opponent {

    private BitmapFont font;
    private GlyphLayout layout;

    private String name;
    private float x;
    private float y;
    private int playerNum;
    private int bean1;
    private int bean2;
    private int bean1Number;
    private int bean2Number;
    private int coins;

    public Opponent(String name, int playerNum, float x, float y) {
        this.name = name;
        this.playerNum = playerNum;
        this.x = x;
        this.y = y;

        font = new BitmapFont();
        layout = new GlyphLayout();
        font.setColor(Color.WHITE);

        coins = 0;
        bean1 = -1;
        bean2 = -1;
        bean1Number = 0;
        bean2Number = 0;
    }

    public String getName() {
        return name;
    }

    public int getCoins() {
        return coins;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void setCoins(int num) {
        coins = num;
    }

    public void resetBean1() {
        bean1 = -1;
        bean1Number = 0;
    }

    public void resetBean2() {
        bean2 = -1;
        bean2Number = 0;
    }

    public void setBean1Number(int num) {
        bean1Number = num;
    }

    public void setBean2Number(int num) {
        bean2Number = num;
    }

    public void setBean1(int cardVal) {
        bean1 = cardVal;
    }

    public void setBean2(int cardVal) {
        bean2 = cardVal;
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.enableBlending();
        batch.draw(Assets.opponentMat, x, y, Settings.OPPONENT_BOX_WIDTH, Settings.OPPONENT_BOX_HEIGHT);
        layout.setText(font, name);
        font.draw(batch, layout, x + Settings.OPPONENT_BOX_WIDTH/2 - layout.width, y + Settings.OPPONENT_BOX_HEIGHT-10);
        batch.draw(Assets.coin, x + 10, y + Settings.OPPONENT_BOX_HEIGHT-25, 15, 15);
        layout.setText(font, "" + coins);
        font.draw(batch, layout, x + 15, y + Settings.OPPONENT_BOX_HEIGHT-17-layout.height/2);
        if(bean1 != -1) {
            batch.draw(Assets.beans[bean1], x + 50, y + 30, 36, 63);
            layout.setText(font, "" + bean1Number);
            font.draw(batch, layout, x + 68-layout.width/2, y + 25);
        }
        if(bean2 != -1) {
            batch.draw(Assets.beans[bean2], x + 96, y + 30, 36, 63);
            layout.setText(font, "" + bean2Number);
            font.draw(batch, layout, x + 114-layout.width/2, y + 25);
        }
        batch.end();
    }

}
