2.5 - Triggers
==============

Now that we're familiar with commands, it's time we learn how to :term:`bind`
those commands to triggers.

The ``Trigger`` class
---------------------

The ``Trigger`` class is provided with WPILib and is effectively a much more
powerful ``BooleanSupplier``. It can even be treated as a ``BooleanSupplier``
because it implements that interface.

It can be constructed by giving it a ``BooleanSupplier`` to wrap.

.. code-block:: java

   Trigger trigger = new Trigger(() -> arm.getPosition() > 0);

Just like with ``BooleanSupplier``\s, you can call ``trigger.getAsBoolean()``
to get the latest value from the supplier.

But we can do a lot more as well. We can combine triggers using logical
operators, such as:

.. code-block:: java

   Trigger a = new Trigger(arm::isAtSetpoint);
   Trigger b = new Trigger(intake::holdingPiece);

   Trigger both = a.and(b);
   Trigger either = a.or(b);
   Trigger neither = either.negate();

Bindings commands to triggers
-----------------------------

Once a trigger has been constructed, we can tell commands to run when a certain
condition regarding the trigger occurs (i.e. trigger changes from ``false`` to
``true``, etc.).

Here is a table of common trigger bindings:

.. list-table::
   :header-rows: 1

   * - Method
     - Result

   * - ``trigger.onTrue(Command command)``
     - Schedules the given command when the trigger's value goes from ``false``
       to ``true``.

   * - ``trigger.onFalse(Command command)``
     - Schedules the given command when the trigger's value goes from ``true``
       to ``false``.


   * - ``trigger.whileTrue(Command command)``
     - Schedules the given command when the trigger's value goes from ``false``
       to ``true`` *and* interrupts the command if the value returns to
       ``false`` before the command finishes.

   * - ``trigger.whileTrue(Command command)``
     - Schedules the given command when the trigger's value goes from ``true``
       to ``false`` *and* interrupts the command if the value returns to
       ``true`` before the command finishes.

   * - ``trigger.onChange(Command command)``
     - Schedules the command if the trigger's value changes.

Buttons
-------

WPILib offers a variety of classes to represent controllers (XBox, PlayStation,
etc.). These all have methods that return ``Trigger``\s, which means that it's
very easy to bind commands to buttons.

.. note:: For many controllers, such as PS5 controllers, we should use the
   ``CommandPS5Controller`` class rather than ``PS5Controller`` because the
   latter doesn't return ``Trigger`` instances.

.. code-block:: java

   CommandPS5Controller controller = new CommandPS5Controller(Constants.port);

   Trigger circle = controller.square();
   circle.onTrue(pivot.stow());

