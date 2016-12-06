package com.mygdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Assets;
import com.mygdx.game.objects.SmallCrate;
import com.mygdx.util.AudioManager;
import com.mygdx.util.CharacterSkin;
import com.mygdx.util.Constants;
import com.mygdx.util.GamePreferences;

/**
 * The high score screen that will display the high scores after the game is over
 * @author Kalan Kriner
 */
public class HighScoreScreen extends AbstractGameScreen
{
    
    private static final String TAG = HighScoreScreen.class.getName();
    
    private Stage stage;
    private Skin skinTankRun;
    
    //Background
    private Image imgBackground;

    
    //Options
    private Window winHigh;
    private Button btnMenu;
    
    private Skin skinLibgdx;
    
    GamePreferences prefs = GamePreferences.instance;
    //Debug
    private final float DEBUG_REBUILD_INTERVAL =5.0f;
    private boolean debugEnabled =false;
    private float debugRebuildStage;
    
    
    /**
     * Constructor that uses the super
     * @param game Keeps track of the main game, can change screens
     */
    public HighScoreScreen(Game game, int score)
    {
        super(game);
       //prefs.load();
        //Check score here
        prefs.addScore(score);
        prefs.save();
    }
    
    /**
     * Constantly refreshes the screen to black until a touch is detected then draws the game
     */
    @Override
    public void render(float deltaTime)
    {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if(debugEnabled)
        {
            debugRebuildStage -= deltaTime;
            if(debugRebuildStage < 0)
            {
                debugRebuildStage = DEBUG_REBUILD_INTERVAL;
                rebuildStage();
            }
        }
        stage.act(deltaTime);
        stage.draw();
        stage.setDebugAll(false);
    }
    
    /**
     * Updates the size of the screen based on the window size
     */
    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
        
    }
    
    /**
     * Creates a viewport, sets up input processing and then builds the high score screen
     */
    @Override
    public void show()
    {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
    }
    
    /**
     * Clears the menu screen
     */
    @Override
    public void hide()
    {
        stage.dispose();
        skinTankRun.dispose();
        
    }
    
    /**
     * Would be used with a paused game
     */
    @Override
    public void pause()
    {
    }
    
    /**
     * Puts together all of the layers of the high score screen
     */
    private void rebuildStage()
    {
        skinTankRun= new Skin(Gdx.files.internal(Constants.SKIN_TANK_RUN_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
        skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI), 
                new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
        
        //Build all layers
        Table layerBackground = buildBackgroundLayer();
        Table layerOptionsWindow = buildHighScoreLayer();
        
        //Assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerOptionsWindow);
    }
    
    /**
     * Builds the Background image layer
     * @return table to be added to menu
     */
    private Table buildBackgroundLayer()
    {
        Table layer = new Table();
        // + Background
        imgBackground = new Image(skinTankRun, "background");
        layer.add(imgBackground);
        return layer;
    }
    
    /**
     * Adds together all of the different options to a window that can be opened
     * @return Layer to be added when option is clicked
     */
    private Table buildHighScoreLayer()
    {
        winHigh = new Window("High Score", skinLibgdx);
        // + Audio Settings: Sound/Music CheckBox and Volume Slider
        winHigh.add(buildHighScoreList()).row();

        
        // Make Options window slightly transparent
        winHigh.setColor(1, 1, 1, 0.8f);
        // Hide options window by default
        winHigh.setVisible(true);
        if(debugEnabled) 
            winHigh.debug();
        // Let TableLayout recalculate widget sizes and positions
        winHigh.pack();
        // Move options window to bottom right corner
        winHigh.setPosition(Constants.VIEWPORT_GUI_WIDTH- winHigh.getWidth() - 50, 50);
        return  winHigh;
    }
    
    /**
     * Creates the high score list and the button
     * @return table to be added to the option menu
     */
    private Table buildHighScoreList()
    {
        Table tbl = new Table();
        // +Title "Audio"
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("High Scores", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        tbl.add(new Label("1.   "+prefs.highScores.get(0),skinLibgdx, "default-font", Color.GOLD)).colspan(9);
        tbl.row();
        tbl.add(new Label("2.   "+prefs.highScores.get(1),skinLibgdx, "default-font", Color.GOLD)).colspan(9);
        tbl.row();
        tbl.add(new Label("3.   "+prefs.highScores.get(2),skinLibgdx, "default-font", Color.GOLD)).colspan(9);
        tbl.row();
        tbl.add(new Label("4.   "+prefs.highScores.get(3),skinLibgdx, "default-font", Color.GOLD)).colspan(9);
        tbl.row();
        btnMenu = new TextButton("Menu", skinLibgdx);
        tbl.add(btnMenu);
        btnMenu.addListener(new ChangeListener() 
        {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onMenuClicked();
            }
        });
        return tbl;
    }

    /**
     * Changes to the menu screen
     */
    private void onMenuClicked()
    {
        game.setScreen(new MenuScreen(game));   
    }
 
    
}
