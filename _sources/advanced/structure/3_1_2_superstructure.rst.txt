3.1.2 - Superstructures and States
==================================

When writing code, it's always a good practice to try to enforce as much safety
as you can in the design that you choose. This means writing code into a
structure that is easy to extend and modify, but makes it difficult to break
the "rules" of the design. For example, if I'm building a subsystem, I don't
want motor objects to be declared as ``public``. This would allow other parts
of the program to potentially interface with protected hardware - something we
really *don't* want. Instead, we want to keep motor objects ``private`` so
there's always only one, consistent, place in the code that can interact
directly with the motor objects.

In robot projects, we have subsystem objects that directly control hardware on
the robot. Although we've incorporated safety inside of these subsystems
themselves (by returning ``Commands`` which use the command scheduler's
requirements system), we can do more to make sure that there isn't any
"unauthorized" access to subsystems.

This also helps avoid "partial" commands which may just come from one
subsystem, i.e. a command that is meant to be part of a multisubsystem
sequence. Think of a command returned by ``shooter.fireShooter()`` that fires a
shooter on the robot, and is meant to be used after the robot finishes one or
more other processes to put the robot in a known configuration, ready to shoot.
We'd generally like to reduce access to the individual, shooter-only
``fireShooter`` command, and instead only expose the complete command
composition that represents the complete preparation and shooting sequence.

One potential solution to this problem is to try and use Java's language rules
regarding visibility modifiers (``private``, ``protected``, etc.) to ensure
that only some parts of the codebase can access the subsystem's command factory
methods. However, the issue here is that this is too restrictive; even using no
modifier would force us (the programmers) into writing code to create these
command compositions in the same package (directory) as the subsystems
themselves, which is a very messy setup.

Instead, we need to limit access to the instances of each subsystem rather than
the methods on them. If only certain parts of our code can access the
subsystems, then only those parts of the code can construct the commands from
the subsystem's factories. To reiterate the importance of this, remember that
this lets us better ensure that the use of the subsystem's factory methods are
used in a consistent place in our codebase.

But how do we possible organize our code such that we limit access to each of
the subsystems?

``Subsystems`` record
---------------------

To avoid passing around multiple subsystems at a time, we can create a simple
``record`` in Java that just contains an instance of each subsystem on the
robot.

For example, a robot with a drivetrain, intake, and shooter may have a
``Subsystems`` record like this:

.. code-block:: java

   public record Subsystems (Drivetrain drive, Intake intake, Shooter shooter) {}

Now, we just have one object to pass around that gives whoever has it access to
all subsystems as one. This makes method signatures easier to write out (only
one argument instead of many), and assuming we don't write code to extract each
subsystem from an instance of the ``Subsystems`` record, we now only have the
objective of limiting the access to the one object. Our code also will change
less when we add or remove subsystems (which is uncommon, but not impossible).

``Superstructure`` class
------------------------

Just creating a ``Subsystems`` record type doesn't solve the problem that
there's no good place to access subsystems that ensure that they're only
accessible from the 'correct' locations.

We can create a ``Superstructure`` class that contains a private instance of
the ``Subsystems`` record. Then, we can freely pass around the
``Superstructure`` instance while ensuring that access to the internal
subsystems record is still restricted (we'll get into how to do this in the
next part).

The following is a basic example of a superstructure:

.. code-block:: java
   :caption: Superstructure.java

   public class Superstructure {
     private final Subsystems subsystems;

     public Superstructure() {
       this.subsystems = new Subsystems(/* snip */);
     }
   }

Think of the ``Superstructure`` class as a locked box with the subsystems
inside; we can pass the ``Superstructure`` class around as much as we please,
but for now, nobody can touch our individual subsystems. Right now, there is no
code that has the ability to read our subsystems, but now we can start making
the subsystems available to certain classes (not based on program structure
like visibility modifiers, but instead using more flexible methods).

The ``Superstructure`` class also is responsible for subsystem-related
initialization (setting default commands if possible, for example). For
instance, the drivetrain's default command can't be constructed without the
controller objects, so we can't completely initialize such a command in the
``Superstructure`` class (it doesn't have, nor should it have, the controller
objects). However, because the superstructure class has access to the
subsystems, it can still be responsible for *assigning* that command to be the
drivetrain's default command (perhaps with some method ``setupDrive()`` which
accepts controller objects and passes those into the drivetrain's
``drivetrain.drive(controller)`` command factory). We're limiting access to the
individual subsystems while still exposing abstract, core functionality.

