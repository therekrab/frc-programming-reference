Advanced Subsystems
===================

The following are more recommendations as to how to properly structure subsystems.

Public methods
--------------

There are really only two types of methods that need to be public on subsystems
(with a few exceptions, granted): getters and setters of state.

Each of which I will now cover.

State getters
~~~~~~~~~~~~~

A state getter is something like a call to ``hasGamepiece()`` or
``isAtSetpoint()``. The naive (and common) implementation of these is to return
primitive booleans. Public accessors that don't return a binary-like type are
extremely rare, and I would discourage their use.

External knowledge of numerical values seems strange when you consider what on
earth something outside of the subsystem needs to do with a number that cannot
be done with a simple boolean check. Instead of asking *what's the speed of the
drivetrain*, just ask the question that you really want to know: *is the
drivetrain ready for action X?*

Back to the point at hand. Returning a boolean that represents the state of the
subsystem isn't always the best approach. For one, it's not at all connected to
the real state of the subsystem. If I ask the question now, there's no
guarantee (in fact, it's unlikely) that will still be valid in the future. But
the compiler doesn't know this, and really we're not setting ourselves up for
success if we give the user a value that isn't closely connected to the actual
state of the system.

The first step up would be to instead return a function object. Something that,
when polled, can always give the correct response.

Here's a Java implementation if you're curious:

.. code-block:: java

   // The old approach:
   public boolean check() {
     return m_value;
   }

   // The new approach:
   public BooleanSupplier check() {
     return () -> m_value;
   }

Now, even when the internal state (``m_value``) changes, the output from the
``check()`` method is still valid. And the compiler is our friend here, too.

We cannot call something like ``if (thatSubsystem.check()) {}``, because
``check()`` doesn't return a boolean - instead, it's a boolean *supplier*. We
must call ``.getAsBoolean()`` whenever we want the real value.

.. note:: Technically, it's possible external code could cache the result of
   ``check().getAsBoolean()``, but this is less likely, and at that point,
   there's nothing we could do about that.

This approach works great. It allows the value from our methods to always be
tied to the state of the robot, as if attached by a string. This is good. But
we can do better.

WPILib provides a ``Trigger`` class that does the same thing, but adds a few
more methods, such as ``.onTrue()``, ``whileFalse()``, etc. For a robot that
may need to trigger an action upon a change in state, this is incredibly
helpful.

Here's our newest version of the code:

.. code-block:: java

   public Trigger check() {
     return new Trigger(() -> m_value);
   }

As we said earlier, the value of this method call is always accurate, so we
don't really need a method call at all...

Instead, we can place the ``Trigger`` object as a ``public final`` field, and
then we only ever make one instance of the ``Trigger`` object. It's directly
connected to internal state, so it's always valid.

.. code-block:: java

   public final Trigger check;

   // in constructor:
   public SomeSubsystem() {
     /* snip */
     check = new Trigger(() -> m_value);
   }

This is the best way to deal with state getters.

.. note:: In case you aren't aware, ``final`` in Java tells the compiler that
   after the value is set once, a new value cannot be set. This protects our
   public field from being overridden from an external source.

There are two notable downside to the "Triggers as fields" approach, however,
that make it less compelling.

Oftentimes, these objects must be declared as a regular field, but not yet
assigned, because the other necessary fields (like ``m_value`` in our case)
weren't yet populated with a value, and the compiler gets upset about that.
This means we have to assign a value to the state getter at a different
location in code than where we declare it.

The second nuance with these state-getters is the occasional time when you want
a state-getter that is specific to some other input. Consider the following method:

.. code-block:: java

   public Trigger atReference(Reference r) {
     return new Trigger(() -> m_reference.equals(r) && atReference.getAsBoolean());
   }

where ``atReference`` is a ``Trigger`` that corresponds to whether the current
position is within some error tolerance of the cached reference,
``m_reference``. Unless we want to write repetitive code for a ``Trigger``
field for each possible reference, we can't replicate this functionality by
making the triggers into fields.

These two downsides, for me, are enough to keep me away from using fields over
methods. However, I figured the choice should be left up to the reader, so I
present both options.

State setters 
~~~~~~~~~~~~~

A state setter is the opposite of a state getter. Instead of returning a state,
it applies a new one.

For example, ``setReference()`` would be an example of a state getter - it
affects the behavior of the subsystem.

One implementation for this type of thing is a ``void`` method that sets a
desired state, and then the subsystem's ``periodic`` or a default command is
responsible for driving the subsystem to that state.

But this isn't the best approach. This makes it more difficult on commands to
set a state, and then reach that state as well. Consider this command:

.. code-block:: java

   public Command makeCommand() {
     return subsystem.runOnce(() -> subsystem.setReference(Reference.Stow))
         .andThen(Commands.waitUntil(subsystem.ready));
   }

where ``ready`` is a public field (a ``Trigger``, really) that represents if
the subsystem is at the desired state. This pattern appears *a lot*, and can
get really ugly if you try to run two different subsystems to a reference in
parallel:

.. code-block:: java

   public Command doThisAndThatSimultaneously() {
     return subsystemA.runOnce(subsystemA::goHere)
         .andThen(subsystemB.runOnce(subsystemB::goThere))
         .andThen(Commands.waitUntil(subsystemA.ready.and(subsystemB.ready)));
   }

This just says *make both subsystems go here and there, then wait for both to
finish* but it was rather large and verbose. The other problem is that the
method ``goHere()`` may be misinterpreted by a less experienced programmer that
doesn't realize that the end of ``goHere()`` does *not* mean that the subsystem
is at its reference. Then, they could mistakenly continue under the false
assumption that the subsystem is already at setpoint. Experience will teach
well, but not experience during a match where expected behavior is all we want.

The solution that I propose is to remove all of the ``void`` setters - or at
least make them ``private``. Then, have the same methods exposed, but instead
return a ``Command`` that does the action - to completion.

For example:

.. code-block:: java

   private void setReference(Reference r) {
     /* snip */
   }

   public Command goToReference(Reference r) {
     return Commands.sequence(
       runOnce(() -> setReference(r)),
       Commands.waitUntil(ready)
     );
   }

This approach is better, because the Command, unless interrupted, will always
finish. Our messy code from earlier then becomes nicer:

.. code-block:: java

   public Command doThisAndThatSimultaneously() {
     return subsystemA.goHere().alongWith(subsystemB.goThere());
   }

Our external code is less verbose, and does exactly what we expect. It also
reads nicer.

It could be said that methods like ``goHere()`` and ``goThere()`` always return
the same command, and that command is always valid, so it would be possible to
replace that method with a ``public final`` field, like we did for the
``Trigger``s from earlier.

However, this doesn't work as well. Firstly, if we use the command in a
composition at one point, it cannot be used later in a different composition.
This is a rule with the Command Scheduler, which means that we want to avoid
multiple instances of the same command floating around in code. Secondly, there
are a lot of commands like ``goToReference()`` that require a parameter, which
we can't do through a regular field. To ensure consistency, it may be best to
just keep the methods in place here.

On ``periodic()``
-----------------

The ``periodic()`` method on subsystems *always* runs, even when a command is
currently using the subsystem. For this reason, and to avoid undefined or
unexpected behavior, **never perform writes in a subsystem's** ``periodic()``.

If you need a subsystem to perform an action passively, use a default command.

The ``periodic()`` method should only be used for updating inputs to a
subsystem, logging values, etc. *Never* put code in this method that could
possibly thwart the Command Scheduler's ability to ensure only one piece of
code has write-access to the subsystem at a time.

If you need a mechanism to stay where it is even when a command isn't running,
*use onboard motor control* - a standard feature on nearly all motor
controllers.

.. note:: Plus, onboard motor control leads to faster update rates on your
   motors, which makes them less prone to jerky behavior.

More abstraction of hardware
----------------------------

Really, the subsystem as it's been treated so far has too much responsibility.
It seems it's got multiple responsibilities:

1. Manage hardware-level objects (configuration, using them to apply voltage,
   set position, etc).

2. Create subsystem-level commands and behavior.

There's no reason that the subsystem needs all this - we can split this
functionality into multiple classes.

For ease of explanation, we'll consider an ``Intake`` subsystem that we're
trying to improve.

The initial response may be to create a ``IntakeHardware`` class that deals
with all things related to hardware - reading and writing to the hardware
happens through this class. This works, and it works well. But we can do
better.

In the future, we may want to try different versions of the hardware. Maybe we
want one version that actually interacts with the real hardware, but we also
want a completely isolated version that just reads "hardware" inputs from
Network Tables and silently discards outputs. This is a simulation version of
the intake's hardware. We don't want to create a sister class to ``Intake``,
because the actual functionality of the intake doesn't change. Instead, we can
introduce a new sister class to the intake hardware class.

As far as I know, team 6328 pioneered this strategy for use with AdvantageKit.
They called the hardware-level classes "IO" classes, and the name stuck. So
we'd call the real hardware version of the intake hardware class
``IntakeIOHardware`` and the simulated version ``IntakeIOSim``. We could then
introduce a new interface ``IntakeIO``, which both ``IntakeIOHardware`` and
``IntakeIOSim`` implement.
 
Dependency injection
~~~~~~~~~~~~~~~~~~~~

To obtain the correct version of the ``IO`` interface for a subsystem, we've
got two options: we could instantiate and store the correct version of the
interface in the subsystem, or we could pass it in as an argument with the
interface specified as the type.

The latter option uses a technique called **dependency injection**, and it is
more versatile. It lets us, in tests, create *more* implementers of the ``IO``
interface that we can use for testing certain features. We just give a
different class to the subsystem that still implements the overall interface,
and the subsystem *has no idea*. It will perform the same thing in either
manner, reading and writing to the IO class just like it was real hardware.

This is clearly very powerful; we can simulate nearly every part of the robot,
or write unit tests to ensure particular features still work after a change.

Input objects
~~~~~~~~~~~~~

One other feature of this system that team 6328 popularized was the creation of
an ``IOInputs`` class, which represented all of the inputs that the robot would
read from these ``IO`` classes.

Instead of having this be the interface definition of our ``IntakeIO``:

.. code-block:: java

   public interface IntakeIO {
     public void setVoltage(double voltage);

     public double getSpeed();
     public boolean getMotorConnected();
     public double getMotorTemperature();
     public double getMotorVoltage();
     public double getMotorCurrent();
   }

We can put all those "inputs" into their own class, with publicly accessible
fields:

.. code-block:: java

   public interface IntakeIO {
     public void setVoltage(double voltage);

     public void updateInputs(IntakeIOInputs inputs);
   }

And we could declare ``IntakeIOInputs`` like this:

.. code-block:: java

   public class IntakeIOInputs {
     public boolean motorConnected;
     public double speed;
     public double temperature;
     public double voltage;
     public double current;
   }

This has numerous advantages. Firstly, we can make changes to what we need to
read from the motors without changing the definition of the ``IO`` interface.
We also can keep the same ``IOInputs`` object, and just let the fields be
reassigned, rather than new objects each loop.

But one of the biggest changes here is consistent inputs throughout a loop. If
a subsystem's ``periodic()`` calls ``updateInputs()``, *all values are updated*
and *all values won't change until the next call to* ``periodic()``. This means
that we ensure consistency during a loop cycle; different places reading the
same input get the same result.

It's up to each implementation of the ``IO`` interface to update the
``IOInputs`` object's fields. This is the downside to this technique; more
fields can be added, and used in the subsystem's code, but if those fields
aren't reassigned in ``updateInputs()``, the compiler won't care and the
subsystem will get faulty data without a warning. This is why good logging is
important; if you log the values from the ``IOInputs`` object each time
``updateInputs()`` is called, you can easily determine that a field wasn't ever
assigned to, making the code change safe and quick.
