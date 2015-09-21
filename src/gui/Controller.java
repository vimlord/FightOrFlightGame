/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.event.KeyEvent;
import physics.Coordinate;
import physics.Vector;
import world.entities.creatures.Player;


/* @author Christopher Hittner


*/
public class Controller {
    
    boolean[] status;
    int[] keyCodes;
    private String name;
    
    
    private long lastCheck;
    private static Camera c;
    
    private static Player currentPlayer = new Player();
    
    
    /**
     * Creates a Controller with key codes
     * @param nm The name of the Controller
     * @param codes The array of codes.
     * @precondition No two entries in the array are the same.
     */
    public Controller(String nm, int[] codes){
        c = new Camera(currentPlayer.getPosition(), 0, 0);
        name = nm;
        keyCodes = codes;
        status = new boolean[codes.length];
        for(boolean b : status){
            b = false;
        }
    }
    
    public Controller(String nm){
        this(nm, generateKeyCodes());
        lastCheck = System.nanoTime();
    }
    
    
    /**
     * Sets a new code at an index, given it actually exists.
     * @param index
     * @param newCode
     */
    public void setCode(int index, int newCode){
        try{
            keyCodes[index] = newCode;
        } catch(Exception ex){
            
        }
    }
    
    /**
     * Sets whether or not a key is being pressed.
     * @param code The key code
     * @param value The boolean value
     */
    public void setState(int code, boolean value){
        for(int i  = 0; i < keyCodes.length; i++){
            if(code == keyCodes[i])
                status[i] = value;
        }
    }
    
    public boolean getState(int index){
        return status[index];
    }
    
    public int getNumerOfControls(){ return keyCodes.length; }
    
    public String getName(){
        return name;
    }
    
    public String toString(){
        String result = "Controller " + name + "\n";
        for(int i = 0; i < 11; i++){
            result += "Index " + i + ": " + "\n"
                    + "    Code: " + keyCodes[i] + "\n"
                    + "    Name: " + KeyEvent.getKeyText(keyCodes[i]) + "\n"
                    + "    Pressed: " + status[i] + "\n\n";
        }
        return result;
    }
    
    public static int[] generateKeyCodes(){
        return new int[]{
            KeyEvent.VK_UP,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_W,
            KeyEvent.VK_S,
            KeyEvent.VK_A,
            KeyEvent.VK_D,
            KeyEvent.VK_SPACE,
            KeyEvent.VK_E,
            KeyEvent.VK_B
        };
    }
    
    
    public void execute() {
        
        //This should be run after any required operations are performed. The next two lines of code ensure that
        //the player cannot control the character.
        if(currentPlayer.getHealth() <= 0)
            return;
        
        double time = (System.nanoTime() - lastCheck)/Math.pow(10,9);
        lastCheck += time * Math.pow(10,9);
        
        
        Vector acc = new Vector(0,0,0);
        Camera c = getCamera();
        
        if(currentPlayer.getPosition().Y() <= currentPlayer.getSize()){
            if(getState(8)){
                currentPlayer.getVelocity().addVectorToThis(new Vector(4 - currentPlayer.getVelocity().getMagnitudeY(),0, Math.PI/2.0));
            } else if(getState(10) && currentPlayer.getVelocity().getMagnitude() > 0){
                currentPlayer.getVelocity().addVectorToThis(new Vector(currentPlayer.getVelocity().unitVector(), -Math.min(time * currentPlayer.getAcceleration() *4, currentPlayer.getVelocity().getMagnitude())));
            }else if(currentPlayer.getVelocity().getMagnitude() < currentPlayer.getSpeedLimit()){
                for(int i = 0; i < 4; i++)
                    if(getState(i))
                        acc.addVectorToThis(new Vector(1 , c.getXZ() + i * 0.5 * Math.PI , 0));

                if(acc.getMagnitude() != 0){
                    acc = new Vector(acc.unitVector(),currentPlayer.getAcceleration() * time);

                    if(currentPlayer.getVelocity().getMagnitude() > 0)
                        acc.multiplyMagnitude(Math.pow(2 - Vector.cosOfAngleBetween(acc, currentPlayer.getVelocity()),1.5));

                    currentPlayer.getVelocity().addVectorToThis(acc);
                }
            }
        }
        double rotSpeed = 2 * time;
        
        if(getState(4))
            c.setDirection(new Vector(1,c.getXZ(), c.getY() + rotSpeed));
        if(getState(5))
            c.setDirection(new Vector(1,c.getXZ(), c.getY() - rotSpeed));
        if(getState(6))
            c.setDirection(new Vector(1,c.getXZ() + rotSpeed, c.getY()));
        if(getState(7))
            c.setDirection(new Vector(1,c.getXZ() - rotSpeed, c.getY()));
        
        c.setDirection(new Vector(1, c.getXZ(), Math.max(-Math.PI/2.0, Math.min(Math.PI/2.0, c.getY()))));
        
        if(getState(9))
            currentPlayer.getWeapon().use(time, currentPlayer);
        
        //System.out.println(currentPlayer.getVelocity().toString(true));
        
    }
    
    public void setPlayer(Player p){ currentPlayer = p; }
    
    public static Camera getCamera(){
        return c;
    }
    
    public static Player getPlayer(){ return currentPlayer; }
    
}