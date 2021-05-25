/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robocodecompetencia;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ericm
 */
public class Hilo extends Thread {
   
    public WinnersDialog winners;
   
  public Hilo(WinnersDialog winners){
       this.winners = winners;
   }

    Hilo() {
    }
    @Override
    public void run() {
    for(int i = 60;i>0;i--){
        try {
            //System.out.println(i);
            winners.setTextCount(i);
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        }    
    }
 
}