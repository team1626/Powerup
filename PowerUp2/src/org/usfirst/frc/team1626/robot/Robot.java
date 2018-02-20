package org.usfirst.frc.team1626.robot;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.usfirst.frc.team1626.robot.Robot.Gear;

@SuppressWarnings("unused")
public class Robot extends IterativeRobot {

	enum Gear {HIGH_GEAR, LOW_GEAR, UNKNOWN};
	enum ArmCmd {UP, LEVEL, DOWN};

	private XboxController xbox;
	private Joystick driverLeft;
	private Joystick driverRight;
	
	Toggle driveGear;
	Toggle backwards;
	private DoubleSolenoid driveTrainShifter;
	private DoubleSolenoid elevatorBrake;
	
	private SpeedController frontLeftSpeed;
	private SpeedController frontRightSpeed;
	private SpeedController backLeftSpeed;
	private SpeedController backRightSpeed;
	private WPI_TalonSRX frontElevator;
	private WPI_TalonSRX backElevator;
	private Spark Elevator;
	
	private SpeedControllerGroup leftSpeed;
	private SpeedControllerGroup rightSpeed;
	
	private DifferentialDrive drive;
	
	int autoLoopCounter;
	ActionRecorder actions;
	private Thread autoThread;

	private TalonSRX inOutMotor0;
	private TalonSRX inOutMotor1;
	
	private Spark stowMotor;
	
	Toggle doMotorBreakIn = new Toggle();
	
	private SpeedController driveMotor0;
	private SpeedController driveMotor1;
	private SpeedController driveMotor2;
	private SpeedController driveMotor3;
	private SpeedControllerGroup driveMotors;
	
	private ControlMode Current;

	public String gameData;
	
	private int startingPosition = 1;
	
	private double maxPower = 1;	//maximum power of the wheels as a fraction of maximum power
	private int smoothing = 3;		//the power to raise the input to to smooth out acceleration
	
	private Compressor compressor;
	private SpeedControllerGroup intakeMotors;
	private int prevPOV;
	private String autoScaleEnd;
	private String autoSwitchEnd;
	private String selector;
	private double previousElevator;
	private Servo armLimitServo;
	
	private ArmCmd armCurrent;
	private ArmCmd armGoal;
	private AnalogInput pressureSensor;

	
	
	@Override	
	public void robotInit() {
		
		System.err.println("Starting the Power Up Robot");
		
        CameraServer.getInstance().startAutomaticCapture();
		
		driverLeft = new Joystick(0);
		driverRight = new Joystick(1);
		xbox = new XboxController(2);

		System.out.println("initializing actions...");
		actions = new ActionRecorder().
				setMethod(this, "robotOperation", DriverInput.class).
				setUpButton(xbox, 1).
				setDownButton(xbox, 2).
				setRecordButton(xbox, 3);

		System.out.println("initializing buttons...");
		
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
		DriverInput.nameInput("Operator-DPad");
		DriverInput.nameInput("Driver-Left-8");

		frontLeftSpeed		= new WPI_TalonSRX(14);
		backLeftSpeed		= new WPI_TalonSRX(15);
		frontRightSpeed		= new WPI_TalonSRX(16);
		backRightSpeed		= new WPI_TalonSRX(1);
		frontElevator		= new WPI_TalonSRX(2);
		backElevator		= new WPI_TalonSRX(3);
		inOutMotor0			= new TalonSRX(8);
		inOutMotor1			= new TalonSRX(11);
		
		Elevator			= new Spark(9);
		stowMotor			= new Spark(0);
		
		inOutMotor1.setInverted(true);
		frontElevator.follow(backElevator);

		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
		
		drive = new DifferentialDrive(leftSpeed, rightSpeed);
		
		System.out.println("Auto Files are:\n" + actions.returnFiles());
		
		compressor = new Compressor();
		pressureSensor = new AnalogInput(0);
		
		driveGear = new Toggle();
		driveTrainShifter       = new DoubleSolenoid(0, 1);
		elevatorBrake			= new DoubleSolenoid(2, 3);
		
		armLimitServo = new Servo(1);
		backwards = new Toggle();
		
//		backElevator.set
		double value = 1; // 1-on, 0-off
		backElevator.configSetParameter(ParamEnum.eClearPositionOnQuadIdx, value, 0x00, 0x00, 10);
		backElevator.configSetParameter(ParamEnum.eClearPositionOnLimitF, value, 0x00, 0x00, 10);
		backElevator.configSetParameter(ParamEnum.eClearPositionOnLimitR, value, 0x00, 0x00, 10);
		
		driveTrainShifter.set(DoubleSolenoid.Value.kForward);
		SmartDashboard.putBoolean("DB/LED 3", false);
		
		elevatorBrake.set(DoubleSolenoid.Value.kForward);

	}
	
