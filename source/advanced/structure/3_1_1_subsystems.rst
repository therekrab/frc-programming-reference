3.1.1 - Subsystems
==================

Subsystems are an important part of code; they let us interface directly with
hardware on the robot, and they help split up large tasks by grouping them into
distinct groups of tasks per subsystem.

This page provides a general "best practices" list for subsystems.

Describe state categorically
----------------------------

Subsystems - like a lot of things in robot programming - have mutable state. It
becomes important to be able to share this state outside of the subsystem
itself to make decisions in the future. For example, a "manipulator" subsystem
would need to expose state, such as whether a gamepiece is present, in order
for the robot to act on this information later.

However, some methods of exposing state are better than others. One thing we
want to avoid is the `XY Problem <https://en.wikipedia.org/wiki/XY_problem>`_,
where some :term:`client` (code external to the subsystem) asks for one value
(say, elevator height) when what they really want to know is something else
(whether the elevator is at setpoint).

Exposing state through some value ``X`` just so that the client can process it
into some value ``Y`` is a bad idea, because it puts too much trust in the
client to do the right thing here. It also violates rules of encapsulation if
we allow public access to physical information about the subsystem, such as an
elevator's height or a shooter's voltage draw.

All of this information is eventually going to be used to make a decision, so
it's better to represent that with a categorical value (more on that word in
:doc:`3_1_4_state_theory`.

Generally tend towards descriptive, discrete types rather than continuous, raw
data.

Here's some examples:

- ``getSpeed()`` should be ``isReady()`` if it's being compared against some
  speed setpoint value.

- ``getMotorTemperature()`` should be ``isOkay()`` if it's being used to verify
  that the subsystem is physically okay.

- ``getArmPosition()`` should be ``atSetpoint()`` if it's being used to see if
  a subsystem has reached it's setpoint.

Notice how all of these examples require context - what are we using the value
for? Help clean up the client's code by moving those checks that they will do
into the subsystem itself.

Expose ``Trigger``\s, not ``boolean``\s
---------------------------------------

Consider this method in a ``Manipulator`` subsystem:

.. code-block:: java

   public boolean hasPiece() {
     /* snip */
   }

This works fine, and is usable outside of our program. It doesn't expose any
information about the subsystem besides what the user actually wants to know,
so it seems good. However, there's no guarantee in code at compile time that
this value continues to be valid. Obviously, this is because such a statement
is false. Not having a piece can easily change to having a piece in the future,
and vice versa.

Instead, it's recommended to use a ``Trigger`` object, which is itself a form
of a ``BooleanSupplier``. This way, we know that the value from this method is
always correct, because it updates with the state of the subsystem.

We should change the method signature to this:

