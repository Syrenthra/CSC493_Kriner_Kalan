package com.mygdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Assets;


/**
 * An abstract screen that has the methods a screen needs to handle
 * @author Kalan Kriner
 */
public abstract class AbstractGameScreen implements Screen
{
    protected Game game;
    
    public AbstractGameScreen (Game game)
    {
        this.game=game;
    }
    
    // Methods that will be unique to each screen and have to be made for each screen
    public abstract void render(float deltaTime);
    public abstract void resize(int width, int height);
    public abstract void show();
    public abstract void hide();
    public abstract void pause();
    
    /**
     * Creates new scene
     */
    public void resume()
    {
        Assets.instance.init(new AssetManager());
    }
    
    /**
     * Clears the screen
     */
    public void dispose()
    {
        Assets.instance.dispose();
    }

}
