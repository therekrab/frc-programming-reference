Glossary
========

.. glossary::

   UB

      UB, short for **undefined behavior**, refers to a type of bug that causes
      the program to be in an unknown state, or a state that violates the rules
      it was intended to abide by. For example, if a program tried to refer to
      a value in memory that didn't exist, the value that they read would be
      unknown and not deterministic. Therefore, any attempt to process that
      value would result in undefined behavior.

   interruption behavior

      The behavior of a command when it is interrupted by another command due
      to a requirement conflict. In code, this is represented by the
      ``InterruptionBehavior`` class.
