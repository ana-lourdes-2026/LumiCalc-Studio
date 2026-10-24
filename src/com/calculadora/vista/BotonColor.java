package com.calculadora.vista;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
/** Botón plano: mantiene su color también con el tema Nimbus de Windows. */
public final class BotonColor extends JButton {
 public BotonColor(String texto,Color color){super(texto);setBackground(color);setForeground(Color.WHITE);setContentAreaFilled(false);setOpaque(false);setBorder(new EmptyBorder(9,12,9,12));setFont(new Font("SansSerif",Font.BOLD,12));setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));setRolloverEnabled(true);}
 @Override protected void paintComponent(Graphics g){Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);Color c=getBackground();if(!isEnabled())c=new Color(175,170,185);else if(getModel().isPressed())c=c.darker();else if(getModel().isRollover())c=new Color(Math.min(255,c.getRed()+10),Math.min(255,c.getGreen()+10),Math.min(255,c.getBlue()+10));a.setPaint((getText().contains("Calcular")||getText().contains("Analizar"))?new GradientPaint(0,0,c,getWidth(),getHeight(),new Color(Math.max(0,c.getRed()-15),Math.max(0,c.getGreen()-18),Math.min(255,c.getBlue()+15))):c);a.fillRoundRect(0,0,getWidth(),getHeight(),18,18);if(isFocusOwner()){a.setColor(getForeground());a.setStroke(new BasicStroke(1.5f));a.drawRoundRect(3,3,getWidth()-7,getHeight()-7,10,10);}a.dispose();super.paintComponent(g);}
 @Override protected void paintBorder(Graphics g){}
}
