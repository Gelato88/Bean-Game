package com.beangame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TradeOffer {

    private BitmapFont font;
    private GlyphLayout layout;
    private Stage stage;

    private Button accept;
    private Button reject;

    private BeanGame game;

    private float startX1;
    private float startX2;
    private int[] requested;
    private int[] offered;
    private int[] actives;

    public TradeOffer(BeanGame game) {
        this.game = game;
        font = new BitmapFont();
        layout = new GlyphLayout();
        stage = new Stage();

        generateElements();
        stage.addActor(accept);
        stage.addActor(reject);

        requested = new int[Assets.beans.length];
        offered = new int[Assets.beans.length];
        actives = new int[2];
        startX1 = Settings.RES_WIDTH/2 - Settings.TRADE_OFFER_WIDTH/4 - 84;
        startX2 = Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 84;
    }

    private void generateElements() {
        accept = new Button(Assets.buttonSkin, "send");
        reject = new Button(Assets.buttonSkin, "close");

        accept.setSize(50, 50);
        reject.setSize(50, 50);

        accept.setPosition(Settings.RES_WIDTH/2 + 10, 80);
        reject.setPosition(Settings.RES_WIDTH/2 - 60, 80);

        accept.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                accept();
            }
        });
        reject.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                reject();
            }
        });
    }

    public void render(SpriteBatch batch, float mouseX, float mouseY) {
        batch.begin();
        batch.enableBlending();
        batch.draw(Assets.offerBackground, Settings.RES_WIDTH/2 - Settings.TRADE_OFFER_WIDTH/2, Settings.RES_HEIGHT/2 - Settings.TRADE_OFFER_HEIGHT/2, Settings.TRADE_OFFER_WIDTH, Settings.TRADE_OFFER_HEIGHT);
        for(int i = 0; i < Assets.beans.length; i++) {
            layout.setText(font, "" + requested[i]);
            font.draw(batch, layout, startX1 + 60*(i%3) + 48/2 - layout.width/2, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 + 90 - 90*(i/3));
            batch.draw(Assets.beans[i], startX1 + 60*(i%3), Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 90*(i/3),  Settings.CARD_WIDTH * 0.4f, Settings.CARD_HEIGHT * 0.4f);
            layout.setText(font, "" + offered[i]);
            font.draw(batch, layout, startX2 + 60*(i%3) + 48/2 - layout.width/2, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 + 90 - 90*(i/3));
            batch.draw(Assets.beans[i], startX2 + 60*(i%3), Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 90*(i/3),  Settings.CARD_WIDTH * 0.4f, Settings.CARD_HEIGHT * 0.4f);
        }
        layout.setText(font, "1");
        if(actives[0] == 1 && game.getPlayer().getFlipped()[0] != null && actives[1] == 1 && game.getPlayer().getFlipped()[1] != null) {
            font.draw(batch, layout, Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 30 - layout.width/2, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 284);
            font.draw(batch, layout, Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 + 30 - layout.width/2, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 284);
            batch.draw(Assets.beans[game.getPlayer().getFlipped()[0].getCardVal()], Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 54, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 380, 48, 84);
            batch.draw(Assets.beans[game.getPlayer().getFlipped()[1].getCardVal()], Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 + 6, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 380, 48, 84);
        } else if(actives[0] == 1 && game.getPlayer().getFlipped()[0] != null) {
            font.draw(batch, layout, Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 30 - layout.width/2, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 284);
            batch.draw(Assets.beans[game.getPlayer().getFlipped()[0].getCardVal()], Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 54, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 380, 48, 84);
        } else if(actives[1] == 1 && game.getPlayer().getFlipped()[1] != null) {
            font.draw(batch, layout, Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 30 - layout.width/2, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 284);
            batch.draw(Assets.beans[game.getPlayer().getFlipped()[1].getCardVal()], Settings.RES_WIDTH/2 + Settings.TRADE_OFFER_WIDTH/4 - 54, Settings.RES_HEIGHT/2 + Settings.TRADE_OFFER_HEIGHT/6 - 380, 48, 84);
        }
        batch.end();
        if(Gdx.input.isKeyPressed(Settings.KEY_ZOOM)) {
            for(int i = 0; i < Assets.beans.length; i++) {
                if(mouseX > startX1+60*(i%3) && mouseX < startX1+60*(i%3)+Settings.CARD_WIDTH*0.4f && mouseY > Settings.RES_HEIGHT/2+Settings.TRADE_OFFER_HEIGHT/6-90*(i/3) && mouseY < Settings.RES_HEIGHT/2+Settings.TRADE_OFFER_HEIGHT/6-90*(i/3)+Settings.CARD_HEIGHT*0.4f) {
                    game.getPlayer().setZoomedCard(i);
                } else if(mouseX > startX2+60*(i%3) && mouseX < startX2+60*(i%3)+Settings.CARD_WIDTH*0.4f && mouseY > Settings.RES_HEIGHT/2+Settings.TRADE_OFFER_HEIGHT/6-90*(i/3) && mouseY < Settings.RES_HEIGHT/2+Settings.TRADE_OFFER_HEIGHT/6-90*(i/3)+Settings.CARD_HEIGHT*0.4f) {
                    game.getPlayer().setZoomedCard(i);
                }
            }
            if(mouseY > Settings.RES_HEIGHT/2+Settings.TRADE_OFFER_HEIGHT/6-380 && mouseY < Settings.RES_HEIGHT/2+Settings.TRADE_OFFER_HEIGHT/6-380+84) {
                if (actives[0] == 1 && game.getPlayer().getFlipped()[0] != null && actives[1] == 1 && game.getPlayer().getFlipped()[1] != null) {
                    if(mouseX > Settings.RES_WIDTH/2+Settings.TRADE_OFFER_WIDTH/4-54 && mouseX < Settings.RES_WIDTH/2+Settings.TRADE_OFFER_WIDTH/4-6) {
                        game.getPlayer().setZoomedCard(game.getPlayer().getFlipped()[0].getCardVal());
                    } else if(mouseX > Settings.RES_WIDTH/2+Settings.TRADE_OFFER_WIDTH/4+6 && mouseX < Settings.RES_WIDTH/2+Settings.TRADE_OFFER_WIDTH/4+54) {
                        game.getPlayer().setZoomedCard(game.getPlayer().getFlipped()[1].getCardVal());
                    }
                }
            }
        }
        stage.draw();
        stage.act();
    }

    public Stage getStage() {
        return stage;
    }

    /*
     * Sets the values of the trade being offered
     */
    public void setValues(int[] requested, int[] offered, int[] actives) {
        for(int i = 0; i < requested.length; i++) {
            this.requested[i] = requested[i];
        }
        for(int i = 0; i < offered.length; i++) {
            this.offered[i] = offered[i];
        }
        for(int i = 0; i < actives.length; i++) {
            this.actives[i] = actives[i];
        }
    }

    /*
     * Accepts the trade offer
     */
    public void accept() {
        boolean acceptable = true;
        for(int i = 0; i < requested.length; i++) {
            if(requested[i] > game.getPlayer().getHand().getCardNum(i)) {
                acceptable = false;
            }
        }
        if(acceptable) {
            game.showTradeOffer = false;
            game.sendMessage(4001, "" + game.playerNumber);
            for(int i = 0; i < requested.length; i++) {
                for(int j = 0; j < requested[i]; j++) {
                    game.getPlayer().getHand().removeCardType(i);
                }
            }
            for(int i = 0; i < offered.length; i++) {
                for(int j = 0; j < offered[i]; j++) {
                    game.getPlayer().getTradedHand().addCard(i);
                }
            }
            for(int i = 0; i < actives.length; i++) {
                if(actives[i] == 1) {
                    game.getPlayer().getTradedHand().addCard(game.getPlayer().getFlipped()[i].getCardVal());
                    game.sendMessage(4006, ""+i);
                }
            }
        }
    }

    /*
     * Rejects the trade offer
     */
    public void reject() {
        game.showTradeOffer = false;
        game.sendMessage(4002, ""+game.playerNumber);
    }


}
