2.4.8 - Default Commands
========================

When a command is interrupted, it will *not* be rescheduled automatically. But
what if we want it to?

Let's consider an example where this is a good idea. Consider two commands: one
is the regular teleop drive command which tells the drivetrain to drive
according to the driver inputs, and another command which automatically drives
to a scoring location. Both of these commands require the drivetrain.

Let's now consider the following driver inputs:

1. Driver drives manually to a location near the scoring area.

2. Driver presses button to run the auto-align command

3. Robot aligns and alignment command ends.

4. Driver no longer can drive because the drive command hasn't restarted.

This is clearly a problem. Let's consider another situation.

Pretend we've got an intake subsystem and we want the intake to just
automatically intake whenever the intake isn't doing anything. This means that
we don't have to have a button for intake. Less buttons is good and helps the
driver.

However, we can't ensure that the "autointake" command continues running after
some other command requires the intake - perhaps our intake also is used to
transport the gamepiece to some scoring mechanism.

This is where the idea of a **default command** comes in.

What's a default command?
-------------------------

A default command is a regular command that has been registered differently
than a normal command would be.

Instead of being scheduled by something like a trigger press, a default command
is coupled with a subsystem that it requires. Whenever the subsystem is
:term:`uncommanded`, the default command will be scheduled.

As per the rules of regular commands, if another command demands requirements
on the subsystem as well, the default command will get interrupted.

.. note:: Default commands should not have end conditions. If they do, the
   subsystem will become uncommanded and its default command will just restart.

Creating a default command
--------------------------

Default commands are created just like any other command. To set a command
``defaultCommand`` to be the default command on some subsystem
``someSubsystem``, it's as easy as this:

.. code-block:: java

   someSubsystem.setDefaultCommand(defaultCommand)

Now ``defaultCommand`` will be scheduled whenever the subsystem becomes
uncommanded.

Note that you will get an error if ``defaultCommand`` does not require
``someSubsystem``.

.. danger:: Under no circumstances set a default command that requires another
   subsystem besides what it's registered for. This is technically legal, but
   can lead to really bad behavior because the default command, when started,
   will end other commands that want the other subsystem.
