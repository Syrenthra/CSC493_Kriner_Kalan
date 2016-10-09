package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Handles the storing, loading and saving of the game preferences in the options menu
 * @author Kalan Kriner
 */
public class GamePreferences
{
    public static final String TAG = GamePreferences.class.getName();
    
    public static final GamePreferences instance = new GamePreferences();
    
    public boolean sound;
    public boolean music;
    public float volSound;
    public float volMusic;
    public int charSkin;
    public boolean showFpsCounter;
    
    private Preferences prefs;
    
    // Singleton: prevent instantiation from other classes
    private GamePreferences()
    {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
    }
    
    /**
     * Loads in the previous preferences from the file
     */
    public void load()
    {
        sound = prefs.getBoolean("sound", true);
        music = prefs.getBoolean("music", true);
        volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
        volMusic = MathUtils.clamp(prefs.getFloat("volMusic", 0.5f), 0.0f, 1.0f);
        charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0), 0, 2);
        showFpsCounter = prefs.getBoolean("showFpsCounter",false);
        
    }
    
    /**
     * Saves the current selected preferences to the file
     */
    public void save()
    {
        prefs.putBoolean("sound", sound);
        prefs.putBoolean("music", music);
        prefs.putFloat("volSound", volSound);
        prefs.putFloat("volMusic", volMusic);
        prefs.putInteger("charSkind", charSkin);
        prefs.putBoolean("showFpsCounter", showFpsCounter);
        prefs.flush();
    }

}
