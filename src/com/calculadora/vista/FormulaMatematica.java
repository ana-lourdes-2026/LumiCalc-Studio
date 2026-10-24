package com.calculadora.vista;

import com.calculadora.matematica.calculo.Calculadora;
import com.calculadora.matematica.expresion.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Dibuja la expresión del motor con fracciones, potencias y notación matemática. */
public final class FormulaMatematica extends JComponent {
 private String texto="",prefijo="",sufijo="";private Nodo expresion;
 public FormulaMatematica(String t){setFont(new Font("Serif",Font.PLAIN,25));setText(t);}
 public void setText(String t){
  texto=t==null?"":t;expresion=null;prefijo="";sufijo="";
  int igual=texto.indexOf('=');
  if(igual>=0){String rhs=texto.substring(igual+1).trim();prefijo=texto.substring(0,igual).trim().replace("'","′")+" = ";
   int aprox=rhs.indexOf(" ≈ ");if(aprox>=0){sufijo=rhs.substring(aprox);rhs=rhs.substring(0,aprox).trim();}
   if(rhs.endsWith(" + C")){rhs=rhs.substring(0,rhs.length()-4);sufijo=" + C";}
   try{expresion=Calculadora.parse(rhs,rhs.matches(".*(?<![A-Za-z])y(?![A-Za-z]).*")?'y':'x');}catch(RuntimeException ex){expresion=null;}
  }
  setToolTipText(texto);revalidate();repaint();
 }
 public String getText(){return texto;}
 private interface Pintor{void dibujar(Graphics2D g,int x,int y);}
 private record Caja(int ancho,int alto,int base,Pintor pintor){void pintar(Graphics2D g,int x,int y){pintor.dibujar(g,x,y);}}
 private Caja literal(String t,Font f){FontMetrics m=getFontMetrics(f);return new Caja(m.stringWidth(t),m.getHeight(),m.getAscent(),(g,x,y)->{g.setFont(f);g.drawString(t,x,y+m.getAscent());});}
 private Caja fila(Caja... cajas){int ancho=0,base=0,bajo=0;for(Caja c:cajas){ancho+=c.ancho;base=Math.max(base,c.base);bajo=Math.max(bajo,c.alto-c.base);}final int baseline=base;return new Caja(ancho,base+bajo,base,(g,x,y)->{int pos=x;for(Caja c:cajas){c.pintar(g,pos,y+baseline-c.base);pos+=c.ancho;}});}
 private Caja parentesis(Caja c,Font f){return fila(literal("(",f),c,literal(")",f));}
 private Caja fraccion(Caja n,Caja d,Font f){int ancho=Math.max(n.ancho,d.ancho)+10,alto=n.alto+d.alto+9,base=n.alto+5+f.getSize()/4;return new Caja(ancho,alto,base,(g,x,y)->{n.pintar(g,x+(ancho-n.ancho)/2,y);g.setStroke(new BasicStroke(1.4f));g.drawLine(x+1,y+n.alto+3,x+ancho-1,y+n.alto+3);d.pintar(g,x+(ancho-d.ancho)/2,y+n.alto+8);});}
 private Caja potencia(Caja b,Caja e,Font f){int subir=Math.max(e.alto/2,f.getSize()/3);return new Caja(b.ancho+e.ancho,b.alto+subir,b.base+subir,(g,x,y)->{b.pintar(g,x,y+subir);e.pintar(g,x+b.ancho,y);});}
 private Caja dibujar(Nodo n,Font f){
  if(n instanceof Numero k){var q=k.getValor();return q.esEntero()?literal(q.toString(),f):fraccion(literal(Long.toString(q.getNumerador()),f),literal(Long.toString(q.getDenominador()),f),f);}
  if(n instanceof Variable v)return literal(v.getNombre(),f);
  if(n instanceof Constante c)return literal(c.getNombre().equals("pi")?"π":c.getNombre(),f);
  if(n instanceof Funcion fn){Caja argumento=dibujar(fn.getArgumento(),f);if(fn.getNombre().equals("exp"))return potencia(literal("e",f),dibujar(fn.getArgumento(),f.deriveFont(f.getSize2D()*.72f)),f);String nombre=switch(fn.getNombre()){case "sin"->"sen";case "sqrt"->"√";case "log"->"log₁₀";default->fn.getNombre();};return fila(literal(nombre,f),parentesis(argumento,f));}
  Operacion o=(Operacion)n;Nodo a=o.getIzq(),b=o.getDer();char op=o.getOp();Caja izq=dibujar(a,f),der=dibujar(b,f);
  if(op=='/')return fraccion(izq,der,f);
  if(op=='^'){if(a.precedencia()<=3)izq=parentesis(izq,f);return potencia(izq,dibujar(b,f.deriveFont(f.getSize2D()*.72f)),f);}
  if(op=='*'){
   if(o.esMenosUnario())return fila(literal("−",f),b.precedencia()<2?parentesis(der,f):der);
   if(a.precedencia()<2&&!(a instanceof Numero))izq=parentesis(izq,f);
   if(b.precedencia()<2)der=parentesis(der,f);
   boolean implicito=a instanceof Numero&&!(b instanceof Numero)||b instanceof Variable&&a instanceof Operacion p&&p.getOp()=='^';
   return fila(izq,literal(implicito?"":" · ",f),der);
  }
  if(op=='-'&&b.precedencia()<=1||op=='+'&&b.esNegativo())der=parentesis(der,f);
  return fila(izq,literal(op=='+'?" + ":" − ",f),der);
 }
 private Caja contenido(){Font f=getFont();return expresion==null?literal(texto,f):fila(literal(prefijo,f),dibujar(expresion,f),literal(sufijo,f));}
 @Override public Dimension getPreferredSize(){Caja c=contenido();return new Dimension(c.ancho+4,Math.max(34,c.alto+8));}
 @Override protected void paintComponent(Graphics g){Graphics2D a=(Graphics2D)g.create();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);a.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);a.setColor(getForeground());Caja c=contenido();double escala=Math.min(1.0,(double)Math.max(1,getWidth()-4)/Math.max(1,c.ancho));a.translate(0,Math.max(0,(getHeight()-c.alto*escala)/2));a.scale(escala,escala);c.pintar(a,0,0);a.dispose();}
}
