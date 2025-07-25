2.4 - Variables
===============

Programming centers around manipulating and storing data. Nearly all
programming deals with data, because if there's no data, there's nothing to do!

Thus, it makes sense to begin learning Java by dealing with how to store data.

In Java (and many other programming languages), we can associate data to a
name, like we could in a spreadsheet. Here's an example table that associates
numeric values with names:

.. list-table:: Expenditures
   :header-rows: 1

   * - Resource Name
     - Money Spent (USD)
   * - Employee Salaries
     - 400
   * - Construction Materials
     - 600
   * - Dining
     - 200

In Java, we would declare these values like so:

.. code-block:: java

   int employeeSalaries = 400;
   int constructionMaterials = 600;
   int dining = 200;

Here, we associate numeric values (400, 600, 200) to names that represent what
they mean.

Let's break down the syntax for the first line. We begin with the word ``int``.
That's short for *integer*, and that tells Java what **type** of data this
variable holds. This is a feature of Java that makes sure that we don't try to
do weird stuff with our variables. I can divide numbers by each other, but can
I divide text by a number? No! That doesn't make any sense.

The second word in the line is the name of the variable: ``employeeSalaries``.
From now on, we can use the variable's name, ``employeeSalaries``, to refer to
whatever value it represents (400).

.. important:: Variable names can't just be anything. We can't use spaces. In
   fact, we can't use most special characters. We're allowed to use the
   underscore ``_``, the dollar sign ``$``, and numbers. However, numbers can't
   be the first character.

   For example, both ``employeeSalaries`` and ``employee_salaries`` are valid
   variable names. However, due to Java convention, the first is preferred.
   This kind of naming is called ``camelCase`` as opposed to ``snake_case``.

Then, we see a single equals sign. This is used to assign a value to the
variable. The value comes right after the equals sign: ``= 400``.

And then, we see something really weird: we put a semicolon (``;``) at the end
of the line. What's up with this? It turns out that the semicolon is the
**statement terminator**. This tells Java that we're done with this instruction.

The end of an instruction doesn't have to be the end of the line. Take a look at this code:

.. code-block:: java

   int x = 4;
   int y = 6;

This could also be written as this:

.. code-block:: java

   int x = 4; int y = 6;

Or even this:

.. code-block:: java

   int x =
   4;
   int
   y = 6;

Where we put the new line is a stylistic choice. Normally, the first option is
recommended, because it's easiest to read. But the language itself doesn't
really care where you press ``Enter``.

The general pattern for creating a new variable looks like this:

.. code-block:: java

   type name = value;

Where ``type`` is the variable's type, ``name`` is the name we're giving it,
and ``value`` is the data is's meant to store.

Mutability
----------

Variables in Java are **mutable**. This means that we can change their value if
we want.

.. code-block:: java

   int x = 4;

   x = 5;

This code sets up a variable x, assigns 4 to its value, and then can give it a
new value. This code isn't very practical; we never use the original value of
4, but this is just an example.

If we don't want a variable to me mutable, we can mark it as ``final``. This
tells Java that when it's compiling the program, it should refuse to continue
if we ever try to reassign a new value to the variable.

.. code-block:: java

   final int x = 4;

   x = 5;

The above code won't compile because it breaks the rules of ``final``
variables.

Primitive Data Types
--------------------

.. admonition:: Primitive?

   In Java, there are a specific sets of data types that are classified as
   **primitive**. These data types, like ``int`` and a few others we're about
   to look at, represent simple, fundamental values like letters, numbers, and
   truth values (which we will call booleans).

Numbers
~~~~~~~

We've already used ``int`` to store integers (whole numbers), but there's
plenty of other numbers out there - such as ``3.414``. These numbers can't be
stored under the ``int`` data type, because they aren't whole numbers.

Instead, there two more primitive types for storing numbers that are
important.

There are two data types that are used to represent decimal numbes - which are
referred to as "floating point" numbers, because the decimal isn't at a defined
place. (we can have ``12.34`` or ``1.234``).

The first data type here is ``float`` (short for floating point). The second
data type is called ``double``. This is a fitting name, becuase it stores
*double* as much data. Therefore, it's more precise. This technically means it
takes up more space in memory, but these numbers are so small in memory that it
doesn't really make an impact on performance, for our use case.

