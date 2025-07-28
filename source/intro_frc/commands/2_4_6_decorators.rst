2.4.6 - Command Decorators
==========================

Because all commands inherit from ``Command`` in WPILib, there is a lot of
prebuilt methods that we can call on commands to modify them. We call these
**decorators** because they modify the behavior of a command with some new
feature or change, but don't actually change the command's execution.

This page does *not* go over all of the methods on the ``Command`` object, but
only the most important ones from experience.

Common decorators
-----------------

``unless``
~~~~~~~~~~

``Command.unless(BooleanSupplier condition)`` returns a new command that does
the exact same thing as the original command, however it will do nothing and
end instantly if ``condition`` evaluates to ``true`` when the command is
scheduled. In simple words, it runs the regular command *unless* the condition
is ``true`` when the command is scheduled.

.. note:: The condition is evaluated when the command is *scheduled*, not when
   the command object is created.

Calling ``command.unless(() -> false)`` will always run the command, and
``command.unless(() -> true)`` will never run the command.

``until``
~~~~~~~~~

``Command.until(BooleanSupplier condition)`` is similar to ``unless()`` however
it doesn't just evaluate the condition at the start of the command - it only
allows the command to continue if the condition *stays* ``false``.

This means that if the condition is ``false``, and the command begins, the
original command will run. However, if the condition turns to ``true``, then
the command is interrupted.

This is the same thing as ``Commands.race(Commands.waitUntil(condition),
command)``.

``onlyIf``
~~~~~~~~~~

``Command.onlyIf(BooleanSupplier condition)`` is the opposite to ``unless()``.
The condition is evaluated once - at the time when the command is scheduled -
and if the condition is ``false``, the command will not run.

``onlyWhile``
~~~~~~~~~~~~~

``Command.onlyWhile(BooleanSupplier condition)`` is the opposite of ``until()``
because it will allow the command to run as long as the condition continues to
evaluate to ``true``. If at any point the condition evaluates to ``false``, the
command will be interrupted and end.

``finallyDo``
~~~~~~~~~~~~~

This is a *very* helpful command decorator. This has two forms:
``finallyDo(Runnable end)`` and ``finallyDo(BooleanConsumer end)``.

If the second option is used, the value of the boolean passed to the consumer
will be the same as the boolean in ``Command.end(boolean interrupted)``. It is
``true`` if the command was interrupted, and ``false`` otherwise.

If a ``Runnable`` is passed in, then that runnable (obviously) cannot determine
if the command was interrupted.

Whatever the argument is, the function that it represents will be called when
the command ends. Just like ``Command.end()``, this will *always* be called,
even if the command was interrupted.

.. tip:: You can have multiple calls to ``.finallyDo()`` chained, and they will
   all run. ``.finallyDo()`` always runs the previous command's ``end()``
   method first, so you can chain calls to ``.finallyDo()`` as many times as
   you would like.
 
A note about requirements
~~~~~~~~~~~~~~~~~~~~~~~~~

Let's consider a command that scores a gamepiece. It may look something like this:

.. code-block:: java

   Command fullScoreCommand = scoreCommand.onlyIf(intake::hasPiece);

A question will naturally arise about the requirements of ``fullScoreCommand``.
Even if the condition is ``false``, does the command still demand requirements
on the subsystems it won't use? Yes. This *is* what happens.

This means that if you have another command running that shares requirements
with ``scoreCommand``, and then ``fullScoreCommand`` is scheduled, the other
command will be interrupted regardless of whether ``scoreCommand`` actually
runs.