``CommandBuilder`` interface
----------------------------

We now can introduce the final idea on this page: command builder classes.
These classes are intended *solely* for composing commands from individual
subsystems into complete, robot-ready commands, and these classes are the
*only* classes that should have the ability to access the subsystem objects.

We can create an interface ``CommandBuilder`` like this:

.. code-block:: java
   :caption: CommandBuilder.java

   public interface CommandBuilder {
     Command build(Subsystems subsystems);
   }

Now we have a special interface that promsises to have a ``build()`` method
that takes a ``Subsystems`` instance and returns a ``Command`` that we can bind
to a ``Trigger``.

To finish this system, we have to tell the superstructure that it's okay to
give any class that implements ``CommandBuilder`` access to the ``Subsystems``
instance by adding the following method to the ``Superstructure`` class:

.. code-block:: java
   :caption: Superstructure.java

   public Command build(CommandBuilder state) {
     return state.build(subsystems);
   }

Now this is the *only* method that we can use to build commands, and it *only*
works with classes that explicitly implement ``CommandBuilder``. We store the
logic to construct the command from subsystem-specific commands in those
classes,  and then from there we can call ``superstructure.build()`` and pass
in a command builder class to build a command. The rest of the code can't
modify the fundamental behavior of this command, because they don't have access
to the subsystems - only the command builder classes themselves do. We've
successfully ensured that all of our commands are constructed in the same,
consistent location, and it's rather difficult to break this design without
some very noticeable tomfoolery.

To see an example, here's an example of a ``IntakePiece`` state:

.. code-block:: java
   :caption: IntakePiece.java

   public class IntakePiece implements CommandBuilder {
     public Command build(Subsystems subsystems) {
       return Commands.sequence(
           subsystems.elevator().stow(),
           subsystems.intake().intakePiece())

           .unless(subsystems.intake().holdingPiece()); 
     }
   }

This short file represents the robot intaking a gamepiece. Note the call to
``.unless()`` after we construct the commands. It's best if states use the
``unless()`` and ``onlyIf()`` decorators to ensure that the starting
configuration for that state is valid.

command builder classes should also remain small - they really only represent
*one* possible state of the robot. If a robot can intake a piece multiple
different ways, different states should be used for each way to intake a piece.

Modifications to ``Superstructure.build()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can add more feature to all commands - not just one, but every command in
``Superstructure.build()``. For example, you may want to proxy each command so
that default commands can run in auton when their subsystem isn't being
directly used (see :doc:`/intro_frc/commands/2_4_9_proxy`). Or you could give the commands better names like so:

.. code-block:: java

   public Command build(CommandBuilder state) {
     return state.build(subsystems)
         // Automatically provides nice names to each command
         .withName(state.getClass().getSimpleName())
         // Proxies each command automatically
         .asProxy();
   }

This gives commands the name of the command builder class that they originated
from, which is helpful for debugging or logging. Any other debugging/logging
postprocessing that you want to apply to each *complete* command can also be
handled inside of ``build()``.

If you decide to use this method of proxying commands, then it is also helpful
to add another method ``buildWithoutProxy()`` which does the same thing, but
doesn't proxy the command, in the case that you need that.

Nesting complete commands
~~~~~~~~~~~~~~~~~~~~~~~~~

Consider a case where you have two command builder classes, ``DoThingA`` and
``DoThingB``. Let's assume that we've followed good principles so far and each
of these classes actually represents a complete robot action. Sometimes we
still want to create a composition where these two commands run in sequence (or
any other composition). Because the only input to a command builder's
``build()`` method is the ``subsystems`` object, command builders can call
``.build()`` on *other command builders*, too! For example, a ``DoBoth``
command builder class may look like:

.. code-block:: java

   public class DoBoth implements CommandBuilder {
     public Command build(Subsystems subsystems) {
       return Commands.sequence(
           DoThingA().build(subsystems),
           DoThingB().build(subsystems));
     }
   }

Now, we don't need to worry about repeating the implementation of ``DoThingA``
or ``DoThingB`` because we can simply use them in the implementation for
``DoBoth``.
