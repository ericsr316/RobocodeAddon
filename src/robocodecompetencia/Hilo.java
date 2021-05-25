/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robocodecompetencia;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


/**
 *
 * @author ericm
 */
public class Hilo extends Thread {
    public Clip audio;
    public WinnersDialog winners;
   
  public Hilo(WinnersDialog winners){
       this.winners = winners;
       
   }

    Hilo() {
    }
    @Override
    public void run() {
    for(int i = 60;i>=-4;i--){
        try {
            //System.out.println(i);
            if(i>=0){
            winners.setTextCount(i);
            }
            
            if(i == 0){
                 sonido();
            }
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        }    
    }
    
    public void sonido(){
        try{
            audio = AudioSystem.getClip();
            audio.open(AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/robocodecompetencia/audio/Buzzer.wav")));
            System.out.println(audio.toString());
            audio.start();
        }
        catch(Exception e){     
            System.out.println(e);
    }
 
}
    
}