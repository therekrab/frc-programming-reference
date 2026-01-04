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

   client

      When we're programming something that can be used somewhere else (e.g.
      subsystems, or any class besides ``Main``), we refer to the client as the
      part of the code that uses our code. From different viewpoints, clients
      can be different. For example, a subsystem's clients are parts of code
      that use that subsystem's published functionality. However, from the
      perspective of a vision/localization tool, anywhere in code where that
      class is used is now the client.

   PV
   
      PhotonVision is vision software for FRC robots. It can track gamepieces,
      colored blobs, or use April Tags on the field to determine an estimated
      robot position.

   bandaid code

      Bandaid code is code that is intended to solve a problem in a temporary
      manner. It may limit functionality, or be generally "bad code" - i.e. is
      hard to update or change without significant refactoring.

   GIGO

      A useful principle in computer science is GIGO, or "Garbage In, Garbage
      Out". This suggests that if we feed invalid inputs to a software
      component, the output from that component cannot be any more correct than
      the data that it was given. If you feed a system garbage, it will process
      the bad data, returning an invalid result - whether the system *always*
      spits out garbage is unknown (the system could be fundamentally broken
      too), but if we know the input to the system was invalid, we can't say
      that the component itself is broken, because bad data as an input nearly
      guarantees an invalid output.

   encapsulation

      From `Wikipedia
      <https://en.wikipedia.org/wiki/Encapsulation_(computer_programming)>`_:

        **encapsulation** refers to the bundling of data with the mechanisms or
        methods that operate on the data. It may also refer to the limiting of
        direct access to some of that data, such as an object's components.
        Essentially, encapsulation prevents external code from being concerned
        with the internal workings of an object.

      Following the principles of encapsulation is generally considered good
      practice, because it makes sure that client code doesn't have to worry
      about implementation details or constants that only pertain to the
      business logic.
