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
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//	Team 1626
//	Power Up (2018)
//
//	Bradley O'Connell, Jonathan Heitz, and Nikhil Sathi
//	
//	ActionRecorder.java and DriverInput.java were copied from last year's code.

//	TODO:
//		Some classes from 2017 are depreciated and we need to rewrite some code to use the new classes. (e.g. in ActionRecorder, 
//		we need to use RobotController instead of Utility)
//

@SuppressWarnings("unused")
public class Robot extends IterativeRobot {

	private XboxController xbox;
	private Joystick driverLeft;
	private Joystick driverRight;
	
	private SpeedController frontLeftSpeed;
	private SpeedController frontRightSpeed;
	private SpeedController backLeftSpeed;
	private SpeedController backRightSpeed;
	
	private SpeedControllerGroup leftSpeed;
	private SpeedControllerGroup rightSpeed;
	
	private DifferentialDrive drive;
	
	int autoLoopCounter;
	ActionRecorder actions;
	
//	8========================================================================D		dicks
	
	@Override
	public void robotInit() {
	
		driverLeft = new Joystick(0);
		driverRight = new Joystick(1);
		xbox = new XboxController(2);
		
		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
		drive = new DifferentialDrive(leftSpeed, rightSpeed);
	
	}

	@Override
	public void autonomousInit() {

		autoLoopCounter = 0;
		actions.autonomousInit();
	
	}

	@Override
	public void autonomousPeriodic() {
		
		try{
			
			if (actions != null){
				
//				actions.playback();
				actions.longPlayback(this, -1);
			
			}else{
				
				Timer.delay(0.010);
				
			}
		}catch (Exception e){
			
			System.out.println("AP: " + e.toString());
			
		}
		
	}

	@Override
	public void teleopPeriodic() {
		
		try {
			actions.input(new DriverInput()
					.withInput("Operator-X-Button", xbox.getXButton())
					.withInput("Operator-Y-Button", xbox.getYButton())
					.withInput("Operator-A-Button", xbox.getAButton())
					.withInput("Operator-B-Button", xbox.getBButton())
					.withInput("Driver-Left", driverLeft.getRawAxis(1))
					.withInput("Driver-Right", driverRight.getRawAxis(1))
					.withInput("Shift-Input", driverRight.getRawButton(1))
					.withInput("Winch-Button",  xbox.getBackButton())
					.withInput("Winch-Reverse", xbox.getStartButton()));
				
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		actions.setMethod(this, "readInput", DriverInput.class);
		
	}

	@Override public void testPeriodic() {}
	
	@Override
	public void disabledInit() {
		
		actions.disabledInit();
	
	}

	@Override
	public void disabledPeriodic() {
	
		actions.disabledPeriodic();
		
	}
	
	public void readInput() {
		
		if (driverLeft.getRawAxis(1) > 0) leftSpeed.set(.99);
		else if (driverRight.getRawAxis(1) > 0) rightSpeed.set(.99);
		else leftSpeed.set(0);
		
	}

}
