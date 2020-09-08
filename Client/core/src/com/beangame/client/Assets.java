package com.beangame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

    public static Texture blackEyedBean;
    public static Texture blueBean;
    public static Texture chiliBean;
    public static Texture cocoaBean;
    public static Texture coffeeBean;
    public static Texture gardenBean;
    public static Texture greenBean;
    public static Texture redBean;
    public static Texture soyBean;
    public static Texture stinkBean;
    public static Texture waxBean;

    public static Texture[] beans = new Texture[11];

    public static Texture load(String filepath) {
        return new Texture(Gdx.files.internal(filepath + ".png"));
    }

    public static void loadTextures() {
        buttonSkin = new Skin(Gdx.files.internal("buttons.json"));
        textFieldSkin = new Skin(Gdx.files.internal("textFields/textFields.json"));
        start = load("start");
        background = load("background");
        tradeBackground = load("trade");
        offerBackground = load("offer");
        mat = load("mat");
        opponentMat = load("opponent_mat");
        playerSymbol = load("player_symbol");
        coin = load("coin");
        blackEyedBean = load("black-eyed_bean");
        blueBean = load("blue_bean");
        chiliBean = load("chili_bean");
        cocoaBean = load("cocoa_bean");
        coffeeBean = load("coffee_bean");
        gardenBean = load("garden_bean");
        greenBean = load("green_bean");
        redBean = load("red_bean");
        soyBean = load("soy_bean");
        stinkBean = load("stink_bean");
        waxBean = load("wax_bean");

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

    public static void disposeAll() {
        buttonSkin.dispose();
        start.dispose();
        background.dispose();
        tradeBackground.dispose();
        mat.dispose();
        opponentMat.dispose();
        playerSymbol.dispose();
        coin.dispose();
        blackEyedBean.dispose();
        blueBean.dispose();
        chiliBean.dispose();
        cocoaBean.dispose();
        coffeeBean.dispose();
        gardenBean.dispose();
        greenBean.dispose();
        redBean.dispose();
        soyBean.dispose();
        stinkBean.dispose();
        waxBean.dispose();
    }

}
