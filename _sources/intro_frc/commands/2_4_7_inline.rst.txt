2.4.7 - Inline Commands
=======================

So far we've learned two methods to create complex commands:

1. Subclass ``Command`` to create super-commands that do a lot.

2. Use command compositions to combine smaller commands to more complex
   commands. These smaller commands, however, still have to be made through
   subclassing ``Command``.

In either option, we have to subclass the ``Command`` base class directly,
which causes a few problems that we've already gone over: 

- Highly verbose commands

- Lots of boilerplate code

- Not very easy to understand (not very :term:`declarative`)

However, there's one way to create commands that doesn't involve subclassing
``Command`` *at all*. These types of commands are referred to as "inline
commands" and make writing commands a lot more simple. Combining the abilities
of command factories, decorators, and compositions, we can make *much* nicer
commands when compared to anything else we can do.


Return to :doc:`2_4_3_subclassing` if you forgot what the command looks like if
we subclass ``Command`` directly. Here's what the logical flow of the command
looked like:

1. Set the intake's position :term:`reference` to the intake position.

2. Wait for the intake to reach the setpoint.

3. Start the intake

4. Wait for a piece

5. When the command ends (either by interruption or the end condition (waiting
   for a piece), set the intake's position :term:`reference` to the stow
   position and stop the intake from continuing to intake the piece.

This really isn't very complex, but the subclassing approach makes large and
verbose files with lots of boilerplate. But we can change that. Here's what the
*exact* same logic looks like when we use command factories, decorators, and
compositions.

.. code-block:: java

   Command intakeCommand = Commands.sequence(
       intake.runOnce(intake::setIntakePosition),
       Commands.waitUntil(intake::atPosition),
       intake.runOnce(intake::setIntakeSpeed),
       Commands.waitUntil(intake::hasPiece))

       .finallyDo(intake::stop)
       .finallyDo(intake::setStowPosition);

.. note:: We could have put both ``intake.stop()`` and
   ``intake.setStowPosition()`` in a lambda and only used one call to
   ``finallyDo`` like so:

   .. code-block:: java

      /* snip */

      .finallyDo(() -> {
        intake.stop();
        intake.setStowPosition();
      });

   The reason this wasn't done is because I prefer the look of the other way.
   There's less indentation.

Overall, creating commands in this style can make commands much easier to work
with and understand. It's much more :term:`declarative` than subclassing
``Command`` in their own separate file.

However, there's one question that arises as a result of this style of creating
commands. We don't create a new class for each command, so where should we be
putting all these commands?

There is a variety of options for this, but here are a few common options:

- ``RobotContainer`` is a good place to create commands. You also have to bind
  those commands to buttons, so it's natural to create the commands there too.

- Creating a custom class that acts as a static factory that creates commands
  is another good option. Static methods like ``buildIntakeCommand(Intake
  intake)`` separate the creation of subsystems and commands from the logic
  inside them.

- Creating subsystem-specific commands inside of each subsystem and exposing
  them through factory methods is a third way to go. This eliminates a lot of
  repetitive code and allows us to hide more of the subsystem's inner features.

More on this topic can be found in the advanced section.

