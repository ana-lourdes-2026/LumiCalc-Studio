package com.calculadora.vista;
import com.calculadora.matematica.calculo.*;import com.calculadora.matematica.expresion.Nodo;import com.calculadora.datos.*;import com.calculadora.util.ExportadorPDF;
import javax.swing.*;import javax.swing.border.*;import java.awt.*;import java.awt.event.*;import java.io.*;import java.nio.charset.StandardCharsets;import java.nio.file.Files;import java.util.*;
public class VentanaPrincipal extends JFrame{
 private final Color bg=new Color(246,245,251),card=Color.WHITE,purple=new Color(111,67,198),pink=new Color(208,61,137),muted=new Color(112,102,128),white=new Color(48,38,64);
 private JTextField segunda=field("2x",20);private JPanel segundaFila;
 private JTextField formula=field("3*x^2 + 2*x + 1",24),li=field("0",6),ls=field("2",6),punto=field("1",5),vectorX=field("3",5),vectorY=field("4",5),vector2X=field("1",5),vector2Y=field("2",5);
 private JComboBox<String> variable=new JComboBox<>(new String[]{"x","y"});private JTextArea salida=new JTextArea();private JLabel respuesta=new JLabel("Elige una operación para comenzar"),estado=new JLabel("● Lista para calcular"),contador=new JLabel("0 operaciones");private GraficaPanel grafica=new GraficaPanel();private JCheckBox guardar=new JCheckBox("Guardar en MySQL");private HistorialDAO dao=new HistorialDAO();private final java.util.List<String> memoria=new ArrayList<>();private javax.swing.Timer animacion;private int pulso=0;
 private final HistorialLocal historialLocal=new HistorialLocal();
 private final JComboBox<String> operacionSeleccionada=new JComboBox<>(new String[]{"Derivada","Integral indefinida","Integral definida","Gráfica","Comparar","Evaluar","Tangente","Raíz","Tabla"});
 private final JTabbedPane pestanas=new JTabbedPane();private final JLabel interpretacion=new JLabel(" ");
 private FormulaMatematica formulaVista;private boolean modoClaro=true;private final java.util.List<JComponent> temaComponentes=new ArrayList<>();
 private static JTextField field(String t,int n){JTextField f=new JTextField(t,n);return f;}
 public VentanaPrincipal(){
  super("LumiCalc Studio · v8 · Comparar y raíces");setDefaultCloseOperation(EXIT_ON_CLOSE);setMinimumSize(new Dimension(540,660));
  Dimension pantalla=Toolkit.getDefaultToolkit().getScreenSize();setSize(Math.min(740,pantalla.width-60),Math.min(900,pantalla.height-65));setLocationRelativeTo(null);
  getContentPane().setBackground(bg);setLayout(new BorderLayout(0,12));setJMenuBar(crearMenuCompacto());
  JPanel cabecera=new TarjetaModerna(new BorderLayout(8,4),new Color(49,28,91),new Color(112,57,185),26);cabecera.setBorder(new EmptyBorder(19,22,19,22));cabecera.putClientProperty("colorFijo",true);
  JLabel marca=label("LumiCalc Studio",26,true);marca.setForeground(Color.WHITE);marca.putClientProperty("colorFijo",true);cabecera.add(marca,BorderLayout.WEST);JLabel subtitulo=label("TU ESPACIO PARA CALCULAR, EXPLORAR Y APRENDER",10,false);subtitulo.setForeground(new Color(222,205,255));subtitulo.putClientProperty("colorFijo",true);cabecera.add(subtitulo,BorderLayout.SOUTH);
  JButton practica=button("Practicar",()->new ModoPractica(this).setVisible(true),purple);JPanel accesos=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,0));accesos.setOpaque(false);accesos.add(button("✦ Tutor IA",()->accionMenu("Tutor IA"),pink));accesos.add(practica);cabecera.add(accesos,BorderLayout.EAST);JPanel envoltura=new JPanel(new BorderLayout());envoltura.setBackground(bg);envoltura.setBorder(new EmptyBorder(10,14,0,14));envoltura.add(cabecera);add(envoltura,BorderLayout.NORTH);
  JPanel centro=new ContenidoDesplazable(new BorderLayout(8,10));centro.setBackground(bg);centro.setBorder(new EmptyBorder(0,16,12,10));JScrollPane desplazamiento=new JScrollPane(centro,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);desplazamiento.setName("desplazamientoCalculadora");EstiloModerno.scroll(desplazamiento);desplazamiento.getViewport().setBackground(bg);desplazamiento.getVerticalScrollBar().setUnitIncrement(24);desplazamiento.getVerticalScrollBar().setToolTipText("Arrastra para ver el procedimiento completo");add(desplazamiento,BorderLayout.CENTER);
  JPanel superior=new JPanel();superior.setOpaque(false);superior.setLayout(new BoxLayout(superior,BoxLayout.Y_AXIS));
  JPanel datos=new TarjetaModerna(new BorderLayout(7,9),card);datos.setBorder(new EmptyBorder(14,16,12,16));
  JPanel filaOperacion=new JPanel(new BorderLayout(8,0));filaOperacion.setOpaque(false);filaOperacion.add(label("01  /  ELIGE TU OPERACIÓN",10,true),BorderLayout.WEST);styleCombo(operacionSeleccionada);filaOperacion.add(operacionSeleccionada,BorderLayout.CENTER);datos.add(filaOperacion,BorderLayout.NORTH);
  JPanel expresion=new JPanel(new BorderLayout(6,5));expresion.setOpaque(false);expresion.add(label("f =",17,true),BorderLayout.WEST);styleField(formula);formula.setFont(new Font("SansSerif",Font.BOLD,22));expresion.add(formula,BorderLayout.CENTER);interpretacion.setFont(new Font("SansSerif",Font.PLAIN,11));interpretacion.setForeground(muted);expresion.add(interpretacion,BorderLayout.SOUTH);JPanel funciones=new JPanel();funciones.setOpaque(false);funciones.setLayout(new BoxLayout(funciones,BoxLayout.Y_AXIS));expresion.setAlignmentX(Component.LEFT_ALIGNMENT);funciones.add(expresion);
  segundaFila=new JPanel(new BorderLayout(6,5));segundaFila.setOpaque(false);segundaFila.setAlignmentX(Component.LEFT_ALIGNMENT);segundaFila.add(label("g =",17,true),BorderLayout.WEST);styleField(segunda);segunda.setFont(new Font("SansSerif",Font.BOLD,20));segundaFila.add(segunda,BorderLayout.CENTER);funciones.add(segundaFila);datos.add(funciones,BorderLayout.CENTER);
  JPanel parametros=new JPanel(new FlowLayout(FlowLayout.LEFT,7,2));parametros.setOpaque(false);parametros.add(label("Variable",12,false));styleCombo(variable);parametros.add(variable);
  JLabel desde=label("Desde",12,false),hasta=label("Hasta",12,false),en=label("x₀",12,false);styleField(li);styleField(ls);styleField(punto);li.setColumns(4);ls.setColumns(4);punto.setColumns(4);
  parametros.add(desde);parametros.add(li);parametros.add(hasta);parametros.add(ls);parametros.add(en);parametros.add(punto);guardar.setOpaque(false);guardar.setForeground(white);guardar.setFont(new Font("SansSerif",Font.PLAIN,11));parametros.add(guardar);datos.add(parametros,BorderLayout.SOUTH);superior.add(datos);superior.add(Box.createVerticalStrut(8));
  Runnable campos=()->{String op=(String)operacionSeleccionada.getSelectedItem();boolean intervalo="Integral definida".equals(op)||"Tabla".equals(op),valor="Evaluar".equals(op)||"Tangente".equals(op);segundaFila.setVisible("Comparar".equals(op));desde.setVisible(intervalo);li.setVisible(intervalo);hasta.setVisible(intervalo);ls.setVisible(intervalo);en.setVisible(valor);punto.setVisible(valor);parametros.revalidate();};
  operacionSeleccionada.addActionListener(e->campos.run());campos.run();
  JPanel teclado=new JPanel(new GridLayout(4,5,7,7));teclado.setOpaque(false);String[][] teclas={{"sen","sin("},{"cos","cos("},{"tan","tan("},{"ln","ln("},{"log","log("},{"x²","^2"},{"xⁿ","^"},{"√","sqrt("},{"π","pi"},{"e","e"},{"(","("},{")",")"},{"÷","/"},{"×","*"},{"←","BORRAR"},{"variable","VARIABLE"},{".","."},{"+","+"},{"−","-"},{"1/2","(1/2)"}};
  for(String[] tecla:teclas){JButton boton=button(tecla[0],()->insertarTecla(tecla[1]),colorTecla(tecla[0]));boton.setForeground(white);boton.setFocusable(false);boton.setBorder(new EmptyBorder(9,5,9,5));teclado.add(boton);}JPanel tecladoCompleto=new JPanel(new BorderLayout(8,0));tecladoCompleto.setOpaque(false);tecladoCompleto.add(teclado,BorderLayout.CENTER);tecladoCompleto.add(new TecladoNumerico(this::insertarTecla),BorderLayout.EAST);superior.add(tecladoCompleto);superior.add(Box.createVerticalStrut(8));
  JPanel acciones=new JPanel(new GridLayout(1,2,8,0));acciones.setOpaque(false);JButton limpiar=button("Limpiar",this::limpiar,new Color(255,220,218));limpiar.setForeground(white);acciones.add(limpiar);acciones.add(button("Calcular",()->calcular((String)operacionSeleccionada.getSelectedItem()),purple));superior.add(acciones);superior.add(Box.createVerticalStrut(8));
  JPanel resultado=new TarjetaModerna(new BorderLayout(6,5),new Color(42,34,67),new Color(67,46,101),20);resultado.setBorder(new EmptyBorder(13,16,14,16));resultado.putClientProperty("colorFijo",true);JLabel etiquetaResultado=label("02  /  TU RESULTADO",10,true);etiquetaResultado.setForeground(new Color(206,186,242));etiquetaResultado.putClientProperty("colorFijo",true);resultado.add(etiquetaResultado,BorderLayout.NORTH);formulaVista=new FormulaMatematica("Tu resultado aparecerá aquí");formulaVista.setFont(new Font("Serif",Font.PLAIN,25));formulaVista.setForeground(new Color(129,239,208));resultado.add(formulaVista,BorderLayout.CENTER);superior.add(resultado);centro.add(superior,BorderLayout.NORTH);
  salida.setEditable(false);salida.setLineWrap(true);salida.setWrapStyleWord(true);salida.setFont(new Font("SansSerif",Font.PLAIN,14));salida.setBackground(card);salida.setForeground(white);salida.setBorder(new EmptyBorder(12,12,12,12));
  JPanel procedimiento=new TarjetaModerna(new BorderLayout(4,4),card);procedimiento.setBorder(new EmptyBorder(6,6,4,6));JScrollPane scrollPasos=new JScrollPane(salida,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);scrollPasos.setName("desplazamientoPasos");scrollPasos.getVerticalScrollBar().setUnitIncrement(22);EstiloModerno.scroll(scrollPasos);procedimiento.add(scrollPasos,BorderLayout.CENTER);JPanel exportaciones=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,4));exportaciones.setOpaque(false);exportaciones.add(button("Guardar TXT",this::exportar,new Color(157,133,180)));exportaciones.add(button("Guardar PDF",this::exportarPDF,purple));procedimiento.add(exportaciones,BorderLayout.SOUTH);pestanas.addTab("Procedimiento",procedimiento);
  JPanel plano=new JPanel(new BorderLayout());plano.setBackground(card);JPanel zoom=new JPanel(new FlowLayout(FlowLayout.RIGHT,5,2));zoom.setOpaque(false);zoom.add(label("Rueda: zoom · arrastra: mover",11,false));zoom.add(small("+",grafica::acercar));zoom.add(small("−",grafica::alejar));zoom.add(small("⌂",grafica::centrar));plano.add(zoom,BorderLayout.NORTH);grafica.tema(true);plano.add(grafica,BorderLayout.CENTER);pestanas.addTab("Gráfica",plano);
  JPanel vectores=new JPanel(new BorderLayout(8,12));vectores.setBackground(card);vectores.setBorder(new EmptyBorder(15,15,15,15));JPanel componentes=new JPanel(new GridLayout(2,2,8,12));componentes.setOpaque(false);componentes.add(label("Vector A = (x, y)",13,true));componentes.add(pair(vectorX,vectorY));componentes.add(label("Vector B = (x, y)",13,true));componentes.add(pair(vector2X,vector2Y));vectores.add(componentes,BorderLayout.NORTH);JPanel operacionesVector=new JPanel(new GridLayout(3,2,8,8));operacionesVector.setOpaque(false);String[][] nombres={{"Dibujar A","Dibujar"},{"Sumar A + B","Sumar"},{"Restar A − B","Restar"},{"Producto punto","Punto"},{"Ángulo entre A y B","Angulo"}};for(String[] n:nombres)operacionesVector.add(button(n[0],()->vector(n[1]),purple));vectores.add(operacionesVector,BorderLayout.CENTER);pestanas.addTab("Vectores",vectores);pestanas.setFont(new Font("SansSerif",Font.BOLD,12));EstiloModerno.pestanas(pestanas);pestanas.setPreferredSize(new Dimension(600,320));pestanas.setMinimumSize(new Dimension(300,260));centro.add(pestanas,BorderLayout.CENTER);
  JPanel pie=new JPanel(new BorderLayout(8,3));pie.setBackground(bg);pie.setBorder(new EmptyBorder(2,13,8,13));estado.setForeground(new Color(47,121,103));estado.setFont(new Font("SansSerif",Font.PLAIN,11));contador.setForeground(muted);contador.setFont(new Font("SansSerif",Font.PLAIN,10));pie.add(estado,BorderLayout.CENTER);pie.add(contador,BorderLayout.EAST);add(pie,BorderLayout.SOUTH);
  javax.swing.Timer previsualizar=new javax.swing.Timer(220,e->actualizarInterpretacion());previsualizar.setRepeats(false);formula.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){public void insertUpdate(javax.swing.event.DocumentEvent e){previsualizar.restart();}public void removeUpdate(javax.swing.event.DocumentEvent e){previsualizar.restart();}public void changedUpdate(javax.swing.event.DocumentEvent e){previsualizar.restart();}});variable.addActionListener(e->actualizarInterpretacion());formula.addActionListener(e->{try{calcular((String)operacionSeleccionada.getSelectedItem());}catch(Exception ex){error(ex);}});
  actualizarInterpretacion();iniciarAnimacion();salida.setText("Bienvenida a LumiCalc.\n\nElige una operación, escribe una expresión y pulsa Calcular.\n\n• Comparar: introduce f(x) y g(x) para ver sus intersecciones.\n• Practicar: ejercicios con pistas y explicaciones.\n• Datos: recupera tu historial o configura MySQL.\n• Aprender: abre el tutor, con ayuda local sin internet.\n\nUsa las pestañas para consultar el procedimiento, explorar la gráfica o trabajar con vectores.");
 }
 private JMenuBar crearMenuCompacto(){
  JMenuBar barra=new JMenuBar();
  JMenu archivos=new JMenu("Archivo");String[][] opciones={{"Abrir ejercicio TXT","Abrir"},{"Abrir lista CSV","Abrir CSV"},{"Guardar ejercicio TXT","Guardar"},{"Exportar solución TXT","Exportar"},{"Exportar solución PDF","PDF"},{"Exportar tabla CSV","Tabla CSV"}};for(String[] o:opciones){JMenuItem i=new JMenuItem(o[0]);i.addActionListener(e->accionMenu(o[1]));archivos.add(i);}barra.add(archivos);
  JMenu operaciones=new JMenu("Operaciones");for(int j=0;j<operacionSeleccionada.getItemCount();j++){String op=operacionSeleccionada.getItemAt(j);JMenuItem i=new JMenuItem(op);i.addActionListener(e->{operacionSeleccionada.setSelectedItem(op);formula.requestFocusInWindow();});operaciones.add(i);}barra.add(operaciones);
  JMenu aprender=new JMenu("Aprender");JMenuItem practica=new JMenuItem("Práctica con pistas"),tutor=new JMenuItem("Tutor IA y ayuda sin internet"),cientifica=new JMenuItem("Calculadora científica");practica.addActionListener(e->accionMenu("Practicar"));tutor.addActionListener(e->accionMenu("Tutor IA"));cientifica.addActionListener(e->accionMenu("Científica"));aprender.add(practica);aprender.add(tutor);aprender.add(cientifica);barra.add(aprender);
  JMenu datos=new JMenu("Datos");JMenuItem historial=new JMenuItem("Historial y recuperar ejercicio"),db=new JMenuItem("Configurar MySQL");historial.addActionListener(e->historial());db.addActionListener(e->configurarDB());datos.add(historial);datos.add(db);barra.add(datos);
  JMenu apariencia=new JMenu("Apariencia");JMenuItem tema=new JMenuItem("Cambiar claro / oscuro");tema.addActionListener(e->cambiarTema());apariencia.add(tema);barra.add(apariencia);return barra;
 }
 private void insertarTecla(String texto){
  if(texto.equals("BORRAR")){int inicio=formula.getSelectionStart(),fin=formula.getSelectionEnd();if(inicio!=fin)formula.replaceSelection("");else if(inicio>0){try{formula.getDocument().remove(inicio-1,1);}catch(javax.swing.text.BadLocationException ex){error(ex);}}}
  else formula.replaceSelection(texto.equals("VARIABLE")?String.valueOf(v()):texto);formula.requestFocusInWindow();
 }
 private void actualizarInterpretacion(){try{String texto=formula.getText().trim();if(texto.isEmpty()){interpretacion.setText("Escribe una expresión para comenzar.");interpretacion.setToolTipText(null);return;}String interpretada=Calculadora.parse(texto,v()).toString();interpretacion.setText("Se interpreta: "+(interpretada.length()>75?interpretada.substring(0,72)+"…":interpretada));interpretacion.setToolTipText(interpretada);}catch(RuntimeException ex){interpretacion.setText("Revisa la expresión: "+ex.getMessage());interpretacion.setToolTipText(ex.getMessage());}}

 private void accionMenu(String accion){try{
  switch(accion){case "PDF":exportarPDF();break;case "Practicar":new ModoPractica(this).setVisible(true);break;case "Historial":historial();break;case "Configurar DB":configurarDB();break;case "Científica":new CalculadoraCientifica(this, texto -> {formula.setText(texto);estado.setText("● Expresión recibida de la calculadora");}).setVisible(true);break;case "Tutor IA":new TutorIA(this).setVisible(true);break;case "Abrir":abrirEjercicio();break;case "Abrir CSV":abrirCSV();break;case "Guardar":guardarEjercicio();break;case "Exportar":exportar();break;case "Tabla CSV":exportarTablaCSV();break;
  case "Ejemplo polinomio":formula.setText("3*x^2 + 2*x + 1");calcular("Gráfica");break;
  case "Ejemplo trigonometría":formula.setText("sin(x) + cos(x)");calcular("Gráfica");break;
  case "Ejemplo exponencial":formula.setText("exp(x)");calcular("Gráfica");break;
  case "Ejemplo vector":vectorX.setText("3");vectorY.setText("4");vector2X.setText("1");vector2Y.setText("2");vector("Dibujar");break;
  case "Dibujar":case "Sumar":case "Restar":case "Punto":case "Angulo":vector(accion);break;
  default:calcular(accion);
  }
 }catch(Exception ex){error(ex);}}
 private File elegir(boolean guardarArchivo,String nombre){JFileChooser c=new JFileChooser();c.setSelectedFile(new File(nombre));int respuesta=guardarArchivo?c.showSaveDialog(this):c.showOpenDialog(this);return respuesta==JFileChooser.APPROVE_OPTION?c.getSelectedFile():null;}
 private void abrirEjercicio()throws IOException{File f=elegir(false,"ejercicio.txt");if(f==null)return;String contenido=new String(Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8);if(contenido.length()>100000)throw new IOException("Archivo demasiado grande (máximo 100 KB)");String[] lineas=contenido.split("\\R");String expr="";for(String linea:lineas){String t=linea.trim();if(t.startsWith("expresion="))expr=t.substring(10);else if(t.startsWith("variable="))variable.setSelectedItem(t.substring(9));else if(t.startsWith("desde="))li.setText(t.substring(6));else if(t.startsWith("hasta="))ls.setText(t.substring(6));else if(t.startsWith("punto="))punto.setText(t.substring(6));else if(!t.isEmpty()&&!t.startsWith("#")&&expr.isEmpty()&&!t.contains("="))expr=t;}
  if(expr.isEmpty())throw new IOException("No encontré una expresión. Usa expresion=3*x^2+2*x+1");formula.setText(expr);estado.setText("● Ejercicio cargado: "+f.getName());}
 private void guardarEjercicio()throws IOException{File f=elegir(true,"ejercicio_lumicalc.txt");if(f==null)return;String txt="# LumiCalc PRO - ejercicio\nexpresion="+formula.getText()+"\nvariable="+v()+"\ndesde="+li.getText()+"\nhasta="+ls.getText()+"\npunto="+punto.getText()+"\n";Files.write(f.toPath(),txt.getBytes(StandardCharsets.UTF_8));estado.setText("● Ejercicio guardado");}
 private void abrirCSV()throws IOException{File f=elegir(false,"ejercicios.csv");if(f==null)return;java.util.List<String> lines=Files.readAllLines(f.toPath(),StandardCharsets.UTF_8);if(lines.size()>1000)throw new IOException("Máximo 1000 ejercicios");StringBuilder listado=new StringBuilder("EJERCICIOS DISPONIBLES\n\n");java.util.List<String> expresiones=new ArrayList<>();for(String line:lines){String t=line.trim();if(t.isEmpty()||t.toLowerCase().startsWith("expresion"))continue;String expr=t.split("[,;]",2)[0].trim();if(!expr.isEmpty()){expresiones.add(expr);listado.append(expresiones.size()).append(". ").append(expr).append("\n");}}if(expresiones.isEmpty())throw new IOException("CSV sin expresiones. Primera columna: expresion");Object choice=JOptionPane.showInputDialog(this,"Selecciona un ejercicio","Importar CSV",JOptionPane.QUESTION_MESSAGE,null,expresiones.toArray(),expresiones.get(0));if(choice!=null){formula.setText(choice.toString());salida.setText(listado.toString());estado.setText("● Ejercicio CSV cargado");}}
 private void exportarTablaCSV()throws IOException{String expr=formula.getText().trim();Nodo f=Calculadora.parse(expr,v());double a=num(li),b=num(ls);if(a>b)throw new IllegalArgumentException("Desde debe ser menor o igual que Hasta");File archivo=elegir(true,"tabla_lumicalc.csv");if(archivo==null)return;StringBuilder sb=new StringBuilder("x,f(x)\n");for(int i=0;i<=100;i++){double x=a+(b-a)*i/100;double y=f.evaluar(x);sb.append(String.format(java.util.Locale.US,"%.8f,%s%n",x,Double.isFinite(y)?Double.toString(y):""));}Files.write(archivo.toPath(),sb.toString().getBytes(StandardCharsets.UTF_8));estado.setText("● Tabla CSV exportada");}
 private void iniciarAnimacion(){animacion=new javax.swing.Timer(650,e->{pulso++;String s=(pulso%3==0?"●":pulso%3==1?"◉":"◎");if(estado.getText().contains("Lista para calcular"))estado.setText(s+" Lista para calcular");});animacion.start();}
 private JPanel pair(JTextField a,JTextField b){JPanel p=new JPanel(new GridLayout(1,2,8,0));p.setOpaque(false);styleField(a);styleField(b);p.add(a);p.add(b);return p;}
 private JLabel label(String s,int size,boolean strong){JLabel l=new JLabel(s);l.setFont(new Font("SansSerif",strong?Font.BOLD:Font.PLAIN,size));l.setForeground(strong?white:muted);return l;}
 private void styleField(JTextField f){EstiloModerno.campo(f);}
 private void styleCombo(JComboBox<String> c){EstiloModerno.combo(c);}
 private Color colorTecla(String t){if(t.equals("←"))return new Color(255,210,216);if(t.equals("+")||t.equals("−")||t.equals("×")||t.equals("÷")||t.equals("1/2"))return new Color(255,233,214);if(t.equals("x²")||t.equals("xⁿ")||t.equals("√")||t.equals("π")||t.equals("e"))return new Color(218,246,236);return new Color(237,229,254);}
 private JButton small(String t,Runnable r){JButton b=button(t,r,new Color(63,77,112));b.setPreferredSize(new Dimension(38,30));return b;}
 private JButton button(String t,Runnable r,Color c){JButton b=new BotonColor(t,c);b.setBackground(c);b.setForeground(Color.WHITE);b.setOpaque(false);b.setFocusPainted(false);b.setBorder(new EmptyBorder(10,9,10,9));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setFont(new Font("SansSerif",Font.BOLD,12));b.addActionListener(e->{try{r.run();}catch(Exception ex){error(ex);}});return b;}
 private char v(){return variable.getSelectedItem().toString().charAt(0);}
 private double num(JTextField f){double x=Double.parseDouble(f.getText().trim());if(!Double.isFinite(x))throw new IllegalArgumentException("Introduce un número finito");return x;}
 private void mostrar(String tipo,String f,String pasos,String finalResult,Double a,Double b){
  String texto="OPERACIÓN: "+tipo+"\nFUNCIÓN: "+f+"\n\n"+pasos+"\n\nRESULTADO FINAL: "+finalResult;
  salida.setText(texto);salida.setCaretPosition(0);respuesta.setText("✓  "+finalResult);formulaVista.setToolTipText(finalResult);
  formulaVista.setText(finalResult);estado.setText("● Operación completada");
  memoria.add(tipo+" | "+f+" | "+finalResult);contador.setText(memoria.size()+(memoria.size()==1?" cálculo esta sesión":" cálculos esta sesión"));
  RegistroOperacion registro=new RegistroOperacion(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),tipo,f,v(),li.getText(),ls.getText(),punto.getText(),finalResult,pasos);
  try{historialLocal.guardar(registro);}catch(IOException ex){salida.append("\nNo se pudo guardar el historial local: "+ex.getMessage());}
  if(guardar.isSelected()){
   final char variableGuardada=v();
   new SwingWorker<Void,Void>(){
    protected Void doInBackground()throws Exception{dao.guardar(tipo,f,variableGuardada,a,b,finalResult,pasos);return null;}
    protected void done(){try{get();estado.setText("● Cálculo guardado en MySQL");}catch(Exception ex){estado.setText("● MySQL no disponible; consulta el historial local");}}
   }.execute();
  }
 }
 private void calcular(String tipo){operacionSeleccionada.setSelectedItem(tipo);actualizarInterpretacion();String entrada=formula.getText().trim();if(entrada.isEmpty())throw new IllegalArgumentException("Escribe una expresión");char v=v();Nodo f=Calculadora.parse(entrada,v);String r,p;Double a=null,b=null;
 switch(tipo){case "Derivada":String largo=Calculadora.derivar(f,v).toString();try{r=Polinomios.derivada(f,v);}catch(Exception e){r=largo;}p="FUNCIÓN ORIGINAL\n f("+v+") = "+entrada+"\n\n"+DerivadaPasoAPaso.generar(f,v)+"\n\nSIMPLIFICACIÓN FINAL\n f'("+v+") = "+r;grafica.funcion(f);r="f'("+v+") = "+r;break;
 case "Integral indefinida":String desarrollo;try{desarrollo=Calculadora.integralSimbolica(f,v);}catch(Exception ex){desarrollo="Aplicar linealidad y regla de potencias a cada término.";}try{r=Polinomios.integral(f,v);}catch(Exception e){if(desarrollo.startsWith("Aplicar"))throw new IllegalArgumentException("Integral simbólica no disponible para esta función. Prueba una integral definida numérica.");r=desarrollo+" + C";}p="REGLAS: linealidad de la integral, regla de la potencia (n distinto de -1) y constante de integración.\n\nPASO 1 · Separar términos y aplicar linealidad\n ∫("+entrada+")d"+v+"\n\nPASO 2 · Regla de potencia: ∫x^n dx = x^(n+1)/(n+1)\n "+desarrollo+" + C\n\nPASO 3 · Simplificar y añadir constante\n F("+v+") = "+r;grafica.funcion(f);r="F("+v+") = "+r;break;
 case "Integral definida":a=num(li);b=num(ls);
  try {
   com.calculadora.matematica.calculo.IntegralExacta.Solucion exacta=com.calculadora.matematica.calculo.IntegralExacta.resolver(f,v,a,b);
   r=exacta.resultado;p="FUNCIÓN ORIGINAL: "+entrada+"\n\n"+exacta.procedimiento;
  } catch(IllegalArgumentException ex) {
   double value=Calculadora.definida(f,a,b);
   r="≈ "+Calculadora.format(value);
   p="INTEGRAL DEFINIDA: "+entrada+"\nIntervalo: ["+a+", "+b+"]\n\n"
     +"Esta función no es un polinomio compatible con el cálculo exacto.\n"
     +"Se usa la regla compuesta de Simpson (4000 subdivisiones).\n"
     +"PASO 1 · Dividir el intervalo en 4000 partes.\n"
     +"PASO 2 · Aplicar la suma ponderada de Simpson.\n"
     +"PASO 3 · Resultado APROXIMADO: "+Calculadora.format(value)
     +"\nAdvertencia: no se obtuvo una antiderivada simbólica; se asume continuidad.";
  }
  grafica.area(f,a,b);break;
 case "Comparar":
  String textoG=segunda.getText().trim();if(textoG.isEmpty())throw new IllegalArgumentException("Escribe la segunda función g("+v+")");
  Nodo g=Calculadora.parse(textoG,v);grafica.comparar(f,g);
  String intersecciones=intersecciones(f,g,v);
  r="f("+v+") y g("+v+"): "+intersecciones;
  p="COMPARAR DOS FUNCIONES\n\nPASO 1 · f("+v+") = "+entrada+"\nPASO 2 · g("+v+") = "+textoG+"\nPASO 3 · Igualar f("+v+") = g("+v+")\nPASO 4 · "+intersecciones+"\n\nEn la pestaña Gráfica aparecen ambas funciones.";break;
 case "Gráfica":grafica.funcion(f);r="y = "+entrada;p="Se dibujó la función en el plano cartesiano.\nUsa la rueda del mouse para zoom, arrastra para mover.\nLas discontinuidades se omiten cuando no se pueden evaluar.";break;
 case "Evaluar":a=num(punto);double y=f.evaluar(a);if(!Double.isFinite(y))throw new IllegalArgumentException("La función no está definida en ese punto");r="f("+Polinomios.numero(a)+") = "+Calculadora.format(y);p="PASO 1 · Sustituir "+v+" = "+a+" en "+entrada+"\nPASO 2 · Evaluar la expresión\nPASO 3 · Resultado: "+Calculadora.format(y);grafica.funcion(f);break;
 case "Tangente":a=num(punto);double yy=f.evaluar(a),m=Calculadora.derivar(f,v).evaluar(a);if(!Double.isFinite(yy)||!Double.isFinite(m))throw new IllegalArgumentException("No hay tangente finita en ese punto");
  double ordenada=yy-m*a;
  String ms=Polinomios.numero(Math.abs(m)),bs=Polinomios.numero(Math.abs(ordenada));
  String termM=Math.abs(m)<1e-10?"":(Math.abs(Math.abs(m)-1)<1e-10?"":ms)+v;
  if(m<0)termM="-"+termM;
  if(Math.abs(ordenada)<1e-10)r="y = "+(termM.isEmpty()?"0":termM);
  else r="y = "+(termM.isEmpty()?(ordenada<0?"-":"")+bs:termM+(ordenada<0?" - ":" + ")+bs);
  p="RECTA TANGENTE\nPASO 1 · f("+Polinomios.numero(a)+") = "+Polinomios.numero(yy)+"\nPASO 2 · f'("+Polinomios.numero(a)+") = "+Polinomios.numero(m)+" (pendiente)\nPASO 3 · y - "+Polinomios.numero(yy)+" = "+Polinomios.numero(m)+"("+v+" - "+Polinomios.numero(a)+")\nPASO 4 · Distribuir y simplificar: "+r;
  grafica.tangente(f,a,yy,m);break;
 case "Raíz":
  String roots=raices(f,v);r=roots;p="RAÍCES DE UNA FUNCIÓN\nPASO 1 · Igualar la función a cero: "+entrada+" = 0\nPASO 2 · Buscar las soluciones reales de la ecuación.\nPASO 3 · "+roots+"\n\nNo es necesario elegir un valor inicial x₀.";grafica.funcion(f);break;
 case "Tabla":a=num(li);b=num(ls);if(a>b)throw new IllegalArgumentException("El límite inferior debe ser menor o igual al superior");StringBuilder sb=new StringBuilder("TABLA DE VALORES (11 muestras)\n\n      "+v+"                 f("+v+")\n-----------------------------------\n");for(int i=0;i<=10;i++){double xx=a+(b-a)*i/10,yy0;try{yy0=f.evaluar(xx);}catch(Exception e){yy0=Double.NaN;}sb.append(String.format(java.util.Locale.US,"%12.5f       %s%n",xx,Double.isFinite(yy0)?Calculadora.format(yy0):"no definida"));}r="Tabla generada";p=sb.toString();grafica.funcion(f);break;
 default:throw new IllegalArgumentException("Operación desconocida");}
 mostrar(tipo,entrada,p,r,a,b);pestanas.setSelectedIndex(tipo.equals("Gráfica")||tipo.equals("Comparar")||tipo.equals("Tangente")?1:0);
 }
 private static String intersecciones(Nodo f,Nodo g,char v){
  try{java.util.SortedMap<Integer,Double> p=new java.util.TreeMap<>(Polinomios.extraer(f));for(java.util.Map.Entry<Integer,Double> e:Polinomios.extraer(g).entrySet())p.merge(e.getKey(),-e.getValue(),Double::sum);return raicesPolinomio(p,v,"Intersecciones");}
  catch(IllegalArgumentException ex){return "No se pudieron obtener intersecciones algebraicas exactas para estas funciones";}
 }
 private static String raices(Nodo f,char v){
  try{return raicesPolinomio(Polinomios.extraer(f),v,"Raíces");}
  catch(IllegalArgumentException ex){return "No se dispone de una solución algebraica automática para esta función; prueba con un polinomio de grado 1 o 2";}
 }
 private static String raicesPolinomio(java.util.SortedMap<Integer,Double> p,char v,String etiqueta){
  int grado=0;for(java.util.Map.Entry<Integer,Double> e:p.entrySet())if(Math.abs(e.getValue())>1e-10)grado=Math.max(grado,e.getKey());
  double c=p.getOrDefault(0,0.0),b=p.getOrDefault(1,0.0),a=p.getOrDefault(2,0.0);
  if(grado==0)return Math.abs(c)<1e-10?"Todos los números reales son solución":"No existen raíces reales";
  if(grado==1)return etiqueta+": "+v+" = "+Polinomios.numero(-c/b);
  if(grado==2){double d=b*b-4*a*c;if(d< -1e-10)return "No existen raíces reales (discriminante negativo)";
   double q=Math.sqrt(Math.max(0,d)),r1=(-b-q)/(2*a),r2=(-b+q)/(2*a);
   if(Math.abs(r1-r2)<1e-9)return etiqueta+": "+v+" = "+Polinomios.numero(r1)+" (raíz doble)";
   return etiqueta+": "+v+"₁ = "+Polinomios.numero(r1)+", "+v+"₂ = "+Polinomios.numero(r2);
  }
  return "Solución algebraica automática disponible para grados 1 y 2; este polinomio es de grado "+grado;
 }
 private void vector(String modo){pestanas.setSelectedIndex(1);double ax=num(vectorX),ay=num(vectorY),bx=num(vector2X),by=num(vector2Y),rx=ax,ry=ay;String r,p="A = ("+ax+", "+ay+")\nB = ("+bx+", "+by+")\n\n";switch(modo){case "Sumar":rx=ax+bx;ry=ay+by;p+="A+B = (Ax+Bx, Ay+By)\n";break;case "Restar":rx=ax-bx;ry=ay-by;p+="A-B = (Ax-Bx, Ay-By)\n";break;case "Punto":double dot=ax*bx+ay*by;r="A · B = "+Calculadora.format(dot);p+="A · B = Ax·Bx + Ay·By\n= "+ax+"·"+bx+" + "+ay+"·"+by;grafica.vector(ax,ay);mostrar("Producto punto", "A, B",p,r,null,null);return;case "Angulo":double len=Math.hypot(ax,ay)*Math.hypot(bx,by);if(len==0)throw new IllegalArgumentException("El ángulo no existe para un vector nulo");double theta=Math.toDegrees(Math.acos(Math.max(-1,Math.min(1,(ax*bx+ay*by)/len))));r="Ángulo(A,B) = "+Calculadora.format(theta)+"°";p+="cos θ = (A · B)/(|A| |B|)\nθ = arccos((A · B)/(|A| |B|))";grafica.vector(ax,ay);mostrar("Ángulo", "A, B",p,r,null,null);return;default:p+="Vector dibujado desde el origen.\n";}
 r="v = ("+Calculadora.format(rx)+", "+Calculadora.format(ry)+")";p+="Vector resultante: "+r+"\nMagnitud |v| = √(vx² + vy²) = "+Calculadora.format(Math.hypot(rx,ry))+"\nDirección = atan2(vy,vx) = "+Calculadora.format(Math.toDegrees(Math.atan2(ry,rx)))+"°";grafica.vector(rx,ry);mostrar("Vector: "+modo,"A, B",p,r,null,null);}
 private void historial(){new VentanaHistorial(this,historialLocal,dao,this::recuperar).setVisible(true);}
 private void recuperar(RegistroOperacion r){
  formula.setText(r.funcion());variable.setSelectedItem(String.valueOf(r.variable()));for(int i=0;i<operacionSeleccionada.getItemCount();i++)if(operacionSeleccionada.getItemAt(i).equals(r.tipo()))operacionSeleccionada.setSelectedIndex(i);
  if(!r.desde().isBlank())li.setText(r.desde());if(!r.hasta().isBlank())ls.setText(r.hasta());if(!r.punto().isBlank())punto.setText(r.punto());
  salida.setText(r.detalle());salida.setCaretPosition(0);respuesta.setText("Recuperado: "+r.resultado());formulaVista.setText(r.resultado());
  try{Nodo f=Calculadora.parse(r.funcion(),r.variable());if(r.tipo().equals("Comparar"))grafica.funcion(f);else if(r.tipo().equals("Integral definida"))grafica.area(f,Double.parseDouble(r.desde()),Double.parseDouble(r.hasta()));else grafica.funcion(f);}catch(RuntimeException ex){grafica.limpiar();}
  pestanas.setSelectedIndex(0);estado.setText("● Ejercicio recuperado. Puedes calcularlo de nuevo o exportar su solución.");
 }
 private void configurarDB(){
  JTextField url=new JTextField(dao.getUrl(),45),usuario=new JTextField(dao.getUsuario());JPasswordField clave=new JPasswordField();
  JPanel p=new JPanel(new GridLayout(0,1,5,5));p.add(new JLabel("Importa primero calculadora_calculo.sql en tu servidor MySQL/XAMPP."));p.add(new JLabel("URL JDBC"));p.add(url);p.add(new JLabel("Usuario"));p.add(usuario);p.add(new JLabel("Contraseña (solo se usa durante esta sesión)"));p.add(clave);
  if(JOptionPane.showConfirmDialog(this,p,"Configurar y probar MySQL",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
  dao.configurar(url.getText().trim(),usuario.getText().trim(),new String(clave.getPassword()));estado.setText("● Probando conexión MySQL...");
  new SwingWorker<Void,Void>(){protected Void doInBackground()throws Exception{dao.probar();return null;}protected void done(){try{get();guardar.setSelected(true);estado.setText("● MySQL conectado. Los siguientes cálculos también se guardarán allí.");}catch(Exception ex){guardar.setSelected(false);estado.setText("● No se pudo conectar a MySQL");Throwable causa=ex.getCause()==null?ex:ex.getCause();JOptionPane.showMessageDialog(VentanaPrincipal.this,"Revisa el servidor, la base de datos y los datos de acceso.\n"+causa.getMessage(),"Conexión MySQL",JOptionPane.WARNING_MESSAGE);}}}.execute();
 }
 private void exportarPDF(){
  if(salida.getText().isBlank()){estado.setText("● Primero realiza un cálculo.");return;}
  JFileChooser c=new JFileChooser();c.setSelectedFile(new File("LumiCalc_Solucion.pdf"));c.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Documento PDF","pdf"));
  if(c.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
  File elegido=c.getSelectedFile();if(!elegido.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".pdf"))elegido=new File(elegido.getAbsolutePath()+".pdf");
  if(elegido.exists()&&JOptionPane.showConfirmDialog(this,"El archivo ya existe. ¿Reemplazarlo?","Guardar PDF",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
  final File destino=elegido;final String texto=salida.getText();final java.awt.image.BufferedImage imagen=grafica.capturar();estado.setText("● Generando PDF...");
  new SwingWorker<Void,Void>(){protected Void doInBackground()throws Exception{ExportadorPDF.guardar(destino.toPath(),texto,imagen);return null;}protected void done(){try{get();estado.setText("● PDF guardado: "+destino.getName());}catch(Exception ex){error(ex);}}}.execute();
 }
 private void limpiar(){formula.setText("");salida.setText("");respuesta.setText("Lista para una nueva operación");grafica.limpiar();}
 private void exportar(){JFileChooser c=new JFileChooser();c.setSelectedFile(new File("LumiCalc_Resultado.txt"));if(c.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){try{Files.write(c.getSelectedFile().toPath(),(salida.getText()+"\n\n"+respuesta.getText()).getBytes(StandardCharsets.UTF_8));estado.setText("● Archivo exportado correctamente");}catch(IOException e){error(e);}}}
 private String formatoMatematico(String texto){
  String t=texto.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
  t=t.replace("*", " · ").replace("≈"," ≈ ");
  t=t.replaceAll("\\^\\((-?[0-9]+)\\)","<sup>$1</sup>");
  t=t.replaceAll("\\^(-?[0-9]+)","<sup>$1</sup>");
  return t;
 }
 private void cambiarTema(){
  modoClaro=!modoClaro;
  Color fondo=modoClaro?bg:new Color(20,22,38);
  Color panel=modoClaro?Color.WHITE:new Color(29,33,50);
  Color tinta=modoClaro?white:new Color(241,245,255);
  Color suave=modoClaro?new Color(81,94,126):muted;
  aplicarTema(getContentPane(),fondo,panel,tinta,suave);
  grafica.tema(modoClaro);
  salida.setBackground(modoClaro?new Color(246,248,253):new Color(18,26,47));
  salida.setForeground(tinta);
  respuesta.setForeground(modoClaro?new Color(12,119,103):new Color(77,234,199));
  formulaVista.setForeground(new Color(129,239,208));
  repaint();
 }
 private void aplicarTema(Container root,Color fondo,Color panel,Color tinta,Color suave){
  for(Component c:root.getComponents()){
   if(c==grafica)continue;
   if(c instanceof JTextField){c.setBackground(modoClaro?new Color(248,249,255):new Color(34,44,75));c.setForeground(tinta);}
   else if(c==salida){c.setBackground(modoClaro?new Color(246,248,253):new Color(18,26,47));c.setForeground(tinta);}
   else if(c instanceof JButton){/* Los botones mantienen sus colores de acción */}
   else if(c instanceof JLabel){JLabel l=(JLabel)c;if(l!=respuesta&&!Boolean.TRUE.equals(l.getClientProperty("colorFijo")))l.setForeground(tinta);}
   else if(c instanceof TarjetaModerna&&!Boolean.TRUE.equals(((JComponent)c).getClientProperty("colorFijo"))){c.setBackground(panel);}
   else if(c instanceof JPanel&&c.isOpaque()){Color old=c.getBackground();c.setBackground(panel);}
   if(c instanceof Container)aplicarTema((Container)c,fondo,panel,tinta,suave);
  }
  getContentPane().setBackground(fondo);
 }
 private void error(Exception e){estado.setText("● Revisa la expresión o los datos");JOptionPane.showMessageDialog(this,e.getMessage()==null?e.toString():e.getMessage(),"LumiCalc · Verificar datos",JOptionPane.WARNING_MESSAGE);}
}