	@Override
	public void robotPeriodic() {
		double pressure = (250.0 * (pressureSensor.getVoltage() / 5.0)) - 25;
		SmartDashboard.putString("DB/String 4", String.format("%.1f", pressure+1));

	}

	@Override
	public void autonomousInit() {
		
		autoLoopCounter = 0;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
//		startingPosition = DriverStation.getInstance().getLocation();
//		String[] s = gameData.split("");                 				 //alternate parsing method - Brad
//		Character allianceSwitch = s[0].toCharArray()[0];
//		Character scale = s[1].toCharArray()[0];
//		Character oppositeSwitch = s[2].toCharArray()[0];
//		File auto = actions.findAutoFile("nothing.csv");
//		switch(startingPosition){
//			case 0:
//				if(allianceSwitch == 'L'){
//					if(scale == 'L'){
//						SmartDashboard.putString("DB/String 0", "new");
//						auto = actions.findAutoFile("new.csv");
//					} else {
//						SmartDashboard.putString("DB/String 0", "otherone.csv");
//						auto = actions.findAutoFile("otherone.csv");
//					}
//				} else {
//					if(scale == 'R'){
//						SmartDashboard.putString("DB/String 0", "nothing.csv");
//						auto = actions.findAutoFile("nothing.csv");
//					} else {
//						SmartDashboard.putString("DB/String 0", "brad.csv");
//						auto = actions.findAutoFile("brad.csv");
//					}
//				}
//				break;
//			case 1:
//				if(allianceSwitch == 'L'){
//					if(scale == 'L'){
//						
//					} else {
//						
//					}
//				} else {
//					if(scale == 'R'){
//						
//					} else {
//						
//					}
//				}
//				break;
//			case 2:
//				if(allianceSwitch == 'L'){
//					if(scale == 'L'){
//						
//					} else {
//						
//					}
//				} else {
//					if(scale == 'R'){
//						
//					} else {
//						
//					}
//				}
//				break;
//		}

		actions.autonomousInit(gameData.substring(0,2));
	
	}

	@Override
	public void autonomousPeriodic() {
		
		String itemLocationString = gameData.substring(0, 2) + startingPosition;
		try{
			
			if (actions != null) actions.longPlayback(this, -1);
			else Timer.delay(0.010);
				
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
					.withInput("Operator-Start-Button",	xbox.getRawButton(8))
					.withInput("Operator-Back-Button",	xbox.getRawButton(7))
					.withInput("Elevator-Back",  		xbox.getTriggerAxis(Hand.kLeft))	//mapped elevator down to left trigger
					.withInput("Elevator-Forward",		xbox.getTriggerAxis(Hand.kRight))	//and up to right trigger -Brad
					.withInput("Operator-DPad",			xbox.getPOV())
					.withInput("Driver-Left", 			driverLeft.getRawAxis(1))
					.withInput("Driver-Right", 			driverRight.getRawAxis(1))
					.withInput("Driver-Left-Trigger", 	driverLeft.getRawButton(1))
					.withInput("Driver-Right-Trigger", 	driverRight.getRawButton(1))
					.withInput("Operator-Left-Bumper",	xbox.getBumper(Hand.kLeft))
					.withInput("Operator-Right-Bumper", xbox.getBumper(Hand.kRight))
					.withInput("Driver-Left-8", 		driverLeft.getRawButton(8))
					);
					
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}

	@Override public void testPeriodic() {}
	
	@Override
	public void disabledInit() {
		
		actions.disabledInit();
		prevPOV=Integer.MIN_VALUE;
		autoSwitchEnd="L";
		autoScaleEnd="L";
		selector="LL";
	
	}


	@Override
	public void disabledPeriodic() {
		int curPOV = xbox.getPOV();
		if (curPOV != prevPOV) {
			
			switch (curPOV) {
			case 0:
				autoScaleEnd="L";
				break;
				
			case 90:
				autoSwitchEnd="R";
				break;
				
			case 180:
				autoScaleEnd="R";
				break;
				
			case 270:
				autoSwitchEnd="L";
				break;
			}
			prevPOV=curPOV;
			selector = autoSwitchEnd+autoScaleEnd;
			SmartDashboard.putString("DB/String 5", selector);
		}
		actions.disabledPeriodic(selector);
	
	}
	
