package robocodecompetencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import robocode.control.*;
import robocode.control.events.BattleCompletedEvent;

/**
 *
 * @author Rafael Vera
 */
public class RobocodeCompetencia{
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
    RobocodeEngine.setLogMessagesEnabled(true);
    RobocodeEngine engine = new RobocodeEngine(
      new java.io.File("C:/Robocode")
    );
    
    BattleObserver observador = new BattleObserver();
    // Le agregamos el oyente que eliminará a los que perdieron
    engine.addBattleListener(observador);
    
    engine.setVisible(true);
    
    // Tamaño del campo de batalla
    BattlefieldSpecification campo = new BattlefieldSpecification(1000, 1000);
    
    // Array con especificación de cada fase
    // {numCompetidoresPorEnfrentamiento, numRondas, numGanadores}
    // Esta es la estructura para 30 competidores
    // Esta estructura cambiará dependiendo de cuantos competidores haya
    int[][] especificaciones = {
      {6, 1, 4},
      {5, 1, 3},
      {4, 1, 2},
      {6, 1, 3},
    };
    
    // ArrayList que almacena a todos los competidores
    // Busco los robots dentro de sample ya que tiene 29 robots dentro
    ArrayList<String> listaDeCompetidores = seleccionaCompetidores(engine, "sample.");
    ArrayList<String> ganadores;
    //listaDeCompetidores.add("competencia.Roboto3000*");
    
    // Hacemos los enfrentamientos de cada fase
    for(int[] fase : especificaciones) {
      listaDeCompetidores = enfrentamientosFase(
        fase[0], // Número de competidores por enfrentamiento en cada fase
        fase[1], // Número de rondas establecidas para cada enfrentamiento de cada fase
        fase[2], // Número de ganadores por cada enfrentamiento
        campo, // Tamaño del campo de batalla
     
        listaDeCompetidores, // Lista con los competidores la fase (se actualiza al final de cada fase)
        observador,
        engine
      );            
      System.out.println("Ganadores de la fase ("+listaDeCompetidores.size()+"):");
      System.out.println(listaDeCompetidores);
             
        
    }   
    System.out.println("Ganadores:");
    

    System.out.println(listaDeCompetidores);
    
