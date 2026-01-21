2.4.5 - Command Factories
=========================

We can create command compositions from some helpful static factories like
``Commands.sequence()`` and ``Commands.parallel()``. But there are more
factories that help us create new commands instead of just combining
preexisting commands.

The command factories that we will be explaining are quite different from the
process of subclassing the ``Command`` class itself. Firstly, they are *much*
shorter to write, and they allow us to write commands using a much more
:term:`declarative` style.

Common factories
----------------

.. tip:: Refer to the `documentation
   <https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj2/command/Commands.html>`_
   in addition to this section; not all factories are listed here and these are
   certainly not the only "helpful" ones, only the ones I found myself using
   the most commonly.

``run()``
~~~~~~~~~

The factory ``Commands.run(Runnable runnable)`` runs the given runable
repeatedly, without end.

For example, the following command would simply print out "Hello, world!" to
the console repeatedly every tick.

.. code-block:: java

   Command printCommand = Commands.run(() -> System.out.println("Hello, world!"));

This isn't really helpful until you learn more about decorators in
:doc:`2_4_6_decorators`, because commands normally should end at some point.

``runOnce()``
~~~~~~~~~~~~~

The factory ``Commands.runOnce(Runnable runnable)`` creates a command that runs
the given runnable *exactly* once, and then the command is done.

The following command would set a motor voltage and then finish.

.. code-block:: java

   Command motorCommand = Commands.runOnce(() -> motor.setVoltage(5.0));

``idle()``
~~~~~~~~~~

This is a helper command - the factory ``Commands.idle()`` simply is a command
that does nothing and never ends.

``none()``
~~~~~~~~~~

This is just like ``idle()`` because it does nothing - however, this command
ends instantly, so it's non-blocking.

``waitUntil()``
~~~~~~~~~~~~~~~

The factory ``Commands.waitUntil(BooleanSupplier condition)`` is a particularly
helpful factory because it creates a command that polls the condition each
cycle and only ends when the condition is ``true``.

``either()``
~~~~~~~~~~~~

The factory ``Commands.either(Command onTrue, Command onFalse, BooleanSupplier
selector)`` can be used to run one of two commands depending on the value of a
given condition, evaluated at runtime.

``select()``
~~~~~~~~~~~~

The factory ``Commands.select(Map<K, Command> commands, Supplier<? extends K>
selector)`` is used in place of ``Commands.either()`` when there are more
options than just two. When the command is initialized, it calls ``get()`` on
``selector`` to get a "key" to use. If the key is found in the map of keys to
commands, the command corresponding to the given key is scheduled. If there is
no corresponding key in the map, a command that simply prints a message to the
console is scheduled instead. This is a rarely used factory, but when you find
a use for it, it is incredibly powerful.

Commands with requirements
--------------------------

Often, we still want commands to hold requirements on subsystems as they
execute. We need some way to specify which subsystems are required for commands
that we generate from factories.

There are two ways to do this.

Manually passing in subsystems
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In any of the above factories that don't take in commands themselves (except
``Commands.none()``), we can also pass subsystems into the factories to make
those commands require those subsystems.

.. note:: The reason that this doesn't work with ``Commands.none()`` is because
   that command does *nothing* and ends instantly.

What this means is that, if we want a command to wait on an ``Intake``
subsystem to reach a setpoint, we can write this:

.. code-block:: java

   Command command = Commands.waitUntil(intake::atSetpoint, intake);

Notice how we pass the subsystem in as an argument to add that subsystem as a
requirement.

This command will wait until the method ``intake.atSetpoint()`` returns
``true``, and it will hold requirements on the intake subsystem while it runs.

We can also pass in multiple subsystems to these factories, such as:

.. code-block:: java

   Command command = Commands.waitUntil(
        () -> (intake.atSetpoint() && elevator.atSetpoint()),
        intake,
        elevator);

The above command waits for *both* elevator and intake to be at their
respective setpoints, and also holds requirements on both.

Using the ``SubsystemBase`` class
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In cases where the factory would normally only have requirements on exactly
*one* subsystem, there's a handy way to create those commands. The
``SubsystemBase`` class (which subsystems inherit from) comes with the *exact
same* factories (except ``Commands.none()`` and the factories that accept
commands) but with one difference: the commands automatically hold requirements
on the subsystem.

So, the following two commands are equivalent:

.. code-block:: java

   // This command:
   Command command = Commands.run(intake::update, intake);
   
   // Is the same as this:
   Command command = intake.run(intake::update);

Because we call ``.run()`` on ``intake``, the ``SubsystemBase`` implementation
of ``run()`` automatically adds requirements on itself.

