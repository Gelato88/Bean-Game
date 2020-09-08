package com.beangame.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/* Main class of the client. Top of render hierarchy and handles communication with server.
 *
 */
public class BeanGame extends ApplicationAdapter {

	private Terminal terminal;

	private Socket s;
	private OutputStream output;
	private PrintWriter writer;
	private InputThread inputThread;

	private SpriteBatch batch;
	private BitmapFont font;
	private GlyphLayout layout;
	private TextField ipInput;
	private Button enterIp;
	private Stage setupStage;
	private Stage introStage;
	private Stage endStage;
	private Stage stage;
	private InputMultiplexer inputMultiplexer;

	private Button connect;
	private Button tradeButton;
	private Button startGame;

	private ArrayList<Opponent> opponents;

	private Player player;
    private Trade trade;
    private TradeOffer tradeOffer;

    private int gameStatus;
    private boolean gameStarted;
    private boolean opponentsGenerated;
    private int connected;
    private String[] names;
    private int[] scores;

	public boolean trading;
	public boolean showTradeOffer;
	public boolean turn;
	public int currentTurn;
	public int playerNumber;

	@Override
	public void create () {

		Assets.loadTextures();

		font = new BitmapFont();
		layout = new GlyphLayout();
		batch = new SpriteBatch();
		setupStage = new Stage();
		introStage = new Stage();
		endStage = new Stage();
		stage = new Stage();
		inputMultiplexer = new InputMultiplexer();

		opponents = new ArrayList<>();

		player = new Player(this);
		trade = new Trade(this);
		tradeOffer = new TradeOffer(this);

		gameStarted = false;
		turn = false;
		opponentsGenerated = false;
		trading = false;
		showTradeOffer = false;
		gameStatus = 0;
		connected = 0;
		currentTurn = 0;
		playerNumber = 0;

		ipInput = new TextField("Enter Host IP", Assets.textFieldSkin);
		ipInput.setSize(480, 60);
		ipInput.setPosition(Settings.RES_WIDTH/2 - ipInput.getWidth()/2, 300);
		ipInput.setAlignment(Align.center);

		connect = new Button(Assets.buttonSkin, "connect");
		connect.setSize(100, 50);
		connect.setPosition(Settings.RES_WIDTH/2 - connect.getWidth()/2, 200);
		connect.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				attemptConnection();
			}
		});

		setupStage.addActor(ipInput);
		setupStage.addActor(connect);

		tradeButton = new Button(Assets.buttonSkin, "trade");
		tradeButton.setSize(100, 100);
		tradeButton.setPosition(50, 50);
		tradeButton.addListener(new ClickListener() {
		    @Override
            public void clicked(InputEvent e, float x, float y) {
		        trading = true;
		        trade.updateHand();
            }
        });
		hideButton(tradeButton);
		startGame = new Button(Assets.buttonSkin, "send");
		startGame.setSize(80, 80);
		startGame.setPosition(Settings.RES_WIDTH - 130, Settings.RES_HEIGHT - 130);
		startGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				requestGameStart();
			}
		});

		stage.addActor(tradeButton);
		introStage.addActor(startGame);

		inputMultiplexer.addProcessor(player.getStage());
		inputMultiplexer.addProcessor(trade.getStage());
		inputMultiplexer.addProcessor(tradeOffer.getStage());
		inputMultiplexer.addProcessor(player.getTradedHand().getStage());
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(setupStage);

	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		switch(gameStatus) {
            case 0:
                batch.begin();
                batch.enableBlending();
                batch.end();
                setupStage.draw();
                setupStage.act();
                break;
			case 3:
				batch.begin();
				batch.enableBlending();
				layout.setText(font, "Game Over!");
				font.draw(batch, layout, Settings.RES_WIDTH-layout.width/2, Settings.RES_HEIGHT-100);
				for(int i = 0; i < scores.length; i++) {
					layout.setText(font, names[i] + ":   " + scores[i]);
					font.draw(batch, layout, Settings.RES_WIDTH - layout.width/2, Settings.RES_HEIGHT - 300 - 50 * i);
				}
				batch.end();
				break;
            default:
                if(gameStarted) {
                    if(!opponentsGenerated) {
                        generateOpponents();
                        trade.generatePlayerChecks();
                    }
                    batch.begin();
                    batch.draw(Assets.background, 0, 0, Settings.RES_WIDTH, Settings.RES_HEIGHT);
                    layout.setText(font, "You are player " + playerNumber);
                    font.draw(batch, layout, Settings.RES_WIDTH/2 - layout.width/2, Settings.RES_HEIGHT - 15);
                    layout.setText(font, "It is currently player " + currentTurn + "'s turn.");
                    font.draw(batch, layout, Settings.RES_WIDTH/2-layout.width/2, Settings.RES_HEIGHT-30);
                    batch.end();

                    stage.draw();
                    stage.act();

                    player.render(batch);
                    for (Opponent o : opponents) {
                        o.render(batch);
                    }
                    if (trading) {
                        trade.render(batch);
                    }
                    if(showTradeOffer) {
                        tradeOffer.render(batch);
                    }
                } else {
                    batch.begin();
                    batch.enableBlending();
                    batch.draw(Assets.start, 0, 0, Settings.RES_WIDTH, Settings.RES_HEIGHT);
                    batch.draw(Assets.playerSymbol, 60, Settings.RES_HEIGHT - 110, 50, 50);
                    layout.setText(font, "" + connected);
                    font.draw(batch, layout, 85 - layout.width/2, Settings.RES_HEIGHT - 120);
                    batch.end();
                    introStage.draw();
                    introStage.act();
                }
                break;
        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		Assets.disposeAll();
	}

	@Override
	public void resize(int width, int height) {
		player.getStage().getViewport().update(width, height, true);
		trade.getStage().getViewport().update(width, height, true);
		tradeOffer.getStage().getViewport().update(width, height, true);
		setupStage.getViewport().update(width, height, true);
		introStage.getViewport().update(width, height, true);
	}

	public void attemptConnection() {
		Settings.IP = ipInput.getText();
		try {
			s = new Socket(InetAddress.getByName(Settings.IP), Settings.PORT);
			output = s.getOutputStream();
			writer = new PrintWriter(output, true);
			inputThread = new InputThread(s, this);
			inputThread.start();
			terminal = new Terminal(s, this);
			terminal.start();
			gameStatus = 1;
			Gdx.input.setInputProcessor(introStage);

		} catch(UnknownHostException e) {
			System.out.println("Server not found: " + e.getMessage());
		} catch(IOException e) {
			System.out.println("I/O error: " + e.getMessage());
		}
	}

	/*
	 * Sends a message to the server
	 */
	public void sendMessage(int code, String str) {
		wait(20);
		writer.println("" + code + str);
	}

	public void sendMessage(String str) {
		wait(20);
		writer.println(str);
	}

	public void hideButton(Button button) {
		button.setVisible(false);
		button.setDisabled(true);
	}

	public void showButton(Button button) {
		button.setVisible(true);
		button.setDisabled(false);
	}

	public ArrayList<Opponent> getOpponents() {
	    return opponents;
    }

    public Player getPlayer() {
	    return player;
    }

    public Trade getTrade() {
		return trade;
	}

    public void setPlayerCount(int count) {
		connected = count;
	}

	public void setPlayerNumber(int num) {
		playerNumber = num;
	}

	public void setCurrentTurn(int player) {
		currentTurn = player;
	}

	/*
	 * Asks the server to initiate the game
	 */
	public void requestGameStart() {
		sendMessage(1001, "");
	}

	/*
	 * Switches off title screen to game
	 */
	public void startGame() {
		gameStarted = true;
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void endGame() {
		names = new String[opponents.size()+1];
		scores = new int[opponents.size()+1];
		for(int i = 0; i < opponents.size(); i++) {
			names[i] = opponents.get(i).getName();
			scores[i] = opponents.get(i).getCoins();
		}
		names[names.length-1] = "Player " + playerNumber;
		scores[scores.length-1] = player.getCoins();
		for(int i = 0; i < scores.length-1; i++) {
			int max = scores[i];
			int maxInd = i;
			for(int j = i+1; j < scores.length; j++) {
				if(scores[j] > scores[i]) {
					max = scores[j];
					maxInd = j;
				}
			}
			int tempScore = scores[i];
			scores[i] = scores[maxInd];
			scores[maxInd] = tempScore;
			String tempName = names[i];
			names[i] = names[maxInd];
			names[maxInd] = tempName;
		}
		gameStatus = 3;
	}

	/*
	 * Generate opponent cards based on number of players
	 */
	public void generateOpponents() {
		for(int i = 1; i <= connected; i++) {
			if(i != playerNumber) {
				int j;
				if(i < playerNumber) {
					j = i;
				} else {
					j = i-1;
				}
				String str = "Player " + i;
				opponents.add(new Opponent(str, i, Settings.RES_WIDTH - (Settings.OPPONENT_BOX_WIDTH+10)*(j%2 + 1), Settings.RES_HEIGHT - (Settings.OPPONENT_BOX_HEIGHT+10)*((j-1)/2 + 1)));
			}
		}
		opponentsGenerated = true;
	}

	/*
	 * Starts turn
	 */
	public void startTurn() {
		turn = true;
		player.startTurn();
	}

	/*
	 * Ends turn
	 */
	public void endTurn() {
		hideButton(tradeButton);
		turn = false;
		sendMessage(3001, "");
	}

	public void showTradeButton() {
		showButton(tradeButton);
	}

	public void showTrade(String info) {
		int players = Integer.parseInt(info.substring(1,2));
		int num;
		if(Integer.parseInt(info.substring(0,1)) < playerNumber) {
			num = playerNumber-1;
		} else {
			num = playerNumber;
		}
		System.out.println(info.substring(num+1, num+2));

		if(Integer.parseInt(info.substring(num+1, num+2)) == 1) {
			int[] offered = new int[Assets.beans.length];
			int[] requested = new int[Assets.beans.length];
			for(int i = 0; i < offered.length; i++) {
				offered[i] = Integer.parseInt(info.substring(i+players+1, i+players+2));
				requested[i] = Integer.parseInt(info.substring(i+players+offered.length+1, i+players+offered.length+2));
			}
			int[] actives = new int[2];
			actives[0] = Integer.parseInt(info.substring(info.length()-2, info.length()-1));
			actives[1] = Integer.parseInt(info.substring(info.length()-1));
			System.out.println("Active 1: " + actives[0]);
			System.out.println("Active 2: " + actives[1]);
			tradeOffer.setValues(requested, offered, actives);
			showTradeOffer = true;
		}
	}

	/*
	 * Searches the opponent list for a player
	 */
	public Opponent findOpponent(int pNum) {
		for(int i = 0; i < opponents.size(); i++) {
			if(opponents.get(i).getPlayerNum() == pNum) {
				return opponents.get(i);
			}
		}
		System.out.println("Could not find player " + pNum);
		return null;
	}

	/*
	 * Updates an opponent's card after they plant
	 */
	public void opponentPlant(String info) {
		int player = Integer.parseInt(info.substring(0,1));
		int spot = Integer.parseInt(info.substring(1,2));
		int cardVal = Integer.parseInt(info.substring(2,4));
		int cards = Integer.parseInt(info.substring(4));
		Opponent o = findOpponent(player);
		if(spot == 1) {
			o.setBean1(cardVal);
			o.setBean1Number(cards);
		} else {
			o.setBean2(cardVal);
			o.setBean2Number(cards);
		}
	}

	/*
	 * Updates an opponent's card after they harvest
	 */
	public void opponentHarvest(String info) {
		int player = Integer.parseInt(info.substring(0,1));
		int spot = Integer.parseInt(info.substring(1,2));
		int coins = Integer.parseInt(info.substring(2));
		Opponent o = findOpponent(player);
		if(spot == 1) {
			o.setBean1(-1);
			o.setBean1Number(0);
		} else {
			o.setBean2(-1);
			o.setBean2Number(0);
		}
		o.setCoins(coins);
	}

	public void flipCard(String info) {
		player.addToActive(Integer.parseInt(info));
	}

	public void hideFlipped(String info) {
		player.hideFlipped(Integer.parseInt(info));
	}

	public static void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException e) {

		}
	}

}
