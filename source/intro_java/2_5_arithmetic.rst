2.5 - Arithmetic
================

Just like with numbers in math, we can perform arithmetic on numbers in
programming. The notation is very similar as well, plus a few technicalities.

Addition and subtraction
------------------------

We can add and subtract numbers with ``+`` and ``-`` respectively.

.. code-block:: java

   int three = 1 + 2;
   int four = 10 - 6;

Here, ``three`` stores the value ``3`` (1+2=3), and ``four`` stores the
value ``4`` (10-6=4).

We can even use variables that store numbers in place of the **literals** (but
that's not just true here - variables can *always* replace literals of the same
type, provided the variable is already defined).

.. admonition:: Literals

   In code, **literals** are values like ``4`` or ``false`` that are *literal*
   values. They are written into the code. A literal can be assigned to a
   variable, such as ``int x = 4``. Here, ``4`` is a literal because we
   *literally* write 4 to mean 4. But ``x`` is a variable.

.. code-block:: java

   int three = 1 + 2;
   int four = 1 + three;

Multiplication and Division
---------------------------

Just like we can add and subtract numbers, it's helpful to be able to multiply
and divide numbers. However, because we don't have the :math:`\times` nor
:math:`\div` keys on most keyboards, we use more accessible symbols instead. We
use ``*`` for multiplication and ``/`` for division. So, ``3 * 2`` evaluates to
``6``, and ``6 / 3`` evaluates to ``2``.

Here's an example:

.. code-block:: java

   int product = 2 * 3 * 4;
   int quotient = 10 / 5;

When this code segment runs, ``product`` will hold the value ``10`` (2 times 3
times 4 is 10), and ``quotient`` will have the value ``2``.

A note on types
~~~~~~~~~~~~~~~

Recall that different types for numbers store different numbers. For example,
we can't have an ``int`` that holds the value ``1.2`` - ``int`` only stores
whole numbers. So, multiplication and division may behave "strangely" when used
with integer variables. For example, if we have this code:

.. code-block:: java

   double top = 1;
   double bottom = 2;
   double fraction = top / bottom;

We would expect the value of ``fraction`` to be ``1/2``, or ``0.5``. And that's
exactly what happens. However, if we instead wrote this code:

.. code-block:: java

   int top = 1;
   int bottom = 2;
   double fraction = top / bottom;

