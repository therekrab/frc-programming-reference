Glossary
========

.. glossary::

   UB

      UB, short for undefined behavior, refers to a type of bug that causes
      the program to be in an unknown state, or a state that violates the rules
      it was intended to abide by. For example, if a program tried to refer to
      a value in memory that didn't exist, the value that they read would be
      unknown and not deterministic. Therefore, any attempt to process that
      value would result in undefined behavior.

   interruption behavior

      The behavior of a command when it is interrupted by another command due
      to a requirement conflict. In code, this is represented by the
      ``InterruptionBehavior`` class.

   reference

      A system's reference is the target point where it wants to be; this is
      the "goal state". In terms of a feedback controller, the reference is the
      point that corresponds to zero error.

   declarative

      Declarative programming is a style that focuses on the *intent* of the
      code written, rather than the *implementation*. For example,
      ``subsystem.setVoltage()`` could be made more declarative if it instead
      said ``subsystem.scorePiece()``. Now you see *why* we're setting the
      voltage.

      Declarative programming is the opposite of :term:`imperative` programming.

   imperative

      Imperative programming is a style that focuses on the implementation of
      behavior rather than the intent of the behavior. This focuses on the
      *how* rather than the *why*, unlike :term:`declarative` paradigms.

      Imperative programming is generally necessary at some part of your
      program, but it's often better to bury it down with lower-level code
      using abstractions. For example, we always have to set a motor voltage to
      score a piece, but we should do that as early as we can -
      ``subsystem.scorePiece()`` is better than continuing the imperative
      programming on with a ``subsystem.setVoltage()`` method.

   uncommanded

      Uncommanded refers to the state of a subsystem in which there is no
      currently scheduled command that has requirements on that subsystem.

   bind

      In the context of triggers and commands, binding refers to the process of
      associating a command with a trigger such that when the trigger is
      triggered, the command is scheduled.

      You can bind commands to button presses, bind commands to changes in
      robot state, or to any arbitrary trigger.
