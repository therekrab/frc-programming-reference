2.2 - Why Organize Code?
========================

This section doesn't teach you any new Java - instead, it presents a
description of a well-liked code architecture for FRC code. So why does this
matter? What's stopping us, as good programmers, from just disregarding
"structure" and "organization" and instead putting all of our code in a 18,000
line long file?

The short answer... our human nature. It's easiest for us to add code, change
behavior, or squash bugs when the code is organized in a way that helps us do
so.

Symptoms of poorly organized code
---------------------------------

These are signs that code is not optimally organized:

- Finding a specific piece of code is not intuitive. A programmer has to look
  through multiple files and classes to find something.

- Multiple people aren't sure how to collaborate properly. Without a set of
  guidelines to follow, programmers are going to have trouble collaborating and
  keeping everything in a good place.

- Changing one part of code completely breaks a different part of the code.
  These seemingly random links between unrelated parts of software are *not
  good* and make the code tightly coupled to itself.

- Change in general is difficult. When you have to make a simple change that
  requires editing multiple other files just so that the change doesn't break
  things, that's an issue.

- It's hard to add new features. This is a sign that the code isn't scalable.

- Verbose or repeated code is a sign of a poor structure that doesn't group
  related behavior.

Principles of well-organized code
---------------------------------

Here are some goals to target when deciding how to structure code. You'll
notice an inverted similarity to the previous points:

- Related ideas are stored close to each other in code. Ideally, you organize
  your code by grouping related ideas and behavior together so that it becomes
  easier to find where something happens.

- Many people have a solid understanding of where a specific change would occur
  if it happened. Removing ambiguity from a system's design is good because it
  allows multiple people to contribute in similar manners, keeping the
  structure's consistency.

- Change is easy. Behavior is properly encapsulated such that small changes
  internally don't require an expensive refactor across the project. Unrelated
  parts of code shouldn't have much - if any - effect on each other.

- Parts of the system that are designed to scale can do so easily without much
  hassle or boilerplate code. The ability to quickly add new features to the
  code is important.

- The structure itself helps prevent bugs due to improper use. Good structure
  makes it difficult to break its own rules.

Well-organized code helps developers write code that's easy to share, change,
scale, and improve.
