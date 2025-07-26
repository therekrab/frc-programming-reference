2.4 - Commands
==============

So far, we've only created a level of abstraction between the robot's hardware
and what we can do with it. But when can we do anything with it? This is where
**commands** come into play. Commands are how we can represent different
actions the robot can take.

What's a command?
-----------------

Commands are code representations of actions that the robot can perform.
Typically, commands represent actions that can occur that are not
instantaneous. This means that we have a code representation of something that
takes time, and therefore may be finished *or* unfinished.

Commands run when you "schedule" them, and they continue to run until they
complete by themselves or when they are interrupted.

Command structure
-----------------

WPILib offers a ``Command`` class that you can subclass (inherit from) to
create new commands. These commands have four important methods that can be
overriden to change the behavior of the command.

Initialization
~~~~~~~~~~~~~~

Commands need to be **initialized**, meaning that there is some setup necessary
to run them. The initialization is just a series of steps that commands run to
ensure that they begin in a known state or configuration. For example, these
might involve setting the desired state for a subsystem or setting variables to
the correct values.

Initialization occurs once when the command is initially scheduled, and is the
first thing the command will do.

When subclassing ``Command``, this behavior is in the ``initialize()`` method.

Execution
~~~~~~~~~

The next phase of a command's life is **execution**, which is where the command
repeats the same steps periodically. On a robot, this occurs by default at 50
hertz (meaning 50 times per second, or every 20ms).

Execution should involve steps such as applying a new output based on a
sensor's readings, or something that needs to be re-ran every cycle.

This behavior is found in the ``execute()`` method of the ``Command`` class.

End conditions
~~~~~~~~~~~~~~

Often, commands have a specific way to check whether they've completed. If a
command is completed, it should stop executing and be unscheduled.

To specify when a command should naturally end, we can override the
``isFinished()`` method in ``Command``, which returns a boolean value -
``true`` if the command is finished and ``false`` otherwise.

.. note:: Be aware that commands can be **interrupted** which causes them to
   terminate and be unscheduled even if ``isFinished()`` returns ``false``. In
   a moment, we'll discuss reasons that commands may be interrupted like this.

Ending
~~~~~~

When a command ends, there may be some behavior that needs to run to wrap up
the command's behavior.

This could involve stopping a subsystem, or doing some other "instant" cleanup.

The method here to override in ``Command`` is ``end()``. Unlike the previously
mentioned methods, however, this method accepts a boolean parameter. This
parameter refers to whether the command was interrupted.

If the argument is ``false``, then the command was *not* interrupted and
therefore completed naturally. However, if the command *was* interrupted, then
the value of the argument must be ``true`` to signify this.

Command requirements
--------------------

One advantage of using the WPILib commands library is its powerful ability to
avoid :term:`UB` by creating a resource-management system to make sure that
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
present command to allow incoming commands to be scheduled.

We can change a command's :term:`interruption behavior` to modify how a command
behaves when it is interrupted.
