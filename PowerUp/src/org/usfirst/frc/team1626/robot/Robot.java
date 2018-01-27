package org.usfirst.frc.team1626.robot;

import java.lang.reflect.InvocationTargetException;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
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

//	TO DO:
//		Some classes from 2017 are deprecated and we need to rewrite some code to use the new classes. (e.g. in ActionRecorder, 
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
	
	private Talon intakeMotorTalon;
	private Talon outtakeMotorTalon;

	
	int autoLoopCounter;
	ActionRecorder actions;
	private Thread autoThread;

	private Talon intakeOneMotor;
	private Talon intakeTwoMotor;
	private Talon outtakeOneMotor;
	private Talon outtakeTwoMotor;
	
	@Override	
	public void robotInit() {
		
		System.err.println("Starting the Power Up Robot");
		
		driverLeft = new Joystick(0);
		driverRight = new Joystick(1);
		xbox = new XboxController(2);

		actions = new ActionRecorder().
				setMethod(this, "robotOperation", DriverInput.class).
				setUpButton(xbox, 1).
				setDownButton(xbox, 2).
				setRecordButton(xbox, 3);

		
		DriverInput.nameInput("Driver-Left");
		DriverInput.nameInput("Driver-Right");
		DriverInput.nameInput("Driver-Left-Trigger");
		DriverInput.nameInput("Driver-Right-Trigger");
		DriverInput.nameInput("Operator-Left-Stick");
		DriverInput.nameInput("Operator-Left-Bumper");
		DriverInput.nameInput("Operator-Left-Trigger");
		DriverInput.nameInput("Operator-Right-Stick");
		DriverInput.nameInput("Operator-Right-Bumper");
		DriverInput.nameInput("Operator-Right-Trigger");
		DriverInput.nameInput("Operator-X-Button");
		DriverInput.nameInput("Operator-Y-Button");
		DriverInput.nameInput("Operator-A-Button");
		DriverInput.nameInput("Operator-B-Button");
		DriverInput.nameInput("Operator-Start-Button");
		DriverInput.nameInput("Operator-Back-Button");
		
		
		
//		frontLeftSpeed = new SpeedController();
		
//		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
//		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
//		drive = new DifferentialDrive(leftSpeed, rightSpeed);
		
		intakeMotorTalon = new Talon(4);
		outtakeMotorTalon = new Talon(6);
	
//		Talon leftFront		= new Talon(0);
//		Talon rightFront	= new Talon(1);
//		Talon leftRear		= new Talon(2);
//		Talon rightRear		= new Talon(3);
		
		frontLeftSpeed		= new Talon(0);
		frontRightSpeed		= new Talon(1);
		backLeftSpeed		= new Talon(2);
		backRightSpeed		= new Talon(3);
		
		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
		
		drive = new DifferentialDrive(leftSpeed, rightSpeed);
		
//		leftFront.setInverted(true);
//		rightFront.setInverted(true);
//		leftRear.setInverted(true);
//		rightRear.setInverted(true);
//		drive				= new RobotDrive(leftFront, leftRear, rightFront, rightRear);
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
	public void teleopInit() {
		DriverInput.setRecordTime();
		actions.teleopInit();
	}

	@Override
	public void teleopPeriodic() throws NullPointerException {

		try {
			actions.input(new DriverInput()
				
					.withInput("Operator-X-Button", xbox.getXButton())
					.withInput("Operator-Y-Button", xbox.getYButton())
					.withInput("Operator-A-Button", xbox.getAButton())
					.withInput("Operator-B-Button", xbox.getBButton())
					.withInput("Driver-Left", driverLeft.getRawAxis(1))
					.withInput("Driver-Right", driverRight.getRawAxis(1))
					.withInput("Driver-Left-Trigger", driverLeft.getRawButton(1))
					.withInput("Driver-Right-Trigger", driverRight.getRawButton(1))
					.withInput("Elevator-Back",  xbox.getBackButton())
					.withInput("Elevator-Forward", xbox.getStartButton()));
		
					
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

		/*
		 * The setMethod should only be called in robotInit when the action recorder is being instantiated.  It sets the
		 * the name of the method that is invoked to perform robot operations.
		 * 
		 * 		actions.setMethod(this, "readInput", DriverInput.class);
		 * 
		 */
		
	}

	@Override public void testPeriodic() {}
	
	@Override
	public void disabledInit() {
		actions.disabledInit();
//		if (autoThread != null) {
//			System.out.println("Checking autonomous thread");
//			if (autoThread.isAlive()) {
//				System.out.println("Interrupting autonomous thread");
//				autoThread.interrupt();
//			}
//			try {
//				System.out.println("Joining autonomous thread");
//				autoThread.join(100);
//			} catch (InterruptedException e) {
//				System.out.println("Too long to join autonomous thread");
//			}
//			if (!autoThread.isAlive()) {
//				autoThread = null;
//				System.out.println("Autonomous thread terminated");
//			}
		}


	@Override
	public void disabledPeriodic() {
	
		actions.disabledPeriodic();
		
	}
	
	public void robotOperation(DriverInput input) {
		
		double leftAxis = input.getAxis("Driver-Left");
		leftAxis = leftAxis*Math.abs(leftAxis);
		double rightAxis = input.getAxis("Driver-Right");
		rightAxis = rightAxis*Math.abs(rightAxis);
		
		drive.tankDrive(leftAxis, rightAxis);

		boolean shift = input.getButton("Shift-Input");
		
//		if (driverLeft.getRawAxis(1) > 0) leftSpeed.set(.40);
//		else if (driverRight.getRawAxis(1) < 0) rightSpeed.set(.40);
//		else leftSpeed.set(0);
//		
//		
//		if (driverLeft.getRawAxis(1) > 0) frontLeftSpeed.set(.99);
//		else if (driverLeft.getRawAxis(1) < 0) backLeftSpeed.set(.99);
//		else leftSpeed.set(0);
////	
////		if (driverRight.getRawAxis(1) > 0) frontRightSpeed.set(-.99);
////		else if (driverRight.getRawAxis(1) < 0) backRightSpeed.set(-.99);
////		else rightSpeed.set(0);
		
		if (input.getButton("Operator-X-Button") == true) {
			intakeOneMotor.set(.99);
			intakeTwoMotor.set(.99);
		}
		
		if (input.getButton("Operator-Y-Button") == true) {
			outtakeOneMotor.set(-99);
			outtakeTwoMotor.set(-99);
		
		}

	}

}
