package com.calculadora.vista;
import javax.swing.*;import java.awt.*;import java.awt.event.*;import java.awt.geom.*;
import com.calculadora.matematica.expresion.Nodo;
public class GraficaPanel extends JPanel{
 private boolean claro=false;private Double areaA=null,areaB=null;private Nodo funcion,comparacion;private Double vx,vy,tx,ty,pendiente;private double escala=38,offsetX=0,offsetY=0;private Point arrastre;
 public GraficaPanel(){setBackground(new Color(19,26,48));setPreferredSize(new Dimension(650,350));setToolTipText("Rueda: zoom · arrastra: desplazar · doble clic: centrar");addMouseWheelListener(e->{escala=Math.max(8,Math.min(240,escala*Math.pow(1.12,-e.getPreciseWheelRotation())));repaint();});MouseAdapter m=new MouseAdapter(){public void mousePressed(MouseEvent e){arrastre=e.getPoint();}public void mouseDragged(MouseEvent e){if(arrastre!=null){offsetX+=e.getX()-arrastre.x;offsetY+=e.getY()-arrastre.y;arrastre=e.getPoint();repaint();}}public void mouseReleased(MouseEvent e){arrastre=null;}public void mouseClicked(MouseEvent e){if(e.getClickCount()==2){offsetX=offsetY=0;escala=38;repaint();}}};addMouseListener(m);addMouseMotionListener(m);}
 public void tema(boolean valor){claro=valor;setBackground(claro?new Color(250,252,255):new Color(19,26,48));repaint();}
 public void area(Nodo n,double a,double b){funcion(n);areaA=Math.min(a,b);areaB=Math.max(a,b);repaint();}
 public void funcion(Nodo n){areaA=areaB=null;comparacion=null;funcion=n;vx=vy=tx=ty=pendiente=null;repaint();}
 public void tangente(Nodo n,double x,double y,double m){funcion(n);tx=x;ty=y;pendiente=m;repaint();}
 public void comparar(Nodo original,Nodo derivada){funcion(original);comparacion=derivada;repaint();}
 public void vector(double x,double y){areaA=areaB=null;comparacion=null;vx=x;vy=y;funcion=null;tx=ty=pendiente=null;repaint();}
 public void limpiar(){areaA=areaB=null;comparacion=null;funcion=null;vx=vy=tx=ty=pendiente=null;repaint();}
 public void acercar(){escala=Math.min(240,escala*1.25);repaint();}public void alejar(){escala=Math.max(8,escala/1.25);repaint();}public void centrar(){offsetX=offsetY=0;escala=38;repaint();}
 private void linea(Graphics2D g,double x1,double y1,double x2,double y2){g.draw(new Line2D.Double(x1,y1,x2,y2));}
 @Override protected void paintComponent(Graphics gg){super.paintComponent(gg);Graphics2D g=(Graphics2D)gg.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);int w=getWidth(),h=getHeight();double cx=w/2.0+offsetX,cy=h/2.0+offsetY;g.setFont(new Font("SansSerif",Font.PLAIN,11));double step=escala*(escala<18?5:1);g.setColor(claro?new Color(222,228,239):new Color(38,49,75));for(double x=cx%step;x<w;x+=step)linea(g,x,0,x,h);for(double y=cy%step;y<h;y+=step)linea(g,0,y,w,y);g.setColor(claro?new Color(75,88,115):new Color(125,143,179));g.setStroke(new BasicStroke(1.5f));linea(g,0,cy,w,cy);linea(g,cx,0,cx,h);g.drawString("X",w-20,(int)Math.max(14,Math.min(h-10,cy-9)));g.drawString("Y",(int)Math.max(8,Math.min(w-20,cx+8)),18);
 g.setColor(claro?new Color(88,101,129):new Color(153,166,195));int labelStep=escala<18?5:1;for(int i=-80;i<=80;i+=labelStep){double xx=cx+i*escala,yy=cy-i*escala;if(i!=0&&xx>15&&xx<w-15)g.drawString(""+i,(int)xx-6,(int)Math.max(12,Math.min(h-4,cy+15)));if(i!=0&&yy>12&&yy<h-10)g.drawString(""+i,(int)Math.max(4,Math.min(w-28,cx+7)),(int)yy-4);}
 if(funcion!=null&&areaA!=null&&areaB!=null){
  double lo=Math.max(areaA,(0-cx)/escala),hi=Math.min(areaB,(w-cx)/escala);
  g.setColor(new Color(151,98,238,105));
  for(int px=(int)Math.max(0,Math.floor(cx+lo*escala));px<=Math.min(w-1,(int)Math.ceil(cx+hi*escala));px+=2){
   double x=(px-cx)/escala;
   if(x<areaA||x>areaB)continue;
   try{double y=funcion.evaluar(x),py=cy-y*escala;
    if(Double.isFinite(py)&&Math.abs(py)<100000){int y1=(int)Math.max(0,Math.min(h,cy)),y2=(int)Math.max(0,Math.min(h,py));g.fillRect(px,Math.min(y1,y2),2,Math.max(1,Math.abs(y2-y1)));}}
   catch(Exception ex){}
  }
  g.setColor(claro?new Color(72,52,126):new Color(212,198,255));
  g.drawString("Área con signo ["+areaA+", "+areaB+"]",12,18);
 }
 if(funcion!=null)pintarCurva(g,funcion,new Color(255,91,174),w,h,cx,cy);
 if(comparacion!=null){pintarCurva(g,comparacion,new Color(38,197,177),w,h,cx,cy);g.setFont(new Font("SansSerif",Font.BOLD,13));g.setColor(new Color(255,91,174));g.drawString("f: función original",12,20);g.setColor(new Color(38,197,177));g.drawString("f': derivada / pendiente",12,40);}
 if(tx!=null){g.setColor(new Color(57,217,194));g.setStroke(new BasicStroke(2f));double left=(0-cx)/escala,right=(w-cx)/escala;linea(g,0,cy-(ty+pendiente*(left-tx))*escala,w,cy-(ty+pendiente*(right-tx))*escala);double px=cx+tx*escala,py=cy-ty*escala;g.fill(new Ellipse2D.Double(px-5,py-5,10,10));}
 if(vx!=null){double ex=cx+vx*escala,ey=cy-vy*escala;g.setColor(new Color(66,217,203));g.setStroke(new BasicStroke(3.3f));linea(g,cx,cy,ex,ey);double a=Math.atan2(ey-cy,ex-cx);Path2D p=new Path2D.Double();p.moveTo(ex,ey);p.lineTo(ex-15*Math.cos(a-.48),ey-15*Math.sin(a-.48));p.lineTo(ex-15*Math.cos(a+.48),ey-15*Math.sin(a+.48));p.closePath();g.fill(p);g.drawString("v = ("+vx+", "+vy+")",(int)Math.max(10,Math.min(w-160,ex+10)),(int)Math.max(15,Math.min(h-10,ey-10)));}g.dispose();}
 private void pintarCurva(Graphics2D g,Nodo f,Color color,int w,int h,double cx,double cy){
  g.setColor(color);g.setStroke(new BasicStroke(2.8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
  Path2D path=new Path2D.Double();boolean prev=false;double last=0;
  for(int px=0;px<w;px++){double x=(px-cx)/escala;try{double py=cy-f.evaluar(x)*escala;
   if(Double.isFinite(py)&&Math.abs(py)<100000&&(!prev||Math.abs(py-last)<h*.8)){if(!prev)path.moveTo(px,py);else path.lineTo(px,py);prev=true;}else prev=false;last=py;
  }catch(RuntimeException ex){prev=false;}}
  g.draw(path);
 }
 public java.awt.image.BufferedImage capturar(){
  Dimension original=getSize();boolean temporal=getWidth()<50||getHeight()<50;
  if(temporal)setSize(1000,500);
  java.awt.image.BufferedImage imagen=new java.awt.image.BufferedImage(getWidth(),getHeight(),java.awt.image.BufferedImage.TYPE_INT_RGB);
  Graphics2D g=imagen.createGraphics();try{printAll(g);}finally{g.dispose();if(temporal)setSize(original);}return imagen;
 }

}
