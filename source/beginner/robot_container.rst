The ``RobotContainer`` class
============================

.. important:: I personally disagree with the way the WPILib presents the
   ``RobotContainer`` class in their documentation. However, that's a
   *design*-based decision, not a robot-based decision. It works either way.
   Here, I will share the WPILib recommended setup of ``RobotContainer``. I'll
   have a very different stance in the **Advanced** section.

The WPILib documentation recommends putting "most of the declarative setup for
the robot":

  In this class, you will define your robot’s subsystems and commands, bind
  those commands to triggering events (such as buttons), and specify which
  command you will run in your autonomous routine.

Subsystems
----------

This is where you may want to instantiate all your subsystems. They should be
declared as both ``private`` so that they are protected from the rest of the
code, and ``final`` so that they can't be changed accidentally.

.. code-block:: java

   private final SomeSubsystem m_someSubsystem = new SomeSubsystem();
   private final OtherSubsystem m_otherSubsystem = new OtherSubsystem();

This adheres to best-practices, by limiting scope on the subsystems as to avoid
hard-to-follow logic as to who uses the subsystems. We explicitly pass in
subsystems to command constructors, so we know exactly who can (and who can't)
write to these subsystems.

Commands
--------

User-defined commands that are subclassed (see :doc:`commands`) should *not* be
saved as private fields. This is because if they are used once in a
composition, that composition *owns* them and they cannot be reused.

So, they are just constructed when they need to be.

.. code-block:: java

   someButton.onTrue(new Shoot(m_shooter, m_arm));

where ``Shoot`` is a command, presumably shooting a gamepiece.

Buttons
-------

Here, controllers should be defined as private, final fields.

.. code-block:: java

   private final CommandXboxController m_driveController = new CommandXboxController(/* button id */);

Buttons can be accessed easily, and act as ``Trigger``s (see :doc:`triggers`)

.. code-block:: java

   m_driveController.leftBumper().whileTrue(new Stow(m_elevator));

Organization
------------

In case the constructor of ``RobotContainer`` gets too dense, you can split the
functionality into smaller ``private`` helper methods.

.. code-block:: java

   public RobotContainer() {
     configureSubsystems();
     configureBindings();
   }

   private void configureSubsystem() {
     /* snip */
   }

   private void configureBindings() {
     /* snip */
   }

This helps clean up the code.

Autonomous
----------

The ``RobotContainer`` class also exposes a method ``getAutonomousCommand``
which returns a command for use during autonomous. Depending on what software
you use for autonomous, the internals of this are going to vary. Most, if not
all, autonomous tools (like Pathplanner or Choreo) provide good documentation
as to what should go into this method.

.. tip:: If you look at ``Robot``'s internals, you'll see that the method
   ``getAutonomousCommand()`` is called at the start of the autonomous period,
   which means that you don't need to worry about alliance color not yet being
   set by the driver station.
