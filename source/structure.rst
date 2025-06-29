Structure of this book
======================

This book is *designed* to be a reference book for programming in FRC. Mainly,
I want to write about good design, but this comes after the necessary
introduction to regular programming in FRC.

This book is divided into two sections: **Beginner** and **Advanced**. If
you're just getting started writing code for FRC, you're a beginner. If you've
either read all of beginner or are thoroughly experienced with programming for
FRC, you can jump to **Advanced**.

Beginner
--------

**Beginner** covers everything you need to know to start writing code for FRC. It
goes over common abstractions in WPILib, like subsystems and commands. Then,
it discusses the other important files and classes in general code for FRC, such
as `Robot`, `RobotContainer`, etc.

We discuss the best ways to start building complex commands and how to store
constants in the right places. At the end of the **Beginner** section, you will
have a solid understanding of the fundamentals of writing code for robots and
WPILib. This section is more of an aggregation of commonly-agreed-upon
definitions than anything else. A complete understanding of the **Beginner**
section is necessary to write code in FRC.

Advanced
--------

The **Advanced** section takes the lessons from **Beginner** and develops them
to create a full code architecture for a malleable robot project. It focuses
more in on my personal recommendations than any conventions, because at this
point, many teams do this many different ways. I'll offer my take, but at the end of
the day, this is a reference as to how *I* find works for me. Adapt the lessons in **Advanced**
to your own style, and take that advice with a grain of salt.

**Advanced** is more of a recommendation than a lesson. Oftentimes, it only
adds complexity to something that could be implemented more simply, not for
performance, but to allow for more variability, power, and a more ergonomic
overall design to your project.

.. note:: Let me repeat myself. **Advanced** *does not result in more features
   for the robot*. It solely exists to recommend ways to improve the quality of
   the code, which can help in the long run to prevent bugs, or more quickly
   implement new features. But *it does not change the robot's final behavior*.
