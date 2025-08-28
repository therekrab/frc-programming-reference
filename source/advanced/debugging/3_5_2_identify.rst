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

Asking what the expected behavior is for the process we're attempting to debug
is crucial. We need to have a baseline to figure out exactly why we're seeing
is wrong. This involves considering *all* inputs to the sytstem and trying to
work through the logic from there.

Walk through your logic in code with the inputs that you expect occured.
Walking through the robot logic (but not the code) with the values that *should
have* occured is a pretty good way to understand what the robot *should* have
done. This shouldn't yet yeild any surprises or breakthroughs; this only sets
you up with the knowledge of what the desired behavior was.

What is happening?
~~~~~~~~~~~~~~~~~~

Now, we take a look at two things: log data and the actual code. Most robot
processes are dependent on initial conditions, which means that if the initial
conditions the robot receives are invalid, it's unlikely the robot will perform
as expected.

Looking at log data is a great way to verify that the robot was in the
configuration that it was expected to be in. Often, issues can be found where
the robot's initial conditions didn't match the expected values that were
observed. If these values aren't what you expect, that may be the solution for
the problem.

Where is the first case of unexpected behavior?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
