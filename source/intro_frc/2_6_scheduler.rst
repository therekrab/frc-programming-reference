2.6 - Command Scheduler
=======================

.. important:: It's generally inadvisable to mess with the scheduler yourself.
   Most of the features that you need are already implemented in a higher-level
   interface.

In ``Robot.java``, the ``robotPeriodic()`` method is called every robot cycle.
This is where all commands are ran through the command scheduler.

.. code-block:: java
   :caption: Robot.java

   @Override
   public void robotPeriodic() {
     CommandScheduler.getInstance().run();
   }

A single call to run does the following thigns in order:

1. Subsystem ``periodic()`` methods are called.

2. ``Trigger``\s are polled, and new commands are scheduled from them.

3. Currently scheduled commands (not including the ones that were just
   scheduled from #2) are executed.

4. End conditions are checked on currently scheduled commands, and commands
   that ended have their ``end()`` methods called.

5. Any subsystem without requirements on them have their default commands
   started.
