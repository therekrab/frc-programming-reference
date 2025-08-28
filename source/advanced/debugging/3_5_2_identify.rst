3.5.2 - Identify the Issue
==========================

The first step to debugging an issue is to develop a *precise* description and
understanding of the problem.

The goal of this phase is to gain a real understanding of what's misbehaving.

Driving Questions
-----------------

Asking the right questions is an important step in solving a problem.

The driving questions here are as follows:

- What *should* be happening?

- What *is* happening?

- Where is the *first* case of unexpected behavior?

Let's go over each question in more details: why we ask it, and what answers to
look for.

What should be happening?
~~~~~~~~~~~~~~~~~~~~~~~~~

It's important to have a general baseline to compare the robot's actions
against. If we say we have a problem, but we omit the desired behavior, it
becomes needlessly more difficult to solve the problem.

When we do this, we want to be avoiding looking at the robot code, because we
are defining what we *want* to happen, not just what the code says *should* be
happening. However, this doesn't mean that we should talk in very abstract
terms, such as "the elevator should move to the correct position". Instead,
it's important to still discuss and understand the complex systems in place
that are a part of the broken component.

What is happening?
~~~~~~~~~~~~~~~~~~

This question addresses the robot's actual behavior that was experimentally
determined. This should be an accurate and specific description of what the
robot software did - not just what the robot did. Logging is paramount here,
because it lets us review the robot's state at some point in code execution
without making any guesses about what such values "ought to be".

Here, we should be able to see the inputs that the robot received (sensor
readings, etc) as well as any outputs the robot may produce (e.g. voltage or
velocity). This lets us pair up the robot's state with its actions at that
point in time.

Where is the first case of unexpected behavior?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Now, we should compare the expected behavior to the experimental behavior. By
comparing the expected inputs and outputs of each software component, we can
look for a difference; that's the first point of failure. After this, the robot
is no longer doing what it was expected to do, which means that our issue has
already occured.

.. note:: Don't take this to mean that there will only ever be one error at a
   time; it's very possible to have multiple errors cumulating in a broken
   series of processes in software. After we've made one programatical mistake,
   the quality of the next stages in the "pipeline" of robot operation are
   considered "compromised" and by the :term:`GIGO` principle, any errors we
   see afterwards are moot, because they resulted from errors from a previous
   stage. 

Figuring out the first place in robot code where something is amiss helps to
lower the scope of what could be broken. If we know that we're reading the
correct position from a pivot mechanism, but the output we apply is in the
wrong direction, we know our processing of the input to create the voltage
output is faulty. This often correlates to a relatively small portion of code,
which makes searching for the bug easier.

Describing the issue
--------------------

Once we've answered all the previous questions, we need to do what we came here
for: we need a detailed description of the problem. This doesn't need to
include us pointing to a line of code and saying "*A* should have been *B*" (at
this point, we're not searching through code yet), but it lets us make it very
clear what we're looking for.

The goal here is to point to some small, named component of the robot code that
we can describe; we need to know what it should have done, and what it did
incorrectly.

Now we can progress to the next step: finding the actual bug and correcting it.
