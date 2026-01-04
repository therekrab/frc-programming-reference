3.1.4 - State, Theory
=====================

.. note:: This is a theoretical document explicity defining some key terms and familiarizing you with some concepts.

.. attention:: I'm no certified expert in CS; thus, I'll provide my own names
   for some concepts/designs. Other names may exist, but I stumbled across
   these ideas myself and therefore are not aware of their "formal names".
   There's no reason for these particular names besides "I figured it sounded
   okay *and* captures the idea well".


What is *state*?
----------------

The idea of "state" comes up a lot as software projects get increasingly
complex. In the context of robotics programs, *state* is a term that describes
the current configuration of the robot. This could be any of the following:

- Hardware state, such as motor position/velocity

- Sensor inputs, such as CANranges or IR sensors.

- Software variables, such as desired position/configuration or match time.

All of these need to be accesed by the robot program to make decisions
dynamically.

Note that state is mutable. This means that it can change, and that change
needs to be considered in the robot program.

Categorical or quantitative?
----------------------------

State, like other variables, can be described as either *categorical* or
*quantitative*. Categorical state is a situation where the variable in question
\- some state variable in our case - happens to be a member of a discrete set
of possible values. Each value is its own thing, and we can't add, multiply,
divide, or perform any "combination" of operations (think of fruits; you can't
add "apple" to "banana" and get a reasonable value).

Quantitative variables, on the other hand, are described as a numeric value.
They don't have to be on a continuous scale, but we can combine them through
well-defined operations like addition. For example, a variable of type ``int``
would be quantitative - we can add, subtract, multiply, etc. integers and get
more integers.

Categorical variables end up being more useful for a few reasons:

1. We're constrained to an explicitly defined list of possible variants. If I
   have an enum ``ElevatorSetpoint`` with two variants ``Stow`` and ``Raise``,
   I know that any value of type ``ElevatorSetpoint`` is either ``Stow`` or
   ``Raise``. If I try to pass in a different value (say ``3.414``), it won't be
   an ``ElevatorSetpoint``, so the compiler will never let that code compile.
   Compare this to a ``double`` value, where I can pass in an arbitrary number
   and there's no intrinsic checking going on to make sure that this value is
   reasonable. We *could* write code to check this ``double`` value to make
   sure it's okay, but that's an extra step that we could forget.
  
2. Categorical variables have more meaning in code. Continuing off the previous
   example, a variable of type ``ElevatorSetpoint`` is clear about its
   intentions and limited in scope of applicability. I'll use it when I need to
   set the setpoint for an elevator, and that's the only type of data it
   stores. This makes it easy to read through the code and see the actual
   meaning of each value rather than just an ``int`` or ``double``. I can't
   pass a ``ShooterSetpoint`` into a method that expects an
   ``ElevatorSetpoint``, even if under the hood they both store ``float``\s.

3. We can abstract away unnecessary information and leave a more
   :term:`declarative` external API for our code. Rather than giving the client
   the responsibility to determine the correct setpoint in motor units, we can
   just use an enum with that value hard-coded in, in private interal code,
   without the rest of the code needing to worry about the internal
   implementation, or the "magic numbers" that make the code work. This also
   follows the principles of :term:`encapsulation`.

Booleans are also a form of categorical state; however, sometimes an enum
``GameModeStatus`` (either ``Auton`` or ``Teleop``) is better than a boolean
``isAuton``, for the reasons described in (2) above (it adds meaning for both
the human and the compiler).

Compound state
--------------

At a high level (robot-wide, where each subsystem is properly abstracted and
encapsulated), categorical state dominates. But what's the best way to organize
and represent categorical state? This section focuses on how we can aggregate
all of our state data from each subsystem into a class representing state for
the robot. Doing a good job here makes our lives easier in the future, because
it lets us select a natural way to represent state that removes edge cases
later.

Most of the time, a robot-wide state variable (such as ``scoringReadiness``) is
dependent on multiple smaller state variables that were obtained from each
individual subsystem. What we're really looking for is a state variable that
conveys the intersection of multiple, smaller, state variables. For this
example, let's say that we've got two categorical state variables:
``GamepieceStatus`` with the variants ``Empty``, ``Staged``, and ``Secured``;
and ``elevatorPosition`` with the variants ``Stowed``, ``ScoreReady``, and
``Traveling``. If we assume that each state variable is *independent*, then we
have a total of nine possible combinations of these two variables. There's
three options for ``GamepieceStatus`` and three options for
``elevatorPosition``. Here, we could use what is simply called **aggregate
state**, which involves relaying each state variable independently to the user.
In our case, this would mean having two fields or methods in our robot state
class: one for the gamepiece status and one for the elevator's position. This
is pretty simple, but is it always the best?

However, let's look at a different set of two categorical variables:
``GamepieceStatus``, with all three fields the same, and a new
``EffectorStatus`` which represents the state of our end effector, with the
variants ``Idle`` and ``Scoring``.

Here, if we used aggregate state like last time, we would have a total of six
possible state combinations. Let's visualize those options using a table:

.. list-table::
   :header-rows: 0
   :width: 50%

   * -
     -  ``Empty``
     - ``Staged``
     - ``Secured``

   * - ``Idle``
     - Empty and idle
     - Staged and idle
     - Secured and idle

   * - ``Scoring``
     - Empty and scoring
     - Staged and scoring
     - Secured and scoring

Here, something doesn't seem right. We've got some combinations in our system
that don't make sense. How can we be scoring a gamepiece *while* there is no
gamepiece present? If a gamepiece is only staged, how is it scoring? This
visualization shows one of the major downsides of aggregate state: we can't
filter out unreasonable values or combinations that seem fine individually, but
are paradoxical or impossible when you look at multiple state variables. There
must be a better way.

This second option of state description is called **compound state**, which is
a *new* categorical variable that describes the state as *one* variable, rather
than multiple, like in aggregate state.

Here, we could create a new enum ``PieceScoringStatus`` and give it the
following variants:

- ``Empty``: this is the combination of ``GamepieceStatus.Empty`` and
  ``EffectorStatus.Idle``.

- ``Staged``: this is the combination of ``GamepieceStatus.Staged`` and
  ``EffectorStatus.Idle``.

- ``Secured``: this is the combination of ``GamepieceStatus.Secured`` and
  ``EffectorStatus.Idle``.

- ``Scoring``: this is the combination of ``GamepieceStatus.Secured`` and
  ``EffectorStatus.Scoring``.

Note that now, there is no way to represent the impossible state of scoring
without a gamepiece secured. If the robot reaches that point, then we will have to
explicity write code to handle that edge case in the logic to turn the
individual values of ``GamepieceStatus`` and ``EffectorStatus`` into
``PieceEffectorStatus``. Here, explicitly  making that decision is exactly what we
want; we don't want to overlook the edge case now and then realize we had a bug
with it later.

Compound state is a good call when there are combinations of two sub-states
that are invalid, impossible, or contradictory.
