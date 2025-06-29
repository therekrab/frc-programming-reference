The Command Scheduler
=====================

If you're familiar with programming outside of FRC, the idea of commands that
run like magic seems foreign. Don't you have to put these things into a loop to
get them to run again and again?

That's correct, so in this page I will describe how the command scheduler (and
other features of the command-based framework) function to keep our code simple
and clean.

If you've ever programmed with an arduino or done game programming, you may be
able to see where this is going.

What is the command scheduler?
------------------------------

The **command scheduler** is the powerful class that makes the command-based
paradigm work. It operates with a very powerful exposed method ``run()``, and
is implemented as a singleton.

.. admonition:: *Singleton*

   A **singleton** is a design pattern that ensures there's only one
   instance of the class at a time. Instead of using a regular constructor, a
   ``getInstance()`` method is available that always returns the same instance
   of the class. There is only ever one instance of a singleton.

According to the WPILib docs:

  The ``CommandScheduler`` is the class responsible for actually running commands.
  Each iteration (ordinarily once per 20ms), the scheduler polls all registered
  buttons, schedules commands for execution accordingly, runs the command
  bodies of all scheduled commands, and ends those commands that have finished
  or are interrupted.

Let's break this down.

  Each iteration (ordinarily once per 20ms)

The command scheduler runs every 20ms, because it's ``run()`` method is called
once in ``robotPeriodic()``.

.. note:: Although the docs don't mention it here, the *first* thing that
   ``run()`` does is calls the ``periodic()`` methods of each registered
   subsystem.

Then,

  the scheduler polls all registered buttons

Behind the scenes, when we create a ``Trigger`` object or something similar, it
tells the command scheduler about itself. This lets the command scheduler know
to check all registered buttons (including the ``Trigger``s) to ask if their
end conditions have been met.

Next up:

  schedules commands for execution accordingly

If a trigger's condition is met, then it actually has to do something about
that. Internally, it calls ``schedule()`` on the command it's bound to, which
tells the command scheduler *Hey! I'm a command, and I've been scheduled!
Please run me from now on.* This is also where requirements get handled. Now
the command scheduler is aware of these commands, and when it gets to the next
step, it can start to run them:

  runs the command bodies of all scheduled commands.

This is the step that we rely on to run commands. When a command is scheduled,
it's ``initialize()`` method gets called, but nothing else. This step is
responsible for running ``execute()``.

  and ends those commands that have finished or are interrupted.

After ``execute()`` is called for a command, it's ``isFinished()`` method is
polled. If that returns ``true``, the command is unscheduled, and ``end()`` is
called. Also, if any commands are marked for being interrupted (for whatever
reason), ``end()`` is called and the command is unscheduled.

.. important:: Note the order of operations here. ``execute()`` will *always*
   run at least once, even if ``isFinished()`` only returns ``true``. By that
   point, ``execute()`` has already ran once! Keep this in mind when you are
   writing commands.

Interacting with the scheduler
------------------------------

.. danger:: It's probably not a good idea to add any code to your program that
   directly talks with the command scheduler, except for adding logging. All of
   the logic for making commands run is already written.

You should not need to talk directly to the command scheduler outside of the
call to ``run()`` in ``robotPeriodic()``. But you *can*, get some help
diagnosing issues with actions that are set to run on command initialization,
execute, and ending.

You can get the current instance of the scheduler with
``CommandScheduler.getIntance()``. *Do not* attempt to call its constructor.

From there, you've got some options: ``onCommandInitialize``,
``onCommandInterrupt``, ``onCommandExecute``, and ``onCommandFinish``. These
accept functions that are useful for logging command status. Especially useful
is the ``onCommandInterrupt``, because it not only tells you that a command was
interrupted (i.e. didn't finish regularly), but also it provides the command
that interrupted it! This is especially useful for diagnosing problems with
requirements.

Here's an example of that method in action:

(in the ``robotPeriodic`` method of the ``Robot`` class)

.. code-block:: java

   public void robotPeriodic() {
     CommandScheduler.getInstance().run();

     // print when commands are interrupted
     CommandScheduler.getInstance().onCommandInterrupt((cancelled, cause) -> {
       cause.ifPresentOrElse(interrupter -> {
         System.out.println(cancelled.getName() + " was killed by " + interrupter.getName());
       }, () -> {
         System.out.println(cancelled.getName() + " was cancelled");
       });
     });
   }
   
Here, ``cause`` is of type ``Optional<Command>`` because it's possible that
another command didn't do the cancelling - disabling the robot, for example,
cancels commands, but it doesn't necessarily have a single command that caused
this.

.. note:: Please use better logging than ``System.out.println()``. This is just
   an example.
