3.1.3 - Binders
===============

WPILib recommends putting button bindings and command initialization and logic
in ``RobotContainer`` along with instantiating subsystems. This is too much
responsibility for this one class.

We've learned how to separate the logic of a command (``ES`` classes) from its
instantiation (with ``superstructure.enter()``). Now we can also move button
bindings out of ``RobotContainer`` to group them by binding kind.

Let's consider a ``Binder`` interface:

.. code-block:: java
   :caption: Binder.java

   public interface Binder {
     void bind(Superstructure superstructure);
   }

This declares a simple interface that ensures a method ``bind()`` which accepts
a ``Superstructure`` instance. This means that we can call
``superstructure.enter()`` to create commands, but we cannot create new
commands inside of the ``Binder`` classes. This enforces separation of
responsibility at the compiler level, which ensures a consistent codebase.

Binders are responsible for creating ``Trigger``\s for input devices, such as
controllers. However, there also can be binders that use public ``Trigger``\s
on the ``Superstructure`` class.

Why an interface?
-----------------

In ``RobotContainer``, we can store all of our binders as ``Binder`` instances
rather than their class. This lets us make very fast changes - perhaps even on
the fly - to decide which button bindings we use. This is called the *strategy*
design pattern.

For example, we could have two methods of driving the robot - one using a PS5
controller, and another utilizing a XBox controller. In code, we can make a
decision on the fly (such as reading a value from SmartDashboard or just using
a constant enum value, and swap in/out binders without any problems in code.

Limited composition
-------------------

We've already established that we shouldn't create command logic where we bind
buttons. *However*, this doesn't mean we can't wrap commands or use decorators.

Here's what's acceptable:

- Changing the behavior of a button with ``Commands.either()`` (for example, a
  score command that needs to know which piece to score). This is okay because
  there are two distinct states that each need to be triggered by the same
  button. It doesn't change the actual command logic, but only helps reduce the
  number of button bindings.

- Only running a command conditionally with ``onlyIf()`` or ``unless()``. Be
  careful here though; just because this is "permitted" doesn't mean this is
  the best place to ensure initial conditions for commands are ran. The logic
  to ensure that a state's initial conditions are valid should be done in the
  state itself.

Here's what should *not* occur in these button binding classes:

- Changing a command's end conditions with ``onlyWhile()`` or ``until()``. This
  should be done in the state. If a command should end when the button is
  changed (unpressed), then use ``whileTrue()`` over ``onTrue()``.

- Sequencing commands. These should either be put into one state, or should be
  bound more loosely with triggers. Often times, the first option is better.

Overall, the basic guideline is to *avoid* state logic and instead only do
*binding* logic.

Non-button bindings
-------------------

There's nothing stopping you from using ``Binder``\s to also bind robot
behavior to other triggers. In fact, that's recommended to add more features to
a robot. For example, you could have the following binding as well
(``handoffReady()`` returns a ``Trigger``):

.. code-block:: java
   :caption: In ``bind()`` method

   superstructure.handoffReady().onTrue(superstructure.enter(new Handoff()));

This allows us to attach robot actions to a change in state that may not be
tied to one single other action. It actually is a great design pattern to have
this style of binding - when the robot enters a state in which an action is
ready, that action occurs automatically. This makes it very easy to add more
automation to a robot without much code clutter.

