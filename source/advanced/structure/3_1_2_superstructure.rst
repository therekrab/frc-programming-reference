3.1.2 - Superstructures and States
==================================

When writing code, it's always a good practice to try to enforce as much safety
as you can in the design that you choose. This means writing code into a
structure that is easy to extend and modify, but makes it difficult to break
the "rules" of the design. For example, if I'm building a subsystem, I don't
want motor objects to be declared as ``public``. This would allow other parts
of the program to potentially interface with protected hardware - something we
really *don't* want.

In robot projects, we have subsystem objects that directly control hardware on
the robot. Although we've incorporated safety inside of these subsystems
themselves (by returning ``Commands`` which use the command scheduler's
requirements system), we can do more to make sure that there isn't any
"unauthorized" access to subsystems.

This also helps avoid "unfinished" or "incomplete" commands which may just come
from one subsystem.

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
all subsystems as one. This makes method signatures easier to write out.

``Superstructure`` class
------------------------

Just creating a ``Subsystems`` record type doesn't solve the problem that
there's no good place to keep subsystems that ensure that they're only
accessible from the correct locations.

We can create a ``Superstructure`` class that contains a private instance of
the ``Subsystems`` record. Then, we can freely pass around the
``Superstructure`` instance while ensuring that access to the internal
subsystems record is still restricted (we'll get into how to do this in the
next part).

We can also forward some ``Trigger``\s from the subsystems themselves to be
public ``Trigger``\s on the ``Superstructure`` class itself.

The following is a basic example of a superstructure:

.. code-block:: java
   :caption: Superstructure.java

   public class Superstructure {
     private final Subsystems subsystems;

     public Superstructure() {
       this.subsystems = new Subsystems(/* snip */);
     }

     // Forwarding some Triggers from subsystems
     public Trigger gamepieceHeld() {
       return subsystems.intake().gamepieceHeld();
     }
   }

Now we've got a class that doesn't let anybody access the subsystems. This may
have gone too far; there needs to be some way to construct commands.

``ES`` interface
----------------

We now can introduce the final idea on this page: enterable states. This is
some special type of code that is allowed to access subsystems and build
robot-wide commands from them.

We can create an interface like this for ``ES`` (enterable states):

.. code-block:: java
   :caption: ES.java

   public interface ES {
     public Command build(Subsystems subsystems);
   }

Now we have a special interface that promsises to have a ``build()`` method
that takes a ``Subsystems`` instance and returns a ``Command`` that we can bind
to a Trigger.

To finish this system, we have to tell the superstructure that it's okay to
give any class that implements ``ES`` access to the ``Subsystems`` instance.

.. code-block:: java
   :caption: Superstructure.java

   public Command enter(ES state) {
     return state.build(subsystems);
   }

Now this is the super awesome method that we can use to build commands. We
store the logic to construct the command from subsystem-specific commands in
states, and then from there we can call ``superstructure.enter()`` and pass in
a valid state to build a command. The client can't modify the fundamental
behavior of this command, because they don't have access to the subsystems -
only the states themselves do.

We can construct states in their own files as well. Here's an example of a
``IntakePiece`` state:

.. code-block:: java
   :caption: IntakePiece.java

   public class IntakePiece implements ES {
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

States should also remain small - they really only represent *one* possible
state of the robot. If a robot can intake a piece multiple different ways,
different states should be used for each way to intake a piece.

Modifications to ``Superstructure.enter()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can add more feature to all commands - not just one, but every command in
``Superstructure.enter()``. For example, you may want to proxy each command so
that default commands can run in auton. Or you could give the commands better
names like so:

.. code-block:: java

   public Command enter(ES state) {
     return state.build(subsystems)
         .withName(state.getClass().getSimpleName())
         .asProxy();
   }

This gives commands the name of the state class that they originated from,
which is helpful for debugging..

If you decide to use this method of proxying commands, then it is also helpful
to add another method ``enterWithoutProxy()`` which does the same thing, but
doesn't proxy the command. This is helpful when nesting states, to avoid
problems with proxy.
