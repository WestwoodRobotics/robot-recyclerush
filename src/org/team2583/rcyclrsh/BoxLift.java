/*
 * Copyright (c) 2015 Westwood Robotics <code.westwoodrobotics@gmail.com>.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 */

package org.team2583.rcyclrsh;

import io.github.robolib.command.Command;
import io.github.robolib.command.SingleActionCommand;
import io.github.robolib.command.Subsystem;
import io.github.robolib.module.controller.CANJaguar;
import io.github.robolib.util.mapper.RobotMap;

/**
 *
 * @author Austin Reuland <amreuland@gmail.com>
 */
public final class BoxLift extends Subsystem {
    
    private static CANJaguar m_motorLeft;
    private static CANJaguar m_motorRight;
    
    static double lift_speed;
    
    static boolean m_atTop;
    
    public static void initialize(){
        
        m_motorLeft = RobotMap.getModule("motor_boxlift_left");
        m_motorRight = RobotMap.getModule("motor_boxlift_right");
        
        lift_speed = RobotMap.getNumber("boxlift_speed");
        setMotors(0);
    }

    static final BoxLift m_instance = new BoxLift();

    public static BoxLift getInstance(){
        return m_instance;
    }

    private BoxLift(){
        super("BoxLift");
    }
    
    static void setMotors(double value){
        m_motorLeft.setSpeed(value);
        m_motorRight.setSpeed(value);
    }
    
    public static Command up(){
        return m_instance.new CMDLiftBoxes();
    }
    
    public static Command down(){
        return m_instance.new CMDDropBoxes();
    }
    
    public static Command toggle(){
        return m_instance.new CMDToggleBoxLift();
    }
    
    public static Command stop(){
        return m_instance.new CMDStopLift();
    }
    
    public static boolean isAtTopLeftLimit(){
        return !m_motorLeft.getForwardLimitOK();
    }
    
    public static boolean isAtBottomLeftLimit(){
        return !m_motorLeft.getReverseLimitOK();
    }
    
    public static boolean isAtTopRightLimit(){
        return !m_motorRight.getForwardLimitOK();
    }
    
    public static boolean isAtBottomRightLimit(){
        return !m_motorRight.getReverseLimitOK();
    }
    
    public static boolean isAtTopLimit(){
        return isAtTopLeftLimit() && isAtTopRightLimit();
    }
    
    public static boolean isAtBottomLimit(){
        return isAtBottomLeftLimit() && isAtBottomRightLimit();
    }
    
    public void initDefaultCommand(){}
    
    /**
     * 
     * @author Austin Reuland <amreuland@gmail.com>
     */
   private class CMDLiftBoxes extends Command {
       public CMDLiftBoxes(){requires(m_instance);}
       protected void initialize(){}
       protected void execute(){setMotors(lift_speed);}
       protected boolean isFinished(){return isAtTopLimit();}
       protected void end(){
           setMotors(0);
           m_atTop = true;
       }
       protected void interrupted(){setMotors(0);}
   }
   
   private class CMDDropBoxes extends Command {
       public CMDDropBoxes(){requires(m_instance);}
       protected void initialize(){}
       protected void execute(){setMotors(-lift_speed);}
       protected boolean isFinished(){return isAtBottomLimit();}
       protected void end(){
           setMotors(0);
           m_atTop = false;
       }
       protected void interrupted(){setMotors(0);}
   }
   
   private class CMDToggleBoxLift extends Command {
       public CMDToggleBoxLift(){requires(m_instance);}
       double dir;
       protected void initialize(){dir = m_atTop ? -lift_speed : lift_speed;}
       protected void execute(){setMotors(dir);}
       protected boolean isFinished(){return m_atTop ? isAtBottomLimit() : isAtTopLimit();}
       protected void end(){
           setMotors(0);
           m_atTop = dir > 0;
       }
       protected void interrupted(){setMotors(0);}
   }
   
   private class CMDStopLift extends SingleActionCommand {
       public CMDStopLift(){requires(m_instance);}
       protected void execute(){setMotors(0);}
   }

}

