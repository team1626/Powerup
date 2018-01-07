package org.usfirst.frc.team1626.robot;

import java.lang.reflect.InvocationTargetException;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.ctre.CANTalon;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//	Team 1626
//	Power Up (2018)
//
//	Bradley O'Connell, Jonathan Heitz, and Nikhil Sathi

//	TODO:
//		Some classes from 2017 are depreciated and we need to rewrite some code to use the new classes. (e.g. in ActionRecorder, 
//		we need to use RobotController instead of Utility)
//

@SuppressWarnings("unused")
public class Robot extends IterativeRobot {

	private XboxController xbox;
	private Joystick driverLeft;
	private Joystick driverRight;
	
	int autoLoopCounter;
	ActionRecorder actions;
	
	@Override
	public void robotInit() {
	
		driverLeft = new Joystick(0);
		driverRight = new Joystick(1);
		xbox = new XboxController(2);
	
	}

	@Override
	public void autonomousInit() {

		autoLoopCounter = 0;
		actions.autonomousInit();
	
	}

	@Override
	public void autonomousPeriodic() {
		
		
		
	}

	@Override
	public void teleopPeriodic() {
		
		
		
	}

	@Override
	public void testPeriodic() {
		
		
		
	}
	
	@Override
	public void disabledInit() {
		
		actions.disabledInit();
	
	}

	@Override
	public void disabledPeriodic() {
	
		actions.disabledPeriodic();
		
	}
	
}
