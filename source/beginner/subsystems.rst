Subsystems
==========

As programmers, it's part of our job description to deal with hardware
components, either for reading data from sensors (e.g. encoders, LiDAR sensors,
etc.) or writing data to motors.

But how should we represent this in code? We've got a couple of options: we
could try to represent each device (motor, sensor, and so forth) as their own
objects, then interface with them directly, or we could add layers of
encapsulation between those physical components and the rest of our code. The
latter is much preferred, for a variety of reasons, which will be covered in
just a moment.

Now that we've decided to group these atomic components of the robot, it
becomes a reasonable question to ask *how much grouping* takes place. What
hardware components belong together - and just as importantly, which components
don't?

This is where the definition of a **subsystem** comes in. According to the
WPILib docs:

  Subsystems are the basic unit of robot organization in the command-based
  paradigm. A subsystem is an abstraction for a collection of robot hardware
  that operates together **as a unit**.

Subsystems represent a mechanical "thing", *not the actual implementation
that is used*. They represent a part of the robot that can do actions, not
the motors and senors that the subsystem uses to achieve those things. Those
components are still represented by a code, but those are hidden inside the
subsystem - they are *how* the subsystem works under the hood, not *what* the
subsystem is.

Why are subsystems better?
--------------------------

Firstly, encapsulation around hardware objects means that we define the terms
though which other parts of code can interact with this protected hardware.
Furthermore, we can abstract away the actual hardware used. This means that we
only need to expose a simple API for dealing with the subsystem. We never want
to expose the internal workings of the subsystem (motors, sensors, PID
configurations, etc) because if *the rest of the code shouldn't care*.

The only place where those objects need to be referenced is in the subsystem's
class itself, and then we can expose a more abstract public interface to deal
with the object. This means that even large changes, like adding another motor,
swapping to a different type of sensor, or changing the motion profile used on
the hardware components **requires no change outside of the subsystem**. We
don't risk breaking code that was already functional.

Secondly, subsystems help add meaning to the code. Consider a simple mechanism,
like a climber that runs on a winch. We could create an instance of the motor
object and pass it around whenever it's needed, but this is very breakable and
confusing. With this approach, there's no way we can tell the compiler that
*this* motor object is for the climber winch, but *that* motor is for a
completely different mechanism. This means that a programmer can make a mistake
and give another part of code the wrong motor, and we get no issues from the
compiler.

However, creating a ``ClimberWinch`` class, even if it only holds the motor
object as a ``private`` field, fixes this problem. I can't pass any other type
of class in for a ``ClimberWinch`` - the compiler won't allow it (in Python,
we'll get a runtime exception when we call climber-specific methods on the
object).

Subsystems, through WPILib, also have a method called ``periodic()``, which is
ran every 20ms, by the Command Scheduler. This method is great for all sorts of
passive behavior, such as logging system states, or other important things that
need to happen "by default".

What makes a good subsystem?
----------------------------

A good subsystem should represent a physical part of the robot that operates to
do one thing. I don't mean that it only does one action, but instead it has one
physical task to do, which we echo in code.

It's possible to create a subsystem with either too little or too much
responsibility. Here's a list of "good" and "bad" subsystems:

``Arm``, representing an arm on the robot. - Good! This does one thing - it
controls the movable arm on the robot. The arm can be used in a variety of
ways, but it really, by itself, has one function.

``UpperBody``, representing all the mechanical parts above the chassis. This
isn't as good, because it probably represents too much and can do too many
things. If your subsystem is responsible for elevator motors, shooting/scoring
components, a turret, and even more, you've gone too far and should split the
components into smaller groups.

``LeftShooter``, representing the left half of a shooter mechanism on the
robot. This is probably not a very good subsystem - it doesn't represent a full
part of the robot! Instead, combine all the shooter components into a single
``Shooter`` class, and *that's* your subsystem. If a potential subsystem cannot
operate by itself, then something's not right.

Subsystem best practices
------------------------

