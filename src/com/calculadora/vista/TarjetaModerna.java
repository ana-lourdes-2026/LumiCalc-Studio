package com.calculadora.vista;
import javax.swing.*;
import java.awt.*;
/** Superficie redondeada con degradado opcional y un borde suave. */
public final class TarjetaModerna extends JPanel {
 private final Color finalColor;private final int radio;
 public TarjetaModerna(LayoutManager layout,Color color){this(layout,color,null,22);}
 public TarjetaModerna(LayoutManager layout,Color color,Color fin,int radio){super(layout);setBackground(color);finalColor=fin;this.radio=radio;setOpaque(false);}
 @Override protected void paintComponent(Graphics g){Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);a.setPaint(finalColor==null?getBackground():new GradientPaint(0,0,getBackground(),getWidth(),getHeight(),finalColor));a.fillRoundRect(0,0,getWidth()-1,getHeight()-1,radio,radio);if(finalColor!=null){a.setColor(new Color(255,255,255,14));a.fillOval(getWidth()-150,-70,240,240);a.fillOval(getWidth()-300,getHeight()-30,200,200);}else{a.setColor(new Color(126,105,166,35));a.drawRoundRect(0,0,getWidth()-1,getHeight()-1,radio,radio);}a.dispose();super.paintComponent(g);}
}
