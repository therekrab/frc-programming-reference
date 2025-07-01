Superstructure & Enterable States
=================================

There are a couple issues that still persist with the command-based framework.

This page addresses two concerns:

1. Subsystems shouldn't be accessible except to compose commands. Having too
   much access to the subsystems means that some part of your code that
   *shouldn't* be talking to subsystems is, which can lead to weird behavior.

2. Those commands do *not* belong in ``RobotContainer``. It is, for some
   reason, responsible for *far* too much. It's also got to bind those commands
   to buttons, construct all the subsystems, *and* handle determining the
   correct auto to run. That's too much responsibility.

The ideas presented on this page offer a solution to both problems.

Superstructure
--------------

In code, the **superstructure** is a container for all the subsystems. It is
responsible for protecting the subsystems' access, as will be demonstrated in a
minute.

A superstructure class has a private Subsystems object.

Subsystems
~~~~~~~~~~

A **subsystems object** is a read-only collection of subsystems. In Java, this
would be a record.

It holds each subsystem in a public field, and offers easy access to all of
them.

The superstructure class holds a private instance of the Subsystems object,
which stores all of the subsystems inside of it.

The superstructure can accept all of the subsystems as parameters, and
internally intstantiate the subsystems object.

What this does is simple: it hides an easy-to-access object with all the
subsystems inside - but under a private field in the superstructure. Like a
well-trained spy, the superstructure will never give up its subsystems object,
unless it's to the right kind of person.

Recall that we still have the problem of not having a place to put
multi-subsystem commands.

Enterable states
----------------

**Enterable states** are a specific type of class that represents a full state
that the robot can enter. For example, there may be an ``Intake`` state, or a
``Score`` and ``ScorePrep`` state.

But these are just code representations of a possible robot configuration. How
do we get there? This is where commands come in. Let's make ``EnterableState``
a really simple interface:

.. code-block:: java

   public interface EnterableState {
     public Command build(Subsystems subsystems);
   }

This interface defines just one method, ``build()``, which accepts a subsystems
object as a parameter. This method then returns a command to reach that state.

This means we can declare really simple classes that exist just to group
subsystem-specific commands into a command that applies to the entire robot.

But how does an instance of this class get the private subsystems object?

Enterable states and the superstructure
---------------------------------------

The superstructure object should have one method called ``enter()``, which
accepts implementers of the ``EnterableState`` interface. Then, ``enter()``
calls the state's ``build()`` method with the private subsystems object.
Then, ``enter()`` can return the built command, after doing any processing.

.. note:: To avoid repetitive code, ``enter()`` may do things such as set the
   name of the resultant command to be the simple class name of the state.

This process results in a system that only allows enterable states to get
access to the subsystems object, but anybody with access to the superstructure
can create these commands.

You can call ``superstructure.enter(new Intake())`` anywhere. But all the logic
for how the ``Intake`` state works *cannot leave the state itself*. This is
because adding any other subsystem behavior requires the subsystems object,
which the caller of ``enter()`` doesn't have access to. Therefore, they can do
simple things, such as add a timeout, but they *are unable to fundamentally
change the behavior of the resultant command*.

This is immensely powerful because it means that the command logic for each
state lives in the state itself, and any attempt to break these rules (without
altering the Superstructure class itself) will result in a *compiler error*
because you can't access the private subsystems object.

This is compiler-enforced code design, so it ensures consistency across the
project, and ensures all programmers know exactly where the logic for commands
should go.

This design ensures that the *only* access to subsystems comes directly from
the superstructure itself, and nobody else can touch the subsystems. They can't
register default commands, nor can they extract subsystem-specific commands,
which may not be safe to run on the robot without other subsystems. This
protects the code and the robot.

The code is also not exceptionally verbose, and easy to change and organize.

For example, we can have a group of related states all together in the same file:

.. code-block:: java

   public class ManyStates {
     public class StateOne implements EnterableState {
       public Command build(Subsystems subsystems) {
         /* snip */
       }
     }
     public class StateTwo implements EnterableState {
       public Command build(Subsystems subsystems) {
         /* snip */
       }
     }
     public class StateThree implements EnterableState {
       public Command build(Subsystems subsystems) {
         /* snip */
       }
     }
   }

Here, we define three states. Although they don't *have* to be related, it
helps to organize code like this.

Proxying commands from ``enter()``
----------------------------------
