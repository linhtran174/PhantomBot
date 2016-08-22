/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OWS;

import java.util.ArrayList;

/**
 *
 * @author 8470p
 */
public class RobotProfiles {
    
    public int robotsOnBattleField = 0;
    public ArrayList<String> robotNames = new ArrayList<String>();
    
    
    
    
    
    public boolean exist(String robotName){
        return (robotNames.contains(new String(robotName)));
    }
    
    public int add(String robotName){
        this.robotNames.add(robotName);
        return 0;
    }
    
    public RobotProfiles(int otherRobots){
        this.robotsOnBattleField = otherRobots;
    }
    
    
    
}
