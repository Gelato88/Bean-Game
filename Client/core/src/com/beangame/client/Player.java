package com.beangame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


/* The player class handles game-side information related to this player's gameplay.
 *
 */
public class Player {

    private BitmapFont font;
    private GlyphLayout layout;
    private Stage stage;

    private Button plant;
    private Button plantFlipped1;
    private Button plantFlipped2;
    private Button flip;
    private Button endTurn;
    private Button trade;
    private Button harvest1;
    private Button harvest2;

    private Card[] flipped;

    private BeanGame game;
    private Hand hand;
    private Spot spot1;
    private Spot spot2;
    private TradedHand tradedHand;

    private int actions;
    private int coins;
    private int zoomedCard;

    public Player(BeanGame game) {
        this.game = game;

        generateElements();

        font = new BitmapFont();
        layout = new GlyphLayout();
        stage = new Stage();

        flipped = new Card[2];

        hand = new Hand(game, this);
        spot1 = new Spot(this, Settings.RES_WIDTH/2 - 100 - Settings.CARD_WIDTH/2, 1);
        spot2 = new Spot(this, Settings.RES_WIDTH/2 + 100 - Settings.CARD_WIDTH/2, 2);
        tradedHand = new TradedHand(this, game);

        actions = 0;

        stage.addActor(plant);
        stage.addActor(plantFlipped1);
        stage.addActor(plantFlipped2);
        stage.addActor(flip);
        stage.addActor(endTurn);
        stage.addActor(trade);
        stage.addActor(harvest1);
        stage.addActor(harvest2);

        hideButton(plant);
        hideButton(plantFlipped1);
        hideButton(plantFlipped2);
        hideButton(flip);
        hideButton(endTurn);
        hideButton(trade);
    }

