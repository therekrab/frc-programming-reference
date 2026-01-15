package frc.robot.subsystems.elevator;

import com.ctre.phoenix6.BaseStatusSignal;
/* snip */

public class ElevatorIOHardware implements ElevatorIO {
  private final TalonFX leftMotor;
  private final TalonFX rightMotor;

  private final CANrange CANrange;
  private final SoftwareLimitSwitchConfigs noLimits = new SoftwareLimitSwitchConfigs()
      .withForwardSoftLimitEnable(false)
      .withReverseSoftLimitEnable(false);

  private final DynamicMotionMagicVoltage control;

  private double reference = Double.NaN;

  private final StatusSignal<Voltage> leftVoltageSignal;
  private final StatusSignal<Voltage> rightVoltageSignal;
  private final StatusSignal<Current> leftCurrentSignal;
  private final StatusSignal<Current> rightCurrentSignal;
  private final StatusSignal<Temperature> leftTempSignal;
  private final StatusSignal<Temperature> rightTempSignal;
  private final StatusSignal<AngularVelocity> leftVelocitySignal;
  private final StatusSignal<AngularVelocity> rightVelocitySignal;
  private final StatusSignal<Angle> leftPositionSignal;
  private final StatusSignal<Angle> rightPositionSignal;

  private final StatusSignal<Distance> CANrangeDistanceSignal;
  private final StatusSignal<Boolean> CANrangeDetectedSignal;
  private final StatusSignal<Double> CANrangeStrengthSignal;

  public ElevatorIOHardware() {
    rightMotor = new TalonFX(ElevatorConstants.kRightMotorID, "*");
    leftMotor = new TalonFX(ElevatorConstants.kLeftMotorID, "*");
    rightMotor.getConfigurator().apply(ElevatorConstants.kMotorConfig);
    leftMotor.getConfigurator().apply(ElevatorConstants.kMotorConfig);
    leftMotor
        .setControl(new Follower(ElevatorConstants.kRightMotorID, ElevatorConstants.kInvertLeft));
    rightMotor.setPosition(0.0);
    leftMotor.setPosition(0.0);

    CANrange = new CANrange(ElevatorConstants.kCANrangeID);
    CANrange.getConfigurator().apply(ElevatorConstants.kCANrangeConfig);

    control = new DynamicMotionMagicVoltage(
        0, // no position setpoint yet
        ElevatorConstants.kMaxSpeed,
        ElevatorConstants.kMaxAcceleration,
        ElevatorConstants.kMaxJerk);

    leftVoltageSignal = leftMotor.getMotorVoltage();
    rightVoltageSignal = rightMotor.getMotorVoltage();
    leftCurrentSignal = leftMotor.getSupplyCurrent();
    rightCurrentSignal = rightMotor.getSupplyCurrent();
    leftTempSignal = leftMotor.getDeviceTemp();
    rightTempSignal = rightMotor.getDeviceTemp();
    leftVelocitySignal = leftMotor.getVelocity();
    rightVelocitySignal = rightMotor.getVelocity();
    leftPositionSignal = leftMotor.getPosition();
    rightPositionSignal = rightMotor.getPosition();

    CANrangeDetectedSignal = CANrange.getIsDetected();
    CANrangeDistanceSignal = CANrange.getDistance();
    CANrangeStrengthSignal = CANrange.getSignalStrength();

    StatusSignalUtil.registerCANivoreSignals(
        leftVoltageSignal,
        rightVoltageSignal,
        leftCurrentSignal,
        rightCurrentSignal,
        leftTempSignal,
        rightTempSignal,
        leftVelocitySignal,
        rightVelocitySignal,
        leftPositionSignal,
        rightPositionSignal);
    StatusSignalUtil.registerRioSignals(
        CANrangeDetectedSignal,
        CANrangeDistanceSignal,
        CANrangeStrengthSignal);
  }

  public void updateInputs(ElevatorIOInputs inputs) {
    inputs.leftMotorConnected = BaseStatusSignal.isAllGood(
        leftVoltageSignal,
        leftCurrentSignal,
        leftTempSignal,
        leftVelocitySignal,
        leftPositionSignal);
    inputs.rightMotorConnected = BaseStatusSignal.isAllGood(
        rightVoltageSignal,
        rightCurrentSignal,
        rightTempSignal,
        rightVelocitySignal,
        rightPositionSignal);
    inputs.leftVoltage = leftVoltageSignal.getValueAsDouble();
    inputs.rightVoltage = rightVoltageSignal.getValueAsDouble();
    inputs.leftCurrent = leftCurrentSignal.getValueAsDouble();
    inputs.rightCurrent = rightCurrentSignal.getValueAsDouble();
    inputs.leftTemp = leftTempSignal.getValueAsDouble();
    inputs.rightTemp = rightTempSignal.getValueAsDouble();
    inputs.leftVelocityRPS = leftVelocitySignal.getValueAsDouble();
    inputs.rightVelocityRPS = rightVelocitySignal.getValueAsDouble();
    inputs.leftPosition = leftPositionSignal.getValueAsDouble();
    inputs.rightPosition = rightPositionSignal.getValueAsDouble();
    inputs.position = inputs.rightPosition;

    inputs.reference = reference;

    inputs.zeroCANrangeConnected = BaseStatusSignal.isAllGood(
        CANrangeDetectedSignal,
        CANrangeDistanceSignal,
        CANrangeStrengthSignal);
    inputs.zeroCANrangeDetected = CANrangeDetectedSignal.getValue();
    inputs.zeroCANrangeDistance = CANrangeDistanceSignal.getValueAsDouble();
    inputs.zeroCANrangeStrength = CANrangeStrengthSignal.getValueAsDouble();
  }

  public void setPosition(double reference) {
    rightMotor.setControl(control.withPosition(reference));
    this.reference = reference;
  }

  public void setVoltage(double voltage) {
    rightMotor.setVoltage(voltage);
  }

  public void enableLimits() {
    rightMotor.getConfigurator().apply(ElevatorConstants.kMotorConfig.SoftwareLimitSwitch);
    leftMotor.getConfigurator().apply(ElevatorConstants.kMotorConfig.SoftwareLimitSwitch);
  }

  public void disableLimits() {
    rightMotor.getConfigurator().apply(noLimits);
    leftMotor.getConfigurator().apply(noLimits);
  }

  public void calibrateZero() {
    rightMotor.setPosition(0.0);
    leftMotor.setPosition(0.0);
  }
}
