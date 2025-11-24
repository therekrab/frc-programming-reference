3.1.4 - Command Builders
========================

.. danger:: This page is currently in alpha and has never been tested. This is
   more of an idea than a finished product.

What is *state*?
----------------

The idea of "state" comes up a lot as software projects get increasingly
complex. In the context of robotics programs, *state* is a term that describes
the current configuration of the robot. This could be any of the following:

- Hardware state, such as motor position/velocity

- Sensor inputs, such as CANranges or IR sensors.

- Software variables, such as desired state or match time.

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
   ``Raise``. If I try to pass in a different value, it won't be an
   ``ElevatorSetpoint``, so the compiler will never let that code compile.
   Compare this to a ``double`` value, where I can pass in an arbitrary number
   and there's no intrinsic checking going on to make sure that this value is
   reasonable. We *could* write code to check this ``double`` value to make
   sure it's okay, but that's an extra step that we could forget.
  
2. Categorical variables have more meaning in code. Continuing off the previous
   example, a variable of type ``ElevatorSetpoint`` is clear about its
   intentions and limited in scope of applicability. I'll use it when I need to
   set the setpoint for an elevator, and that's the only type of data it
   stores. This makes it easy to read through the code and see the actual
   meaning of each value rather than just an ``int`` or ``double``.

3. We can abstract away unnecessary information and leave a more
   :term:`declarative` external API for our code. Rather than giving the client
   the responsibility to determine the correct setpoint in motor units, we can
   just use an enum with that value hard-coded in without the client needing to
   worry about the internal implementation, or the "magic numbers" that make
   the code work. This also follows the principles of :term:`encapsulation`.

Representing state
------------------

.. attention:: I'm no expert in CS; thus, I'll provide my own names for some
   concepts/designs. Other names may exist, but I stumbled across these ideas
   myself and therefore are not aware of their "formal names". There's no
   reason for these particular names besides "I figured it sounded cool *and*
   captures the idea well".

At a high level (robot-wide, where each subsystem is properly abstracted and
encapsulated), categorical state dominates. But what's the best way to organize
and represent categorical state? This section focuses on how we can aggregate
all of our state data from each subsystem into a single object representing
state for the robot. Doing a good job here makes our lives easier in the
future.

What we're really looking for is a complete state description that conveys the
intersection of multiple, more specialized, states. For this example, let's say
that we've got two categorical state variables: ``GamepieceStatus`` with the
variants ``Empty``, ``Staged``, and ``Secured``; and ``elevatorPosition`` with
the variants ``Stowed``, ``ScoreReady``, and ``Traveling``. If we assume that
each state variable is *independent*, then we have a total of nine possible
combinations of these two variables. There's three options for
``GamepieceStatus`` and three options for ``elevatorPosition``. Here, we could
use what is simply called **aggregate state**, which involves relaying each
state variable independently to the user. In our case, this would mean having
two fields on our robot state object: one for the gamepiece status and one for
the elevator's position. This is pretty simple.

However, let's look at a different set of two categorical variables:
``GamepieceStatus``, with all three fields the same, and a new ``ScoreStatus``
which represents the state of our end effector, with the variants ``Idle`` and
``Scoring``.

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
filter out unreasonable values or combinations that are paradoxical or
impossible. There is a better way.

This second option of state description is called **compound state**, which is
a *new* categorical variable that describes the state as *one* variable, rather
than multiple, like in aggregate state.

Here, we could create a new enum ``PieceScoringStatus`` and give it the
following variants:

- ``Empty``: this is the combination of ``GamepieceStatus.Empty`` and
  ``ScoreStatus.Idle``.

- ``Staged``: this is the combination of ``GamepieceStatus.Staged`` and
  ``ScoreStatus.Idle``.

- ``Secured``: this is the combination of ``GamepieceStatus.Secured`` and
  ``ScoreStatus.Idle``.

- ``Scoring``: this is the combination of ``GamepieceStatus.Secured`` and
  ``ScoreStatus.Scoring``.

Note that now, there is no way to represent the impossible state of scoring
without a gamepiece secured. If the robot reaches that point, then we will have to
explicity write code to handle that edge case in the logic to turn the
individual values of ``GamepieceStatus`` and ``ScoreStatus`` into
``PieceScoreStatus``. Here, explicitly  making that decision is exactly what we
want; we don't want to overlook the edge case now and then realize we had a bug
with it later.

Compound state is a good call when there are combinations of two sub-states
that are invalid, impossible, or contradictory.

Finally, we can represent a complete robot state as a combination of aggregate
state and compound state. Consider the following object from our example:

.. code-block:: java

   public record RobotState(
     GamepieceStatus gamepieceStatus,
     ElevatorPosition elevatorPosition,
     PieceScoreStatus pieceScoreStatus,
   ) {}

This simple container of robot state uses both aggregate state (the fields
``gamepieceStatus`` and ``elevatorPosition``) as well as compound state (the
field ``pieceScoreStatus``). Both can be used in parallel, as they each have
different use cases.

.. note:: We probably shouldn't use a ``record`` here. In the next step, we're
   going to take the next step with tracking state transitions and we may not
   always want to expose *all* of our inner fields. Plus, it'll be nice to
   allow us to have mutable state rather than rebuilding a new ``record`` each
   time the state changes.

   Using a ``record`` here only makes the point more clear without as much
   boilerplate.

Detecting changes in state
--------------------------

If we're creating a ``RobotState`` object, we may want to be able to create
``Trigger``\s to bind to changes in state. There's two ways to do this:

1. Have the user create their own ``Trigger`` object for whatever criteria they
   want.

2. Create some ``Trigger`` objects and attach it to the ``RobotState`` class.

The latter option is probably better design, because it keeps similar code
(code that interfaces with the robot state) together rather than making each
different use of the ``RobotState`` class create their own ``Trigger``\s.

So, let's give our ``RobotState`` class some ``Trigger`` factories that return
useful triggers.

.. code-block:: java
   :caption: RobotState.java

   /* snip */

   public Trigger gamepiecePresent() {
     return new Trigger (() -> !this.gamepieceState.equals(GamepieceState.Empty);
   }

This creates a simple ``Trigger`` factory that produces a trigger corresponding
to whether the robot has a piece or not. Here, the variants ``Staged`` and
``Secured`` both consider the gamepiece to be a "present piece", but this
behavior could be changed easily.

Setting state
-------------

Now that we can read from the state object, it's important to consider *where*
these values are coming from. There's two important locations.

Subsystems
----------

This is the easier part - we've already considered how subsystems should expose
state. They can just provide a public ``Trigger`` factory, and then - because
that trigger object is *always* valid - we just need to construct it once and
hand it to the robot state object at initialization.

But how should that handoff happen? We *could* provide a public method on the
robot state class, such as ``setXXXTrigger`` or ``setXXXSupplier``, for
non-boolean objects.

But this may not be ideal. We don't need to provide access to these setters
publicly, nor should we just allow a user to swap triggers whenever they want.
Plus, that's a lot of boilerplate.

Instead, we can just pass each subsystem - or even better, the ``Subsystems``
record - into the constructor of our robot state class, and the constructor can
access the public fields of each subsystem to easily create unchangeable and
correct fields.

Software
--------

The other, often overlooked source of "state" is from the software itself - not
any hardware inputs, but just from the state of the robot program. The robot
program needs to update its own state rather than read its state from anywhere.
This is a completely new application of state that makes this architecture even
more powerful.

However, it's hard because there may be different locations in software that
need to change this value. This means that we need a way to read this value as
well as a way to set this value.

