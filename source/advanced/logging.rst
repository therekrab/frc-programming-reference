Logging
=======

Logging data is an important part of finding and fixing bugs. Done correctly,
it lets us completely determine the state of the robot at some time, and we can
see if what the robot saw is what we expected.

What to log
-----------

Device Inputs
~~~~~~~~~~~~~

Anything that the robot reads from a sensor 100% *must* be logged. This is
invaluable to diagnose issues in a match, or to catch a broken device.

If the robot uses it to make a decision, *log it*! This includes joystick inputs
and button presses.

Device Status
~~~~~~~~~~~~~

There's a lot of other data that we may not directly use in code, but should
still be captured and logged. For example, if a motor stops working, we want to
know why. It could be overheating, a CAN disconnect, or some other reason.

But, for most of the robot code, we don't actually read the device temperature
or connection status.

That doesn't mean we shouldn't log those fields. Other fields that aren't
always used, but should be logged include:

- Motor voltage

- Current draw

- Motor velocity

This data helps diagnose failure modes and understand what happened.

.. tip:: Especially for vision-related applications, *always* log the
   connection status of the cameras.

Robot status
~~~~~~~~~~~~

Logging important robot information, such as battery voltage, command scheduler
loop times, and maybe even loop times for each subsystem's ``periodic()`` calls.

Logging loop times
~~~~~~~~~~~~~~~~~~

You can calculate loop times by using a simple ``Timer`` from WPILib. Call
``restart()`` at the top of ``periodic()``, and then get the elapsed time with
``get()``.

You can also use this in ``robotPeriodic()`` to log the amount of time the
robot's periodic method takes to execute.

This is helpful to make sure your code isn't causing performance issues and
loop overruns.

.. note:: Loop overruns every now and then aren't a big problem. You can just
   about ignore small overruns provided they occur relatively infrequently.

How to log data
---------------

There's a variety of ways to log data. Tools like Advantage Kit are highly
powerful tools, but some teams don't use all their features and would be better
to use easier and more friendly tools like DogLog, Epilogue, or Monologue.

.. hint:: If you want to have fun with it, read about WPILib's ``DataLogger``
   and implement your own logger. It's really fun and gives a good overview of
   how logging works. It also ensures that your logging is fully customizable.

Logging to a USB stick
~~~~~~~~~~~~~~~~~~~~~~

Logging to a USB stick is the way to go. You don't need to worry about storage
on the robot, nor does the robot need to be on to extract logs. You can swap
USB sticks every other match, and review logs from one match while the robot
plays.

Read the documentation for whatever logger you choose to see how to ensure logs
go to a USB drive.

Logging and Simulation
----------------------

Logging in simulation is also a *must*. Trying to watch tons of numbers in real
time as you simulate a robot is a bad idea, and can lead to missing important
errors. Instead, simulate, then load the logs for the simulator into your log
viewing program (Advantage Scope is excellent). From there, you can make sure
your logs show the robot doing exactly what you thing it should do.
