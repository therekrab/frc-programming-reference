Commands
========

Robots, are you are probably aware, can do things. But how do we express these
actions in code?

The solution that WPILib provides is by using **Commands**. Directly from the WPILib docs:

  Commands represent actions the robot can take. Commands run when scheduled,
  until they are interrupted or their end condition is met.

That's a lot to unpack. Let's go through this.

  Commands represent actions the robot can take

A command is an instance of a class. The class has methods such as
``initialize()``, ``execute()``, ``end()``, and ``isFinished()`` that provide
control over its behavior. These methods are called by the Command Scheduler
depending on the state of the command (if it's just starting, it'll call
``initialize()``. When it ends, ``end()``). Modifying these methods changes the
behavior of a command.

.. note:: The return value of ``isFinished()`` is a boolean. The method
   ``end()`` accepts a boolean. The meaning of these will be explained later in
   this page, but know that those are the only two inputs/outputs to these
   methods.

The ``execute()`` method is run every cycle, whereas ``initialize()`` and
``end()`` are only ran once, at the beginning and end of the command,
respectively.

  Commands run when scheduled

The Command Scheduler doesn't run your command until you tell it to. This could
be caused by a button being clicked, or a change in the robot's state. But the
Command Scheduler doesn't just run your command when you instantiate it.
*Commands must be bound to a trigger, or scheduler manually with*
``schedule()``. 

.. warning:: It's highly unlikely you'll ever need to call ``.schedule``
   yourself. If you do, ask yourself if what you're doing is the best
   implemented like whatever you're doing. It's probably not.

Triggers will be discussed in much more detail in another
article; they are incredibly powerful.


  until they are interrupted

Commands can be interrupted. One common way to interrupt a command is to
disable the robot through the DriverStation. There are other ways to interrupt
commands, but those will be covered in later sections. For now, just understand
that your command *can* be effectively killed, and it will not run anymore. The
only thing that happens when a command is killed is its ``end()`` method is
called with a value of ``true`` passed into it to indicate that yes, it was
cancelled and didn't finish regularly. Once a command is interrupted, it will
have to be rescheduled by the Command Scheduler to start up again. Even if the
robot is disabled, then re-enabled, the command will not restart.

  or their end condition is met.

Some, if not most, robot actions can be classified as "done". For example, a
command to raise an elevator to a certain point doesn't keep running forever.
The command should stop when the elevator is at the right position. This is
where ``isFinished()`` comes into play.

Every time that the Command Scheduler runs your command (either
``initialize()`` or ``execute()``), it then checks the value of
``isFinished()``. If that method returns ``true``, then your command is over.
Just like when it's interrupted, it will not continue until rescheduled. Like
when a command is interrupted, ``end()`` is called. However, the parameter to
end is ``false`` instead of ``true``. This is because the command wasn't
interrupted. Instead, it ended by its own accord.

.. note:: Even though it has to be specified as a parameter, it's a common use
   case not to use the boolean passed into ``end()``.

The ``Command`` class
---------------------

WPILib provides an abstract class ``Command`` along with their dependency
``NewCommands``. This provides a lot of default behavior for the commands that
we don't need to implement ourselves - it's always the same thing.

Subsystems and Commands
-----------------------

Commands often (actually, nearly all the time) use subsystems to perform
actions. But what if we accidentally run two commands at the same time, and
they both want to use the same subsystem? The compiler has no problem with
this, so our code will run. But what should happen if one command says
``subsystem.goHere()`` and the other method says ``subsystem.goThere()``?

If you just run each command without considering what subsystems it uses, we
get **undefined behavior** - quite literally, we don't know (or can't easily
tell) which command "wins".

This question demonstrates one of the reasons to use the Command Scheduler.
Commands can have **requirements**, which is a collection of subsystems that
the command uses. This isn't automatically generated - instead, we have to call
``addRequirements()`` in the command's constructor with all of the subsystems
it "requires".

.. important:: It's important to call ``addRequirements()`` in the command's
   constructor, not ``initialize()``.

The effect of this is the Command Scheduler's ability to now know what
subsystem each command plans on writing to. This lets the Command Scheduler
make sure that no commands are ever running at the same time if they require
the same subsystem(s).

When a command is scheduled that uses a subsystem, the Command scheduler knows.
So later, when another command is scheduled with the same requirements, the
Command Scheduler knows that's bad. It has to interrupt, or cancel, a command.

But which one should get cancelled? The default behavior is for the Command
Scheduler to cancel the older, already running, command to allow for the new
command to run.

There are many more features of the ``Command`` class that are important to be
familiar with. Read `this page
<https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html>`_
of the docs for more information on commands and what you can do with them.

Default commands
----------------

Sometimes, we want some behavior to run when a subsystem's not really doing
anything (no commands current require it). This is where **default commands**
come into play.

Default commands are ran, *by default* whenever their associated subsystem
currently isn't required by any other commands. They are automatically
cancelled when another command requires their subsystem, *but they come back*
when the command finishes. This makes default commands a powerful tool to add
behavior to a robot.

Consider a feature where the robot has to do some automated sequence when a
button is pressed. We'll get into running commands on button presses like this
in the future, but for now, let's consider how we want driving to work.

We could tell the robot that while the button is not held down, to run the
driving command, and then when the button is held down, to run the automated
movement command, but this gets really bad when you have two buttons. But that
doesn't scale well - adding more buttons makes the logic really bad for this.
Plus, this approach doesn't properly handle the condition where the automated
command ends. Now no command is using the drivetrain, and we're just sitting
there! We want the driving command to *always run* whenever something else
isn't using the drivetrain. Hey... that sounds a lot like a default command!

If we setup the drive command as a default command on the drivetrain, and we
only bind the automated motion command to when the button is pressed, we get
the exact behavior we want.

While the automated command is running, it has full control over the
drivetrain. But the moment that command ends, the default command (our drive
command) starts up again. We can add more buttons to our heart's content, and
make no change to the drive command logic. This is good, and this is how drive
commands should be implemented.

How to make a default command
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Default commands are just regular commands - they are created the exact same
way - but not all commands can be default commands for a subsystem.

The command must only require the *one* subsystem. A default command shouldn't
have access to write to another subsystem. The logic to decide if it can run
gets wonky. So default commands must require their subsystem, and no other
subsystems.

Then the default command must be registered with the subsystem with the following method:

.. code-block:: java

   subsystem.setDefaultCommand(defaultCommand);

That's it! The command will now run on the subsystem whenever there isn't
another command who wants to.

Where Commands live
-------------------

Commands that you subclass yourself (i.e. write ``initialize()``,
``execute()``, ``isFinished()``, and ``end()``) should live in the
``commands/`` directory of the code.

This is what a simple (albeit poorly named) project may look like:

.. code-block::

   subsystems/
   commands/
     CommandA.java
     CommandB.java
   Main.java
   Robot.java
   RobotContainer.java
   Constants.java

.. note:: The contents of the ``subsystems/`` directory were omitted for
   simpliticy.
