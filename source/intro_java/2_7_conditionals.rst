2.7 - Conditionals
==================

Sometimes, we want parts of our code to execute **conditionally**. That means
that the code section should only run if some given *condition* is true. For
example, consider this situation:

- Let's pretend we're looking to see if we should go for a walk. If the weather
  is sunny, we want to go outside.

Here, *is it sunny?* is the **condition**, which decides if some behavior
executes.

In code, we can use a new type of syntax that allows us to run code
conditionally. We use a new "word", ``if``.

Let's look at some code to see if we can develop an idea of what is going on.

.. code-block:: java

   boolean x = true;

   if (x) {
     System.out.println("x was true");
   }

Here, we declare a ``boolean`` named ``x``, and give it the value ``true``.
Then, we have some weird code going on. Let's break this down.

The first thing that we see is the ``if`` token. In English, we normally follow
the word "if" with some *predicate* (or condition). For example, this would be
"if *it's raining*, ..." where we follow "if" with some condition.

In Java, this condition *must* be written in between parenthesis. In our case,
we write ``(x)``. Because the condition is whatever is between the parenthesis,
we just see that the condition ``x``. More specifically, the condition is
whatever ``boolean`` that ``x`` evaluates to. In our example, it's easy to tell
what ``x`` evaluates to - it's just ``true``; we assigned that to it the
previous line!

Finally, we see that we use the curly brackets (``{`` and ``}``) after the
``if`` and its condition. Anything between these brackets is the code we want
to run *if* the condition evaluates to ``true``. Therefore, with the current
code, we know that the condition is ``true`` (``x`` is ``true``). So, that
means that whatever code we put inside of the brackets *will* run.

In this case, the code inside the brackets is a simple ``System.out.println``,
which displays the text "x was true" to the screen. The code ``"x was true"``
uses what we call a **string**, which is what programmers called text. We
learned about strings in :doc:`2_6_strings`.

So, when this code runs, we'd expect that it *would* run the code in the
brackets, because the condition is ``true``. However, if we switched the value
of ``x`` to ``false`` instead, the code would completely skip the line with
``System.out.println`` because the condition was false.

Conditional Operators
---------------------

Java needs the condition in an ``if`` statement to be a ``boolean`` value,
because it needs to know if the condition is ``true`` or ``false``. This means
that we can't use numbers as a condition.

Consider this example in English: "If 7, I'd like to go play". Unless there's
some other context here, we can't really tell if the condition is true.

But sometimes we want to run a condition if a number is equal to another number
- or if any two values are equal.

For this, we need some way to compare two values - either literals or
variables. Java provides some operators, like ``+`` or ``-``, which act on two
inputs (like addition) and evaluate to some value. Unlike addition, however,
these **comparison operators** don't return numbers - instead, they return
``boolean``\s, because they tell whether some comparison is true.

Let's learn about the comparison operators Java provides.

Equality
~~~~~~~~

To test if two values are equal, we use the ``==`` operator. We can't use the
regular ``=`` symbol, because that's already used for variable assignment. So
we must resort to the double-equals operator, which returns ``true`` if two
values are *exactly* equal, and ``false`` otherwise.

.. note:: Due to how content is rendered on this site, the double equals
   operator may look like a single, elongated, character. But this is not the
   case; the double equals operator is just two ``=``\s in a row.

   This will continue to be the case, so I'll make sure to explain the *actual*
   characters.

Let's look at an example:

.. code-block:: java

   int someNumber = 3;
   int someOtherNumber = 4 - 2 + 1;

   if (someNumber == someOtherNumber) {
     System.out.println("The numbers ARE the same");
   }

Inequality
~~~~~~~~~~

It can also be possible to check if two things are *not* equal. This is helpful
when checking for invalid or erroneous values. For example, we can divide by
any number that is *not* zero.

The operator here is ``!=``, which is the ``!`` followed by ``=``. It evaluates
to ``true`` if the two arguments are *not* exactly the same:

.. code-block:: java

   int someValue = 2;

   if (someValue != 0) {
     System.out.println("You CAN divide by this number!");
   }

This ``if`` statement compares their value to ``0``. If the value is *not*
equal to zero, then the code inside the ``if`` statement runs, informing the
user that it's possible to divide by the value of ``someValue``.

Greater or lesser
~~~~~~~~~~~~~~~~~

It can be helpful to tell if a number is within a certain range, which involves
checking if that number is less than or greater than some other number.

If you want to tell if some number, ``a``, is less than some other number,
``b``, we can use the "less-than" operator, ``<``:

.. code-block:: java

   if (a < b) {
     System.out.println("a is LESS than b");
   }

Similarly, the "greater-than" operator: ``>`` returns ``true`` if the first
number is greater than the second:

