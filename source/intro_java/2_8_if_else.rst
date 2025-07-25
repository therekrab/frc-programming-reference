2.8 - If/Else Conditionals
==========================

Let's consider the following bit of logic, in English:

1. If the weather is rainy, we will play a board game.

2. Otherwise, we will play outside.

Now let's think about how we would write this in code. Right now, we could try
something like this, assuming we've already created a ``boolean`` named
``isRainy``:

.. code-block:: java

   if (isRainy) {
     System.out.println("Play a board game!");
   }
   if (!isRainy) {
     System.out.println("Play outside!");
   }

This code works as we expect, but it's not great to read for a few reasons:

1. The code is repetitive. We've got to write out the condition twice.

2. It doesn't *nicely* show that these two things are one or the other, and
   *never both*.

Now Java has a solution for both of these problems. It's called the ``else``
statement, and it lets us run some code if an ``if`` statement's condition is
``false``.

Here's an example of an ``else`` statement to re-do what we did earlier:

.. code-block:: java

   if (isRainy) {
     System.out.println("Play a board game!");
   } else {
     System.out.println("Play outside!");
   }

This is shorter, more readable, and easier to understand. The contents of an
``else`` statement run if the previous ``if`` statement don't.

Let's take a look at another example:

.. code-block:: java

   if (x && y) {
     System.out.println("A");
   } else {
     System.out.println("B");
   }

Under what configuration (what values of ``x`` and ``y``) would ``B`` be
printed out to the screen? Are there multiple combinations that would result in
the same output?

``else if`` statements
----------------------

Returning to our weather example, let's add one more decision - if it's a
Friday, then we want to watch baseball on TV. Here's the new decision list:

1. If it's a Friday, we watch TV.

2. Next, if it's raining, we want to play a board game.

3. Finally, in all other cases, we want to play outside.

.. admonition:: Exclusivity

   Here, we are looking to do *one thing* and one thing *only*. This means that
   we can't watch TV *and* play a board game simultaneously. Therefore, the
   program we write has to only ever pick one.

.. hint:: Try to write some code yourself before we continue to solve the
   problem. Make sure that your code doesn't every allow for multiple
   recomendations.

Here's a basic example using only what we know so far:

.. code-block:: java

   if (isFriday) {
     System.out.println("Watch TV");
   } else {
     if (isRaining) {
       System.out.println("Play a board game");
     } else {
       System.out.println("Play outside");
     }
   }

This code works as we would expect. If it's Friday, then we say we should watch
TV and nothing else runs. In any other case, we check whether it's raining. If
so, then we recommend that the client plays a board game. In that ``else``
block, we finally tell the user that they should play outside.

The problem, however, is that this syntax is verbose - and overly nested. We've
got an ``if`` statement inside of another ``if`` statement, which creates two
levels of indentation. This strategy of nested ``if``-``else`` statements
creates another layer of indentation for each additional condition. This grows
*too fast*.

But don't fret! Java has a solution to this problem, called the ``else if``
statement. It replaces ``else`` statements to be effectively "``else`` with a
condition".

It overall looks like this:

.. code-block:: java

   if (condition1) {
     System.out.println("A");
   } else if (condition2) {
     System.out.println("B");
   } else if (condition3) {
     System.out.println("C");
   } else {
     System.out.println("D");
   }

Here, the program goes through the conditions. If any condition is met, the
code inside of that branch runs. Then the conditional is over. Even if two
conditions (say ``condition1`` and ``condition2`` are both ``true``) whichever
is first will run, and stop continuing. Finally, if no conditions are met, the
``else`` block will run.

.. note:: The final ``else`` here is optional. We could just as easily have
   written the code like this, without the final ``else`` section:

   .. code-block:: java

      if (condition1) {
        System.out.println("A");
      } else if (condition2) {
        System.out.println("B");
      } else if (condition3) {
        System.out.println("C");
      }

.. important:: Because the conditions are evaluated from first to last (top to
   bottom) in an ``if`` statement, and because only one section of code will
   run, the order of conditions is very important. Conditions that have a
   higher precedence should be listed earlier.

Practice
--------

Let's make one final change to our recommendation program. We're going to add
*another* condition that deals with whether it's breakfast time. If it's
breakfast time, then we should eat breakfast.

.. code-block:: java
   :caption: Main.java

   public class Main {
     public static void main(String[] args) {
       boolean isRaining = false;
       boolean isFriday = false;
       boolean isBreakfast = false;

       ...
     }
   }

Here, I've used ``...`` to indicate unfinished code. This isn't valid Java, but
it marks a location where the code needs to be completed.

Your mission (should you choose to accept it) is to write out all the code for
our recommendation program, like before, but including the new additional
breakfast condition.

Here's the order of precedence (from highest priority to lowest):

1. Breakfast time

2. Baseball Fridays

3. Raining weather

4. Play outside

Try to write out the code, then run it a couple of times but change the values
of the three boolean values that configure how the program runs.
