2.4.2 - Interrupting Commands
=============================

One advantage of using the WPILib commands library is its powerful ability to
avoid :term:`undefined behavior` by creating a resource-management system to make sure that
multiple commands can't concurrently access the same subsystem. Imagine telling
an elevator to go up and down at the same time. What would happen? We can't
determine without more information.

Commands should declare what subsystems they "require", creating a list of
**requirements**. Then, when a command is scheduled, the scheduler looks for
other commands that use *any* of the subsystems that this command requires.
This gives the scheduler a list of any commands that cannot run at the same
time as the newly scheduled command. The scheduler *must* interrupt a command -
but which one?

There are two options:

1. Cancel the already-running command to allow the new command to take over.

2. Cancel the incoming command so that the old one can keep going.

Both of these have their advantages, but the default option is to cancel the
present command to allow incoming commands to be scheduled. We can change a
command's :term:`interruption behavior` to modify how a command behaves when it
is interrupted (e.g. ``someCommand.withInterruptBehavior(behavior)`` where
``behavior`` is a variant of the ``InterruptBehavior`` enum).

When a command is cancelled due to requirement conflicts, it interrupts the
command and it is unscheduled.

Example
-------

Let's say we have two buttons that raise and lower the elevator. If we press
one button to raise the elevator, and then press the second button (to lower
it) while the elevator is still moving, the old command (raise) gets
interrupted and unscheduled. The new command will now take the elevator down,
and when that command finishes, the elevator will be uncommanded.

It's important to remember that commands that are interrupted will not be
rescheduled.

Other ways to interrupt commands
--------------------------------

You can also interrupt commands by disabling the robot. Disabling the robot
already turns off the ability to apply voltage to motors on a hardware level,
so the fact that it also cancels commands is in order to ensure that the robot
is not re-enabled and it springs to life.

However, you can change whether the command is interrupted on disable with
``someCommand.ignoringDisabled(true)``.

Commands can also be cancelled with their ``cancel()`` method.

