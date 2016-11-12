package com.mygdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.Assets;
import com.mygdx.util.CharacterSkin;
import com.mygdx.util.Constants;
import com.mygdx.util.GamePreferences;
import com.mygdx.util.AudioManager;

/**
 * Main menu that has a play button and the options menu and a background for the game
 * @author Kalan Kriner
 */
public class MenuScreen extends AbstractGameScreen
{
    private static final String TAG = MenuScreen.class.getName();
    
    private Stage stage;
    private Skin skinCanyonBunny;
    
    //Menu
    private Image imgBackground;
    private Image imgLogo;
    private Image imgInfo;
    private Image imgCoins;
    private Image imgBunny;
    private Button btnMenuPlay;
    private Button btnMenuOptions;
    
    //Options
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;
    private CheckBox chkSound;
    private Slider sldSound;
    private CheckBox chkMusic;
    private Slider sldMusic;
    private SelectBox <CharacterSkin> selCharSkin;
    private Image imgCharSkin;
    private CheckBox chkShowFpsCounter;
    
    private Skin skinLibgdx;
    
    //Debug
    private final float DEBUG_REBUILD_INTERVAL =5.0f;
    private boolean debugEnabled =false;
    private float debugRebuildStage;
    
    
    /**
     * Constructor that uses the super
     * @param game Keeps track of the main game, can change screens
     */
    public MenuScreen(Game game)
    {
        super(game);
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
        //stage.setDebugAll(true);
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
     * Creates a viewport, sets up input processing and then builds the menu screen
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
        skinCanyonBunny.dispose();
        
    }
    
    /**
     * Would be used with a paused game
     */
    @Override
    public void pause()
    {
    }
    
    /**
     * Puts together all of the layers of the menu after calling their respective construction methods
     */
    private void rebuildStage()
    {
        skinCanyonBunny= new Skin(Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
        skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI), 
                new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
        
        //Build all layers
        Table layerBackground = buildBackgroundLayer();
        Table layerObjects = buildObjectsLayer();
        Table layerLogos = buildLogosLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();
        
        //Assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerObjects);
        stack.add(layerLogos);
        stack.add(layerControls);
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
        imgBackground = new Image(skinCanyonBunny, "background");
        layer.add(imgBackground);
        return layer;
    }
    
