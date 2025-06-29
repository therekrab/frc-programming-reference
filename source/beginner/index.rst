The Very Basics
===============

It's hard to know where to start when just introduced to the FRC programming
ecosystem. But this page walks through the common frameworks of robot
programming for FRC, their benefits, and their shortcomings.

Command-based programming
-------------------------

Command-based programming is a paradigm that sees common use across FRC. It
operates on a combination of *Commands* and *Subsystems*.

Because of their importance throughout all of FRC, Subsystems, Commands, and
the Command Scheduler will be handled more deeply in their own respective
pages, but it's a good idea to give an abstract representation of the topics
such that you can start to think about them.

A **subsystem** is a class in code that represents a physical component of the
robot. On their own, they don't really do much. **Commands** are code
representations of what a robot can do. They represent completed actions, and
they interact with subsystems to indirectly control hardware components.
Commands can be instantaneous, or they can have a nonzero duration. This means
that a command may not just set a reference point for a mechanism, but it also
can wait to reach said setpoint.

The underlying logic that runs commands (and a lot more) is part of the
**Command Scheduler**, a part of WPILib that handles running commands,
protecting subsystem requirements, and a lot more.

The next few pages will introduce you to each of these topics, plus more.
