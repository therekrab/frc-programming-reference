2.4 - Commands
==============

So far, we've only created a level of abstraction between the robot's hardware
and what we can do with it. But when can we do anything with it? This is where
**commands** come into play. Commands are how we can represent different
actions the robot can take.

What's a command?
-----------------

Commands are code representations of actions that the robot can perform.
Typically, commands represent actions that can occur that are not
instantaneous. This means that we have a code representation of something that
takes time, and therefore may be finished *or* unfinished.

Commands run when you "schedule" them, and they continue to run until they
complete by themselves or when they are cancelled.

.. toctree::
   :maxdepth: 1
   :glob:

   *
