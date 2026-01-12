3.1 - Program Structure
=======================

This subsection deals with a particularly effective way to structure a robot
project for FRC.

.. note:: Although it is heavily focused on commands to represent robot
   actions, I will not refer to this paradigm as "command-based" because it
   also includes ideas from other design patterns, such as finite state
   machines.

Why structure matters
---------------------

This section of this page extends upon what was said in
:doc:`/intro_frc/2_2_why_organize`.

There are a couple of more advanced goals and strategies that are helpful when
writing bulletproof code (not close to a complete list, just a few big ideas):

Zero trust philosophy
~~~~~~~~~~~~~~~~~~~~~

A good principle for programming is *never* trust the :term:`client`. Assume
that they will do anything that they can do with the exposed code. If we design
our code such that it's as hard as possible to *misuse* our code, the higher
the likelihood that our code will be used properly.

Oftentimes, it's very much impossible to fully have a zero-trust relationship
with the client. So we have to turn to the next best option: making a system
such that the client would have to *actively* try to mess something up.

Enforce rules with types, not comments
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In code, when we expose features to the client, we are inherently making a
*promise* with the client that we will do what we say we will do. This goes the
other way as well - the client makes an agreement to supply the correct values
to whatever system we're building.

But see the previous point; we don't trust the client to keep up their end of
the deal. And therefore, the client could either a) give us invalid data, or b)
misuse whatever output we give them. So we want to make sure that it's easy for
the client to see what should be done with our code.

We *could* write up some great documentation in javadocs and hope that the
client reads our nice comments and listens. But we don't know if they will. So
let's turn to our best friend - the compiler. If we can somehow make the
*compiler* understand our intentions and the rules of what we're trying to do,
then if the client tries to misuse our code, *their entire program will break.*

Let's consider a ``Stopwatch`` class that we're making. Imagine a publicly
exposed method called ``getElapsedTime()`` that returns the elapsed time since
the stopwatch was created.

We could return a ``double`` value and write in the javadoc that these units
are seconds, but it's impossible for us to verify that this is how the client
understands this value. They could make a mistake and assume that we returned
milliseconds, so they use the value as such. This is clearly error-prone.

Instead, we should be using a type system to make sure that our code is
understood properly. Instead of returning a ``double``, let's make use of
WPILib's vast library of unit types, and return a ``Time`` value.

Now, our client would have to explicitly call ``in(Seconds)`` and then proceed
to use that value as a value with units of milliseconds. Although they still
*can* misuse our code, that's really on them, not us. Forcing the client to
face the fact that they need to know the units of this data before they can
process it makes sure that we don't get any weird errors here.

Generally, we want to use the fact that Java is a typed language to our
advantage. We can use types to express what values are valid, limit access to
some variables, and more. This section focuses on how *I* approached the
problem of designing a robot codebase that tries to follow these principles and
make the code easy to write and modify, but hard to break.

.. toctree::
   :titlesonly:
   :glob:

   *

