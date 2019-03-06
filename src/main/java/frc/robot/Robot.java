/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
public class Robot extends TimedRobot {
  // wscript "C:\\Users\\Public\\frc2019\\tools\\SmartDashboard.vbs"
  private static final int kFrontLeftChannel = 0;
  private static final int kRearLeftChannel = 1;
  private static final int kFrontRightChannel = 2;
  private static final int kRearRightChannel = 3;

  private static final int kJoystickChannel = 0;
  private static final int kBoardStickChannel = 1;
  private static final int kControlStickChannel = 2;

  private MecanumDrive m_robotDrive;
  private Joystick ppStick;
  private Joystick boardStick;
  private Joystick controlStick;

  private Compressor compressor;

  Solenoid solenoid1;
  Solenoid solenoid2;
  Solenoid solenoid3;
  Solenoid solenoid4;
  Solenoid solenoid5;
  Solenoid solenoid6;

  VictorSP rightArmMotor;
  Spark leftArmMotor;
  VictorSP intakeMotor;

  // Camera
  UsbCamera backCamera;
  UsbCamera frontCamera;


  @Override
  public void robotInit() {
    // Drivetrain
    VictorSP frontLeft = new VictorSP(kFrontLeftChannel);
    VictorSP rearLeft = new VictorSP(kRearLeftChannel);
    VictorSP frontRight = new VictorSP(kFrontRightChannel);
    VictorSP rearRight = new VictorSP(kRearRightChannel);
    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

    // Arm/Intake
    rightArmMotor = new VictorSP(7);
    leftArmMotor = new Spark(5);
    intakeMotor = new VictorSP(4);

    
    // Joysticks
    ppStick = new Joystick(kJoystickChannel);
    boardStick = new Joystick(kBoardStickChannel);
    controlStick = new Joystick(kControlStickChannel);

    // Pneumatics
    solenoid1 = new Solenoid(0);
    solenoid2 = new Solenoid(1);
    solenoid3 = new Solenoid(2);
    solenoid4 = new Solenoid(3);
    solenoid5 = new Solenoid(4);
    solenoid6 = new Solenoid(5);

    compressor = new Compressor(0);

    // Camera
    frontCamera = CameraServer.getInstance().startAutomaticCapture();
    backCamera = CameraServer.getInstance().startAutomaticCapture();
  }

  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();
  }

  @Override
  public void teleopPeriodic() {
  
    // Driving
    double slider = -ppStick.getRawAxis(3);
    double maxSpeed = (slider + 1) / 2;
    
    if (maxSpeed < .25) {
      maxSpeed = .25;
    }
    m_robotDrive.setMaxOutput(maxSpeed);
    SmartDashboard.putNumber("Speed: ", maxSpeed);

    // PPstick and controlstick
    m_robotDrive.driveCartesian(controlStick.getY(), -ppStick.getY(), ppStick.getZ(), 0.0);

    // controlstick and boardstick
    //m_robotDrive.driveCartesian(controlStick.getY(), -boardStick.getY(), boardStick.getX(), 0.0);

    
    // Pneumatics

    compressor.setClosedLoopControl(false);

    if (ppStick.getRawButton(1)) {
      solenoid3.set(false);
      solenoid4.set(true);

    }
    if (ppStick.getRawButton(2)) {
      solenoid4.set(false);
      solenoid3.set(true);
    }
    //0,1 / 1,2

    if(ppStick.getRawButton(3)){
      solenoid1.set(false);
      solenoid2.set(true);
    }

    if(ppStick.getRawButton(4)){
      solenoid2.set(false);
      solenoid1.set(true);
    }

    if(ppStick.getRawButton(5)){
      solenoid5.set(false);
      solenoid6.set(true);
    }

    if(ppStick.getRawButton(6)){
      solenoid6.set(false);
      solenoid5.set(true);
    }
    
    // Intake
    while(boardStick.getRawButton(11))
    {
      intakeMotor.set(0.8);
    }

    if(boardStick.getRawButton(11) != true && boardStick.getRawButton(12) != true)
    {
      intakeMotor.set(0);
    }

    while(boardStick.getRawButton(12))

    {
      intakeMotor.set(-0.6);
    }

    // Other
    if(boardStick.getRawButton(9))
    {
      spin();
    }

  }

  void spin()
  {
    // Change spinTime to adjust speed duration
    double spinTime = 1.9;
    Timer timer = new Timer();
    m_robotDrive.setMaxOutput(0.3);
    timer.start();
    while(timer.get() < spinTime)
    {
      m_robotDrive.driveCartesian(0.0, 0.0, 1, 0.0);
    }
  }
}
