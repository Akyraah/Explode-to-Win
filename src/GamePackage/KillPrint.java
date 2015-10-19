/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GamePackage;

/**
 *
 * @author Aurélien
 */
public class KillPrint {
    int endTimer;
    Player killer;
    Player victim;
    String itemType;
    int timer;
    
    public KillPrint(int endTimer, Player killer, Player victim, String itemType){
        this.endTimer = endTimer;
        this.killer = killer;
        this.victim = victim;
        this.itemType = itemType;
        timer = 0;
    }
    
}
