Triggers
========

We can construct  commands, but the Command Scheduler doesn't run them until...
*when exactly does the scheduler run commands?*

The most common way to run commands - by a long shot - is by binding them to
triggers to run when a condition is met.

Binding?
--------

Binding a command is the process of creating some "event" or "cause" that can
be "triggered", and then telling a command to run when the condition is met.

When a driver presses a button on the controller, an underlying condition (the
button's state, expressed as a boolean) changes, which can trigger an action.
We call the wrapper around the condition a "trigger".

.. note:: If you've ever worked with event-driven design (like a javascript
   event listener), this should feel relatively familiar.

The ``Trigger`` class
---------------------

The ``Trigger`` class is a class provided by WPILib and it handles binding a
command to some condition. Frequently, but not always, that condition is a
button press/release/hold.

Triggers have four super-important methods, all of which accept a command to
bind to the trigger:

- ``.onTrue()``: This schedules the command to run whenever the condition the
  trigger wraps changes from ``false`` to ``true``. This only schedules the
  command, so if the condition returns to ``false``, the command will *not* be
  interrupted.

- ``.onFalse()``: This works the same way as ``.onTrue()``, but schedules the
  command to run when the underlying condition changes from ``true`` to
  ``false``.

- ``.whileTrue()``: This schedules the given command to run when the condition
  changes from ``false`` to ``true``, but *ends the command early* if the
  condition returns to ``false`` before the command ends.

- ``.whileFalse()``: This is just like ``.whileTrue()``, but reversed. This
  schedules the given command when the condition changes from ``true`` to
  ``false``, and interrupts it if the condition returns to ``true`` before the
  command finishes.

These four methods allow triggers to bind commands to be scheduler on an
action, or while the condition is a certain value.

Binding actions to buttons and custom conditions
------------------------------------------------

Binding actions to run depending on the state of a physical button is really
easy. Depending on the type of controller, WPILib has a class for it that
allows direct access to its buttons, as ``Trigger`` objects. This means that
you can use all of the features of a ``Trigger`` on buttons!

To create a ``Trigger`` object with a custom condition, you use pass in a
function that returns a boolean to the ``Trigger``'s constructor, like so:

.. code-block:: java

   Trigger trigger = new Trigger(() -> atSetpoint());

This code block creates a trigger with the value of ``atSetpoint()`` at all
times. To bind an action to run when - *and only while* the ``atSetpoint()``
function returns ``true``, we could write code like this:

.. code-block:: java

   trigger.whileTrue(new DoThing());

where the ``DoThing`` class extends ``Command``.

Composite triggers
------------------

The ``Trigger`` class also has a few more tricks up its sleeve. You can combine
the conditions inside two triggers with ``or()`` or ``and()``, and you can flip
the result of a trigger (to get a new Trigger) with the ``negate()`` method.

The possibilities are endless, but the code simple.