.. admonition:: Exact precision of ``double`` and ``float``

   While the ``float`` data type can represent numbers with around 7 digits of
   precision, ``double`` can store around 15 digits.

Therefore, ``float`` is rarely used and ``double`` should pretty much always be
used. Here are some examples of ``double``\s in action:

.. code-block:: java

   double oneHalf = 0.5;
   double closeToOneThird = 0.33333;
   double seventy = 70;

With ``seventy``, we can still store it as a ``double`` type even if we omit
the decimal place.

Booleans
~~~~~~~~

In programming, it's *really* common to know if something is correct or not. We
use the terms ``true`` and ``false`` to refer to values that represent some
truth.

Named after their inventor, George Boole, these values of ``true`` and
``false`` are called ``boolean``\s. Here's some examples of creating and
initializing ``boolean`` values.

.. code-block:: java

   boolean iLikeIceCream = true;
   boolean iLoveBroccoli = false;
   boolean codingIsFun = true;

Characters
~~~~~~~~~~

In programming, letters are referred to as **characters**. When we talk about
storing text, we will learn that text is just made up as an ordered group of
characters (letters).

For now, all we will learn how to do is create single characters:

.. code-block:: java

   char theFirstLetter = 'a';
   char theLastLetter = 'z';
   char uppercaseA = 'A';
   char funSymbol = '@';
   char semicolon = ';';

As we see here, we just wrap the character we want to store with *single*
quotes (``'``) and save it to a variable with the name ``char`` (short for
character).

More Variables
--------------

There's a lot more basic operations with variables that we should know about
before we do anything else.

Definition without initialization
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

For now, all we've done is defined variables' names *and* gave them values. It
turns out it's relatively handy to define the variable *without* assigning a
value to it. This "creates" the variable in the computer's memory, but doesn't
actually store anything yet.

To do this, we just don't assign anything to it using the ``=`` sign.

.. code-block:: java

   int unitializedNumber;
   boolean unitializedBoolean;
   double unitializedDecimal;

And we're free to write valeus to these variables later:

.. code-block:: java

   int someNumber;

   someNumber = 5;

The above is the same as writing ``int someNumber = 5;``.

This technique is useful when we know we want the language to recognize the
variable, but not yet give it a value.

Assigning variables to variables
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

We also sometimes want to use the value of one variable to change another. To
set the variable ``a`` to the value of another variable ``b``, we can do this:

.. code-block:: java

   int a = 4;
   int b = a;

Here, ``b`` also gets the value of ``4``. But a question surely arises: what
happens if we reassign ``a`` or ``b`` to a different number, say ``5``? Does
the other number update as well? No.

.. code-block:: java

   int a = 4;
   int b = a;
   a = 5;

When this code finishes executing, the value of ``b`` is still ``4``. This same
thing is still true even if we used different types.

Example Program
---------------

Copy and paste this code into a new ``Variables.java`` file:

.. code-block:: java
   :caption: Variables.java

   public class Variables {
     public static void main(String[] args) {
       int x = 4;
       System.out.println(x);
     }
   }

This code is still somewhat foreign to us, but we should recognize one line:
``int x = 4;``. This, we know, declares a variable called ``x``, tells the
language it's of type ``int`` (whole numbers), and then gives it the value
``4``.

But the line right under that seems to use ``x`` in some regard. It turns out
that the line ``System.out.println(x)`` just *prints* the value of ``x`` to the
screen. In fact, whatever we put between the parenthesis will be printed. This
is called a **function**, and we'll get to these in another section.

But for now, run the program and see what it does. It should print out ``4`` to
the console.

.. admonition:: Do I need a printer?

   The concept of "printing" here just means displaying some text in the
   console (aka terminal). **Printing** is just a verb used to talk about this
   idea of writing text to the console.

Now try and guess what this program should do when ran:

.. code-block:: java
   :caption: Variables.java

   public class Variables {
     public static void main(String[] args) {
       int x = 4;
       System.out.println(x);
       x = 5;
       System.out.println(x);
     }
   }

Here, we create ``x`` just like we did last time, then print it out using
``System.out.println(x);``. But then, we change the value of ``x`` and print it
out again. Run this program. Does it match what you expected?

Try printing out other types of variables, such as booleans, characters, and
floating-point numbers (i.e. decimals).
