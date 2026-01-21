2.4.9 - Proxy Commands
======================

.. danger:: Be aware that there can be self-cancelling commands if proxy
   commands are used improperly.

Before we introduce the idea of a proxied command, we need to really understand
the behavior that they alter.

Let's say we've got the following command composition:

.. code-block:: java

   Command command = Commands.sequence(
       arm.stow()
       intake.intake());

Here, we assume that ``arm`` and ``intake`` are each subsystems that expose
commands through public methods. We'll also assume that each command requires
the subsystem that produces it, i.e. ``arm.stow()`` requires ``arm`` and
``intake.intake()`` requires ``intake``.

Now, let's assume that we've got a default command on ``arm``. The point of
this doesn't really matter - the main purpose of this example is to consider
*when* the subsystem ``arm`` is commanded (and more importantly, when it's
not).

Because a command composition's requirements are the union of the requirements
of its component commands, then *entire* command group ``command`` will have
requirements on ``arm``, even when ``arm.stow()`` is not running. This means
that after ``arm.stow()`` completes, the arm has no command actively doing
anything on it - i.e. is effectively :term:`uncommanded`.

Hiding requirements via proxy
-----------------------------

The best way to avoid problems with requirements "bubbling up" to bigger and
bigger compositions is by using a "proxy command" that has the following
properties:

1. The proxy command has no requirements itself.

2. When the proxy command is ran, it schedules the real command.

3. The proxy command ends when the real command ends.

4. If the proxy command is interrupted, the real command is also interrupted.

What properties 2-4 mean is that the proxy command acts just like the regular
command. It will run the same thing, and it will end just like the regular command.

However, this command has no requirements so if used in compositions, the
composition itself won't require whatever the regular command would. When the
real command is scheduled (independently of the composition), it will have its
requirements as usual. The only difference is that the composition can't tell
that this is going to happen, so the entire group doesn't get the requirements
of the original command.

Making proxies
--------------

In code, we can make a command proxied via the decorator ``.asProxy()``.

In our example, if we had wanted ``arm``'s default command to run when the
``arm.stow()`` command was finished, we would replace ``arm.stow()`` with
``arm.stow().asProxy()``:

.. code-block:: java

   Command command = Commands.sequence(
       arm.stow().asProxy(),
       intake.intake());

Now the default command on ``arm`` will run when the stow command finishes,
even while the sequential group is still running.

Proxies in autonomous routines
------------------------------

Most autonomous routines are implemented using simple sequential and parallel
command groups, which means that proxy commands are *necessary* if you want
default commands to run in auton.

Simply proxy the commands that shouldn't leak their requirements, and you're
good to go.

Misuse
------

Let's consider this command group:

.. code-block:: java

   Command command = Commands.sequence(
       elevator.stow().asProxy(),
       intake.intake(),
       elevator.up());

Something bad happens if we try to run this. The entire command seems to never
run, but why?

Let's walk through the command logic to find out:

1. The entire sequence ``command`` has the following requirements:

   - No requirements bubble up from the proxy command.

   - The intake is required because of ``intake.intake()``.

   - The elevator is required because of ``elevator.up()``.

   So in total, the group has requirements on the intake and elevator.

2. The first command in the group - the proxy command - runs. This schedules
   the real command, ``elevator.stow()`` separately from the group.

3. ``elevator.stow()`` demands requirements on the elevator. Thus, any other
   command with requirements on the elevator is cancelled. This includes the
   group ``command``.

4. Cancelling the group cancels the proxy command, which in turn cancels the
   real ``elevator.stow()`` command.

Therefore, the proxied ``elevator.stow()`` just *cancelled itself* and the rest
of the group! This can happen wheneve a composition contains a command
requiring some subsystem ``S`` as well as a proxy for a command that also
requires ``S``.
