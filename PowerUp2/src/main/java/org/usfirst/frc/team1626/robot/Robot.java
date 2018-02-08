package main.java.org.usfirst.frc.team1626.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.lang.reflect.InvocationTargetException;

//import org.usfirst.frc.team1626.robot.Toggle;
//org.usfirst.frc.team1626.robot.DriverInput;
@SuppressWarnings("unused")
public class Robot extends IterativeRobot {

	private XboxController xbox;
	private Joystick driverLeft;
	private Joystick driverRight;
	
	private SpeedController frontLeftSpeed;
	private SpeedController frontRightSpeed;
	private SpeedController backLeftSpeed;
	private SpeedController backRightSpeed;
	private WPI_TalonSRX frontElevator;
	private WPI_TalonSRX backElevator;
	
	private SpeedControllerGroup leftSpeed;
	private SpeedControllerGroup rightSpeed;
	
	private DifferentialDrive drive;
	
	private WPI_TalonSRX inOutMotorTalon0;
	private WPI_TalonSRX inOutMotorTalon1;
	
	int autoLoopCounter;
	ActionRecorder actions;
	private Thread autoThread;

	private WPI_TalonSRX inOutMotor0;
	private WPI_TalonSRX inOutMotor1;
	
	Toggle doMotorBreakIn = new Toggle();
	
	private SpeedController driveMotor0;
	private SpeedController driveMotor1;
	private SpeedController driveMotor2;
	private SpeedController driveMotor3;
	private SpeedControllerGroup driveMotors;
	
	private ControlMode Current;

	public String gameData;
	
	private char startingPosition = 'L';
	
	private double maxPower = .7;	//maximum power of the wheels as a fraction of maximum power
	private int smoothing = 3;		//the power to raise the input to to smooth out acceleration
	
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
		DriverInput.nameInput("Elevator-Forward");
		DriverInput.nameInput("Elevator-Back");
		
		
//		frontLeftSpeed = new WPI_TalonSRX();
		
//		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
//		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
//		drive = new DifferentialDrive(leftSpeed, rightSpeed);
		
//		inOutMotorTalon0 = new WPI_TalonSRX(4);
//		inOutMotorTalon1 = new WPI_TalonSRX(6);
//	
//		elevatorMotorOneWPI_TalonSRX = new WPI_TalonSRX(7);
//		elevatorMotorTwoWPI_TalonSRX = new WPI_TalonSRX(8);
		
//		WPI_TalonSRX leftFront		= new WPI_TalonSRX(0);
//		WPI_TalonSRX rightFront	= new WPI_TalonSRX(1);
//		WPI_TalonSRX leftRear		= new WPI_TalonSRX(2);
//		WPI_TalonSRX rightRear		= new WPI_TalonSRX(3);
		
