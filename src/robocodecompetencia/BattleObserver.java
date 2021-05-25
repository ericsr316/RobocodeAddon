package robocodecompetencia;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.control.events.*;

/**
 *
 * @author Rafael Vera
 */
public class BattleObserver extends BattleAdaptor {
  private final ArrayList<String> ganadores;
  private final ArrayList<String> listaPresentar;
  private int numGanadores;
  
  public BattleObserver() {
    this.ganadores = new ArrayList();
    this.numGanadores = 0;
    this.listaPresentar = new ArrayList();
  }
  
  public void setNumGanadores(int numGanadores) {
    this.numGanadores = numGanadores;
  }
  
  public ArrayList<String> getGanadores() {
    return this.ganadores;
  }
  
  @Override
  public void onBattleCompleted(BattleCompletedEvent e) {
    System.out.println("-- Battle has completed --");
    ganadores.clear();
    listaPresentar.clear();
    System.out.println("Battle results:");
    int c = 0;
    for(robocode.BattleResults result : e.getSortedResults()) {
      System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());
      if(c < numGanadores) {
        String nombre = result.getTeamLeaderName();
        
        String puntuacion = Integer.toString(result.getScore());
        
        int espacio = nombre.indexOf(" ");
        
        ganadores.add(nombre.substring(0, espacio));
        listaPresentar.add(nombre.substring(0, espacio).concat(" "+puntuacion+" puntos"));
        //ganadores.get(c).concat(" "+puntuacion);
//ganadores.add(nombre);
        c++;
      }
    }    
        WinnersDialog winners = new WinnersDialog(listaPresentar,null,false);
        winners.setVisible(true);
      try {
          Thread.sleep(60000);
      } catch (InterruptedException ex) {
          Logger.getLogger(BattleObserver.class.getName()).log(Level.SEVERE, null, ex);
      }
      winners.setVisible(false);
      listaPresentar.clear();

  }
  
  @Override
  public void onBattleMessage(BattleMessageEvent e) {
    System.out.println("Msg> " + e.getMessage());
  }
  
  @Override
  public void onBattleError(BattleErrorEvent e) {
    System.out.println("Err> " + e.getError());
  }
}