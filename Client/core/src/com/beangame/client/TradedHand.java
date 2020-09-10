package com.beangame.client;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import java.util.ArrayList;

public class TradedHand {

    private ArrayList<Card> cards;
    private ArrayList<Button> buttons;
    private Stage stage;

    private BeanGame game;
    private Player player;

    public TradedHand(Player player, BeanGame game) {
        this.player = player;
        this.game = game;
        stage = new Stage();
        cards = new ArrayList<>();
        buttons = new ArrayList<>();
    }

    public void addCard(int cardVal) {
        if(cards.size() == 0) {
            game.sendMessage(4004, "");
        }
        Card card = new Card(cardVal);
        cards.add(card);
        Button button = new Button(Assets.buttonSkin, "plant");
        button.setSize(60, 60);
        button.addListener(new TradedHandListener(card, button) {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if(player.plantFromTraded(c.getCardVal())) {
                    b.addAction(Actions.removeActor());
                    buttons.remove(b);
                    cards.remove(c);
                    adjustPositions();
                    if(cards.size() == 0) {
                        game.sendMessage(4005, "");
                    }
                }
            }
        });
        stage.addActor(button);
        buttons.add(button);
        float startX = Settings.RES_WIDTH/2 - cards.size() * 100/2;
        for(int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setPosition(startX + 100*i + 25, 390);
        }
    }

    public void adjustPositions() {
        float startX = Settings.RES_WIDTH/2 - cards.size() * 100/2;
        for(int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setPosition(startX + 100*i + 30, 390);
        }
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.enableBlending();
        float startX = Settings.RES_WIDTH / 2 - (cards.size() * 100/2);
        for (int i = 0; i < cards.size(); i++) {
            batch.draw(cards.get(i).getTexture(), startX + 100 * i, 240, Settings.CARD_WIDTH * 0.7f, Settings.CARD_HEIGHT * 0.7f);
        }
        batch.end();
        stage.draw();
        stage.act();
    }

    public int getSize() {
        return cards.size();
    }

    public Stage getStage() {
        return stage;
    }

}
