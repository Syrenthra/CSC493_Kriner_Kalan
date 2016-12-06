package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

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
    public Array<Integer> highScores=new Array<Integer>();
    private boolean scoresLoaded;
    
    
    private Preferences prefs;
    
    // Singleton: prevent instantiation from other classes
    private GamePreferences()
    {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
        scoresLoaded=false;
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
        if(!scoresLoaded)
        {
            highScores.add(prefs.getInteger("score1"));
            highScores.add(prefs.getInteger("score2"));
            highScores.add(prefs.getInteger("score3"));
            highScores.add(prefs.getInteger("score4"));
            scoresLoaded=true;
        }
    }
    
    /**
     * Adds a new score to the list and then sorts it
     * @param score to be added to the list
     */
    public void addScore(int score)
    {
        highScores.insert(0,score);
        sortScores();
        highScores.reverse();
        highScores.removeIndex(4);
    }
    
    /**
     * Sorts the scores of the high scores
     */
    private void sortScores()
    {
        highScores.sort();
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
        prefs.putInteger("score1", highScores.get(0));
        prefs.putInteger("score2", highScores.get(1));
        prefs.putInteger("score3", highScores.get(2));
        prefs.putInteger("score4", highScores.get(3));
        prefs.flush();
    }

}
