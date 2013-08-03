/**
 * Enum permettant de lister les diff�rentes couleurs possibles des diff�rentes faces d'un cube composant une face du Rubki's Cube
 * 
 * La couleur {None} correspond � l'absence de couleur, c'est � dire le cas ou une face d'un cube n'est pas visible
 * 
 * @author Arnaud Forgues
 */
public enum Color {
    NONE(java.awt.Color.BLACK),
    BLUE(java.awt.Color.BLUE),
    RED(java.awt.Color.RED),
    GREEN(java.awt.Color.GREEN),
    YELLOW(java.awt.Color.YELLOW),
    WHITE(java.awt.Color.WHITE),
    ORANGE(java.awt.Color.ORANGE);
    
    private java.awt.Color awtColor;
    public java.awt.Color getAwtColor() {return this.awtColor;}
    
    Color(java.awt.Color awtColor) {
    	this.awtColor = awtColor;
    }
    
    public String toString() {
    	return this.name().substring(0, 1);
    }
}