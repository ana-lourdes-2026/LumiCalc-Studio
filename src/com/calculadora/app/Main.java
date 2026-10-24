package com.calculadora.app;
import javax.swing.*;
import com.calculadora.vista.VentanaPrincipal;
public class Main {
 public static void main(String[] args){
  System.setProperty("awt.useSystemAAFontSettings","on");System.setProperty("swing.aatext","true");
  try{for(UIManager.LookAndFeelInfo estilo:UIManager.getInstalledLookAndFeels())if(estilo.getName().equals("Nimbus")){UIManager.setLookAndFeel(estilo.getClassName());break;}}
  catch(Exception ex){/* Usa el estilo Swing disponible. */}
  com.calculadora.vista.EstiloModerno.instalar();
  SwingUtilities.invokeLater(()->new VentanaPrincipal().setVisible(true));
 }
}
