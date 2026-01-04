3.1.3 - Binders
===============

WPILib recommends putting button bindings and command initialization and logic
in ``RobotContainer`` along with instantiating subsystems. IMO this is too much
responsibility for this one class.

We've learned how to separate the logic of a command (``CommandBuilder``
classes) from its instantiation (with ``superstructure.build()``) as well as
subsystem construction (via a ``Superstructure`` class). Now we can also move
button bindings out of ``RobotContainer`` to organize them by binding kind.

Let's consider a ``Binder`` interface:

.. code-block:: java
   :caption: Binder.java

   public interface Binder {
     void bind(Superstructure superstructure);
   }

This declares a simple interface that ensures a method ``bind()`` exists which
accepts a ``Superstructure`` instance. This means that we can call
``superstructure.build()`` to create commands, but we cannot create new
command logic inside of the ``Binder`` classes. This enforces separation of
responsibility at the compiler level, which ensures a consistent codebase.

Binders are responsible for creating ``Trigger``\s for input devices, such as
controllers and constructing those HID objects.

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
This also just encourages consistency in how we write the ``Binder`` classes,
which means multiple programmers won't be confused looking at each other's
code.

Limited composition
-------------------

We've already established that we shouldn't create command logic where we bind
buttons. *However*, this doesn't mean we can't wrap commands or use decorators.

Here's what's acceptable:

- Changing the behavior of a button with ``Commands.either()`` (for example, a
  score command that needs to know which piece to score). This is okay because
  there are two distinct commands that each need to be triggered by the same
  button. It doesn't change the actual command logic, but only helps reduce the
  number of button bindings.

- Only running a command conditionally with ``onlyIf()`` or ``unless()``. Be
  careful here though; just because this is "permitted" doesn't mean this is
  the best place to ensure initial conditions for commands are ran. The logic
  to ensure that a command's initial conditions are valid should be done in the
  command builder class itself.

Basically, the conditions should be based on the *button* state, not the state
of the robot (that should be built into the command builder class itself.

Here's what should *not* occur in these button binding classes:

- Changing a command's end conditions with ``onlyWhile()`` or ``until()``. This
  should be done in the command builder class. If a command should end when the
  button is changed (unpressed), then use ``button.whileTrue()`` over
  ``onTrue()``.

- Sequencing commands. These should either be put into one command builder.
  Remember: classes that implement ``CommandBuilder`` should represent
  *complete* actions the robot can take. We shouldn't have to group those
  actions together when we bind buttons. If you want to run two independent
  commands in a composition, don't create the composition when binding buttons.
  Instead, consider just building another command builder class.

Overall, the basic guideline is to *avoid* command logic and instead only do
*binding* logic.

Non-button bindings
-------------------

There's nothing stopping you from using ``Binder``\s to also bind robot
behavior to other triggers. In fact, that's recommended to add more features to
a robot. For example, you could have the following binding as well
(say ``handoffReady()`` returns a ``Trigger``):

.. code-block:: java
   :caption: In ``bind()`` method

   superstructure.handoffReady().onTrue(superstructure.build(new Handoff()));

This allows us to attach robot actions to a change in state that may not be
tied to one single other action. It actually is a great design pattern to have
this style of binding - when the robot enters a state in which an action is
ready, that action occurs automatically. This makes it very easy to add more
automation to a robot without much code clutter.
