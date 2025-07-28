2.4.4 - Command Compositions
============================

It's helpful to split up logic across multiple commands.

We don't want a single command for total robot operation, because that will be
massively oversized.

Instead, it would be great to be able to say "Do A *then* B".

What's a composition?
---------------------

In simple words, a **command composition** is simply a collection of indidual
commands that runs them in a group. There are multiple types of groups which
will be explained here later.

Command compositions also are commands themselves, so you can put compositions
in compositions.

The requirements of a group are the union of the requirements of its inner
commands. This means if a single command in the composition has conflicts with
another command, the entire composition can be unscheduled.

Compositions are powerful features of the command-based library and they let us
separate actions based on logical groupings.

Types of compositions
---------------------

This section focuses on common compositions that are used in WPILib.

Sequential group
~~~~~~~~~~~~~~~~

A sequential group runs its internal commands in sequence. Once one command
finished, the next command starts. The group as a whole is finished when the
last command completes.

In code, we can use the static factory ``Commands.sequence()`` to create
sequential groups.

.. code-block:: java

   Command sequentialCommand = Commands.sequence(
       commandA,
       commandB,
       commandC);

This group will run ``commandA`` followed by ``commandB`` and then ``commandC``.

Parallel group
~~~~~~~~~~~~~~

A parallel group runs all of its commands in parallel, meaning that they all
run at the same time. For this reason, two commands in a parallel composition
cannot share requirements.

The composed command ends when each internal command is over. If one command
finished, other commands in the composition would not be affected.

We can use the static factory ``Commands.parallel()`` to create a parallel
group.

.. code-block:: java

   Command parallelCommand = Commands.parallel(
       commandA,
       commandB,
       commandC);

This creates a command that runs ``commandA`` alongside ``commandB`` and
``commandC``. Once all three commands are done, the composition is finished.

Race group
~~~~~~~~~~

This is an alteration of a parallel composition that doesn't wait for all of
its commands to finish. Instead, race groups wait for any one command to finish
and then interrupt all the others.

In code, we use ``Commands.race()`` to race a group of commands.

.. code-block:: java

   Command raceCommand = Commands.race(
       commandA,
       commandB,
       commandC);

This runs ``commandA``, ``commandB``, and ``commandC`` in parallel until any
one finsihes, then cancels all the remaining (unfinished) commands.

Deadline group
~~~~~~~~~~~~~~

Deadline groups are just like race groups, however a specific command is set as
the "deadline command". The commands are run in parallel, but when the
"deadline command" ends, all other still-running commands are interrupted.

The ``Commands.deadline()`` factory creates these.

.. code-block:: java

   Command deadlineCommand = Commands.deadline(
       commandA,
       commandB,
       commandC);

This runs ``commandA``, ``commandB``, and ``commandC`` in parallel until
``commandA`` finishes. Any other commands that are still running are
interrupted.

Alternative creation
--------------------

There is one more way to construct command compositions.

All commands inherit from the ``Command`` base class in WPILib, which means
that they have a lot of prebuilt functionality. Some of this functionality
involves creating compositions.

Here is a table that demonstrates the alternative ways to construct commands.

.. list-table::
   :header-rows: 1

   * - Composition kind
     - ``Commands`` factory
     - ``Command`` method

   * - Sequential
     - ``Commands.sequential(a, b, c, ...)``
     - ``a.andThen(b, c, ...)``

   * - Parallel
     - ``Commands.parallel(a, b, c, ...)``
     - ``a.alongWith(b, c, ...)``

   * - Race
     - ``Commands.race(a, b, c, ...)``
     - ``a.raceWith(b, c, ...)``

   * - Deadline
     - ``Commands.deadline(a, b, c, ...)``
     - ``a.deadlineFor(b, c, ...)``

Tradeoffs
~~~~~~~~~

Writing ``a.andThen(b)`` seems a lot nicer than writing ``Commands.sequence(a,
b)``. So why wouldn't we just always use the methods on ``Command`` instead of
the static factories?

As do most decisions of this sort, it comes down to style. Anything that can be
done in one format can be donne in the other, but the difference can affect how
easy it is to create compositions.

The first reason to avoid this style is because it doesn't make each command in
a composition seem "equal". For example, consider ``Commands.parallel(a, b,
c)``. Each command is just another argument to the factory, and they all are
similar.

However, that's not true if we write ``a.alongWith(b, c)``. Why is ``a`` the
command that gets the method called on it? As more commands get added to the
group, the question of "which command do I call the method on" becomes
prevalent - further so when we don't ues simple named commands.

On another note, look at this:

.. code-block:: java

   Command commandGroup = Commands.sequence(Commands.parallel(a, b), c);

This makes it clear that we run ``a`` and ``b`` in parallel, then we run ``c``
after both have ended.

Let's write this with methods on the commands themselves:

.. code-block:: java

   Command commandGroup = a.alongWith(b).andThen(c);

This is shorter, but it can be error prone. Spot the difference between the
previous one and this:

.. code-block:: java

   Command commandGroup = a.alongWith(b.andThen(c));

Now, we run a completely different operation. As commands written like this
grow in complexity, it can be harder to keep track of what is in what group.
This is much less of a problem when using the factories.

So should we use the static factories for complex commands and the methods on
``Command`` for simpler things? This is another stylistic decision, but I would
argue not to use the ``Command`` methods at all. You'd have to draw an
arbitrary line as to what was "too complex" and your code could get polluted
with different calls to methods that do the same thing.

The ``Commands`` static factories scale better and for that reason are what are
recommended.