    private void generateElements() {
        plant = new Button(Assets.buttonSkin, "plant");
        plantFlipped1 = new Button(Assets.buttonSkin, "plant");
        plantFlipped2 = new Button(Assets.buttonSkin, "plant");
        flip = new Button(Assets.buttonSkin, "flip");
        endTurn = new Button(Assets.buttonSkin, "end_turn");
        trade = new Button(Assets.buttonSkin, "trade");
        harvest1 = new Button(Assets.buttonSkin, "harvest");
        harvest2 = new Button(Assets.buttonSkin, "harvest");

        plant.setSize(60, 60);
        plantFlipped1.setSize(60, 60);
        plantFlipped2.setSize(60, 60);
        flip.setSize(100, 100);
        endTurn.setSize(100, 100);
        trade.setSize(100, 100);
        harvest1.setSize(60,60);
        harvest2.setSize(60,60);

        plant.setPosition(Settings.RES_WIDTH/2 + 350,100);
        plantFlipped1.setPosition(160, 300);
        plantFlipped2.setPosition(160, 480);
        flip.setPosition(Settings.RES_WIDTH - 150, 50);
        endTurn.setPosition(Settings.RES_WIDTH - 150, 50);
        trade.setPosition(50, 50);
        harvest1.setPosition(Settings.RES_WIDTH/2-100 - harvest1.getWidth()/2, 640);
        harvest2.setPosition(Settings.RES_WIDTH/2+100 - harvest1.getWidth()/2, 640);

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
        flip.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                flip();
            }
        });
        endTurn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                askToEndTurn();
            }
        });
        trade.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.openTrade();
            }
        });
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
    }

    public void render(SpriteBatch batch, float mouseX, float mouseY) {
        zoomedCard = -1;
        drawMat(batch);
        drawFlipped(batch, mouseX, mouseY);
        stage.draw();
        stage.act();
        spot1.render(batch, mouseX, mouseY);
        spot2.render(batch, mouseX, mouseY);
        tradedHand.render(batch, mouseX, mouseY);
        hand.render(batch, mouseX, mouseY);
        drawZoomedCard(batch);
    }

    /*
     * Draws static board features: mat, deck, discard, coins
     */
    public void drawMat(SpriteBatch batch) {
        batch.begin();
        batch.enableBlending();
        batch.draw(Assets.coin, 20, Settings.RES_HEIGHT - 70, 50, 50);
        font.setColor(Color.WHITE);
        layout.setText(font, ""+coins);
        font.draw(batch, layout, 80, Settings.RES_HEIGHT-45-layout.height/2);
        batch.draw(Assets.cardBack, 100, Settings.RES_HEIGHT-100-20, Settings.CARD_WIDTH/2, Settings.CARD_HEIGHT/2);
        layout.setText(font, ""+game.deckCards);
        font.draw(batch, layout, 100+Settings.CARD_WIDTH/4-layout.width/2, Settings.RES_HEIGHT-100-25);
        if(game.discardTop == -1) {
            batch.draw(Assets.cardBack, 170, Settings.RES_HEIGHT-100-20, Settings.CARD_WIDTH/2, Settings.CARD_HEIGHT/2);
        } else {
            batch.draw(Assets.beans[game.discardTop], 170, Settings.RES_HEIGHT-100-20, Settings.CARD_WIDTH/2, Settings.CARD_HEIGHT/2);
        }
        layout.setText(font, ""+game.discardCards);
        font.draw(batch, layout, 170+Settings.CARD_WIDTH/4-layout.width/2, Settings.RES_HEIGHT-100-25);
        batch.draw(Assets.mat, Settings.RES_WIDTH/2 - 200, 440, 400, 200);
        batch.end();

    }

    /*
     * Draws cards that have been flipped face up from the deck
     */
    public void drawFlipped(SpriteBatch batch, float mouseX, float mouseY) {
        batch.begin();
        batch.enableBlending();
        for(int i = 0; i < flipped.length; i++) {
            if(flipped[i] != null) {
                batch.draw(flipped[i].getTexture(), 40, 240 + 180 * i, Settings.CARD_WIDTH * 0.8f, Settings.CARD_HEIGHT * 0.8f);
            }
        }
        batch.end();
        if(Gdx.input.isKeyPressed(Settings.KEY_ZOOM)) {
            if(mouseX > 40 && mouseX < 40+Settings.CARD_WIDTH*0.8f) {
                for(int i = 0; i < flipped.length; i++) {
                    if(flipped[i] != null && mouseY > 240+180*i && mouseY < 240+180*i+Settings.CARD_HEIGHT*0.8f) {
                        setZoomedCard(flipped[i].getCardVal());
                    }
                }
            }
        }
    }

    public void drawZoomedCard(SpriteBatch batch) {
        if(zoomedCard != -1) {
            batch.begin();
            batch.enableBlending();
            batch.draw(Assets.beans[zoomedCard], Settings.RES_WIDTH/2 - Settings.CARD_WIDTH*2/2, Settings.RES_HEIGHT/2-Settings.CARD_HEIGHT/2, Settings.CARD_WIDTH*2, Settings.CARD_HEIGHT*2);
            batch.end();
        }
    }

    public int getCoins() {
        return coins;
    }

    public Card[] getFlipped() {
        return flipped;
    }

    public TradedHand getTradedHand() {
        return tradedHand;
    }

    public Stage getStage() {
        return stage;
    }

    public Hand getHand() {
        return hand;
    }

    public void setZoomedCard(int cardVal) {
        zoomedCard = cardVal;
    }

    /*
     * Hides a button
     */
    public void hideButton(Button button) {
        button.setVisible(false);
        button.setDisabled(true);
    }

    /*
     * Shows a button
     */
    public void showButton(Button button) {
        button.setVisible(true);
        button.setDisabled(false);
    }

    /*
     * Starts this player's turn
     */
    public void startTurn() {
        actions = 2;
        showButton(plant);
        showButton(harvest1);
        showButton(harvest2);
    }

    /*
     * Attempts to end this player's turn
     */
    public void askToEndTurn() {
        if(actions < 2 && flipped[0] == null && flipped[1] == null) {
            game.sendMessage(3008, "");
        }
    }

    /*
     * Ends this player's turn
     */
    public void endTurn() {
        actions = 0;
        hideButton(flip);
        hideButton(endTurn);
        hideButton(trade);
        game.endTurn();
    }

    /*
     * Adds a card to the hand
     */
    public void drawCard(int cardVal) {
        hand.getCards().add(new Card(cardVal));
        hand.incrementCard(cardVal, 1);
        game.sendMessage(3010, ""+game.playerNumber+hand.getCards().size());
    }

    /*
     * Sends to the server that the player is ready to flip cards from the deck
     */
    public void flip() {
        hideButton(flip);
        hideButton(plant);
        showButton(endTurn);
        showButton(trade);
        game.sendMessage(3004, "");
    }

    /*
     * Adds a card to the flipped up cards
     */
    public void addToFlipped(int cardVal) {
        for(int i = 0; i < flipped.length; i++) {
            if(flipped[i] == null) {
                flipped[i] = new Card(cardVal);
                if(game.currentTurn == game.playerNumber) {
                    if (i == 0) {
                        showButton(plantFlipped1);
                        game.getTrade().showCheck(0);
                    } else {
                        showButton(plantFlipped2);
                        game.getTrade().showCheck(1);
                    }
                }
                return;
            }
        }
        System.out.println("Could not find an open spot to add a flipped card");
    }

    /*
     * Hides a card that was flipped from the deck
     */
    public void hideFlipped(int index) {
        flipped[index] = null;
        if(index == 0) {
            hideButton(plantFlipped1);
        } else if(index == 1) {
            hideButton(plantFlipped2);
        }
        game.getTrade().hideCheck(index);
    }

    /*
     * Plants top card of hand
     */
    public void plantNext() {
        if(game.currentTurn == game.playerNumber && actions > 0 && hand.getCards().size() > 0) {
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

    /*
     * Plants a card that has come from the hand
     */
    public void plantFromHand(Spot spot) {
        plant(spot, hand.getCards().get(0).getCardVal());
        hand.getCards().remove(0);
        game.sendMessage(3010, ""+game.playerNumber+hand.getCards().size());
        actions--;
        if(actions < 2) {
            showButton(flip);
        }
        if(actions == 0) {
            hideButton(plant);
        }
    }

    /*
     * Plants a card that has come from the traded hand
     */
    public boolean plantFromTradedHand(int cardVal) {
        if(cardVal == spot1.getType()) {
            return plant(spot1, cardVal);
        } else if(cardVal == spot2.getType()) {
            return plant(spot2, cardVal);
        } else if(spot1.isOpen()) {
            return plant(spot1, cardVal);
        } else if(spot2.isOpen()) {
            return plant(spot2, cardVal);
        } else {
            return false;
        }
    }

    /*
     * Plants a card that was flipped from the deck
     */
    public void plantFromFlipped(int index) {
        boolean planted = false;
        if(flipped[index].getCardVal() == spot1.getType()) {
            planted  = plant(spot1, flipped[index].getCardVal());
        } else if(flipped[index].getCardVal() == spot2.getType()) {
            planted = plant(spot2, flipped[index].getCardVal());
        } else if(spot1.isOpen()) {
            planted = plant(spot1, flipped[index].getCardVal());
        } else if(spot2.isOpen()) {
            planted = plant(spot2, flipped[index].getCardVal());
        }
        if(planted) {
            if(index == 0) {
                hideButton(plantFlipped1);
            } else {
                hideButton(plantFlipped2);
            }
            flipped[index] = null;
            game.sendMessage(3006, ""+index);
        }
    }

    /*
     * Plants a card in a spot
     */
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
        game.sendMessage(3002, "" + game.playerNumber + spot.getSpotNum() + cardInfo);
        return true;
    }

    /*
     * Harvests a spot
     */
    public void harvest(int spot) {
        if(game.currentTurn == game.playerNumber || tradedHand.getSize() > 0) {
            int card;
            int num;
            int profit;
            if (spot == 1 && (spot1.getCards() > 1 || spot2.getCards() < 2)) {
                card = spot1.getType();
                num = spot1.getCards();
                profit = spot1.harvest();
            } else if(spot == 2 && (spot2.getCards() > 1 || spot1.getCards() < 2)) {
                card = spot2.getType();
                num = spot2.getCards();
                profit = spot2.harvest();
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
            game.sendMessage(3003, "" + game.playerNumber + spot + coins);
            game.sendMessage(3007, info);
        }
    }
}