Here, even though ``fraction`` is of type ``double``, its value is... ``0``.
What's going on? Is Java broken? No. This is because the value that is being
assigned to the variable (``top / bottom`` is evaluated first, before its
assigned to ``fraction``. But because ``top`` and ``bottom`` are ``int``\s, we
see that the result of their division must be an integer. What this means is
that ``1 / 2`` in Java evaluates to ``0``. Division with integers happens such
that the remainder is ignored - there is never any rounding that is done. So
even ``9999 / 10000`` evaluates to zero because it's zero with a remainder of
9999. Similarly, ``3 / 2`` evaluates to ``1``, not ``1.5``.

.. caution:: This means that even if you write ``double x = 1 /2``, the ``1 /
   2`` bit gets evaluated first. Because there are no decimal points present,
   Java decides that these must be integers, so it performs integer division,
   and returns 0. Then, Java automatically turns the ``int`` value (``0``) into
   a ``double`` value, but the damage is done; ``x`` gets the value ``0``.

   To avoid this problem, explicityly write each number as a decimal: ``1 / 2``
   should be ``1.0 / 2.0``. Now, when Java evaluates the expression, it sees
   that the values are ``double``\s, not ``int``\s, so it performs decimal
   divsion and gets ``0.5``.

Modulo (remainder)
-------------------

.. note:: This is only an operation that we can do with ``int``\s, not
   ``double``\s or ``float``\s. This is because with division of floating-point
   numbers, there *is* no remainder, so this operation doesn't make sense.

When we are doing integer division (division with integers), we can sometimes
have a remainder. For example, 5 divided by 3 is 1 with a remainder of 2. This
is because 3 goes into 5 once, with 2 left over. So how can we calculate that
remainder?

Let's introduce ourselves to an operation, just like division or
multiplication, that returns the *remainder* of the (integer) divison of two
numbers. We call this operator the **modulo** or *remainder* operation.

In code, the symbol we use for this is ``%``. So, ``10 % 3`` would evaluate to
``1``, becuase 10 divided by 3 (in integer math) is 3 with a remainder of 1. If
there is no remainder for the division, the modulo operation will evaluate to
``0``. Here's a few more examples:

.. code-block:: java

   int x = 16 % 4;
   int y = 3 % 5;
   int z = 5 % 3;

If we ran this code, ``x`` would hold the value ``0``. ``y`` would hold the
value ``3``, and ``z`` would store the value ``2``. ``3 % 5`` is ``3`` because
5 doesn't go into 3 at all, so the final answer of integer division would be 0
with a remainder of 3, and the remainder is what the modulo operator gets us.

Reassignment of result
----------------------

We can assign the value of an arithmetic expression involving a variable to
that variable:

.. code-block:: java

   int x = 4;
   x = x + 2;

This declares an ``int`` named ``x``, and sets its value to ``4``. Then we
evaluate the expression ``x + 2``, which results in ``6``. Finally, this value
(``6``) is set as the new value of ``x``. So ``x`` ends up as ``6``.

Here's another example; try to predict the final value of ``y``:

.. code-block:: java

   int y = 3;
   y = y * 5;

The final value of ``y`` is ``15``.

This pattern of using a value it an expression that ends up reassigning to that
same value is so common that we have a few new operators: ``+=``, ``-=``,
``*=``, ``/=``, and ``%=``.

Let's look at some use cases to see if we can determine the meaning.

.. code-block:: java

   int a = 2;
   a = a + 3;

This does the *exact* same thing as this:

.. code-block:: java

   int a = 2;
   a += 3;

Similarly, the following two expressions are functionally identical:

.. code-block:: java

   int z = 3;
   z = z % 2;

.. code-block:: java

   int z = 3;
   z %= 2;

However, the previous two examples do *not* do the same thing as this:

.. code-block:: java

   int z = 3;
   z = 3 % z;

This is because with the ``+=``, ``-=``, etc. operators, the variable is always
the first value.

Try rewriting the following couple variable assignments to be more concise with
this new syntax:

.. code-block:: java

   int w = 4;
   w = w - 2;

   int x = 3;
   x = x * 2;

   int y = 10;
   y = y / 5;

   int z = 16;
   z = z + z;

.. note:: The last example here is fun, because you could write ``z *= 2`` *or*
   ``z += z`` and either option would do the same thing.

``++`` and ``--`` operators
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The most common use case is incrementing a variable by one or decrementing it
by 1 as well:

.. code-block:: java

   int s = 80;
   s += 1;

This leaves ``s`` with a value of ``81``. However, beccause this pattern is so
useful for counting things, Java has two more operators to add 1 to a number or
subtract 1 from a number.

The ``++`` operator goes after a variable and increments that variable's value
by 1:

.. code-block:: java

   int s = 80;
   s ++;

This does the same thing as the first example in this section. The ``--``
operator works similarly:

.. code-block:: java

   int d = 23;
   d --;

Here, ``d`` ends up with the value ``22``. This is useful when counting up, or
counting down.

Practice
--------

To try it yourself, we can start with our regular file:

.. code-block:: java
   :caption: Arithmetic.java
   :emphasize-lines: 3,4

   public class Arithmetic {
     public static void main(String[] args) {
       int x = 3;
       System.out.println(x);
     }
   }

Replace the highlighted lines with whatever code you want to run. Try using
``System.out.println`` to display some values from arithmetic expressions. Try
doing complex computations, such as ``123456.987 * 3.1415`` and see just how
fast the computer computes and displays these values.

Order of operations
-------------------

Consider the expression ``4 - 3 * 2``. How should Java parse this? Should this
be 4 minus 3, which is 1, and *then * multiply 1 by 2? Or should the expression
``3 * 2`` come first, giving 6, then subtract that from 4 to get -2?

To ensure compatability with regular math, Java uses the same order of
operations as math. This is often called PEMDAS, and will not be overviewed
here; there are already lots of internet writeups on order of operations.

Just like in math, we are allowed to use parenthesis to group operations. For
example, ``4 - 3 * 2`` becomes ``4 - 6`` which becomes ``-2``. However, if we
instead had ``(4 - 3) * 2``, then we would get ``1 * 2``, which ends up as
``2``.
