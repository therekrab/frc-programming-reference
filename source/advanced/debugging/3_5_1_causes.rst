3.5.1 - What Causes Buggy Code?
===============================

Any sort of code that misbehaves compared to its expected behavior is
considered "buggy". There are a few things that can lead to buggy code, which
are outlined in this document. Avoid any of these situations if you can.

These issues fall into three main groups:

1. Lack of understanding/information

2. Physical failures

3. Simple logical errors

4. Unanticipated coupling

Lack of information
-------------------

If a programmer doesn't have all the most recent information about the context
of the code that they write, they will inevitably write code that doesn't
behave as expected.

Here are some situations where the programmer doesn't have all the necessary
information to solve a problem, and the solution the programmer *should* have
taken:

.. list-table::
   :header-rows: 1

   * - Issue
     - Preventative action

   * - External method behavior doesn't match expectation. 
     - Thoroughly reading the documentation for *all* dependencies of a code
       can help prevent issues where the programmer doesn't understand what a
       method or variable represents in the real world.

   * - Not understanding a problem all the way.
     - Make sure to research everything related to a problem if you are even
       unsure that you understand the problem. Consult with other programmers
       who may understand the problem better and collaborate to ensure each
       student has a sufficient understanding of the issue such that the
       solution becomes relatively apparent.

   * - Lack of communication between mechanical and software subgroups. If
       robot parts/hardware changes, software students should always be consulted
       to ensure that these changes are either accounted for in code, or don't
       require a code change.
     - Make sure that all hardware changes are discussed with programming
       students *who understand the impacts of the decision* before any
       mechanical changes are made. If a solution makes it significantly more
       difficult to use in code, that should be known *before* the mechanical
       team gets to work.

   * - Not considering edge cases/failure modes.
     - Although accounting for each possible way that each way the robot can
       fail is difficult, it becomes easier with practice. Especially as the
       robot practices, the robot's actions need to be carefully analyzed and
       *anything* out of the ordinary thoroughly, because failure to do so
       often results in unexpected behavior later.
 
Physical failures
-----------------

Sometimes, the code works fine, but the hardware components don't behave as
expected. This can be due to a variety of reasons, but are often due to one of
the following physical errors:

- Disconnected devices. These cause no motor output, and often result in a lack
  of status signal lights as well.

- Motor overheating leaves the motor connected but disables output. This can be
  a real issue when a motor draws a lot of current and/or is stalling.

Physical failures are nice for programmers, because they mean that the software
isn't entirely at fault. Perhaps it's possible that software put the mechanism
into a position in which it was able to experience a failure, but the prevalent
issue is the mechanical failure.

However, blaming issues on hardware is risky; if incorrect, you're bypassing
the idea that the software was at fault, which can cause a problem to persist
for longer than it should. As a general rule, if programmers want to blame the
physical hardware on the robot, there *must* be evidence that the mechanism is
at fault. This could be log data (if you assume that the logging system is
accurate), or data from some other source (live updates via NT or a visual
proof that the mechanism is disconnected. If you cannot prove that is was
hardware, you may assume it was while you also ensure that code is functioning
as expected. We'll discuss methods to verify the functionality of code in later
parts of this section.

Simple logical errors
---------------------

The easiest mistake to make is just messing up. Maybe you put a ``+`` where
there ought to have been a ``-``, or just didn't think about the problem from
the correct frame of reference. These issues haunt developers not because
they're difficult to understand, but because they're difficult to spot by the
programmer who wrote the code originally.

To avoid these sorts of errors, it's important to review your code consistently
in order to verify that it is void of these simple mistakes. However, to err is to be 

Unanticipated coupling
----------------------

Different parts of code can be "coupled" if changing one changes/requires
changes in the other. This can be a good thing, but becomes dangerous when the
programmer either is unaware of the coupling or simply forgets it. For example,
motors' outputs are governed by at least two things: voltage demanded *and*
current limits. If the programmer forgets that those current limits are in
place, the robot may not behave as expected (i.e. real voltage is not equal to
voltage demanded). 

These type of errors are really a subsection of the first major category, but
are so prevalent that they require their own catergory. These types of issues
manifest from either a lack of reasonable understanding of the code, or a
strange code structure that makes the coupling obscured or difficult to detect.

