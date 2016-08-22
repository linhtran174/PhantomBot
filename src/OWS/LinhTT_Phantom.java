/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OWS;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
//import java.awt.event.KeyEvent;
//import static java.awt.event.KeyEvent.*;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseWheelEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LinhTT_Phantom extends AdvancedRobot {

    public LinhTT_Phantom() {

    }

    public int robotWidth = 37;
    public int robotHeight = 35;
    public RobotProfiles allRobots;
    public Random random;
    public int loopTimer;
    @Override
    public void run() {
        System.out.println("New verasdasdn111ad");
        
        random = new Random(System.currentTimeMillis());
        Color Phantom = new Color(0, 0, 0, (float) 0.8);
        setColors(Phantom, Phantom, Phantom, Phantom, Phantom);
        allRobots = new RobotProfiles(getOthers());
        gunLock = 0;

        addCustomEvents();

        //System.out.println("OK");
        setAdjustGunForRobotTurn(true);
        //setAdjustRadarForGunTurn(true);
        //setAdjustRadarForRobotTurn(true);

        while (true) {
            
            //setFire(1);
            loopTimer++;
            //re-determine target
            //allRobots.updateTarget();
            //DoMyStuffs
            DoMyStuffs();
            //setTurnRadarRight(360);
            execute();
        }
    }

    public int gunLock;
    public int moveLock = 0;
    long fireTime = 0;
    boolean fireNow = false;
    public int firePow = 0;
    public void DoMyStuffs() {
        if (gunLock != 1) {
            setTurnGunRelative(360);
        } else {
            scan();
        }
        if(moveLock==0){
//            System.out.print("random 1:");
//            System.out.println((random.nextInt(1)==1)?(random.nextInt(5)*10):(-random.nextInt(5)*10));
            
//            System.out.print("random 2:");
//            System.out.println((random.nextInt(1)==1)?(random.nextInt(30)+45):(random.nextInt(30)-75));
//            System.out.println(random.nextInt(2));
            moveLock += 5;
            setAhead((random.nextInt(2)==1)?(random.nextInt(100)+50):(-random.nextInt(100)-50));
            setTurnRight((random.nextInt(2)==1)?(random.nextInt(30)+45):(random.nextInt(30)-75));
        }
        
        if(moveLock != 0){
            System.out.println("moveLock = " +moveLock);
            moveLock--;
        } 

        determineFire();
    }

    public void determineFire(){
        if(fireNow){
            if (getGunHeat() == 0) {
                //setFire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
                setFire(firePow);
            }
            fireNow = false;
        }
    }

    public void onCustomEvent(CustomEvent e) {
        if (e.getCondition().getName().equals("closeWall")) {
            
            //lockMove
            moveLock += 1;

            //move far away
            if (getX() < 50) {
                if (getHeading() > 180 && getHeading() < 359) {
                    setBack(100);
                } else {
                    setAhead(100);
                }

            } else if (getX() > 700) {
                if (getHeading() > 180 && getHeading() < 359) {
                    setAhead(100);
                } else {
                    setBack(100);
                }
            }
            if (getY() < 100) {
                if (getHeading() > 90 && getHeading() < 270) {
                    setBack(100);
                } else {
                    setAhead(100);
                }
            } else if (getY() > 500) {
                if (getHeading() > 90 && getHeading() < 270) {
                    setAhead(100);
                } else {
                    setBack(100);
                }
            }
        }
    }

    //public List<String> scannedRobots = ;
    public void otherRobotProfiler(ScannedRobotEvent e) {
        if (!allRobots.exist(e.getName())) {
            allRobots.add(e.getName());
            System.out.println("new robot " + e.getName() + " added to the profiler");
        } else {

        }
    }

    public double solveDegreeToTurn(double d, double v, double mfg, double bfg){
        if (v == 0) return bfg;
        //calculate dtt - degreeToTurn, based on:
        //d - current enemy distance,
        //v - current enemy velocity
        //mfg - enemy movement bearing from gun
        //bfg - enemy bearing from gun
        
        
        //tth - timeToHit - time for bullet to hit enemy
        double tth;
        //ttm - timeToMove - enemy's time to move before hit by bullet
        double ttm;
        //ed - enemy's displacement from current position
        double ed;
        //bd - future bullet's displacement from current position
        double bd;
        
        //temp
        double temp;
        
        //Equation: 
        //dtt - arcsin(v*dtt/20*d/sin(PI-mfg-dtt)) = bfg
        
        
        mfg = mfg /180*Math.PI;
        bfg = bfg /180*Math.PI;
        double dtt = bfg;
        double accuracy = 0.01;
        double step = 0.0001;
        double trials = 30;
        double result;
        for(int i = 2; i < trials; i++){
            tth = i;
            ttm = tth +1;
            ed = ttm*v;
            bd = 14*tth;
            
            //triangle constraint: bd^2 = ed^2 + d^2 - 2*cos*(180-mfg-bfg);
//            System.out.println("ed = "+ed);
//            System.out.println("bd = "+bd);
//            System.out.println("angle = "+((Math.PI-mfg-bfg)*180));
            if((temp = Math.pow(bd, 2) - Math.pow(ed,2) - Math.pow(d, 2) + 2* Math.cos(Math.PI-mfg-bfg) )  > 0){
//                System.out.println("temp = " + temp);
                if(temp  < 20) {
                    dtt = Math.asin(ed*Math.sin(mfg+bfg)/bd);
                    return dtt/Math.PI*180;
                }
                else{
                    
                }
            }
            
            
        }
        return bfg/Math.PI*180;
    }
    
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
//        System.out.println("onScannedRobot:");
        //scannedRobots
        gunLock = 1;
        double absoluteBearing = getHeading() + e.getBearing();
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
        double movementFromGun = normalRelativeAngleDegrees(this.getGunHeading()- e.getHeading());

        System.out.println("bfg, mfg = " +bearingFromGun +", "+movementFromGun);
        double degreeToTurn = solveDegreeToTurn(e.getDistance(),e.getVelocity(),movementFromGun, bearingFromGun);
        
        if (Math.abs(bearingFromGun) <= 3) {
            setTurnGunRelative(normalRelativeAngleDegrees(degreeToTurn)+6);
            fireNow = true;
            if (getGunHeat() == 0) {
                //setFire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
//                if (e.getDistance() < 170) {
//                    firePow = 3;
//                }
//                if (e.getDistance() > 170 && e.getDistance() < 400) {
//                    firePow =2;
//                }
//                if (e.getDistance() > 400) {
//                    firePow = 1;
//                } 
                firePow =2;
            }
            //setTurnGunLeft( random.nextInt(3)+3 );
        } else {
            setTurnGunRelative(bearingFromGun);
        }

        otherRobotProfiler(e);