    /**
     * Builds the Objects layer for the menu
     * @return table to be added to menu
     */
    private Table buildObjectsLayer()
    {
        Table layer = new Table();
        // + Coins
        imgCoins = new Image(skinCanyonBunny, "coins");
        layer.addActor(imgCoins);
        imgCoins.setOrigin(imgCoins.getWidth()/2, imgCoins.getHeight()/2);
        imgCoins.addAction(sequence(
                moveTo(135,-20),
                scaleTo(0,0),
                fadeOut(0),
                delay(2.5f),
                parallel(moveBy(0, 100, 0.5f, Interpolation.swingOut), scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear)),
                alpha(1.0f, 0.5f)));
        // + Bunny
        imgBunny = new Image(skinCanyonBunny, "bunny");
        layer.addActor(imgBunny);
        imgBunny.addAction(sequence(
                moveTo(655, 510),
                delay(4.0f),
                moveBy(-70, -100, 0.5f, Interpolation.fade),
                moveBy(-100, -50, 0.5f, Interpolation.fade),
                moveBy(-150, -300, 1.0f, Interpolation.fade)));
        return layer;
    }
    
    /**
     * Adds the logos to a layer for the menu 
     * @return table to be added to the menu
     */
    private Table buildLogosLayer()
    {
        Table layer = new Table();
        layer.left().top();
        // + Game Logo
        imgLogo = new Image(skinCanyonBunny, "logo");
        layer.add(imgLogo);
        layer.row().expandY();
        // +Info Logos
        imgInfo = new Image(skinCanyonBunny, "info");
        layer.add(imgInfo).bottom();
        if(debugEnabled) layer.debug();
        return layer;
    }
    
    /**
     * Adds the buttons for play and options
     * @return table to be added to the menu
     */
    private Table buildControlsLayer()
    {
        Table layer = new Table();
        layer.right().bottom();
        // + Player Button
        btnMenuPlay = new Button(skinCanyonBunny, "play");
        layer.add(btnMenuPlay);
        btnMenuPlay.addListener(new ChangeListener() 
        {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        layer.row();
        // + Options Button
        btnMenuOptions = new Button(skinCanyonBunny, "options");
        layer.add(btnMenuOptions);
        btnMenuOptions.addListener(new ChangeListener() 
        {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });
        if(debugEnabled) layer.debug();
        return layer;
    }
    
    /**
     * Changes the screen when the play button is clicked
     */
    private void onPlayClicked()
    {
        game.setScreen(new GameScreen(game));
    }
    
    /**
     * Opens the options menu when it is clicked
     */
    private void onOptionsClicked()
    {
        loadSettings();
        showMenuButtons(false);
        showOptionsWindow(true, true);
    }
    
    /**
     * Adds together all of the different options to a window that can be opened
     * @return Layer to be added when option is clicked
     */
    private Table buildOptionsWindowLayer()
    {
        winOptions = new Window("Options", skinLibgdx);
        // + Audio Settings: Sound/Music CheckBox and Volume Slider
        winOptions.add(buildOptWinAudioSettings()).row();
        // + Character Skin: Selection Box (White, Gray, Brown)
        winOptions.add(buildOptWinSkinSelection()).row();
        // + Debug: Show FPS Counter
        winOptions.add(buildOptWinDebug()).row();
        // + Seperatoe and Buttons (Save, Cancel)
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
        
        // Make Options window slightly transparent
        winOptions.setColor(1, 1, 1, 0.8f);
        // Hide options window by default
        showOptionsWindow(false, false);
        if(debugEnabled) 
            winOptions.debug();
        // Let TableLayout recalculate widget sizes and positions
        winOptions.pack();
        // Move options window to bottom right corner
        winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH- winOptions.getWidth() - 50, 50);
        return  winOptions;
    }
    
    /**
     * Creates all of the UI elements for the Audio settings of music and sound
     * @return table to be added to the option menu
     */
    private Table buildOptWinAudioSettings()
    {
        Table tbl = new Table();
        // +Title "Audio"
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        // + Checkbox "Sound" label, sound volume slider
        chkSound = new CheckBox("", skinLibgdx);
        tbl.add(chkSound);
        tbl.add(new Label("Sound", skinLibgdx));
        sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldSound);
        tbl.row();
        // + Checkbox "Music" label, music volume slider
        chkMusic = new CheckBox("", skinLibgdx);
        tbl.add(chkMusic);
        tbl.add(new Label("Music", skinLibgdx));
        sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldMusic);
        tbl.row();
        return tbl;
    }
    
    /**
     * Builds the skin selection drop down for choosing the color of the bunny
     * @return table to be added to the option menu
     */
    private Table buildOptWinSkinSelection()
    {
        Table tbl = new Table();
        // + Title: "Character Skin"
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Character Skin", skinLibgdx,"default-font",Color.ORANGE)).colspan(2);
        tbl.row();
        // + Drop down box filled with skin items
        selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
        
        selCharSkin.setItems(CharacterSkin.values());
        
        selCharSkin.addListener(new ChangeListener()
                {
                    @Override
                    public void changed(ChangeEvent event, Actor actor)
                    {
                        onCharSkinSelected(((SelectBox<CharacterSkin>)actor).getSelectedIndex());
                    }
                });
        tbl.add(selCharSkin).width(120).padRight(20);
        // + Skin preview image
        imgCharSkin = new Image(Assets.instance.bunny.head);
        tbl.add(imgCharSkin).width(50).height(50);
        return tbl;  
    }
    
    /**
     * Adds the debug options to the option window
     * @return layers for the option window
     */
    private Table buildOptWinDebug()
    {
        Table tbl = new Table();
        // + Title: "Debug"
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        // + Checkbox, "Show FPS Counter" label
        chkShowFpsCounter = new CheckBox("", skinLibgdx);
        tbl.add(new Label("Show FPS Counter",skinLibgdx));
        tbl.add(chkShowFpsCounter);
        tbl.row();
        return tbl;
    }
    
    /**
     * Adds the save and cancel button for saving or canceling current changes to options
     * @return table for the options menu
     */
    private Table buildOptWinButtons()
    {
        Table tbl = new Table();
        // +Seperator
        Label lbl = null;
        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new LabelStyle(lbl.getStyle()));
        lbl.getStyle().background= skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        tbl.row();
        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.5f, 0.5f, 0.5f, 1);
        lbl.setStyle(new LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        tbl.row();
        // + Save Button with event handler
        btnWinOptSave = new TextButton("Save", skinLibgdx);
        tbl.add(btnWinOptSave).padRight(30);
        btnWinOptSave.addListener(new ChangeListener()
                {
                    @Override
                    public void changed (ChangeEvent event, Actor actor) 
                    {
                        onSaveClicked();
                    }
            
                });
        // + Cancel Button with event Handler
        btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
        tbl.add(btnWinOptCancel);
        btnWinOptCancel.addListener(new ChangeListener()
                {
                    @Override
                    public void changed (ChangeEvent event, Actor actor) 
                    {
                        onCancelClicked();
                    }
            
                });
        return tbl;
    }
    
    /**
     * Loads the settings of the options in the preferences file
     */
    private void loadSettings()
    {
        GamePreferences prefs = GamePreferences.instance;
        prefs.load();
        chkSound.setChecked(prefs.sound);
        sldSound.setValue(prefs.volSound);
        chkMusic.setChecked(prefs.music);
        sldMusic.setValue(prefs.volMusic);
        selCharSkin.setSelectedIndex(prefs.charSkin);
        onCharSkinSelected(prefs.charSkin);
        chkShowFpsCounter.setChecked(prefs.showFpsCounter);
    }
    
    /**
     * Saves the settings of the options in the preferences file
     */
    private void saveSettings()
    {
        GamePreferences prefs = GamePreferences.instance;
        prefs.sound = chkSound.isChecked();
        prefs.volSound = sldSound.getValue();
        prefs.music = chkMusic.isChecked();
        prefs.volMusic = sldMusic.getValue();
        prefs.charSkin = selCharSkin.getSelectedIndex();
        prefs.showFpsCounter = chkShowFpsCounter.isChecked();
        prefs.save();
    }
    
    /**
     * Changes the character skin to the specified skin from the indexes
     * @param index which skin is being chosen
     */
    private void onCharSkinSelected(int index)
    {
        CharacterSkin skin = CharacterSkin.values()[index];
        imgCharSkin.setColor(skin.getColor());
    }
    
    
    /**
     * Saves the current preferences from the option menu and closes option menu
     */
    private void onSaveClicked()
    {
        saveSettings();
        onCancelClicked();
        AudioManager.instance.onSettingsUpdated();
    }
    
    
    /**
     * Removes the option window 
     */
    private void onCancelClicked()
    {
        showMenuButtons(true);
        showOptionsWindow(false, false);
        AudioManager.instance.onSettingsUpdated();
    }
    
    /**
     * Gives an animated sense to the menu buttons
     * @param visible if the buttons are visible or not
     */
    private void showMenuButtons(boolean visible)
    {
        float moveDuration =1.0f;
        Interpolation moveEasing = Interpolation.swing;
        float delayOptionsButton = 0.25f;
        
        float moveX = 300 * (visible ? -1 : 1);
        float moveY = 0 * (visible ? -1 : 1);
        final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
        btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
        
        btnMenuOptions.addAction(sequence(
                delay(delayOptionsButton),
                moveBy(moveX, moveY, moveDuration, moveEasing)
                ));
        SequenceAction seq = sequence();
        if(visible)
        {
            seq.addAction(delay(delayOptionsButton + moveDuration));
            seq.addAction(run(new Runnable()
                    {
                        public void run()
                        {
                            btnMenuPlay.setTouchable(touchEnabled);
                            btnMenuOptions.setTouchable(touchEnabled);
                        }
                    }));
            stage.addAction(seq);
        }
    }
    
    /**
     * animates the option window
     * @param visible if the window is enabled
     * @param animated if it is to be animated
     */
    private void showOptionsWindow(boolean visible, boolean animated)
    {
        float alphaTo = visible ? 0.8f : 0.0f;
        float duration = animated ? 1.0f : 0.0f;
        Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
        winOptions.addAction(sequence(
                touchable(touchEnabled),
                alpha(alphaTo, duration)
                ));
    }
}
