Advanced Commands
=================

Commands are such an important part of the paradigm that we use that they're
literally in the name. *Command-based* isn't an inaccurate description. For
this reason, there's a lot of complex rules to follow to keep your project
malleable and open to change.

Inline Commands
---------------

Writing commands as their own well-defined classes with ``initialize()``,
``execute()``, ``end()``, and ``isFinished()`` gets verbose quickly.

Instead, using the inline command builders, such as ``Commands.sequence()``,
``Commands.run()``, ``Subsystem.runOnce()``, etc. are the only way to write
commands. They reduce redundancy by a large amount, and make code much less
prone to error.

Check out this example for a demonstration.

.. code-block:: java

   private boolean m_didFirstThing;
   private SomeSubsystem m_subsystem1;
   private SomeOtherSubsystem m_subsystem2

   public DoubleThingCommand(SomeSubsystem subsystem1, SomeOtherSubsystem subsystem2) {
     m_subsystem1 = subsystem1;
     m_subsystem2 = subsystem2;
   }

   @Override
   public void initialize() {
     m_didFirstThing = false;
   }

   @Override
   public void execute() {
     if (m_didFirstThing) {
       m_subsystem2.executeSecondThing();
     } else {
       m_subsystem1.executeFirstThing();
       if (m_subsystem1.isDone()) {
         m_didFirstThing = true;
       }
     }
   }

   @Override
   public void end() {
     /* snip */
   }

   @Override
   public void isFinished() {
     return m_didFirstThing && m_subsystem2.isDone();
   }

This is awful. Imagine having to add a third step - or run two steps in
parallel, making sure both finish before another starts. We want to write a
really simple command that does three things:

1. Runs the subsystem's ``executeFirstThing()`` method until the subsystem is
   done (via ``isDone()``)
  
2. Runs the subsystem's ``executeSecondThing()`` method until the subsystem is
   done again.

This is really quite simple, and should *never* be expressed so verbosely. By
moving the responsibility to construct the subsystem-specific commands to the
subsystems themselves (see :doc:`subsystems`), we arrive at a much
friendlier solution:

.. code-block:: java

   Command compositeCommand = Commands.sequence(
     someSubsystem.firstThing(),
     someOtherSubsystem.secondThing()
   );

This is *so* much more compact, and it also makes it easier to see what we're
doing. In sequence, we run the first thing from ``someSubsystem`` and then the
second thing from ``someOtherSubsystem``. This reads nicer, and is less work to
add more commands, or run a smaller group in parallel:

.. code-block:: java

   Command superCommand = Commands.sequence(
     Commands.parallel(
       firstSubsystem.a(),
       secondSubsystem.b()
     ),
     thirdSubsystem.c()
   );

Imagine implementing this logic in the first style.

Naming Commands
---------------

The class ``CommandScheduler`` can be displayed on SmartDashboard (or Elastic,
which I recommend):

.. code-block:: java

   SmartDashboard.putData("Command scheduler", CommandScheduler.getInstance());

This displays all currently running commands.

The ``Subsystem`` type also has this ability:

.. code-block:: java

   SmartDashboard.putData("Some subsystem", someSubsystem);

This displays the default command for the subsystem as well as the currently
active command.

This is very useful, because it lets us see what commands are running at any
given point. However, it becomes quickly apparent that because we define *all*
commands using the inline style (with command decorators),  the commands have
no reasonable name on the dashboard. And why should they? At this point, we're
just using ``run()``\s and ``runOnce()``\s.

But, there exists a command decorator that, it seems, is not known by many but
can make your life much easier: the ``.withName()`` method on commands gives
them a customizable name.

.. code-block:: java

   Command namedCommand = regularCommand.withName("More Specific Name");

These make it more obvious as to what command(s) are currently running. We'll
implement this automatically on all commands in the section
:doc:`enterable_states`.

Proxy commands
--------------

.. danger:: Proxy commands, if used incorrectly, are able to not only cancel
   themselves, but also the group they run in.

