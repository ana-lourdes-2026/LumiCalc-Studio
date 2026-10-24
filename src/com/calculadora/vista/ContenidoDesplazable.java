package com.calculadora.vista;
import javax.swing.*;
import java.awt.*;
/** Conserva el ancho de la ventana y deja bajar hasta las pestañas sin aplastarlas. */
public final class ContenidoDesplazable extends JPanel implements Scrollable {
 public ContenidoDesplazable(LayoutManager layout){super(layout);}
 public Dimension getPreferredScrollableViewportSize(){return new Dimension(680,700);}
 public int getScrollableUnitIncrement(Rectangle visible,int orientacion,int direccion){return 24;}
 public int getScrollableBlockIncrement(Rectangle visible,int orientacion,int direccion){return Math.max(24,visible.height-48);}
 public boolean getScrollableTracksViewportWidth(){return true;}
 public boolean getScrollableTracksViewportHeight(){return getParent() instanceof JViewport&&getParent().getHeight()>getPreferredSize().height;}
}
