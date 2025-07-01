Commands
========

Commands are how the robot performs actions in command-based code.

Command-based code
------------------

We've used the phrase **command-based** when referring to our code a lot
throughout this book. Let's add a concrete definition of what command-based
programming is, any why it's powerful.

Commands
~~~~~~~~

From a high level, commands represent actions the robot, or parts of the robot,
can perform. They are ran by a tool called the command scheduler, and they
provide an easy way to combine complex subsystem logic.

Commands are objects that represent a complete action. They can be used to
perform an action, even well after initialization.

Commands represent robot-action, such as raising an elevator to a certain
point, or intaking a game piece. They vary in simplicity, but commonly interact
with subsystems to make the robot function.

Command-based
~~~~~~~~~~~~~

Command-based programming is just another programming paradigm - a common style
that helps write the important code (the robot functions) without needing to
write out the complex underworkings (for a description of what goes on under
the hood, read :doc:`command_scheduler` next).

Command-based programming centers around how commands interact with subsystems
and are combined with other commands to create powerful robot actions.

It is a **declarative** paradigm, which means that it focuses more on *what* is
done than the actual implementation of that logic. This lets us, the
programmers, focus more heavily on the robot logic rather than the underlying
system that runs said logic.

Inner workings
--------------

Commands are a little more complex than regular functions. To make a command
active, you "schedule" it. Every robot cycle (every 20 milliseconds), the robot
"runs" all scheduled commands, then checks to see if they're done. This lets us
carry out an action over an extended period of time.

Commands rely on four key methods to define robot behavior. For this section,
don't worry about how the bodies of these methods are written (that'll be
handled in the next section).

When a command starts, it has some initialization behavior. This comes from a
method called ``initialize()``. This method is called once, directly when the
command starts (when it is "scheduled"). This contains any logic that "sets up"
the rest of the command.

Every time the command is ran (repeated every 20ms), it has some regular
execution logic. This is handled with a method called ``execute()``. From the
point where this command is scheduled, this runs *every* cycle.

When a command finishes, it has some end behavior to run (sort of like
cleanup). This is handled with a method called ``end()``. The ``end()`` method
is special - it accepts a boolean parameter to tell whether the command was
interrupted by some other part of the program. We'll talk about interruption in
just a second, but this lets us change the end behavior of the command
depending on if the command finished regularly.

To tell if a command is done, the scheduler doesn't just guess. Instead, each
command also needs to define *when* it's done. This is done with a method
called ``isFinished()``, which returns a boolean - ``true`` if the command can
be stopped, and ``end(false)`` called, and ``false`` if the command should keep running.

Here's an overview of what runs during the process of a command:

1. When the command starts (is scheduled), ``initialize()`` is called.

2. Repeatedly, ``execute()`` is called, then the output of ``isFinished()`` is
   checked. If that returns ``true``, the cycle is broken and the command's
   ``execute()`` method will no longer run, and we move onto step 3. Otherwise,
   we restart this step.

3. To wrap up the command, ``end(false)`` is called once. The command will not run
   again unless it is rescheduled later.

Interrupting commands
---------------------

Sometimes, commands are stopped by some other part of the program. This is
called **interrupting** a command, and it stops the command, regardless of the
value returned by ``isFinished()``.

A command can be interrupted for a variety of reasons, some of which will be
covered on this page, and others elsewhere.

When a command is interrupted, ``end(true)`` is called to indicate that the
command didn't finish regularly, and instead was interrupted.

Creating commands
-----------------

Commands can be created in one of two ways: subclassing the ``Command``
abstract class, or defining inline commands with lambda functions and command
decorators.

.. tip:: I'm going over both methods, but there's very scarely reasons to
   subclass commands. Using the command decorators and command factories is
   less verbose and less error-prone, so use those options whenever possible.

Subclassing ``Command``
~~~~~~~~~~~~~~~~~~~~~~~

If you declare a custom class that inherits from the ``Command`` class, you can
manually override the four methods listed above. This lets you create custom
commands to do just about anything.

For example, if we want a command to intake a piece, then stop once a gamepiece
is in the intake subsystem, we could use the following four methods:

.. code-block:: java

   public class IntakeCommand extends Command {
     private final Intake m_intake;

     public IntakeCommand(Intake intake) {
       /* snip */
     }

     @Override
     public void initialize() {
       // tell the intake motor to begin intaking a gamepiece
       m_intake.setIntake();
     }

     @Override
     public void execute() {
       // We don't make any changes while the command is running, so this can be empty.
     }

     @Override
     public boolean isFinished() {
       return m_intake.hasPiece();
     }

     @Override
     public void end(boolean interrupted) {
       m_intake.stop();
       if (interrupted) {
         System.out.println("Intake command was interrupted");
       }
     }
   }

.. note:: I purposely left out the constructor, but just know it assigns the
   ``intake`` parameter to the ``m_intake`` field. There's more going on here,
   which I'll cover when we discuss subsystem requirements.

This command sets tells the intake subsystem to begin intaking once, then
doesn't do anything until the end condition is met. Once the ``isFinished()``
returns ``true``, the command ends and we stop the intake.