.. code-block:: java

   public Trigger hasPiece() {
     return new Trigger(() -> {
       /* snip 
     });
   }

Now, our code returns a ``Trigger`` object which will *always* be valid, and is
significantly more easily usable. Fun fact: ``Trigger`` objects also act as
``BooleanSupplier``\s, so they are *very* versatile.

Return ``Command``\s
--------------------

Sometimes, programmers will expose public ``void`` methods, such as
``setStow()`` or ``startAction()``. However, these can be confusing and
difficult to work with. Here's why:

1. These commands don't finish instantly. Instead, they may only *set* a
   desired state - they won't wait to actually achieve said state. The user has
   to then wait for the subsystem to finish the desired action, which can be
   forgotten by programmers.

2. It's up to the client to make even remotely complex actions. For example, to
   intake a gamepiece with this method, the client is expected to call the
   ``startIntake()`` method, wait for a piece, and then call ``stopIntake()``.
   If they forget a step, their command will not run.

3. This induces repetitive code. If two large commands require different
   behavior for the most part, but have *one* subsystem do the same thing, then
   the implementation for that action will have to be copied across multiple
   files.

Instead, it's preferred to avoid exposing incomplete actions and instead
returning ``Command`` instances that do a whole action.

This helps separate basic subsystem level behavior from full robot behavior and
makes it easier to make new commands. This also ensures that if that behavior
is ran, the rules of command requirements are followed.

Use enums extensively
---------------------

It's rarely a good idea to allow a nearly-infinite set of inputs to a system.
If your elevator has a method ``go(double position)`` and returns a command
that sends the elevator to that height, you're allowing all of the following
problems:

- Inputs outside a reasonable range could break the robot. Checking for those
  is necessary here, but is verbose and can be incorrectly done.

- Inputs that don't actually make sense for the mechanism are also allowed
  unchecked. There's rarely a finite number of positions for a system to be in,
  which isn't great.

- It's up to the client to make sure that the setpoints are valid, which
  violates subsystem's rules of isolated internals. The user shouldn't have to
  know what the correct position values are for a subsystem.

All of these problems can be replaced with a simple enum in code. Enums that
represent possible subsystem states have the following advantages:

- Ensures a finite set of valid inputs. The user can't just create a new enum
  variant and apply that. The variants are fixed, and can be declared inside
  the subsystem.

- The user has a better idea of what these values mean. ``Stow`` is a better
  term in code than ``0.241`` for an elevator's position.

- It's no longer up to the end user to supply the physical values; instead,
  that can be handled inside the subsystem.

Enums are incredibly non-verbose in Java as well (for this use case). We can
construct enums with associated values like so:

.. code-block:: java

   public enum ArmState {
     Stow(0),
     Score(0.2),
     Reverse(-0.11);

     protected final double position;
     private ArmState(double position) {
       this.position = position;
     }
   }

With this code, clients (outside of the subsystem's package) can still refer to
``ArmState.Stow``, but they cannot see the value of ``position``. But the
subsystem can. The subsystem can see the ``position`` field of the state and
easily set that as the position reference internally.

Separate hardware logic from subsystem logic
--------------------------------------------

If subsystems are expected to expose subsystem-specific commands, *and* they
have to handle the hardware objects, this is too much responsibility. Instead,
we can use composition to create an "IO layer" as another layer of abstraction
between hardware and subsystem logic.

These "IO" classes could be simple classes that are just instantiated
internally and used to write to the hardware, but there are a few other
optimizations we can make to ensure that subsystems work well.

For the rest of this small section, we'll look at an ``Intake`` subsystem.

Cache State
~~~~~~~~~~~

It's helpful to have consistent state throughout one cycle of the robot. This
can be done by caching each value from the hardware (voltage, temperature,
sensor states, etc.) independently, but this leaves the opportunity for
forgetting to update one value.

Instead, we can create an "IO inputs" class that looks like this:

.. code-blcok:: java
   :caption: IntakeIOInputs.java

   public class IntakeIOInputs {
     public boolean coralSensorTriggered;
     public double pivotPosition;
     public double intakeVoltage;
   }

Now, we can also define a method ``updateInputs(IntakeIOInputs inputs)`` method
on the IO class, which updates the values in the entire inputs object, which
can be called once in the subsystem's ``periodic()`` method. This ensures a
consistent state across an entire loop, which is good.

Use interfaces
~~~~~~~~~~~~~~

We *could* create an ``IntakeIOHardware`` class, which would handle all of our
IO operations with hardware. However, let's consider more advanced features in
the long run - mainly simulation. If we want to simulate the robot, it's best
to have a strict division between the robot's hardware code and it's associated
code in simulation.

Instead, let's create an ``IntakeIO`` interface, which ``IntakeIOHardware``
implements. Here's what the interface may look like in code:

.. code-block:: java
   :caption: IntakeIO.java

   public interface IntakeIO {
     void setRollerVoltage(double voltage);
     void setPivotPosition(double position);

     void updateInputs(IntakeIOInputs inputs);
   }

Now, instead of storing the io layer as ``IntakeIOHardware``, we can simply
update ``IntakeIOHardware`` to implement ``IntakeIO`` and store the IO object
as an ``IntakeIO`` object, allowing for other classes that also implement
``IntakeIO``. We can create a separate class, ``IntakeIOSim`` which has all the
same methods, but can vary in implementation, making simulations easier to
change.

We can also create more classes that implement ``IntakeIO`` for unit testing
purposes if we so desire.