    engine.close();
    System.exit(0);
  }

       
    

 
  /**
   * Método que genera los enfrentamientos de cada fase y devuelve los ganadores que pasan a la siguiente fase
   * @param competidoresPorEnfrentamiento
   * @param numRondas
   * @param numGanadores
   * @param campo
   * @param competidoresFase
   * @param observador
   * @param engine
   * @return Una lista con los nombres de los robots que pasan a la siguiente fase
   */
  public static ArrayList<String> enfrentamientosFase(
    int competidoresPorEnfrentamiento, int numRondas, int numGanadores,
    BattlefieldSpecification campo, ArrayList<String> competidoresFase, BattleObserver observador, RobocodeEngine engine) {
    Collections.shuffle(competidoresFase); // Desordena la lista de competidores dentro de la fase
    ArrayList<String> ganadoresFase = new ArrayList(); // Lista con los ganadores de la fase
    observador.setNumGanadores(numGanadores); // Establece la cantidad de ganadores para cada enfrentamiento de la fase
    // Selecciona los competidores del enfrentamiento correspondiente
    ArrayList<ArrayList<String>> grupos = crearGrupos(competidoresPorEnfrentamiento, competidoresFase);
    System.out.println("Lista completa de competidores ("+grupos.size()+"):");
    System.out.println(grupos);
    // Para cada grupo de la fase hace un efrentamiento
    for(ArrayList<String> grupo : grupos) {
      System.out.println("Se enfrentan ("+grupo.size()+"):");
      System.out.println(grupo);
      // Agrega los ganadores del enfrentamiento a la lista de ganadores
      ganadoresFase.addAll(enfrentamiento(numRondas, campo, grupo, engine, observador));
    }
    return ganadoresFase;
  }
  
  /**
   * Método que hace un enfrentamiento
   * @param numRondas
   * @param campo
   * @param competidores
   * @param engine
   * @param observador
   * @return Una lista con los ganadores de dicho enfrentamiento
   */
  public static ArrayList<String> enfrentamiento(
    int numRondas, BattlefieldSpecification campo, ArrayList<String> competidores,
    RobocodeEngine engine, BattleObserver observador) {
    // Junta la lista de competidores de este enfrentamiento volviendolo un String separado por coma
    RobotSpecification[] robotsSeleccionados = engine
      .getLocalRepository(
        juntar(competidores)
      );
    // Especifico los lineamientos de la batalla
    BattleSpecification battleSpec = new BattleSpecification(numRondas, campo, robotsSeleccionados);
    engine.runBattle(battleSpec, true); // Corre el enfrentamiento
    return observador.getGanadores(); // Devuelve la lista con los ganadores de este enfrentamiento
  }
  
  /**
   * Método que crea los grupos de enfrentamientos, hace una lista de listas con los nombres de los robots que conforman cada grupo
   * @param competidoresPorEnfrentamiento
   * @param competidores
   * @return Una lista de listas con los conbres de los robots que conforman cada grupo de enfrentamiento
   */
  public static ArrayList<ArrayList<String>> crearGrupos(int competidoresPorEnfrentamiento, ArrayList<String> competidores) {
    // Crea una lista de listas vacía donde se almacenarán los grupos
    ArrayList<ArrayList<String>> grupos = new ArrayList();
    // obtiene el número de grupos que se pueden generar dado el número de competidores por enfrentamiento
    int numGrupos = competidores.size() / competidoresPorEnfrentamiento;
    // Obtiene el número de robots que sobran y no completan un grupo
    int resto = competidores.size() - ( numGrupos * competidoresPorEnfrentamiento );
    int c = 0; // Variable que recorrerá la lista de competidores de la fase
    
    // Para cada grupo va a llenar una lista con robots
    for(int i=0; i<numGrupos; i++) {
      ArrayList<String> grupo = new ArrayList(); // Crea un grupo vacío
      for(int j=0; j<competidoresPorEnfrentamiento; j++) {
        grupo.add(competidores.get(j+c)); // Agrega los robots al grupo
      }
      c += competidoresPorEnfrentamiento; // Aumentamos el valor de c
      grupos.add(grupo); // Agrega el grupo lleno a la lista de grupos
    }
    
    // En caso de que haya robots restantes los agrega a los grupos ya establecidos
    while(resto > 0) {
      // Dentro de cada grupo agrega un robot hasta que no haya más restos
      for(ArrayList<String> grupo : grupos) {
        if(c < competidores.size()) { // Comprueba que no se salga de la lista de competidores
          grupo.add(competidores.get(c));
          c++;
          resto--;
        }
      }
    }
    
    return grupos; // Regresa la lista de grupos
  }
  
  /**
   * Método que busca todos los robots que estén dentro del paquete especificado
   * @param engine
   * @param paquete
   * @return Un ArrayList con los nombres de todos los robots que competirán
   */
  public static ArrayList<String> seleccionaCompetidores(RobocodeEngine engine, String paquete) {
    ArrayList<String> nombres = new ArrayList(); // Creamos un ArrayList donde se almecenarán los robots
    for(RobotSpecification robot : engine.getLocalRepository()) { // Buscamos en todos los robots disponibles
      if(robot.getName().startsWith(paquete)) { // Solo buscaremos aquellos que estén dentro del paquete seleccionado
        nombres.add(robot.getName());
      }
    }
    return nombres;
  }
  
  /**
   * Método que junta toda la lista de robots en un solo String, si se puede mejorar adelante
   * @param lista
   * @return Un String con los nombres de los robots separados por coma
   */
  public static String juntar(ArrayList<String> lista) {
    return lista
      .toString()
      .replace("[", "")
      .replace("]", "");
  }
}