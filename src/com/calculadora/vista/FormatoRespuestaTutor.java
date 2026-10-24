package com.calculadora.vista;
import java.util.Map;
/** Traduce notación frecuente a texto legible; no ejecuta HTML de la IA. */
public final class FormatoRespuestaTutor {
 private FormatoRespuestaTutor(){}
 public static String texto(String origen){
  String s=origen==null?"":origen.replace("\r\n","\n");
  for(String d:new String[]{"\\(","\\)","\\[","\\]","$$","$"})s=s.replace(d,"");
  s=s.replace("\\left","").replace("\\right","");
  s=grupos(s,0);
  Map<String,String> simbolos=Map.ofEntries(Map.entry("cdot","·"),Map.entry("times","×"),Map.entry("div","÷"),Map.entry("sin","sen"),Map.entry("cos","cos"),Map.entry("tan","tan"),Map.entry("ln","ln"),Map.entry("log","log"),Map.entry("pi","π"),Map.entry("theta","θ"),Map.entry("infty","∞"),Map.entry("int","∫"),Map.entry("approx","≈"),Map.entry("neq","≠"),Map.entry("leq","≤"),Map.entry("geq","≥"),Map.entry("pm","±"));
  java.util.regex.Matcher m=java.util.regex.Pattern.compile("\\\\([a-zA-Z]+)").matcher(s);StringBuffer b=new StringBuffer();
  while(m.find())m.appendReplacement(b,java.util.regex.Matcher.quoteReplacement(simbolos.getOrDefault(m.group(1),m.group(1))));m.appendTail(b);s=b.toString();
  s=s.replace("\\,"," ").replace("\\;"," ").replace("\\!","").replace("\\ "," ").replace("\\\\","\n");
  s=s.replaceAll("(?m)^\\s*#{1,6}\\s+","").replace("**","").replace("`","").replaceAll("(?m)^\\s*[-*_]{3,}\\s*$","");
  s=s.replaceAll("\\^\\{([^{}]+)\\}","^($1)").replaceAll("_\\{([^{}]+)\\}","_($1)");
  s=s.replaceAll("\\n[ \\t]*\\n(?:[ \\t]*\\n)+","\n\n");return s.trim();
 }
 private static String grupos(String s,int profundidad){
  if(profundidad>32)return s;StringBuilder b=new StringBuilder();
  for(int i=0;i<s.length();){
   String comando=null;for(String c:new String[]{"dfrac","tfrac","frac","sqrt","text","mathrm","operatorname","mathbf"})if(s.startsWith("\\"+c,i)){comando=c;break;}
   if(comando==null){b.append(s.charAt(i++));continue;}
   int inicio=i+comando.length()+1;while(inicio<s.length()&&Character.isWhitespace(s.charAt(inicio)))inicio++;
   int fin=cierre(s,inicio);if(fin<0){b.append(s.charAt(i++));continue;}
   String a=grupos(s.substring(inicio+1,fin),profundidad+1);int siguiente=fin+1;
   if(comando.endsWith("frac")){while(siguiente<s.length()&&Character.isWhitespace(s.charAt(siguiente)))siguiente++;int f=cierre(s,siguiente);if(f<0){b.append(s.charAt(i++));continue;}String d=grupos(s.substring(siguiente+1,f),profundidad+1);b.append("(").append(a).append(")/(").append(d).append(")");i=f+1;}
   else {b.append(comando.equals("sqrt")?"√("+a+")":a);i=siguiente;}
  }return b.toString();
 }
 private static int cierre(String s,int inicio){if(inicio>=s.length()||s.charAt(inicio)!='{')return -1;int nivel=0;for(int i=inicio;i<s.length();i++){if(s.charAt(i)=='{')nivel++;else if(s.charAt(i)=='}'&&--nivel==0)return i;}return -1;}
 public static String html(String origen){
  StringBuilder b=new StringBuilder("<html><head><style>body{font-family:SansSerif;font-size:13pt;color:#302640;margin:16px;}p{margin-top:5px;margin-bottom:9px;}h3{color:#7950ad;font-size:14pt;margin-top:14px;} .resultado{color:#137964;background-color:#e4f6ef;padding:8px;}</style></head><body>");
  for(String linea:texto(origen).split("\\n",-1)){String t=linea.trim();if(t.isEmpty()){b.append("<br>");continue;}String seguro=t.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");boolean titulo=t.matches("(?i)(nivel|ejercicio|paso|regla|desarrollo).*[:：]?")&&t.length()<85;boolean resultado=t.matches("(?i)(resultado.*|respuesta final.*|f['′]\\(.*)");b.append(titulo?"<h3>":resultado?"<p class='resultado'>":"<p>").append(seguro).append(titulo?"</h3>":"</p>");}
  return b.append("</body></html>").toString();
 }
}
