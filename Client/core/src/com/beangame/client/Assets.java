package com.beangame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    public static Skin buttonSkin;
    public static Skin textFieldSkin;

    public static Texture start;
    public static Texture background;
    public static Texture tradeBackground;
    public static Texture offerBackground;
    public static Texture mat;
    public static Texture opponentMat;
    public static Texture playerSymbol;
    public static Texture coin;
    public static Texture cardBack;

    public static TextureAtlas beansAtlas;

    public static TextureRegion blackEyedBean;
    public static TextureRegion blueBean;
    public static TextureRegion chiliBean;
    public static TextureRegion cocoaBean;
    public static TextureRegion coffeeBean;
    public static TextureRegion gardenBean;
    public static TextureRegion greenBean;
    public static TextureRegion redBean;
    public static TextureRegion soyBean;
    public static TextureRegion stinkBean;
    public static TextureRegion waxBean;

    public static TextureRegion[] beans = new TextureRegion[11];

    public static Texture load(String filepath) {
        return new Texture(Gdx.files.internal(filepath + ".png"));
    }

    public static void loadTextures() {
        buttonSkin = new Skin(Gdx.files.internal("buttons/buttons.json"));
        textFieldSkin = new Skin(Gdx.files.internal("textFields/textFields.json"));
        start = load("start");
        background = load("background");
        tradeBackground = load("trade");
        offerBackground = load("offer");
        mat = load("mat");
        opponentMat = load("opponent_mat");
        playerSymbol = load("player_symbol");
        coin = load("coin");
        cardBack = load("beans/textures/card_back");

        switch(Settings.TEXTURE_PACK) {
            case 1:
                loadPack1();
                break;
            default:
                Gdx.app.log("Assets", "Invalid texture pack found.");
        }

        beans[0] = blackEyedBean;
        beans[1] = blueBean;
        beans[2] = chiliBean;
        beans[3] = cocoaBean;
        beans[4] = coffeeBean;
        beans[5] = gardenBean;
        beans[6] = greenBean;
        beans[7] = redBean;
        beans[8] = soyBean;
        beans[9] = stinkBean;
        beans[10] = waxBean;
    }


    public static void loadPack1() {

        beansAtlas = new TextureAtlas(Gdx.files.internal("beans/beans.atlas"));

        blackEyedBean = beansAtlas.findRegion("black-eyed_bean");
        blueBean = beansAtlas.findRegion("blue_bean");
        chiliBean = beansAtlas.findRegion("chili_bean");
        cocoaBean = beansAtlas.findRegion("cocoa_bean");
        coffeeBean = beansAtlas.findRegion("coffee_bean");
        gardenBean = beansAtlas.findRegion("garden_bean");
        greenBean = beansAtlas.findRegion("green_bean");
        redBean = beansAtlas.findRegion("red_bean");
        soyBean = beansAtlas.findRegion("soy_bean");
        stinkBean = beansAtlas.findRegion("stink_bean");
        waxBean = beansAtlas.findRegion("wax_bean");
    }

    public static void disposeAll() {
        buttonSkin.dispose();
        start.dispose();
        background.dispose();
        tradeBackground.dispose();
        mat.dispose();
        opponentMat.dispose();
        playerSymbol.dispose();
        coin.dispose();
        beansAtlas.dispose();

    }

}
