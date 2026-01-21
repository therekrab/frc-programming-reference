
2.4.1 - Command structure
=========================

WPILib offers a ``Command`` class that you can subclass (inherit from) to
create new commands. These commands have four important methods that can be
overridden to change the behavior of the command.

Initialization
--------------

Commands need to be **initialized**, meaning that there is some setup necessary
to run them. The initialization is just a series of steps that commands run to
ensure that they begin in a known state or configuration. For example, these
might involve setting the desired state for a subsystem or setting variables to
the correct values.

Initialization occurs once when the command is initially scheduled, and is the
first thing the command will do.

When subclassing ``Command``, this behavior is in the ``initialize()`` method.

Execution
---------

The next phase of a command's life is **execution**, which is where the command
repeats the same steps periodically. On a robot, this occurs by default at 50
hertz (meaning 50 times per second, or every 20ms).

Execution should involve steps such as applying a new output based on a
sensor's readings, or something that needs to be re-ran every cycle.

This behavior is found in the ``execute()`` method of the ``Command`` class.

End conditions
--------------

Often, commands have a specific way to check whether they've completed. If a
command is completed, it should stop executing and be unscheduled.

To specify when a command should naturally end, we can override the
``isFinished()`` method in ``Command``, which returns a boolean value -
``true`` if the command is finished and ``false`` otherwise.

.. note:: Be aware that commands can be **interrupted** which causes them to
   terminate and be unscheduled even if ``isFinished()`` returns ``false``. In
   a moment, we'll discuss reasons that commands may be canceled like this.

Ending
------

When a command ends, there may be some behavior that needs to run to wrap up
the command's behavior.

This could involve stopping a subsystem, or doing some other "instant" cleanup.

The method here to override in ``Command`` is ``end()``. Unlike the previously
mentioned methods, however, this method accepts a boolean parameter. This
parameter refers to whether the command was cancelled.

If the argument is ``false``, then the command was *not* interrupted and
therefore completed naturally. However, if the command *was* interrupted, then
the value of the argument must be ``true`` to signify this.

