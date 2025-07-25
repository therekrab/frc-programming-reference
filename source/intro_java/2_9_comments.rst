2.9 - Comments
==============

Sometimes, we want to leave notes in our code - either to explain some
particular section or to leave a reminder to fix, test, or modify something.

Why comment on your code?
-------------------------

Remember that when we write code, our goal isn't *just* to write code that
works. Ideally, we want some combination of the following things:

1. We want our code to be **scalable**. This means additions to the code are
   easy, and this is done through how you design complex systems.

2. We want our code to be **testable**. It's helpful to be able to test
   particular parts of the code to make sure that everything works as expected.

3. We want our code to be **understandable**. We want somebody else (or
   ourselves at a later time) to dive into the code and quickly understand what
   the purpose of different parts of the code are, and how they affect the
   system.

Leaving **comments**, or notes in your code, helps with #3. Collaboration is
important, even if it's just between yourself now and yourself in the future.

What's a comment?
-----------------

A comment is a bit of plain text that's in your code that doesn't affect the
program itself. The compiler (the program that turns your code into a
computer-readable format) completely ignores the contents of comments.

Comments are unlike the rest of programming because - as long as they are
identifiable - they can contain anything. There's no rules as to what can be in
a comment, so plain text like this is acceptable, along with emojis.

In Java, we can write comments using the symbol ``//`` followed by our comment.

Here's an example:

.. code-block:: java

   if (isRaining) {
     // If it's raining, then we should tell the user to play a board game.
     System.out.println("Play a board game!");
   }

.. note:: It's conventional to put a space after the double slash (``//``), but
   not necessary.

Notice how the text after the ``//`` is not real code, but the code runs the
same way. This is because the ``//`` symbol tells Java "ignore the rest of this line".

These style of comments, referred to as single-line comments, are helpful when
leaving small notes in code. Here's an example of how it may be helpful to
write some comments:

.. code-block:: java

   // Calculate the total points scored from the number of students and the average score

   int students = 504; // The number of students in the school
   double avgScore = 88.3; // The average score on an exam. 
   double totalPts = avgScore * students; // The total number of points scored.

These don't affect the program's function, but they help us explain to other
humans (ourselves included) *why* the program does what it does.

In this example above, it's not *necessary* to include comments to describe
what each line does - it's obvious enough from the actual code. But this is
just an example - more complicated logic may require explanation, such as using
a mathematical formula.

If we want text across multiple lines to be commented out with this style of
comment, we have to start *each* line with the ``//`` symbol, not just the
first one.

.. code-block:: java

   // This is a series of long comments. The purpose of these comments is to
   // show how single-line comments require the comment symbol, // on each new
   // line, which can be frustrating to type out.

As a functionally-identical alternative, Java allows us to use multi-line
comments. These comments are allowed to span multiple lines, and don't just
have a start symbol, but a stop symbol as well. Otherwise, the compiler
wouldn't know where the comment ends.

The symbol to start a multi-line comment is ``/*`` and the symbol to end it is
``*/``. Therefore, these comments look like this:

.. code-block:: java

   /* This is a multi-line comment in Java. I can write as much as I'd like and
   I don't have to worry about the end of the comment. These comments are
   helpful for larger descriptions, which may require more space. The comment
   isn't over until I write one last thing, though. */
