package com.mygdx.util;

import com.badlogic.gdx.graphics.Color;

/**
 * An enum type for changing the colors of the character skin 
 * @author Kalan Kriner
 */
public enum CharacterSkin
{

        WHITE("White", 1.0f, 1.0f, 1.0f),
        GRAY("Gray", 0.7f, 0.7f, 0.7f),
        BROWN("Brown", 0.7f, 0.5f, 0.3f);
        
        private String name;
        private Color color = new Color();
        
        /**
         * Creates the structure for the name and color
         * @param name name of the color
         * @param r red
         * @param g green
         * @param b blue
         */
        private CharacterSkin (String name, float r, float g, float b)
        {
            this.name = name;
            color.set(r, g, b, 1.0f);
        }
        
        /**
         * Returns the name
         */
        @Override
        public String toString()
        {
            return name;
        }
        
        /**
         * @return Color value to use
         */
        public Color getColor()
        {
            return color;
        }
}
