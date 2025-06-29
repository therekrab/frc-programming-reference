The ``Robot`` class
===================

The entrypoint to the robot program is the ``Robot`` class.

The ``Robot`` class is effectively the bridge between regular control flow on a
robot scale and the command interface that is easier to deal with.

Timed Robot
-----------

The most basic robot may look like this:

.. code-block:: java

   while (true) {
     doRobotActions();
   }

If this was the default implementation of the robot, and all the user had to do
was specify the meaning of ``doRobotActions()``, then the user's code would be
repeated over and over as fast as possible.

In this case, ``doRobotActions()`` is a function that runs all robot-related
operations. This code can look simple, but it hides a variety of strange
problems that are hard to fix without hacked-together solutions.

The issue comes from the fact that we don't really know how often this will
run. Do we want to run code more frequently when we do less processing? No!
Instead, we want to specify a cycle time. The code may look like this:

.. code-block:: java

   while(true) {
     doRobotActions();
     clock.tick(20.0); // ms
   }

The method ``clock.tick()`` (just an example method; not real) blocks program
execution until it has been 20ms since the last call. This means that if
``doRobotActions()`` takes 5ms to run, the line ``clock.tick()`` will last
15ms.

This provides consistent running speeds, regardless of *what* the program is
doing. This is also why command scheduler loop overruns are a common concern.
If your code takes too long to run (> 20ms), then you will not only miss out on
faster update cycles (like a laggy video), but you could miss important data.

Instead of thinking of the robot's cycles as watching a video, think of the
cycles like watching live TV. If you miss a frame or two, *that's it*. You
can't go back in time and see what you missed. If the robot takes too long to
process its inputs, then it may miss changes in those inputs while it's
processing. This results in a command scheduler loop overrun warning on the
driver station.

.. note:: The actual implementation of the robot loop has more features than
   just calling a single method many times. But this gives the idea as to how
   code runs repeatedly, and sets us up to understand the command scheduler.

When looking at regular code, the ``robotPeriodic()`` method in the ``Robot``
class plays the role of the ``doRobotActions()`` method in our example. It runs
every 20 ms.

Modifications to ``Robot``
--------------------------

There's not a lot of user code that needs to run in the ``Robot`` class.
Changes here are somewhat outside the scope of the command-based paradigm, so
putting robot features here violates the regular declarative philosiphy of the
command-based paradigm.

However, if you're not declaring robot behavior in this class, and only utility
methods, this is a great place to add those things.

.. admonition:: ``robotPeriodic()``

   The method ``robotPeriodic()`` is very helpful, because it *always* runs,
   regardless of robot disable. This is a good place to put systemwide logging,
   or other code that *must* run.

Most of the time, whatever you want to implement in ``Robot`` can be done
through just calling a method in ``RobotContainer``. However, the choice of one
over the other is up to you.
