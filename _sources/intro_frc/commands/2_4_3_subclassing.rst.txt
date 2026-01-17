2.4.3 - Subclassing Commands
============================

As mentioned in :doc:`2_4_1_structure`, you can declare classes that extend ``Command`` in WPILib
and override the four major methods. Let's put together a command that intakes
a gamepiece using a pivoting floor intake. Here's the steps that we need to do
in order to ensure that we pick up a gamepiece. For now, assume that subsystems
have any methods that are shown here:

.. code-block:: java
   :caption: MyCommand.java

   /* imports not shown */

   public class MyCommand extends Command {
     private Intake intake;
     private boolean setIntakeSpeed;

     public MyCommand(Intake intake) {
       this.intake = intake;
       addRequirements(intake); // <-- note this line!
     }

     @Override
     public void initialize() {
       intake.setIntakePosition();
       setIntakeSpeed = false;
     }

     @Override
     public void execute() {
       if (!intake.atPosition()) {
         // waiting for intake
         return;
       }
       if (setIntakeSpeed) {
         // We've already set the speed, so there's no reason to set it again
         return;
       }
       intake.setIntakeSpeed();
       setIntakeSpeed = true;
     }

     @Override
     public void end(boolean interrupted) {
       intake.stop();
       intake.setStowPosition();
     }

     @Override
     public boolean isFinished() {
       return intake.hasPiece();
     }
   }

Ignoring the fact that this is very verbose and overly imperative, this is a
pretty simple command. Let's walk through how the command works.

1. When the command is initialized, we tell the intake to set its position
   setpoint to the intake setpoint. This does *not* directly set the motor
   position - that takes time. Instead, it sets the motor's :term:`reference`.

   ``initialize()`` also sets the value of the boolean ``setIntakeSpeed`` to
   ``false``. Later, we don't want to reapply the *exact* same speed multiple
   times, so we only call it once.

2. When the command is scheduled, ``execute()`` is called periodically. In this
   method, we first check if the intake is at its position (which is the intake
   state we set from ``initialize()``). If we're not there yet, this method is
   done.

   If we *are* at the correct setpoint, then we look at whether we've already
   applied the speed request. If we have applied the speed already, we stop.
   This is because we don't need to reapply it.

   If we *have not* already applied the speed, we set the intake's speed and we
   change the value of ``setIntakeSpeed`` to ``true`` to reflect that we have
   now set the correct speed.

3. The command ends naturally when the intake believes it has a piece. This is
   shown in the ``isFinished()`` method.

4. When the command is interrupted, we don't really care whether it was
   interrupted. We still have to match the function signature, however, so we
   keep the argument.

   We tell the intake to stop intaking a piece - we don't want to overshoot
   whatever mechanism we use to run the intake. We also tell the intake to set
   its new :term:`reference` to the stow position.

This is a relatively simply command that shows how you can subclass the
``Command`` class to write commands that perform actions on the robot.

However, it is important to notice that this has a variety of cons:

- This is a very heavy approach. Imagine if our intake system required us to
  perform a third step. We'd need to keep track of whether or not we'd done the
  second step (set speed) *and* the third step to avoid duplicate writes. This
  logic gets larger faster than it needs to.

- This is a longer file for something so simple as just performing a few easy
  steps.

- This doesn't do a good job of conveying *what* the command is doing. When we
  discussed subsystems, we said it was important to make sure that the *intent*
  of the code is clear. That's still true here, and this method of subclassing
  commands is *not* very :term:`declarative`.
