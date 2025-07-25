2.10 - Functions
===============

What's a function?
------------------

If you've ever heard the word "function" in math class, this is a very similar
topic. In math, a function is this:

  In mathematics, a function from a set X to a set Y assigns to each element of
  X exactly one element of Y. (`Wikipedia
  <https://en.wikipedia.org/wiki/Function_(mathematics)>`_)

What this means is that a function is some operation that we can define that
takes in some X and spits out a Y.

This is *exactly* what functions are in programming, too! We can create some
operation, called a **function**, that accepts inputs, and returns some output.

Both of these are optional. We could have a function, such as
``favoriteNumber()`` that doesn't take an input but only returns an output.
Similarly, we could write a function that takes an input for some text, and
saves that text to a file. This function doens't return any data, so it doesn't
have a **return value**. A return value is the result value of a function.

In math, we may have the function :math:`f(x) = x + 2`. This takes some input,
:math:`x`, and maps it to some output, :math:`f(x)`. In this case, the output
is always two more than the input.

Functions process their inputs to make an output.

Syntax
------

In Java, we can **call** or **invoke** a function by writing its name, then
writing its inputs between opening and closing parenthesis, separated by
commas.

For example, this is a function call to some function named ``foo`` with the
inputs of ``1`` and ``3``:

.. code-block:: java

   foo(1, 3)

Note that spacing is important sometimes. We can write ``foo(1,3)`` or ``foo(1,
3)`` (note the spacing between the comma and ``3``). However, we aren't allowed
to write ``fo o(1, 3)``. This would be interpreted as writing a call to the
function ``o``, not ``foo``.


If we called another function, ``bar``, without any inputs:

.. code-block:: java

   bar()

``println()``
-------------

We've already seen a function in use:

.. code-block:: java

   System.out.println("Hello, World");

This function is being used to output some text on the console. In this case,
we don't get a return value, but we do use an input - the string ``"Hello,
World"`` in this case.

Declaring functions
-------------------

It's important to know how to call functions, but it's just as important to be able to 
