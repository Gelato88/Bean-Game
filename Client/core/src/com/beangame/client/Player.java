package com.beangame.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


/* The player class handles client-side information related to this player's gameplay.
 *
 */
public class Player {

    private BitmapFont font;
    private GlyphLayout layout;
    private Stage stage;

    private Button harvest1;
    private Button harvest2;
    private Button plant;
    private Button plantFlipped1;
    private Button plantFlipped2;
    private Button next;
    private Button endTurn;

    private Card[] active;

    private BeanGame client;
    private Hand hand;
    private Spot spot1;
    private Spot spot2;
    private TradedHand tradedHand;

    private int actions;
    private int coins;

    public Player(final BeanGame client) {

        this.client = client;

        font = new BitmapFont();
        layout = new GlyphLayout();
        stage = new Stage();

        active = new Card[2];

        hand = new Hand();
        spot1 = new Spot(Settings.RES_WIDTH/2 - 100 - Settings.CARD_WIDTH/2, 1);
        spot2 = new Spot(Settings.RES_WIDTH/2 + 100 - Settings.CARD_WIDTH/2, 2);
        tradedHand = new TradedHand(this, client);

        actions = 0;

        harvest1 = new Button(Assets.buttonSkin, "harvest");
        harvest2 = new Button(Assets.buttonSkin, "harvest");
        harvest1.setSize(60,60);
        harvest2.setSize(60,60);
        harvest1.setPosition(Settings.RES_WIDTH/2-100 - harvest1.getWidth()/2, 640);
        harvest2.setPosition(Settings.RES_WIDTH/2+100 - harvest1.getWidth()/2, 640);
        harvest1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                harvest(1);
            }
        });
        harvest2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                harvest(2);
            }
        });
        plant = new Button(Assets.buttonSkin, "plant");
        plantFlipped1 = new Button(Assets.buttonSkin, "plant");
        plantFlipped2 = new Button(Assets.buttonSkin, "plant");
        next = new Button(Assets.buttonSkin, "next");
        endTurn = new Button(Assets.buttonSkin, "end_turn");
        plant.setSize(60, 60);
        plantFlipped1.setSize(60, 60);
        plantFlipped2.setSize(60, 60);
        next.setSize(100, 100);
        endTurn.setSize(100, 100);
        plant.setPosition(Settings.RES_WIDTH/2 + 350,100);
        plantFlipped1.setPosition(160, 300);
        plantFlipped2.setPosition(160, 480);
        next.setPosition(Settings.RES_WIDTH - 150, 50);
        endTurn.setPosition(Settings.RES_WIDTH - 150, 50);
        plant.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                plantNext();
            }
        });
        plantFlipped1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                plantFromFlipped(0);
            }
        });
        plantFlipped2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                plantFromFlipped(1);
            }
        });
        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                flip();
            }
        });
        endTurn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if(actions < 2) {
                    askToEndTurn();
                }
            }
        });

        stage.addActor(harvest1);
        stage.addActor(harvest2);
        stage.addActor(plant);
        stage.addActor(plantFlipped1);
        stage.addActor(plantFlipped2);
        stage.addActor(next);
        stage.addActor(endTurn);

        hideButton(plant);
        hideButton(plantFlipped1);
        hideButton(plantFlipped2);
        hideButton(next);
        hideButton(endTurn);
    }


    public void render(SpriteBatch batch) {
        drawMat(batch);
        drawActive(batch);
        stage.draw();
        stage.act();
        spot1.render(batch);
        spot2.render(batch);
        tradedHand.render(batch);
        hand.render(batch);
    }

    public void drawMat(SpriteBatch batch) {

        batch.begin();
        batch.enableBlending();
        batch.draw(Assets.coin, 50, Settings.RES_HEIGHT - 100, 50, 50);
        font.setColor(Color.WHITE);
        layout.setText(font, ""+coins);
        font.draw(batch, layout, 110, Settings.RES_HEIGHT-75);
        batch.draw(Assets.mat, Settings.RES_WIDTH/2 - 200, 440, 400, 200);
        batch.end();

    }

    public void drawActive(SpriteBatch batch) {
        batch.begin();
        batch.enableBlending();
        for(int i = 0; i < active.length; i++) {
            if(active[i] != null) {
                batch.draw(active[i].getTexture(), 40, 240 + 180 * i, Settings.CARD_WIDTH * 0.8f, Settings.CARD_HEIGHT * 0.8f);
            }
        }
        batch.end();

    }

    public void hideButton(Button button) {
        button.setVisible(false);
        button.setDisabled(true);
    }

    public void showButton(Button button) {
        button.setVisible(true);
        button.setDisabled(false);
    }

    public int getCoins() {
        return coins;
    }

    public Card[] getActive() {
        return active;
    }

    public TradedHand getTradedHand() {
        return tradedHand;
    }

    public void startTurn() {
        actions = 2;
        showButton(plant);
    }

    public void askToEndTurn() {
        if(active[0] == null && active[1] == null) {
            client.sendMessage(3008, "");
            client.wait(500);
        }
    }

    public void endTurn() {
        actions = 0;
        hideButton(next);
        hideButton(endTurn);
        client.endTurn();
    }

    public void plantNext() {
        if(client.currentTurn == client.playerNumber && actions > 0 && hand.getCards().size() > 0) {
            if(hand.getCards().get(0).getCardVal() == spot1.getType()) {
                plantFromHand(spot1);
            } else if(hand.getCards().get(0).getCardVal() == spot2.getType()) {
                plantFromHand(spot2);
            } else if (spot1.isOpen()) {
                plantFromHand(spot1);
            } else if (spot2.isOpen()) {
                plantFromHand(spot2);
            }
        }
    }

    public void plantFromHand(Spot spot) {
        plant(spot, hand.getCards().get(0).getCardVal());
        hand.getCards().remove(0);
        actions--;
        if(actions < 2) {
            showButton(next);
            client.showTradeButton();
        }
        if(actions == 0) {
            hideButton(plant);
        }
    }

    public void plantFromFlipped(int index) {
        boolean planted = false;
        if(active[index].getCardVal() == spot1.getType()) {
            planted  = plant(spot1, active[index].getCardVal());
        } else if(active[index].getCardVal() == spot2.getType()) {
            planted = plant(spot2, active[index].getCardVal());
        } else if(spot1.isOpen()) {
            planted = plant(spot1, active[index].getCardVal());
        } else if(spot2.isOpen()) {
            planted = plant(spot2, active[index].getCardVal());
        }
        if(planted) {
            if(index == 0) {
                hideButton(plantFlipped1);
            } else {
                hideButton(plantFlipped2);
            }
            active[index] = null;
            client.sendMessage(3006, ""+index);
        }
    }

    public void hideFlipped(int index) {
        active[index] = null;
        if(index == 0) {
            hideButton(plantFlipped1);
        } else if(index == 1) {
            hideButton(plantFlipped2);
        }
        client.getTrade().hideCheck(index);
    }

    public boolean plantFromTraded(int cardVal) {
        String untruncCardVal;
        if(cardVal < 10) {
            untruncCardVal = "0"+cardVal;
        } else {
            untruncCardVal = ""+cardVal;
        }
        if(cardVal == spot1.getType()) {
            client.sendMessage(3002,  ""+client.playerNumber+1+untruncCardVal+spot1.getCards());
            return plant(spot1, cardVal);
        } else if(cardVal == spot2.getType()) {
            client.sendMessage(3002, ""+client.playerNumber+2+untruncCardVal+spot2.getCards());
            return plant(spot2, cardVal);
        } else if(spot1.isOpen()) {
            client.sendMessage(3002, ""+client.playerNumber+1+untruncCardVal+spot1.getCards());
            return plant(spot1, cardVal);
        } else if(spot2.isOpen()) {
            client.sendMessage(3002, ""+client.playerNumber+2+untruncCardVal+spot2.getCards());
            return plant(spot2, cardVal);
        } else {
            return false;
        }
    }

    public boolean plant(Spot spot, int type) {
        spot.setType(type);
        spot.addCard();
        String cardInfo;
        if (spot.getType() < 10) {
            cardInfo = "0" + spot.getType();
        } else {
            cardInfo = "" + spot.getType();
        }
        if (spot.getCards() < 10) {
            cardInfo = cardInfo + "0" + spot.getCards();
        } else {
            cardInfo = cardInfo + spot.getCards();
        }
        client.sendMessage(3002, "" + client.playerNumber + spot.getSpotNum() + cardInfo);
        return true;
    }

    public void harvest(int spot) {
        if(client.currentTurn == client.playerNumber || tradedHand.getSize() > 0) {
            int card;
            int num;
            int profit;
            if (spot == 1 && (spot1.getCards() > 1 || spot2.getCards() < 2)) {
                profit = spot1.harvest();
                card = spot1.getType();
                num = spot1.getCards();
            } else if(spot == 2 && (spot2.getCards() > 1 || spot1.getCards() < 2)) {
                profit = spot2.harvest();
                card = spot2.getType();
                num = spot2.getCards();
            } else {
                return;
            }
            num -= profit;
            coins += profit;
            String info;
            if(card < 10) {
                info = "0" + card + num;
            } else {
                info = "" + card + num;
            }
            client.sendMessage(3003, "" + client.playerNumber + spot + coins);
            client.sendMessage(3007, info);
        }
    }

    public void flip() {
        hideButton(next);
        hideButton(plant);
        showButton(endTurn);
        client.sendMessage(3004, "");
    }

    public void addCard(int cardVal) {
        hand.getCards().add(new Card(cardVal));
        hand.incrementCard(cardVal, 1);
    }

    public void addToActive(int cardVal) {
        for(int i = 0; i < active.length; i++) {
            if(active[i] == null) {
                active[i] = new Card(cardVal);
                if(client.currentTurn == client.playerNumber) {
                    if (i == 0) {
                        showButton(plantFlipped1);
                        client.getTrade().showCheck(0);
                    } else {
                        showButton(plantFlipped2);
                        client.getTrade().showCheck(1);
                    }
                }
                return;
            }
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Hand getHand() {
        return hand;
    }
}
