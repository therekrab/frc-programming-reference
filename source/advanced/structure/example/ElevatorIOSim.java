package frc.robot.subsystems.elevator;

public class ElevatorIOSim implements ElevatorIO {
  private double position = 0;
  private double reference = 0;

  public void updateInputs(ElevatorIOInputs inputs) {
    position = 0.9 * position + 0.1 * reference;
    inputs.position = position;
    inputs.reference = reference;
    inputs.zeroCANrangeDetected = position < 1e-3;
  }

  public void setPosition(double position) {
    reference = position;
  }

  public void setVoltage(double voltage) {}
  
  public void enableLimits() {}
  public void disableLimits() {}

  public void calibrateZero() {}

}
