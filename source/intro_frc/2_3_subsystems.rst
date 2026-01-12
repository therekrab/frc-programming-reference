2.3 - Subsystems
================

Breaking up robot parts
-----------------------

Robots are made up of lots of different things, and as programmers, it's our
job to make those things do actions. However, a problem quickly arises: what is
the best way to divide up physical parts on a robot?

WPILib (or vendor-specific libraries) offer classes to represent objects such
as motors, encoders, and servos, but we can make our lives easier if we
structure parts of code around slightly "larger" or more abstract parts of the
robot.

This is where we get idea of a **subsystem** - a part of the robot that we
represent in code that represents a "feature" of the robot rather than its
mechanical implementation.

For example, let's consider a shooter on the robot. For example, we'll say
that the shooter system consists of two motors. In code, we wouldn't want to
create a subsystem for each of these motors themselves - those subsystems focus
too much on the hardware of the robot instead of the actual thing we're trying
to encode in the program: the shooter!

A better subsystem here would be a "shooter" subsystem for a few reasons.
Firstly, it represents an actual feature of the robot - the shooter. That is
more than just an arbitrary motor; instead, it's a collection of hardware
components that *do something*. A single motor spins. Two motors can also spin.
But a shooter *shoots*. In code, this is preferable because it focuses on more
of a **declarative** structure than an **imperative** design.

Declarative vs imperative programming
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**Declarative** programming is where we can express *what* we want something to
do. For example, I could say "Make a PB&J sandwich" to produce a peanut butter
and jelly sandwich.

**Imperative** programming is the opposite. Imperative programming focuses more
on the *how* than the *why*. For example, "Spread peanut butter on one side of
a loaf of bread. Then spread jelly on one side of a separate slice of bread.
Then touch the two slices together such that the peanut butter and jelly are
touching" is an :term:`imperative` command.

The main problem with :term:`imperative` programming is that it distracts us from what
we're actually doing. Programming is an art of encoding real-world behavior
into code. If possible, I'd prefer to focus on *what* I'm doing and what the
goal is here (make a meal) versus *how* exactly I'm going about such a task.

In our example here, we are going to need to at some point need to work with
the motor objects directly. However, we've got two options as to *when* we want
to do this:

1. When we want to shoot a gamepiece, we'll just have to call
   ``motor.setVoltage(12.0)`` (or whatever voltage) to spin up the motor.

2. We could also create some sort of in-between class, say `Shooter`, and
   instead make a call to ``shooter.shoot()``.

These options could do the same thing, but one of them focuses on *what* we're
doing (setting a motor's voltage to 12V) while the other option focuses on
*what* that accomplishes (shooting a gamepice).

The logic is easier to follow if we say "to score the gamepiece, we need to
shoot it" rather than "to score the gamepiece, we have to apply some voltage to
this motor here".

.. note:: It's also *safer* to call ``shooter.shoot()`` because we don't
   require anything outside of the shooter subsystem to know what voltage the
   shooter motor needs to properly score the gamepiece. We could introduce a
   bug if a different programmer says ``shooterMotor.setVoltage(2.0)`` and the
   piece doesn't score.

What subsystems do
------------------

.. note:: This focuses on the *concept* of the subsystem, not any particular
   subsystem.

Creating a layer in code between hardware objects (motors, sensors, etc.) and
external robot code is beneficial for a variety of reasons.

Firstly, subsystems provide encapsulation of protected hardware. Subsystems
ensure that we can't just demand arbitrary outputs from hardware, such as
setting a voltage to 100V or just stopping a motor externally.

Next, there is now a big difference between *how* the subsystem works under the
hood and *what* it does. Back with our shooter example, we separate the code
for shooting from the code that uses it. This makes it easier to change the
internal implementation of the subsystem. If we remove one of our motors for
the shooter and we're left with just one motor powering it, we only need to
change the shooter subsystem's internal implementation. Everywhere else, we
were only calling ``shoot()`` so the behavior of that call does the same thing.
This makes changing the inner workings of the subsystem *very easy*.

This also means that we don't *allow* access to subsystem hardware outside of
the subsystem. No code outside of the subsystem needs to know about the actual
hardware that the subsystem uses - instead, it only cares about what the
subsystem *as a whole* can do.

Finally, this is a much more :term:`declarative` style of programming. Although
we do have to be :term:`imperative` when we program the inner workings of the
subsystem, it becomes easier to deal with *externally* (outside of that class).
Declarative programming is more flexible, because it separates *what* is being
done from *how* it is happening.

How to create a subsystem
-------------------------

In code, we can create a subsystem by simply extending WPILib's
``SubsystemBase`` abstract class. The benefits that this gives us are explained
later when we discuss using commands to represent robot actions.

The most important thing that this gives us - for now - is the introduction of
a ``periodic()`` method that we can override. The ``periodic()`` method on a
``Subsystem`` is called once every robot loop. This is helpful.

Subsystem best practices
------------------------

Protect your hardware
~~~~~~~~~~~~~~~~~~~~~

Hardware in a subsystem should *always* be declared as ``private``. Subsystems
are a protected interface to a robot's mechanical components; don't leak those
motors and sensors outside of the subsystem.

This also makes sure that the hardware that makes the subsystem work isn't
relevant outside of the subsystem itself. For example, an intake subsystem
should *not* expose a sensor that determines if a gamepiece is present in the
robot. Not as bad would be to publish a method ``getSensorTriggered()`` that
returns the value of reading said sensor. However, this method should be
renamed to something that actually describes the *meaning* of the data, such as
``hasGamepiece()``. This is not coupled to the hardware that makes this work,
and makes it obvious what this data actually represents.

Don't allow infinite states
~~~~~~~~~~~~~~~~~~~~~~~~~~~

In order to disallow direct access to hardware, subsystems don't have methods
like ``setVoltage()`` on them. These methods are too dependent on the actual
hardware and they don't convey the *reason* for this. A naive solution is to
instead make the ``setVoltage()`` method private (it is a useful helper; don't
remove it entirely) and create a method ``setReference()`` which takes a
position as a reference.

The subsystem doesn't allow the external user to know *how* the position will
be reached, but instead only promises that the position will be reached - the
goal of the method. So is this a good method to expose on a subsystem?

No, and here's why. This still requires knowledge about the physical hardware
to make these calls. The position values are meaningless numbers outside of the
subsystem, so a call to ``setReference(3.53)`` in your code looks weird and a
reader can't tell that ``3.53`` is just the stow position of the subsystem.

This also isn't great because it's hard to verify that the value we were given
is an actual, reasonable value for the specific hardware.

A solution to both problems is to instead create a method ``stow()`` which may
call the internal ``setReference()`` method with the correct input. Then,
create more small, public methods like this for each other *valid* state the
subsystem should be in. This makes sure that from an external standpoint, the
calls to these methods are both understandable and safe.

There are other alternatives besides creating a new method for each possible
state, but those are more advanced and won't be covered in this section.

Make no writes in ``periodic()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``periodic()`` method always runs, so there's no way to tell if we *should*
be making a write to a subsystem in the ``periodic()`` method (or at least, no
good way). If something else wants to write to the subsystem, but
``periodic()`` also writes to the same motor or device, problems will occur
resulting in undefined behavior.
