2.6 - Strings
=============

Besides the primtive data types (``int``, ``double``, ``boolean``, etc.),
there's a *lot* of other data types that we can use in Java easily (in fact,
we'll eventually learn how to create our own types).

The collection of non-primitive types that we can use exists as part of the
**standard library**.

.. admonition:: Standard library

   The **standard library** is a set of code (in Java for us) that has a lot of
   pre-programmed features. For example, it has declarations for mathematical
   constants (Pi, Euler's constant, etc.) as well as complex data types, like
   **lists** and **sets** (which we'll cover in later sections).

One of the biggest features that the standard library gives us programmers is
the ability to store *text* in code. Text is stored as a series of
*characters*, which means that any valid character can be part of text.

In computer science, we refer to this kind of textual data **strings**, because
they're made up of a *string* of characters.

In Java, we can use strings to store text with the ``String`` type:

.. code-block:: java

   String greeting = "Hi, programmers!";

This looks familiar, but still a little unique to us. We see the familiar
syntax for creating a variable (``greeting``) and assigning a value to it
(``"Hi, programmers!"``), but there's some weird things here.

For one, why is ``String`` the type and not ``string``? Until now, we've been
using lowercase names for types, but ``String`` breaks that convention. It
turns out that a type that starts with an uppercase letter is actually the norm
for most types in Java. Only the primitive types get fully lowercase names.
Every other class normally has its leading letter in uppercase.

.. note:: Why do we care about the way that types are named? Why does it matter
   how they're named, instead of *what* they represent? This is a reasonable
   point, but the reason that we care so much about naming standards is
   important. When we write code, we want to make sure that it's easy for other
   people to see what's going on. This means that we need to adopt a common
   "style", which makes it easier for other programmers to understand our
   convention and write code that visually matches code already written.

The final, and arguable most confusing, part about this variable declaration is
the value that the variable gets: ``"Hi, programmer!"`` What's up with the
double quotes wrapping this text?

The double quotes surrounding text here indicates to Java that this *isn't*
code, but instead actual text data that the program needs to just read like
regular text, i.e. a *literal* value.

Java doesn't like weird symbols or words in code that it doesn't understand, so
if we just wrote ``greeting = Hi, pal!;``, Java's compiler wouldn't like that.
But when we say ``greeting = "Hi, pal!";``, Java sees the quotes and says *"Ah,
okay! You literally just want to store the value between the quotes!"*, and
permits this.

Java also requires that we use quotes on *both* sides of a string literal. The
string ``"halfway`` (notice the lack of closing quote) isn't allowed, nor is
``another string"``.

Strings in action
-----------------

We've already used strings in Java already, if you haven't noticed.

.. code-block:: java

   System.out.println("Hello, world!");

This is using a **function** (more on that in a later section) called
``System.out.println`` that displays text on the console for us. We use the
string literal ``"Hello, world!"`` here because we want the program to display
the text ``Hello, world!`` on the screen.

Notice how the quotes don't show up when we print out this value. This tells us
something very interesting: the quotes themselves aren't a part of the text
data we store - only the content between them is stored as the actual value.

Concatenation
-------------

There's one more thing we can do with strings in Java. We can combine strings
with more strings, or other data types, and create big strings.

For example, we can do this:

.. code-block::

   String name = "Mr. Programmer";

   String greeting = "Hello, " + name;

This is a bit of a weird operation. We declare a ``String`` called ``name``
with the value ``Mr. Programmer``. But then we *add* another string literal
(``Hello, ``) with the name.

This is new. How do we add text to text? What does this mean?

This operation isn't *really* adding - instead this is an operation called
**concatenation**. We just use the ``+`` symbol because it's easy.

**Concatenation** is just combining two strings, one after another, to form a
new string. For example, ``"FRC " + "is cool"`` evaluates to ``FRC is cool``.

.. important:: Note that we left a space between the C and the end quote. This
   is because when we combine strings, they're just pushed end to end. There's
   no space automatically inserted between them. So if we had instead evaluated
   ``"FRC" + "is cool"``, we would have gotten ``"FRCiscool"``.

We can also concatenate strings and other types, like ``int``. The expression
``"Number: " + 5`` just adds "5" to the end of the first string, creating
``"Number: 5"``. This works for *all* types in Java!

Try to guess the output of this code, then run it:

.. code-block:: java
   :caption: Strings.java

   public class Strings {
     public static void main(String[] args) {
       String firstName = "Joe";
       String lastName = "Smith";
       String fullName = firstName + " " + lastName;
       System.out.println("My name is" + fullName);
     }
   }

See if you can spot the weird behavior here. Is the output what you'd expect?
Can you change the code slightly to fix this? Answer: Yes, you can!
