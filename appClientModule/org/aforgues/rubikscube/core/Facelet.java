package org.aforgues.rubikscube.core;
/**
 * Enum permettant de lister les différentes couleurs possibles des différentes faces d'un cube composant une face du Rubki's Cube
 * 
 * La couleur {None} correspond à l'absence de couleur, c'est à dire le cas ou une face d'un cube n'est pas visible
 * 
 * @author Arnaud Forgues
 */
public enum Facelet {
    NONE(java.awt.Color.BLACK),
    BLUE(java.awt.Color.BLUE),
    RED(java.awt.Color.RED),
    GREEN(java.awt.Color.GREEN),
    YELLOW(java.awt.Color.YELLOW),
    WHITE(java.awt.Color.WHITE),
    ORANGE(java.awt.Color.ORANGE);
    
    private java.awt.Color awtColor;
    public java.awt.Color getAwtColor() {return this.awtColor;}
    
    Facelet(java.awt.Color awtColor) {
    	this.awtColor = awtColor;
    }
    
    public String toString() {
    	return this.name().substring(0, 1);
    }
}