		frontLeftSpeed		= new WPI_TalonSRX(14);
		backLeftSpeed		= new WPI_TalonSRX(15);
		frontRightSpeed		= new WPI_TalonSRX(16);
		backRightSpeed		= new WPI_TalonSRX(1);
		frontElevator		= new WPI_TalonSRX(2);
		backElevator		= new WPI_TalonSRX(3);
		
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
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		String[] s = gameData.split("");                 				 //alternate parsing method - Brad
		Character allianceSwitch = s[0].toCharArray()[0];
		Character scale = s[1].toCharArray()[0];
		Character oppositeSwitch = s[2].toCharArray()[0];
		switch(startingPosition){
			case 'L':
				if(allianceSwitch == 'L'){
					if(scale == 'L'){
						SmartDashboard.putString("DB/String 0", "brad.csv");
					} else {
						SmartDashboard.putString("DB/String 0", "owo.csv");
					}
				} else {
					if(scale == 'R'){
						SmartDashboard.putString("DB/String 0", "nothing.csv");
					} else {
						SmartDashboard.putString("DB/String 0", "brad.csv");
					}
				}
				break;
			case 'M':
				if(allianceSwitch == 'L'){
					if(scale == 'L'){
						
					} else {
						
					}
				} else {
					if(scale == 'R'){
						
					} else {
						
					}
				}
				break;
			case 'R':
				if(allianceSwitch == 'L'){
					if(scale == 'L'){
						
					} else {
						
					}
				} else {
					if(scale == 'R'){
						
					} else {
						
					}
				}
				break;
		}
		actions.autonomousInit();
	
	}

	@Override
	public void autonomousPeriodic() {
		
//		String StartingPosition;
//		if(true){
//			
//			StartingPosition = "0";
//			
//		}else if(true){
//			
//			StartingPosition = "1";
//			
//		}else if(true){
//			
//			StartingPosition = "2";
//			
//		}
		String itemLocationString = gameData.substring(0, 2) + startingPosition;
		
		
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
					.withInput("Operator-X-Button",		xbox.getXButton())
					.withInput("Operator-Y-Button",		xbox.getYButton())
					.withInput("Operator-A-Button", 	xbox.getAButton())
					.withInput("Operator-B-Button",		xbox.getBButton())
					.withInput("Operator-Start-Button",	xbox.getRawButton(10))
					.withInput("Elevator-Back",  		xbox.getTriggerAxis(Hand.kLeft))	//mapped elevator down to left trigger
					.withInput("Elevator-Forward",		xbox.getTriggerAxis(Hand.kRight))	//and up to right trigger -Brad
					.withInput("Operator-DPad",			xbox.getPOV())
					.withInput("Driver-Left", 			driverLeft.getRawAxis(1))
					.withInput("Driver-Right", 			driverRight.getRawAxis(1))
					.withInput("Driver-Left-Trigger", 	driverLeft.getRawButton(1))
					.withInput("Driver-Right-Trigger", 	driverRight.getRawButton(1))
					);
		
					
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
		
		if (xbox.getPOV() == 0) {
			frontElevator.set(ControlMode.Position, 0);
			backElevator.set(ControlMode.Position, 0);
		}
		else if (xbox.getPOV() == 90) {
			frontElevator.set(ControlMode.Position, 10000);
			backElevator.set(ControlMode.Position, 10000);
		}
		else if (xbox.getPOV() == 180) {
			frontElevator.set(ControlMode.Position, 20000);
			backElevator.set(ControlMode.Position, 20000);
		}
		else if (xbox.getPOV() == 270) {
			frontElevator.set(ControlMode.Position, 30000);
			backElevator.set(ControlMode.Position, 30000);
		}
		else if (xbox.getBButton() == true) {
			frontElevator.set(ControlMode.Position, 40000);
			backElevator.set(ControlMode.Position, 40000);
		}
		
	}

	@Override public void testPeriodic() {
	
	}
	
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
		SmartDashboard.putString("DB/String 1", "" + gameData + startingPosition);
		
		double leftAxis = input.getAxis("Driver-Left");
		double rightAxis = input.getAxis("Driver-Right");
		leftAxis = Math.abs(Math.pow(leftAxis, smoothing)) * leftAxis/Math.abs(leftAxis);
		rightAxis = Math.abs(Math.pow(rightAxis, smoothing)) * rightAxis/Math.abs(rightAxis);
		
		double elevatorAxis = input.getAxis("Elevator-Forward") - input.getAxis("Elevator-Back");
		
		drive.tankDrive(-1*maxPower*leftAxis, -1*maxPower*rightAxis, false);
		
		frontElevator.set(elevatorAxis);
		backElevator.set(elevatorAxis);

		boolean shift = input.getButton("Shift-Input");
		
		boolean elevatorButton = input.getButton("Elevator-Forward");
		
		//--end snippet--
		
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
//		
		if (input.getButton("Operator-X-Button") == true) {
			inOutMotor0.set(Current, .99);
			inOutMotor1.set(Current, .99);
		}
		
		if (input.getButton("Operator-Y-Button") == true) {
			inOutMotor0.set(Current, -99);
			inOutMotor1.set(Current, -99);
		}

//			}
		
//		boolean elevator = false;
//		elevatorMotorOne.set(0);
//		elevatorMotorOne.set(0);
//		if (elevator == false);
//			if(input.getButton("Elevator-Front")){
//				elevator = true;
//				elevatorMotorOne.set(1);
//				elevatorMotorTwo.set(1);
//	}
//		if(elevator == true) {
//			if(input.getButton("Elevator-Back")){
//				elevator = true;
//				elevatorMotorOne.set(0);
//				elevatorMotorTwo.set(0);
//			}

	}
}
