package com.beangame.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
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
    private ActionThread actionThread;
    private InputThread inputThread;

	private SpriteBatch batch;
	private BitmapFont font;
	private GlyphLayout layout;
	private TextField ipInput;
	private TextField nameInput;
	private Stage setupStage;
	private Stage introStage;
	private Stage endStage;
	private Stage stage;
	private InputMultiplexer inputMultiplexer;

	private Button connect;
	private Button startGame;

	private ArrayList<Opponent> opponents;

	private Player player;
    private Trade trade;
    private TradeOffer tradeOffer;

    private int gameStatus;
    private boolean gameStarted;
    private boolean opponentsGenerated;
    private int connected;
    private String name;
    private String[] names;
    private int[] scores;

	public boolean trading;
	public boolean showTradeOffer;
	public boolean turn;
	public int currentTurn;
	public int playerNumber;
	public int deckCards;
	public int discardCards;
	public int discardTop;

	@Override
	public void create () {

		Assets.loadTextures();
		generateElements();

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
		deckCards = 0;
		discardCards = 0;
		discardTop = -1;
		opponentsGenerated = false;
		trading = false;
		showTradeOffer = false;
		gameStatus = 0;
		connected = 0;
		currentTurn = 0;
		playerNumber = 0;

		setupStage.addActor(ipInput);
		setupStage.addActor(nameInput);
		setupStage.addActor(connect);
		introStage.addActor(startGame);

		inputMultiplexer.addProcessor(player.getStage());
		inputMultiplexer.addProcessor(trade.getStage());
		inputMultiplexer.addProcessor(tradeOffer.getStage());
		inputMultiplexer.addProcessor(player.getTradedHand().getStage());
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(setupStage);
}

	/*
	 * Creates buttons and text fields
	 */
	private void generateElements() {

		nameInput = new TextField("", Assets.textFieldSkin);
		ipInput = new TextField("", Assets.textFieldSkin);

		nameInput.setMessageText("Enter Name");
		ipInput.setMessageText("Enter Host IP");

		nameInput.setSize(480, 60);
		ipInput.setSize(480, 60);

		nameInput.setPosition(Settings.RES_WIDTH/2 - nameInput.getWidth()/2, 380);
		ipInput.setPosition(Settings.RES_WIDTH/2 - ipInput.getWidth()/2, 300);

		nameInput.setAlignment(Align.center);
		ipInput.setAlignment(Align.center);


		connect = new Button(Assets.buttonSkin, "connect");
		startGame = new Button(Assets.buttonSkin, "send");

		connect.setSize(100, 50);
		startGame.setSize(80, 80);

		connect.setPosition(Settings.RES_WIDTH/2 - connect.getWidth()/2, 200);
		startGame.setPosition(Settings.RES_WIDTH - 130, Settings.RES_HEIGHT - 130);

		connect.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				attemptConnection();
			}
		});
		startGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				requestGameStart();
			}
		});
	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		OrthographicCamera camera = new OrthographicCamera();
		camera.setToOrtho(false, Settings.RES_WIDTH, Settings.RES_HEIGHT);
		Vector3 mousePos3 = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
		float mouseX = mousePos3.x;
		float mouseY = mousePos3.y;

		switch(gameStatus) {
            case 0: //setup screen
                batch.begin();
                batch.enableBlending();
                layout.setText(font, "https://github.com/Gelato88/Bean-Game");
                font.draw(batch, layout, 20, Settings.RES_HEIGHT - 20 - layout.height);
                layout.setText(font, "Code and crappy art by Scott");
                font.draw(batch, layout, 20, Settings.RES_HEIGHT - 40 - layout.height);
                layout.setText(font, "Card art by Joey");
                font.draw(batch, layout, 20, Settings.RES_HEIGHT - 60 - layout.height);
                batch.end();
                setupStage.draw();
                setupStage.act();
                break;
			case 3: //game over screen
				batch.begin();
				batch.enableBlending();
				layout.setText(font, "Game Over!");
				font.draw(batch, layout, Settings.RES_WIDTH-layout.width/2, Settings.RES_HEIGHT-100);
				for(int i = 0; i < scores.length; i++) {
					layout.setText(font, names[i] + ":   " + scores[i]);
					font.draw(batch, layout, Settings.RES_WIDTH - layout.width/2, Settings.RES_HEIGHT - 300 - 30 * i);
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
                    layout.setText(font, "You are " + name + " (Player " + playerNumber + ")");
                    font.draw(batch, layout, Settings.RES_WIDTH/2 - layout.width/2, Settings.RES_HEIGHT - 15);
                    if(playerNumber == currentTurn) {
                    	layout.setText(font, "It is currently your turn.");
					} else if(currentTurn != 0) {
                    	Opponent turn = findOpponent(currentTurn);
						layout.setText(font, "It is currently " + turn.getName() + "'s turn. (Player " + currentTurn + ")");
					} else {
                    	layout.setText(font, "");
					}
                    font.draw(batch, layout, Settings.RES_WIDTH/2-layout.width/2, Settings.RES_HEIGHT-30);
                    batch.end();

                    stage.draw();
                    stage.act();

                    player.render(batch, mouseX, mouseY);
                    for (Opponent o : opponents) {
                        o.render(batch, mouseX, mouseY);
                    }
                    if (trading) {
                        trade.render(batch, mouseX, mouseY);
                    }
                    if(showTradeOffer) {
                        tradeOffer.render(batch, mouseX, mouseY);
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
		sendMessage(1003, ""+playerNumber+name);
	}

	public void setCurrentTurn(int player) {
		currentTurn = player;
	}

	/*
	 * Attempts to connect this client to a server
	 * If successful, creates a terminal and goes to the waiting screen
	 */
	public void attemptConnection() {
		Settings.IP = ipInput.getText();
		name = nameInput.getText();
		actionThread = new ActionThread(this);
		actionThread.start();
		try {
			s = new Socket(InetAddress.getByName(Settings.IP), Settings.PORT);
			output = s.getOutputStream();
			writer = new PrintWriter(output, true);
			inputThread = new InputThread(s, this, actionThread);
			inputThread.start();
			if(Settings.DEV_MODE) {
				terminal = new Terminal(this);
				terminal.start();
			}
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
		writer.println("" + code + str);
	}

	/*
	 * Sends a message to the server
	 */
	public void sendMessage(String str) {
		writer.println(str);
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
				opponents.add(new Opponent(this, str, i, Settings.RES_WIDTH - (Settings.OPPONENT_BOX_WIDTH+10)*(j%2 + 1), Settings.RES_HEIGHT - (Settings.OPPONENT_BOX_HEIGHT+10)*((j-1)/2 + 1)));
			}
		}
		for(int i = 0; i < opponents.size(); i++) {
			sendMessage(1004, ""+playerNumber+opponents.get(i).getPlayerNum());
		}
		opponentsGenerated = true;
	}

	/*
	 * Sets and opponent's name
	 */
	public void setOpponentName(String info) {
		int opponentNumber = Integer.parseInt(info.substring(0,1));
		String name = info.substring(1);
		if(opponentNumber > playerNumber) {
			opponentNumber--;
		}
		opponents.get(opponentNumber-1).setName(name);
	}

	/*
	 * Ends the game and sorts the players by score
	 */
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
			int maxInd = i;
			for(int j = i+1; j < scores.length; j++) {
				if(scores[j] > scores[i]) {
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
		turn = false;
		sendMessage(3001, "");
	}

	/*
	 * Shows a trade request
	 */
	public void showTrade(String info) {
		int sender = Integer.parseInt(info.substring(0,1));
		int players = Integer.parseInt(info.substring(1,2));
		int num;
		if(sender < playerNumber) {
			num = playerNumber-1;
		} else {
			num = playerNumber;
		}
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
			tradeOffer.setValues(requested, offered, actives);
			showTradeOffer = true;
		}
	}

	/*
	 * Opens the trade menu
	 */
	public void openTrade() {
		trade.resetValues();
		trading = true;
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

	/*
	 * Updates the number of cards in an opponent's hand
	 */
	public void opponentCardUpdate(String info) {
		int player = Integer.parseInt(info.substring(0,1));
		int cards = Integer.parseInt(info.substring(1));
		findOpponent(player).setCards(cards);
	}

	/*
	 * Updates the number of cards in the deck
	 */
	public void deckSizeUpdate(String info) {
		deckCards = Integer.parseInt(info);
	}

	/*
	 * Updates the number of cards in the discard and the top discarded card
	 */
	public void discardUpdate(String info) {
		discardCards = Integer.parseInt(info.substring(0,1));
		discardTop = Integer.parseInt(info.substring(1));
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		player.getStage().getViewport().update(width, height, true);
		player.getTradedHand().getStage().getViewport().update(width, height, true);
		trade.getStage().getViewport().update(width, height, true);
		tradeOffer.getStage().getViewport().update(width, height, true);
		setupStage.getViewport().update(width, height, true);
		introStage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose () {
		batch.dispose();
		Assets.disposeAll();
	}

}