//        System.out.println("bear " + e.getBearing());
//        System.out.println("distance: " + e.getDistance());
//        System.out.println("heading: " + e.getHeading());
//        System.out.println("v: " + e.getVelocity());

    }
    
    public void setTurnGunRelative(double degrees){
        setTurnGunRight(degrees);
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
//        switch(random.nextInt(2)){
//            case 0:
//                moveLock += 5;
//                setAhead(random.nextInt(50)+100);
//               
//                break;
//            case 1:
//                moveLock += 5;
//                setBack(random.nextInt(50)+100);
//                break;
//                
//        }
//         switch(random.nextInt(2)){
//            case 0:
//                moveLock += 5;
//                setTurnRight(random.nextInt(45)+10);
//                break;
//            case 1:
//                moveLock += 5;
//                setTurnLeft(random.nextInt(45)+10);
//                break;
//                
//        }
//          
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        
    }

//    @Override
//    public void onHitWall(HitWallEvent event){
//        
//    }
    private void moveToClosestCorner(double x, double y) {

    }

    public void addCustomEvents() {
        addCustomEvent(new Condition("closeWall") {
            public boolean test() {
                return (getX() - 50 < 0
                        || getX() + 50 > 800
                        || getY() - 50 < 0
                        || getY() + 50 > 600);
            }
        });

    }

}
