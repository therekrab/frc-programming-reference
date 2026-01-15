// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Seconds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.CoralLevel;
import frc.robot.Robot;
import frc.robot.RobotObserver;
import frc.robot.subsystems.PassiveSubsystem;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOInputs;
import frc.robot.utils.LoopTimer;
import frc.robot.utils.OnboardLogger;

public class Elevator extends PassiveSubsystem {
  private final ElevatorIO io;
  private final ElevatorIOInputs inputs;

  private final OnboardLogger ologger;

  private final Debouncer debouncer = new Debouncer(ElevatorConstants.kRangeDebounceTime.in(Seconds));

  public ElevatorState reference = ElevatorState.Stow;

  public Elevator() {
    super();
    if (Robot.isReal()) {
      io = new ElevatorIOHardware();
    } else {
      io = new ElevatorIOSim();
    }
    inputs = new ElevatorIOInputs();
  }

  private void setPosition(ElevatorState state) {
    // calculate goal we should go to
    double goal = state.position;
    // floor values for the goal between our two extrema
    goal = Math.min(goal, ElevatorConstants.kForwardSoftLimit);
    goal = Math.max(goal, ElevatorConstants.kReverseSoftLimit);
    io.setPosition(goal);
    reference = state;
  }

  public Trigger atSetpoint() {
    return new Trigger(
        () -> Math.abs(reference.position - inputs.position) < ElevatorConstants.kTolerance);
  }

  public Trigger ready(ElevatorState state) {
    return atSetpoint().and(new Trigger(() -> reference.equals(state)));
  }

  private boolean atZero() {
    return debouncer.calculate(inputs.zeroCANrangeDetected);
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
  }

  /**
   * Whether or not the elevator is above the "safe" range
   */
  public Trigger unsafe() {
    return new Trigger(() -> inputs.position >= ElevatorConstants.kUnsafeRange
        || reference.position >= ElevatorConstants.kUnsafeRange);
  }

  public Command go(ElevatorState state) {
    return Commands.sequence(
        runOnce(() -> setPosition(state)),
        Commands.waitUntil(atSetpoint()))
        .withName("Elevator(" + state.toString() + ")");
  }

  /**
   * Automatically zeroes the elevator.
   */
  public Command autoZero() {
    return Commands.waitUntil(this::atZero).deadlineFor(
        Commands.sequence(
            go(ElevatorState.Zero),
            runOnce(io::disableLimits),
            runOnce(() -> io.setVoltage(ElevatorConstants.kZeroVoltage))))

        .finallyDo(io::enableLimits)
        .finallyDo(interrupted -> {
          if (!interrupted) {
            io.calibrateZero();
          }
        })
        .withName("Autozero");
  }
}
