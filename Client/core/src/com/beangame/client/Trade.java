package com.beangame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class Trade {

    private BitmapFont font;
    private GlyphLayout layout;
    private Stage stage;

    private Button flipped1;
    private Button flipped2;
    private Button close;
    private Button send;
    private Button[] buttons;
    private Button[] buttons2;
    private Button[] checks;
    private Button[] offerChecks;

    private ArrayList<Card> hand;

    private BeanGame game;

    private int waiting;
    private float startX;
    private int[] counts;
    private int[] counts2;
    private boolean[] active;
    private boolean[] activeSelected;
    private boolean[] offered;

    public Trade(BeanGame game) {
        this.game = game;
        stage = new Stage();
        font = new BitmapFont();
        layout = new GlyphLayout();
        font.setColor(Color.WHITE);
        startX = Settings.RES_WIDTH/2 - Assets.beans.length * 60/2 - 100;
        counts = new int[Assets.beans.length];
        counts2 = new int[Assets.beans.length];
        buttons = new Button[Assets.beans.length * 2];
        buttons2 = new Button[Assets.beans.length * 2];
        waiting = 0;
        activeSelected = new boolean[2];
        activeSelected[0] = false;
        activeSelected[1] = false;

        generateElements();

        stage.addActor(flipped1);
        stage.addActor(flipped2);
        stage.addActor(close);
        stage.addActor(send);

        hideButton(flipped1);
        hideButton(flipped2);
    }

    private void generateElements() {
        for(int i = 0; i < Assets.beans.length; i++) { //buttons for requesting cards
            counts2[i] = 0;
            buttons2[2*i] = new Button(Assets.buttonSkin, "minus");
            buttons2[2*i+1] = new Button(Assets.buttonSkin, "plus");
            buttons2[2*i].setSize(20, 20);
            buttons2[2*i+1].setSize(20,20);
            buttons2[2*i].setPosition(Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2 + 60 * i + 32, 236);
            buttons2[2*i+1].setPosition(Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2 + 60 * i + 56, 236);
            buttons2[2*i].addListener(new TradeButtonListener(i) {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    if(counts2[i] > 0 && waiting == 0) {
                        counts2[i]--;
                    }
                }
            });
            buttons2[2*i+1].addListener(new TradeButtonListener(i) {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    if(counts2[i] < 9 && waiting == 0) {
                        counts2[i]++;
                    }
                }
            });
            stage.addActor(buttons2[2*i]);
            stage.addActor(buttons2[2*i+1]);
        }

        flipped1 = new Button(Assets.buttonSkin, "check");
        flipped2 = new Button(Assets.buttonSkin, "check");
        close = new Button(Assets.buttonSkin, "close");
        send = new Button(Assets.buttonSkin, "send");

        flipped1.setSize(20, 20);
        flipped2.setSize(20, 20);
        close.setSize(20, 20);
        send.setSize(50, 50);

        flipped1.setPosition(Settings.RES_WIDTH/2 + Settings.TRADE_BOX_WIDTH/2 - 132, 446);
        flipped2.setPosition(Settings.RES_WIDTH/2 + Settings.TRADE_BOX_WIDTH/2 - 72, 446);
        close.setPosition(Settings.RES_WIDTH/2 + Settings.TRADE_BOX_WIDTH/2 - 50, Settings.RES_HEIGHT/2 + 50 + Settings.TRADE_BOX_HEIGHT/2 - 50);
        send.setPosition(Settings.RES_WIDTH/2 + Settings.TRADE_BOX_WIDTH/2 - 70, Settings.RES_HEIGHT/2 + 50 - Settings.TRADE_BOX_HEIGHT/2 + 20);

        flipped1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                activeSelected[0] = !activeSelected[0];
            }
        });
        flipped2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                activeSelected[1] = !activeSelected[1];
            }
        });
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                closeTrade();
            }
        });
        send.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                sendTrade();
            }
        });
    }

    /*
     * Generates checks to allow the trader to select who to send the offer to
     */
    public void generatePlayerChecks() {
        active = new boolean[game.getOpponents().size()];
        checks = new Button[game.getOpponents().size()];
        for(int i = 0; i < game.getOpponents().size(); i++) {
            active[i] = false;
            checks[i] = new Button(Assets.buttonSkin, "check");
            checks[i].setSize(20, 20);
            checks[i].setPosition(960,350 - 25 * i);
            checks[i].addListener(new TradeButtonListener(i) {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    active[i] = !active[i];
                }
            });
            stage.addActor(checks[i]);
        }
    }

    public void render(SpriteBatch batch, float mouseX, float mouseY) {
        batch.begin();
        batch.enableBlending();
        batch.draw(Assets.tradeBackground, Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2, Settings.RES_HEIGHT/2 - Settings.TRADE_BOX_HEIGHT/2 + 50, Settings.TRADE_BOX_WIDTH, Settings.TRADE_BOX_HEIGHT);
        hand = game.getPlayer().getHand().getCards();
        for(int i = 0; i < hand.size(); i++) {
            batch.draw(Assets.beans[hand.get(i).getCardVal()], Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2 + 30 + 60*i, 470, Settings.CARD_WIDTH * 0.4f, Settings.CARD_HEIGHT * 0.4f);
        }

        for(int i = 0; i < Assets.beans.length; i++) {
            layout.setText(font, "" + counts2[i]);
            font.draw(batch, layout, Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2 + 54 + 60*i - layout.width/2, 360);
            batch.draw(Assets.beans[i], Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2 + 30 + 60*i, 260, Settings.CARD_WIDTH * 0.4f, Settings.CARD_HEIGHT * 0.4f);
        }
        for(int i = 0; i < game.getOpponents().size(); i++) {
            layout.setText(font, game.getOpponents().get(i).getName());
            font.draw(batch, layout, 990, 350 - 25 * i + 20 - layout.height/2);
        }
        for(int i = 0; i < game.getPlayer().getFlipped().length; i++) {
            if(game.getPlayer().getFlipped()[i] != null) {
                batch.draw(game.getPlayer().getFlipped()[i].getTexture(), Settings.RES_WIDTH/2 + Settings.TRADE_BOX_WIDTH/2 - 30 - 60*(game.getPlayer().getFlipped().length-i), 470, Settings.CARD_WIDTH * 0.4f, Settings.CARD_HEIGHT * 0.4f);
            }
        }
        batch.end();
        if(Gdx.input.isKeyPressed(Settings.KEY_ZOOM)) {
            if(mouseY > 470 && mouseY < 470+Settings.CARD_HEIGHT*0.4f) {
                for(int i = 0; i < hand.size(); i++) {
                    if(mouseX > Settings.RES_WIDTH/2-Settings.TRADE_BOX_WIDTH/2+30+60*i && mouseX < Settings.RES_WIDTH/2-Settings.TRADE_BOX_WIDTH/2+30+60*i+Settings.CARD_WIDTH*0.4f) {
                        game.getPlayer().setZoomedCard(hand.get(i).getCardVal());
                    }
                }
                for(int i = 0; i < game.getPlayer().getFlipped().length; i++) {
                    if(game.getPlayer().getFlipped()[i] != null && mouseX > Settings.RES_WIDTH/2+Settings.TRADE_BOX_WIDTH/2-30-60*game.getPlayer().getFlipped().length-i && mouseX < Settings.RES_WIDTH/2+Settings.TRADE_BOX_WIDTH/2-30-60*game.getPlayer().getFlipped().length-i+Settings.CARD_WIDTH*0.4f) {
                        game.getPlayer().setZoomedCard(game.getPlayer().getFlipped()[i].getCardVal());
                    }
                }
            } else if(mouseY > 260 && mouseY < 260+Settings.CARD_HEIGHT*0.4f) {
                for(int i = 0; i < Assets.beans.length; i++) {
                    if(mouseX > Settings.RES_WIDTH/2-Settings.TRADE_BOX_WIDTH/2+30+60*i && mouseX < Settings.RES_WIDTH/2-Settings.TRADE_BOX_WIDTH/2+30+60*i+Settings.CARD_WIDTH*0.4f) {
                        game.getPlayer().setZoomedCard(i);
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
     * Clears all previously selected values when opening trade
     */
    public void resetValues() {
        for(int i = 0; i < counts2.length; i++) {
            counts2[i] = 0;
        }
        for(int i = 0; i < activeSelected.length; i++) {
            activeSelected[i] = false;
        }
        flipped1.setChecked(false);
        flipped2.setChecked(false);
        for(int i = 0; i < checks.length; i++) {
            checks[i].setChecked(false);
            active[i] = false;
        }
        if(game.getPlayer().getFlipped()[0] == null) {
            hideButton(flipped1);
        }
        if(game.getPlayer().getFlipped()[1] == null) {
            hideButton(flipped2);
        }
        updateHand();
    }

    /*
     * Generates new checks to allow cards from hand to be selected
     */
    public void updateHand() {
        if(!(offerChecks == null)) {
            for (int i = 0; i < offerChecks.length; i++) {
                offerChecks[i].addAction(Actions.removeActor());
            }
        }
        hand = game.getPlayer().getHand().getCards();
        offerChecks = new Button[hand.size()];
        offered = new boolean[hand.size()];
        for(int i = 0; i < offerChecks.length; i++) {
            Button b = new Button(Assets.buttonSkin, "check");
            b.setSize(20, 20);
            b.setPosition(Settings.RES_WIDTH/2 - Settings.TRADE_BOX_WIDTH/2 + 44 + 60*i, 446);
            b.addListener(new TradeButtonListener(i) {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    offered[i] = !offered[i];
                }
            });
            stage.addActor(b);
            offerChecks[i] = b;
        }
    }

    /*
     * Changes how many trade responses are being waited for
     */
    public void incrementWaiting(int delta) {
        waiting += delta;
        if(waiting == 0) {
            game.trading = false;
            game.showTradeOffer = false;
        }
    }

    /*
     * Sends the trade to selected players
     */
    public void sendTrade() {
        waiting = 0;
        for(int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
        String info = ""+game.playerNumber+(game.getOpponents().size()+1);
        for(int i = 0; i < offered.length; i++) {
            if(offered[i]) {
                counts[hand.get(i).getCardVal()]++;
            }
        }

        for(int i = 0; i < active.length; i++) {
            if(active[i]) {
                info = info + 1;
                waiting++;
            } else {
                info = info + 0;
            }
        }
        for(int i = 0; i < counts.length; i++) {
            info = info + counts[i];
        }
        for(int i = 0; i < counts2.length; i++) {
            info = info + counts2[i];
        }
        for(int i = 0; i < activeSelected.length; i++) {
            if(activeSelected[i]) {
                info = info + 1;
            } else {
                info = info + 0;
            }
        }
        game.sendMessage(4000, info);
    }

    /*
     * Handles and accepted trade
     */
    public void confirm() {
        waiting = 0;
        game.trading = false;
        game.showTradeOffer = false;
        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                game.getPlayer().getHand().removeCardType(i);
            }
        }
        for (int i = 0; i < counts2.length; i++) {
            for (int j = 0; j < counts2[i]; j++) {
                game.getPlayer().getTradedHand().addCard(i);
            }
        }
    }

    /*
     * Closes the trade window
     */
    public void closeTrade() {
        if(waiting == 0) {
            game.trading = false;
        }
    }


    public void hideCheck(int index) {
        if(index == 0) {
            hideButton(flipped1);
        } else {
            hideButton(flipped2);
        }
    }

    public void showCheck(int index) {
        if(index == 0) {
            showButton(flipped1);
        } else {
            showButton(flipped2);
        }
    }

    public void hideButton(Button button) {
        button.setVisible(false);
        button.setDisabled(true);
    }

    public void showButton(Button button) {
        button.setVisible(true);
        button.setDisabled(false);
    }
}