	private void shiftTo(Gear speed) {
		
		if (speed == Gear.HIGH_GEAR) {
			driveTrainShifter.set(DoubleSolenoid.Value.kReverse);
			SmartDashboard.putBoolean("DB/LED 3", true);
		}
		if (speed == Gear.LOW_GEAR) {
			driveTrainShifter.set(DoubleSolenoid.Value.kForward);
			SmartDashboard.putBoolean("DB/LED 3", false);
		}
		
	}

	public void robotOperation(DriverInput input) {
		
		SmartDashboard.putString("DB/String 1", "" + gameData + startingPosition);
		
		double leftAxis = input.getAxis("Driver-Left");
		double rightAxis = input.getAxis("Driver-Right");
		leftAxis = Math.abs(Math.pow(leftAxis, smoothing-1)) * leftAxis/Math.abs(leftAxis);
		rightAxis = Math.abs(Math.pow(rightAxis, smoothing-1)) * rightAxis/Math.abs(rightAxis);
		

		boolean shift = (input.getButton("Driver-Right-Trigger") || input.getButton("Driver-Left-Trigger"));
		
		driveGear.input(shift);
		if (driveGear.getState()) shiftTo(Gear.HIGH_GEAR);
		else shiftTo(Gear.LOW_GEAR);
		
		backwards.input(input.getButton("Driver-Left-8"));
		SmartDashboard.putBoolean("DB/LED 1", backwards.getState());
		if (!backwards.getState()) drive.tankDrive(-1 * maxPower * leftAxis, -1 * maxPower * rightAxis, false);
		else drive.tankDrive(maxPower * rightAxis, maxPower * leftAxis, false);
		
//		if(!backwards){
//			drive.tankDrive(-1 * maxPower * leftAxis, -1 * maxPower * rightAxis, false);
//		} else {
//			drive.tankDrive(maxPower * rightAxis, maxPower * leftAxis, false);
//		}
		double elevatorAxis = input.getAxis("Elevator-Forward") - input.getAxis("Elevator-Back");
		

		if (Math.abs(elevatorAxis) > 0.10)
		{
			if (Math.abs(previousElevator) < 0.10)
			{
				elevatorBrake.set (Value.kForward);
			}
			else
			{
//				frontElevator.set(ControlMode.PercentOutput, elevatorAxis);
				backElevator.set(ControlMode.PercentOutput, elevatorAxis);	
			}
		}
		else
		{
			elevatorBrake.set(Value.kReverse);
//			frontElevator.set(0);
			backElevator.set(ControlMode.PercentOutput, 0);
		}
		previousElevator = elevatorAxis;

		int dpadAxis = (int) input.getAxis("Operator-DPad");
		switch(dpadAxis){
		case 0:
			backElevator.set(ControlMode.Position, 0);
			break;
		case 90:
			backElevator.set(ControlMode.Position, 100);
			break;
		case 180:
			backElevator.set(ControlMode.Position, 200);
			break;
		case 270:
			backElevator.set(ControlMode.Position, 300);
			break;
		}
		
		
		SmartDashboard.putString("DB/String 6", "" + backElevator.getSelectedSensorPosition(0));

		if (input.getButton("Operator-X-Button")) {
			inOutMotor0.set(ControlMode.PercentOutput, .99);
			inOutMotor1.set(ControlMode.PercentOutput, -.99);
		} else if (input.getButton("Operator-A-Button")) {
			inOutMotor0.set(ControlMode.PercentOutput, -.50);
			inOutMotor1.set(ControlMode.PercentOutput, .50);
		} else if (input.getButton("Operator-B-Button")) {
			inOutMotor0.set(ControlMode.PercentOutput, -.99);
			inOutMotor1.set(ControlMode.PercentOutput, .99);
		} else {
			inOutMotor0.set(ControlMode.PercentOutput, 0);			// You have forgotten this clause more than once.
			inOutMotor1.set(ControlMode.PercentOutput, 0);			// Without this the motors never stop once they start.
		}
		
		if (input.getButton("Operator-Left-Bumper")) stowMotor.set(.40);
		else if (input.getButton("Operator-Right-Bumper")) stowMotor.set(-.75);
		else stowMotor.set(0.0);
		
		if (input.getButton("Operator-Back-Button"))		// Release arm stop
		{
			armLimitServo.set(.2);
		}
		if (input.getButton("Operator-Start-Button")) 		// Set arm stop
		{
			armLimitServo.set (.57);
		}

	}
	
	/*
	 * This is a placeholder for a more automated way to control the arm limit servo.
	 */
	private void armLimit() {
		switch (armGoal) {
		case UP:
			break;
			
		case LEVEL:
			break;
			
		case DOWN:
			break;
		}

	}

}
