package com.calculadora.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;

/** Tutor optativo: no envía datos sin que el usuario pulse Analizar. */
public class TutorIA extends JDialog {
 private final JTextArea entrada=new JTextArea();
 private final JEditorPane respuesta=new JEditorPane("text/html", "");
 private String respuestaTexto="";
 private final JLabel estado=new JLabel("Selecciona un parcial o escribe tu pregunta.");
 private final JButton resolver=new BotonColor("✦ Analizar y explicar con IA",new Color(200,65,132));
 private File archivo;private String claveSesion=System.getenv("OPENAI_API_KEY");
 private final JTextField expresionLocal=new JTextField("sin(2*x)",20);
 private final JComboBox<String> operacionLocal=new JComboBox<>(new String[]{"Derivada","Integral básica"}),variableLocal=new JComboBox<>(new String[]{"x","y"});
 public TutorIA(JFrame padre){super(padre,"LumiCalc Studio · Tutor IA",false);
  Dimension pantalla=Toolkit.getDefaultToolkit().getScreenSize();setSize(Math.min(1100,pantalla.width-60),Math.min(790,pantalla.height-65));setDefaultCloseOperation(DISPOSE_ON_CLOSE);setLocationRelativeTo(padre);setLayout(new BorderLayout(12,12));
  Color fondo=new Color(246,245,251),panel=new Color(239,231,248),blanco=new Color(48,38,64);
  getContentPane().setBackground(fondo);
  JPanel cab=new TarjetaModerna(new BorderLayout(),new Color(49,28,91),new Color(112,57,185),24);cab.setBorder(new EmptyBorder(18,20,18,20));
  JLabel titulo=new JLabel("✦ Tutor IA · Aprende paso a paso");titulo.setFont(new Font("SansSerif",Font.BOLD,20));titulo.setForeground(Color.WHITE);cab.add(titulo,BorderLayout.CENTER);JButton volver=new BotonColor("← Volver a la calculadora",new Color(116,70,181));volver.addActionListener(e->{dispose();padre.toFront();padre.requestFocus();});cab.add(volver,BorderLayout.EAST);add(cab,BorderLayout.NORTH);
  JPanel centro=new JPanel(new GridLayout(1,2,12,0));centro.setBackground(fondo);centro.setBorder(new EmptyBorder(12,14,4,14));
  centro.add(columna("ENUNCIADO / DOCUMENTO",entrada,panel,blanco));centro.add(columna("SOLUCIÓN Y EXPLICACIÓN",respuesta,new Color(224,243,238),blanco));
  entrada.setText("Escribe aquí una pregunta o pulsa Cargar parcial.\n\nEjemplo: Deriva 3x²+2x+1 y explica las reglas.");respuesta.setEditable(false);mostrarRespuesta("Tu explicación aparecerá aquí.\n\nCarga un ejercicio y pulsa Analizar, o prueba la ayuda sin internet.");
  centro.setPreferredSize(new Dimension(900,500));add(centro,BorderLayout.CENTER);
  JPanel abajo=new JPanel(new BorderLayout(8,8));abajo.setBackground(fondo);abajo.setBorder(new EmptyBorder(8,14,14,14));
  JPanel botones=new JPanel(new FlowLayout(FlowLayout.LEFT,9,0));botones.setOpaque(false);
  JButton abrir=new BotonColor("Cargar documento / imagen",new Color(52,112,166));abrir.addActionListener(e->cargar());botones.add(abrir);
  resolver.addActionListener(e->analizar());botones.add(resolver);
  JButton configurar=new BotonColor("Configurar clave IA",new Color(116,70,181));configurar.addActionListener(e->configurarClave());botones.add(configurar);
  JButton exportar=new BotonColor("Exportar TXT",new Color(35,128,111));exportar.addActionListener(e->exportar());botones.add(exportar);
  abajo.add(botones,BorderLayout.NORTH);
  JPanel local=new JPanel(new FlowLayout(FlowLayout.LEFT,6,4));local.setOpaque(false);JLabel tituloLocal=new JLabel("Sin internet · Expresión:");tituloLocal.setForeground(blanco);local.add(tituloLocal);local.add(expresionLocal);local.add(variableLocal);local.add(operacionLocal);JButton resolverLocal=new BotonColor("Explicar sin internet",new Color(35,128,111));resolverLocal.addActionListener(e->resolverLocal());local.add(resolverLocal);abajo.add(local,BorderLayout.CENTER);
  EstiloModerno.campo(expresionLocal);EstiloModerno.combo(variableLocal);EstiloModerno.combo(operacionLocal);
  estado.setForeground(new Color(47,121,103));abajo.add(estado,BorderLayout.SOUTH);add(abajo,BorderLayout.SOUTH);
 }
 private JPanel columna(String titulo,javax.swing.text.JTextComponent area,Color fondo,Color texto){JPanel p=new TarjetaModerna(new BorderLayout(0,12),Color.WHITE);p.setBorder(new EmptyBorder(14,14,14,14));JLabel l=new JLabel(titulo);l.setForeground(texto);l.setFont(new Font("SansSerif",Font.BOLD,13));p.add(l,BorderLayout.NORTH);if(area instanceof JTextArea a){a.setLineWrap(true);a.setWrapStyleWord(true);}area.setFont(new Font("SansSerif",Font.PLAIN,14));area.setForeground(texto);area.setCaretColor(texto);area.setBackground(Color.WHITE);area.setBorder(new EmptyBorder(12,12,12,12));JScrollPane scroll=new JScrollPane(area);EstiloModerno.scroll(scroll);p.add(scroll,BorderLayout.CENTER);return p;}
 private void mostrarRespuesta(String texto){respuestaTexto=FormatoRespuestaTutor.texto(texto);respuesta.setText(FormatoRespuestaTutor.html(texto));respuesta.setCaretPosition(0);}
 private void cargar(){JFileChooser c=new JFileChooser(System.getProperty("user.home")+File.separator+"Downloads");c.setFileFilter(new FileNameExtensionFilter("Parciales y ejercicios", "txt","csv","docx","pdf","png","jpg","jpeg"));if(c.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)return;archivo=c.getSelectedFile();try{
  if(archivo.length()>12_000_000)throw new IOException("Archivo demasiado grande (máximo 12 MB).");
  String ext=extension(archivo);
  if(ext.equals("txt")||ext.equals("csv"))entrada.setText(Files.readString(archivo.toPath(),StandardCharsets.UTF_8));
  else if(ext.equals("docx"))entrada.setText(leerDocx(archivo));
  else if(ext.equals("pdf"))entrada.setText(leerPdf(archivo));
  else entrada.setText("Imagen seleccionada: "+archivo.getName()+"\nSe enviará a la IA al pulsar Analizar.");
  estado.setText("✓ Archivo listo: "+archivo.getName()+". No se envía hasta pulsar Analizar.");
 }catch(Exception ex){archivo=null;JOptionPane.showMessageDialog(this,ex.getMessage(),"No se pudo abrir",JOptionPane.WARNING_MESSAGE);}}
 private String extension(File f){String n=f.getName().toLowerCase(Locale.ROOT);return n.contains(".")?n.substring(n.lastIndexOf('.')+1):"";}
 private String leerDocx(File f)throws Exception{StringBuilder sb=new StringBuilder();try(ZipFile zip=new ZipFile(f)){ZipEntry e=zip.getEntry("word/document.xml");if(e==null)throw new IOException("DOCX sin contenido legible");String xml=new String(zip.getInputStream(e).readAllBytes(),StandardCharsets.UTF_8);xml=xml.replaceAll("</w:p>","\n").replaceAll("<w:tab[^>]*/>","\t").replaceAll("<[^>]*>","");sb.append(xml.replace("&amp;","&").replace("&lt;","<").replace("&gt;",">").replace("&quot;","\""));}return sb.toString();}
 private String leerPdf(File f)throws Exception{
  Process p;try{p=new ProcessBuilder("pdftotext","-layout",f.getAbsolutePath(),"-").redirectErrorStream(true).start();}catch(IOException e){throw new IOException("Para PDF instala Poppler/pdftotext y agrégalo al PATH. También puedes copiar el texto a esta ventana.");}
  String s=new String(p.getInputStream().readAllBytes(),StandardCharsets.UTF_8);if(p.waitFor()!=0||s.isBlank())throw new IOException("No se pudo extraer texto. Si es PDF escaneado, conviértelo en imagen JPG/PNG para usar la IA visual.");return s;
 }
 private void analizar(){String key=claveSesion;if(key==null||key.isBlank()){JOptionPane.showMessageDialog(this,"Pulsa Configurar clave IA o configura OPENAI_API_KEY.\nPara expresiones básicas también puedes usar Explicar sin internet.","API no configurada",JOptionPane.INFORMATION_MESSAGE);return;}
  String pregunta=entrada.getText().trim();if(pregunta.isEmpty()){estado.setText("Escribe una pregunta o carga un archivo.");return;}
  resolver.setEnabled(false);estado.setText("Analizando con IA... Requiere internet; puede tardar.");mostrarRespuesta("Procesando...");
  new SwingWorker<String,Void>(){protected String doInBackground()throws Exception{return consultar(key,pregunta);}protected void done(){resolver.setEnabled(true);try{mostrarRespuesta(get());respuesta.setCaretPosition(0);estado.setText("✓ Análisis terminado. Verifica resultados antes de entregarlos.");}catch(Exception e){Throwable causa=e.getCause()==null?e:e.getCause();mostrarRespuesta("No se pudo completar el análisis.\n"+causa);estado.setText("Error de conexión o de API.");}}}.execute();
 }
 private void configurarClave(){
  JPasswordField campo=new JPasswordField(34);JPanel panel=new JPanel(new BorderLayout(5,8));panel.add(new JLabel("La clave se usa solo durante esta sesión; no se guarda en el proyecto."),BorderLayout.NORTH);panel.add(campo,BorderLayout.CENTER);
  if(JOptionPane.showConfirmDialog(this,panel,"Clave API personal",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){char[] valor=campo.getPassword();claveSesion=new String(valor).trim();java.util.Arrays.fill(valor,'\0');estado.setText(claveSesion.isBlank()?"Sin clave: usa las expresiones sin internet.":"Clave cargada para esta sesión. La consulta requiere internet y acceso a la API.");}
 }
 private void resolverLocal(){
  try{String texto=com.calculadora.matematica.calculo.TutorLocal.resolver(expresionLocal.getText().trim(),variableLocal.getSelectedItem().toString().charAt(0),operacionLocal.getSelectedIndex()==1);mostrarRespuesta(texto);respuesta.setCaretPosition(0);estado.setText("✓ Explicación calculada sin internet. El modo local trabaja con expresiones matemáticas.");}
  catch(RuntimeException ex){mostrarRespuesta(ex.getMessage());estado.setText("Revisa la expresión o usa una operación compatible con el motor local.");}
 }
 private String consultar(String key,String pregunta)throws Exception{
  String instruccion="Eres LumiCalc Tutor, un profesor paciente de Cálculo II. Resuelve únicamente lo que puedas verificar. Enumera ejercicios, explica reglas, desarrollo y resultado final simplificado. Si falta información, dilo. Si se trata de evaluación en curso, ayuda a aprender con orientación y pasos. Responde en español. No inventes datos. Usa texto sencillo, sin Markdown ni comandos LaTeX. Escribe encabezados como Ejercicio 1, Regla, Paso 1 y Resultado final. Usa símbolos legibles: f′(x), x², ×, √ y fracciones (numerador)/(denominador); usa paréntesis para agrupar potencias. No escribas barras invertidas ni delimitadores de fórmulas.";
  String contenido="{\"type\":\"text\",\"text\":\""+esc(pregunta.length()>50000?pregunta.substring(0,50000):pregunta)+"\"}";
  if(archivo!=null && Arrays.asList("png","jpg","jpeg").contains(extension(archivo))){String mime=extension(archivo).equals("png")?"image/png":"image/jpeg";String b64=Base64.getEncoder().encodeToString(Files.readAllBytes(archivo.toPath()));contenido+=",{\"type\":\"image_url\",\"image_url\":{\"url\":\"data:"+mime+";base64,"+b64+"\"}}";}
  String json="{\"model\":\"gpt-4.1-mini\",\"max_tokens\":3000,\"messages\":[{\"role\":\"system\",\"content\":\""+esc(instruccion)+"\"},{\"role\":\"user\",\"content\":["+contenido+"]}]}";
  HttpRequest req=HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions")).header("Authorization","Bearer "+key).header("Content-Type","application/json").timeout(java.time.Duration.ofSeconds(100)).POST(HttpRequest.BodyPublishers.ofString(json,StandardCharsets.UTF_8)).build();
  HttpResponse<String> resp=HttpClient.newHttpClient().send(req,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
  if(resp.statusCode()!=200)throw new IOException("API respondió HTTP "+resp.statusCode()+". Revisa clave, saldo y conexión.");
  // Evita expresiones regulares sobre respuestas JSON extensas: pueden provocar StackOverflowError.
  String texto=extraerContenido(resp.body());
  if(texto==null)throw new IOException("Respuesta inesperada de API; no se encontró el contenido del asistente.");
  return texto;
 }
 /** Extrae una cadena JSON sin regex ni recursión, incluso con respuestas grandes. */
 private static String extraerContenido(String json)throws IOException {
  int i=0;
  while(i<json.length()){
   if(json.charAt(i)!='"'){i++;continue;}
   CadenaJson clave=leerCadenaJson(json,i);
   i=clave.fin;
   int j=i;
   while(j<json.length()&&Character.isWhitespace(json.charAt(j)))j++;
   if(j>=json.length()||json.charAt(j)!=':')continue;
   j++;
   while(j<json.length()&&Character.isWhitespace(json.charAt(j)))j++;
   if("content".equals(clave.valor)&&j<json.length()&&json.charAt(j)=='"'){
    return leerCadenaJson(json,j).valor;
   }
   i=j;
  }
  return null;
 }
 private static final class CadenaJson {
  final String valor;final int fin;
  CadenaJson(String valor,int fin){this.valor=valor;this.fin=fin;}
 }
 private static CadenaJson leerCadenaJson(String s,int inicio)throws IOException {
  StringBuilder out=new StringBuilder();
  for(int i=inicio+1;i<s.length();i++){
   char c=s.charAt(i);
   if(c=='"')return new CadenaJson(out.toString(),i+1);
   if(c!='\\'){out.append(c);continue;}
   if(++i>=s.length())throw new IOException("JSON incompleto");
   char e=s.charAt(i);
   switch(e){
    case '"':out.append('"');break;
    case '\\':out.append('\\');break;
    case '/':out.append('/');break;
    case 'n':out.append('\n');break;
    case 'r':out.append('\r');break;
    case 't':out.append('\t');break;
    case 'b':out.append('\b');break;
    case 'f':out.append('\f');break;
    case 'u':
     if(i+4>=s.length())throw new IOException("Escape Unicode incompleto");
     try{out.append((char)Integer.parseInt(s.substring(i+1,i+5),16));}
     catch(NumberFormatException ex){throw new IOException("Escape Unicode inválido",ex);}
     i+=4;break;
    default:throw new IOException("Escape JSON inválido");
   }
  }
  throw new IOException("Cadena JSON sin cerrar");
 }
 private static String esc(String s){return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t");}
 private static String desesc(String s){StringBuilder b=new StringBuilder();for(int i=0;i<s.length();i++){char c=s.charAt(i);if(c=='\\'&&i+1<s.length()){char n=s.charAt(++i);switch(n){case 'n':b.append('\n');break;case 'r':b.append('\r');break;case 't':b.append('\t');break;case '"':b.append('"');break;case '\\':b.append('\\');break;case 'u':if(i+4<s.length()){try{b.append((char)Integer.parseInt(s.substring(i+1,i+5),16));i+=4;}catch(Exception ex){b.append('?');}}break;default:b.append(n);}}else b.append(c);}return b.toString();}
 private void exportar(){JFileChooser c=new JFileChooser();c.setSelectedFile(new File("LumiCalc_Tutor_Solucion.txt"));if(c.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)try{Files.writeString(c.getSelectedFile().toPath(),"ENUNCIADO\n"+entrada.getText()+"\n\nSOLUCIÓN\n"+respuestaTexto,StandardCharsets.UTF_8);estado.setText("✓ Solución guardada.");}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}}
}