Command decorators and static factories
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The major disadvantage to subclassing ``Command`` is that the code becomes very
verbose and difficult to modify. We've got four functions where things are
happening, and the code is written very imperatively - that is, we focus more
on the *how is this implemented* than *what does it do*.

Thus we introduce the use of command decorators and command factories to create
commands.

This method offers a more concise, but equally as powerful, way to create
commands. These require the use of function objects (often lambda functions)
and command decorators, plus inline command helper factories.

The ``Command`` class itself has a variety of useful methods that are
implemented by default when you extend it. These return ``Command`` instances
too, so method chaining becomes a powerful way to create complex commands

Some of these include:

``andThen()``: runs the provided command immediately after this command ends.
The resultant command ends when both commands have ran.

``alongWith()``: runs the provided command at the same time as this command.
The resultant command ends when both commands have finished.

``onlyIf()``: only schedules this command if the provided condition is met at.

``unless()``: the inverse of ``onlyIf()``; schedules the command as long as the
condition is *not* met.

``onlyWhile()``: adds another end condition for the command. This command will
only continue to run if the provided condition continues to evaluate to
``true``.

``until()``: the opposite of ``onlyWhile()``; runs this command until it ends
or this condition is met.

``finallyDo()``: runs the given callback whenever this command ends.

.. hint:: There are *many* more of these methods, all of which make writing
   complex commands very easy.

Using just regular command decorators like these, we can modify the behavior of
existing commands:

.. code-block:: java

   Command superCommand = commandA
       .alongWith(commandB)
       .andThen(commandC)
       .onlyWhile(this::getActive)
       .finallyDo(this::setDone);


Notice here that I passed in a function to ``onlyWhile()`` rather than a
boolean. This is necessary to ensure that the result actually can change!

But this alone still requires a command to modify. Luckily, WPILib offers a
utility class called ``Commands`` (note the 's' at the end) to help build
commands in a more declarative style.

Here are a few notable methods:

``run()``: Returns a command that runs the given callback repeatedly, without
end.

``runOnce()``: Returns a command that runs the given callback once, then ends.

``startEnd()``: Returns a command that runs a given callback once, then upon
finish, runs a different callback. This command has no default end condition.

These three methods listed above (plus others) are also implemented on
subsystems themselves, through the ``Subsystem`` class.

We can rewrite our subclassing example to be a much more concise form:

.. code-block:: java

   Command intakeCommand = intake.runOnce(intake::setIntake)
       .andThen(Commands.waitUntil(intake::holdingPiece)
       .finallyDo(interrupted -> {
         intake.stop()
         if (interrupted) {
           System.out.println("Intake command was interrupted");
         }
       });

This code is *much* easier to read, write, and understand.

.. note:: We'll discuss the differences between using ``Commands.runOnce()``
   and ``Subsystem.runOnce()`` in the section on command requirements.

.. important:: Notice how we *don't* declare entire classes for these commands.
   But we don't want command definitions scattered throughout the codebase.
   This means this approach relies on you, the programmer, to control where
   commands are build. I make some recommendations that I think work well in
   the **Advanced** section of this book.

Subsystem requirements
----------------------

Most of the time, commands use subsystems to do important functions. But from
what we know so far, there's no system in place to ensure that different
commands don't try to write to the same subsystem, causing undefined behavior.

.. admonition:: Undefined behavior

   **Undefined behavior** (UB) in code is a situation where we cannot tell what the
   code is going to do.

Because we want to avoid any undefined behavior, the smart folks over at WPILib
implemented a feature into the command-based paradigm referred to as the
**requirements system**.

If each command has some way to *telling* the code what subsystems it'll use,
then the command scheduler (the thing that runs the commands every loop) can
safely handle the situation of multiple commands running at the same time that
use the same subsystem.

Here's how that works:

If a currently scheduled command has requirements on ``SomeSubsystem``, and
another command is scheduled with requirements on the same subsystem, the
command scheduler looks at a property of the currently scheduled command called
``InterruptionBehavior``. This tells the scheduler whether the currently
scheduled command should be interrupted, or if the incoming command should be
cancelled instead. By default, the currently scheduled command is cancelled to
make way for a new command, but this behavior is customizable for each command.

.. note:: Notice that the command whose interruption behavior matters here is
   the *currently scheduled command*, not the incoming command.

This ensure that two commands with requirements on the same subsystem are
*never* ran together.

Because of this protection added, it is *crucial* to always make sure that
commands have the right requirements.

If you subclass commands yourself (again, not recommended), you will have to
call the ``addRequirements()`` method in the constructor of your command.

The constructor for our subclassing example would look like this:

.. code-block:: java

   public IntakeCommand(Intake intake) {
     m_intake = intake;
     addRequirements(intake);
   }

This tells the scheduler you will be using this subsystem while your command
runs.

If you use command decorators and factories, calling ``Subsystem.run()`` or
other methods automatically adds that subsystem as a requirement. Read the docs
to get more information about how to explicitly set requirements for whatever
method(s) you use.

Default commands
----------------

Command compositions
--------------------

Where commands live
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

.. tip:: There are many more features of the ``Command`` class that are
   important to be familiar with. Read `this page
   <https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html>`_
   of the docs for more information on commands.