1. Keep your subsystems "black boxes". The inner working of the subsystem is
   just that - *inner*. Don't ever promise any other part of the code direct
   access to the motors or sensors that make up the physical component of the
   robot.

   This means that you shouldn't expose hardware-specific setters or getters.
   Even if it just requires a name change, but does the same thing, make sure
   that nobody outside the subsystem can deal with the hardware itself (see
   examples below).

2. Do all motion configuration inside subsystems. For example, PID
   configurations reflect the physical capabilities of the hardware components
   on the robot. Don't expose them. You can read configurations from a
   subsystem constants file, but don't allow other parts of the code to change
   the inner workings of the subsystem at runtime.

3. A subsystem method call should *never* wait for anything. This prevents the
   entire robot is waiting for one action to complete. method calls that should
   set state should either set a reference state (and not reach it) or, for a
   more complex option, these methods could instead return Commands that, when
   ran, drive the subsystem to that state.

Examples
~~~~~~~~

Consider a subsystem for an intake, ``Intake``. It has a method called
``setMotorVoltage()`` that takes a floating-point value for the voltage that
the motor applies. The motor itself never leaves the ``Intake`` class. Does
this follow best practices?

**No**. Even though the direct motor object didn't leave the subsystem (which
is good), direct access over a physical component left. That's bad. Instead,
make the ``setMotorVoltage()`` method ``private`` to the subsystem and expose a
wrapper function ``setSpeed()``, which accepts an enum of possible speeds for
the subsystem (``Stopped``, ``Intaking``, and any others that may be
important). Then pair the variants of the enum up one-to-one with voltage
values and pass the correct value into ``setMotorVoltage``.

This approach is better because it doesn't make **any** guarantees about the
effects of the public-facing method. Instead, we only promise functionality
that is independent of mechanical implementation. We don't force the external
code to make any assumptions about how the ``Intake`` class works under the
hood. This also makes sure that nobody else in the code could ever care about
the exact voltage that we apply to the motor - that only matters inside of the
``Intake`` class.

Consider another method on our intake called ``getIRSensorTriggered()`` that
returns a boolean corresponding to whether or not the IR sensor was tripped.
Instead of exposing the object for the IR sensor, we just provided a public
method to read from it. Did we follow best practices?

Again, **no**. This is a very mere semantic difference, but naming things well
is just as important as implementing them well. We shouldn't reference the fact
that an IR sensor is part of our subsystem externally. Even if we kept the
implementation of the body the exact same, it would be better to rename this to
something better, such as ``getGamepieceHeld``, varying the name to care about
the actual functionality of the sensor.

What we just did here is important. We renamed a method, and by doing so, we
changed its purpose. It no longer tells us if the sensor value for some part of
the subsystem, which isn't really something the rest of our code should care
about. Now, instead, it relays **important information**: do we have a
gamepiece? We want to avoid the XY problem in our code.

.. note:: The XY problem is where you ask question X when what you really want
   in the end is Y. For example, *does your restaurant have bread, cheese, and
   a stove?* when you really want to know *can you make me a grilled cheese?*
   We want to avoid asking *around* the intent of our question, and instead
   just ask the question. Nobody outside of our ``Intake`` subsystem should
   care if an arbitrary IR sensor is tripped (X). Instead, what they really care
   about is whether we have a gamepiece (Y).

I'll add one more method on my ``Intake`` class. This one is supposed to tell
me to stop intaking a piece, even if I already started. I'll call it
``stop()``. Does this follow best practices?

**Yes**. This is because ``stop()`` makes it clear what we'll do, but it
doesn't make any sort of promise as to how we will achieve "stopping". This is good.

.. note:: Often, I'll say that parts of code "read from" or "write to" a
   subsystem. Reading to a subsystem means accessing data about the state of
   the subsystem, such as ``subsystem.holdingPiece()``. "Writing to" a subsystem means
   telling it to do something, such as ``setReference()``.