.. code-block:: java

   if (a > b) {
     System.out.println("a is GREATER than b");
   }

It's important to note that in both of these operations, if ``a == b``, then
the expression evaluates to ``false``; 3 is NOT less than or greater than 3.

If we want to also include the case where the numbes are equal, we can use
these two operators:

- Greater than *or* equal to: ``>=`` (which is ``>`` followed by ``=``).

- Less than *or* equal to: ``<=`` (which is ``<`` followed by ``=``).

Operations on booleans
----------------------

Let's go over a couple operators that apply strictly to booleans. These can be
useful to manipulate conditions, as we'll see in the rest of the section.

And
~~~

Let's consider two ``boolean``\s, ``a`` and ``b``. If we want to know if ``a``
and ``b`` are both true, we could write this code:

.. code-block:: java

   if (a) {
     if (b) {
       ...
     }
   }

However, this is very verbose and hard to read. It also doesn't read in the way
that we've expressed this compound condition: both ``a`` and ``b`` need to be true.

Instead, Java has an operator for this: ``&&``. This is called the **and**
operator, and is used to verify that two ``boolean`` values are both ``true``.

With this, we can write:

.. code-block:: java

   if (a && b) {
     ...
   }

This is much more concise, and also makes the code read like it executes: *if
``a`` and ``b``, then do ...*.

For reference, here's a **truth table** - a table of values of ``a`` and ``b``
as well as the result of the operation (in this case, ``&&``):

.. list-table::
   :header-rows: 1
   :width: 40%
   :widths: 20,20,30

   * - ``a``
     - ``b``
     - ``a && b``

   * - ``true``
     - ``true``
     - ``true``

   * - ``true``
     - ``false``
     - ``false``

   * - ``false``
     - ``true``
     - ``false``

   * - ``false``
     - ``false``
     - ``false``

Or
~~

We can also combine two ``boolean``\s with the result being ``true`` if
*either* ``boolean`` is true. This is done with the **or operator**, which is
``||`` in Java (two ``|``\s in a row).

To tell if either ``a`` or ``b`` (both ``boolean``\s) are ``true``, we can use
the ``||`` operator. Here's an example:

.. code-block:: java

   if (a || b) {
     System.out.println("Either a or b is true");
   }

The ``||`` operator is *not* an exclusive *or*. This means that it isn't *just*
one argument that must be ``true`` - either can be ``true`` *or* both can be
``true``. This is shown in the truth table below:


.. list-table::
   :header-rows: 1
   :width: 40%
   :widths: 20,20,30

   * - ``a``
     - ``b``
     - ``a || b``

   * - ``true``
     - ``true``
     - ``true``

   * - ``true``
     - ``false``
     - ``true``

   * - ``false``
     - ``true``
     - ``true``

   * - ``false``
     - ``false``
     - ``false``

Not
~~~

Sometimes, it's more helpful to know if a condition is ``false`` instead of
``true``. But luckily, Java has an operator for this as well: the **not
operator**, which is ``!`` in Java.

Unlike the other operators, this is a **unary** operator; it only applies to
one value, not two. So *not A* in code would be expressed as ``!a``.

.. code-block:: java

   if (!a) {
     System.out.println("a is NOT true");
   }

Here's a truth table for the ``!`` operator:

.. list-table::
   :width: 40%
   :widths: 20,20
   :header-rows: 1

   * - ``a``
     - ``!a``

   * - ``true``
     - ``false``

   * - ``false``
     - ``true``

Order of operations
~~~~~~~~~~~~~~~~~~~

When it comes to ``&&``, ``||``, and ``!``, which operators apply first? For
example, with the expression ``a && b || c``, is this ``(a && b) || c`` *or* is
this ``a && (b || c)``.

Just like math operators, there's an order of these boolean operations for
Java:

1. The ``!`` operator comes first, so ``!a | b`` is equivalent to ``(!a) ||
   b``.

2. The ``&&`` operator comes second, so ``b && c || d`` is equivalent to ``(b
   && c) || d``.

3. The ``||`` operator comes last.

Practice
--------

Try and generate truth tables for the following, complex, groups of comparison
and logical operators:

.. code-block::

   a || !(b && c)

.. code-block::

   !(a || b)

.. code-block::

   a || !a


Advanced Practice
-----------------

Let's use everything that we've learned so far to try and analyze the following
code snippet:

.. code-block:: java
   :caption: Main.java

   public class Main {
     public static void main(String[] args) {
       int x = 3 + 1; // 4
       int y = 8 / 5; // 1
       int z = 11 % 3; // 2

       if (x + y > z) {
         System.out.println("Output 1");
       }
       if (x <= y + z) {
         System.out.println("Output 2");
       }
     }
   }

Looking at the program code, try to figure out what will be printed out to the
console. Then run the code.
