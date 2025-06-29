Constants
=========

I'll kick this section off with an example (non-FRC). Say we're making some
software that needs to compute the distance travelled when a 1 foot wheel
rotates X times.

We could write a function like this:

.. code-block:: java

   public double getDistanceTravelled(double rotations) {
     return rotations * Math.PI;
   }

This works fine, but then we notice something: our wheel isn't 1 foot! It's 1.1
feet.

.. code-block:: java

   public double getDistanceTravelled(double rotations) {
     return rotations * Math.PI * 1.1;
   }

This is the quickest way to account for this. But there's one issue - unless we
leave a comment in the code telling the reader what this number means, the
``1.1`` in code is just a weird floating-point value in code, and a reader
could misunderstand its purpose.

But a comment isn't the best way to go about this. It's better to have code
that reads like it works than code that requires explanation.

We call these values (values that show up in code and sometimes need more
explanation) **magic numbers** because they are necessary for the program to
function, but they can be seen as "without reason" or "mysterious".

If it's not obvious, mysterious code isn't good. Our solution is to extract the
magic number into its own variable. More specifically, we want to create a
**constant** - a bit of information about the system the code will run on that
is required to run, *but won't change as the program runs*.

Here's how we could implement that in our simple codebase:

.. code-block:: java

   public static final double WHEEL_DIAMETER = 1.1;

   public double getDistanceTravelled(double rotations) {
     return rotations * Math.PI * WHEEL_DIAMETER;
   }

The good effects here are twofold. For one, we have code that reads easier and
shows its functionality, rather than a comment that tells the reader what the
code does.

Secondly, we allow other parts of the code to use this value too. If we wanted
to create another method ``getWheelRotations()`` (implementation not shown)
which accepted a ``distance`` parameter, we would need to use the wheel radius
constant again. That leads to repetition, so we would *definitely* want to
avoid using magic numbers.

If we have the same magic number in two places in code, and they mean the same
thing, if the real-world value that represents changes in any way, we have to
remember *everywhere* in code that value was used.

This isn't the case if we extract those values to constants.

With constants, we know we have to change the value *once*, and then that
constant gets used everywhere that value is used.

What constant represent
-----------------------

There are a variety of types of constants, but the major two are **behavior
configuration constants** and **physical configuration constants**.

Behavior configuration constants are constants that affect the behavior of the
system (for example, if we have a shooter subsystem, we want to know how fast
to spin the motor to shoot a gamepiece.

Motion profiling constants, such as max velocity, acceleration, etc. are
considered behavioral configuration constants, along with PID values and other
"tuning" numbers.

These constants can be changed to vary the robot's behavior. Normally, you
settle on a behavior configuration you like, and then don't change the
constants any more, but that's besides the point. When building the robot, *you
are free to vary these values*.

On the other hand, there's physical configuration constants, which refer to the
actual hardware being used. For example, you have motor and sensor CAN IDs,
robot dimensions, field constants (location of important things on the field),
etc.

These types of constants reflect the physical systems that the code represents,
and they are *never* changed unless their underlying system is, too.

You can't go in code and change up CAN IDs and expect the robot to perform a
little differently.

There is one more type of constant that is commonly used in code, however:
**state constants** are normally public enums that represent the possible
states of the subsystem. These normally are used instead of numbers or booleans
as inputs to methods like ``setReference()``, because they ensure only a finite
set of states are possible for a subsystem.
 
Constant locations in code
--------------------------

In our simple example, we just put the constant field outside the function
class. But that's not the best approach for FRC. We've often got tens of files,
and putting each constant in with the code that uses it isn't great.

Instead, constants belong in a separate file. But don't throw all robot
constants into one file. I've done that, and it ended up being over a thousand
lines of code.

.. warning:: Too much code in a file is often a sign that there's too much
   happening on one file/class. I'd put a soft cap at around 400 LoC (lines of
   code) before you should start to ask yourself, *is this all for one thing,
   or can I split this up?* Chances are, you're probably going to be able to
   separate the logic for whatever you're doing into multiple files & classes.

Instead, we want to separate and organize constants by what they relate to. For
subsystem-specific constants, they belong with the subsystem that they
associate with. 

But we don't want a ``subsystems`` directory with all the subsystem logic as
well as all the associated constants jumbled in as well. Instead, we should
create smaller packages (subfolders) for each subsystem.

This lets us separate code by subsystem more easily, and can create a nicer project structure.

This has too many files floating around:

.. code-block::

   subsystems/
     Drivetrain.java
     DrivetrainConstants.java
     Intake.java
     IntakeConstants.java
     Shooter.java
     ShooterConstants.java

This has a lot going on, and more importantly, it exposes too much scope. There
is really *no* good reason as to why the intake needs access to
``DrivetrainConstants``. With this approach, if ``Drivetrain`` can raad it, then so
can ``Intake`` - not always what we want.

Let's try this:

.. code-block::

   subsystems/
     drivetrain/
       Drivetrain.java
       DrivetrainConstants.java
     intake/
       Intake.java
       IntakeConstants.java
     shooter/
       Shooter.java
       ShooterConstants.java

This is nicer. We separate code through packages by functionality, and we set
ourselves up to properly limit access on the constants.

Non-subsystem-specific constants
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It's rare to have constants that are shared between multiple subsystems, or are
not associated with any one subsystem in particular. But it is possible to want
constants that are accessible from all throughout the codebase and you don't
want them associated with any particular subsystem.

In this case, there exists a ``Constants`` class in the source root directory
that serves this purpose.

.. note:: There's not a lot of robot functionality that requires constants like
   this. If you find yourself with the need to scroll through the ``Constants``
   class, something's not right.

Constant visibility and scope
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. caution:: This is a bit of a controversial idea. Lots of people believe that
   constants should *always* be declared as ``public``, making the claim that
   they're ``final`` and don't offer the possibility of harm. But I disagree.

Constants are important, but only to the parts of code they apply to. For this
reason, *most* constants (especially those that are subsystem-specific), should
be contained in the subsystem's package as to avoid leaking scope.

There's no real reason that another subsystem would *ever* need access to most
of the constants from our subsystem. Consider an ``Intake`` subsystem, and an
``Arm`` subsystem. What good reason is there for the intake's current limit to
be exposed to the arm? Why does the arm care? Answer: it doesn't.

Exposing all subsystem constants also can violate the "black box" principle we
setup in :doc:`subsystems`. If we expose constants like current limits, CAN
IDs, etc., we expose information about how the subsystem works. Nobody outside
the subsystem has any real reason to know the CAN ID of the motors that we use
\ - remember that only the subsystem itself is responsible for connecting with
hardware.

But there are still some valid - but rare - reasons to mark *some* fields in
subsystem-specific constants ``public``. So don't consider marking constants
``public`` a bad idea.

Instead of providing a method like ``setPosition(double position)`` on an
elevator, we should probably limit the values that we're allowed to enter.

