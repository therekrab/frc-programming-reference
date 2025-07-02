The Programming Reference Book
==============================

Welcome to the programming reference book!

I have been the programming lead for FRC team 3414 for a couple years now and
have learned a lot about how to write code for FRC. I have stumbled across many
ideas that I think help me write better code, and I wanted to leave my team
with some resources for after I leave.

What is this book?
------------------

This book is intended to be several things:

1. Firstly, I want to give a good primer for getting started programming with
   FRC. The WPILib docs do a good job with more advanced concepts, and are
   excellent for learning about WPILib. However, I find that the resources are
   slightly lacking when it comes to introducing newer programmers to good
   design choices for FRC. There will definitely be *some* repetition between
   the WPILib docs and here, but the major difference is that those docs teach
   students how to use the libraries; this reference focuses more on how to
   design a robot's systems *well*.
   
2. Secondly, I want to offer my personal take on how to structure projects as
   they become more advanced, providing simple abstractions and an
   easy-to-grasp code structure that still retains a lot of safety features to
   keep code concise, clear, and well-organized.

.. note:: Because of the structure I've chosen for this book, I begin with
   simple concepts and recommendations, and then change my tune to a different
   implementation later in the more advanced sections. This is an intentional
   decision that is aimed to provide programmers of all skill levels with a
   base to start learning from. Reading the whole book is still *highly
   encouraged*.

Is this Coding?
---------------

This is **not** a book to teach coding. This book teaches a subset of
programming in FRC. **Coding** is the skill of understanding how to express
ideas in code that compiles. **Programming** is the skill of knowing how to
solve problems with code.

Really, this (especially the **Advanced** section) is more a book about
**design**, which focuses more in on the structure of code than the code
itself. Design is a subset of programming that focuses solely on the
architecture of the system, ensuring code is easy to change, understand, and
scale. 

For this reason, this book will not include a lot of complete code examples.
Sure, I'll include some code (Java) to demonstrate my ideas, but translating
this book's ideas into code is left to you, the reader - and the programmer.

In fact, this book will often reference classes, methods, etc. from WPILib
without explaining such things in detail. This is because you should have
already read the `WPILib docs <https://docs.wpilib.org>`_ before starting this
book. I offer a quick review of the major learnings from that book, but I will
not repeat what the docs have to say. **Read the docs!!**
 
.. toctree::
   :hidden:
   :maxdepth: 2
   :caption: Contents

   structure
   resources

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: Beginner

   beginner/index
   beginner/subsystems
   beginner/commands
   beginner/triggers
   beginner/constants
   beginner/command_scheduler
   beginner/robot
   beginner/robot_container

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: Advanced

   advanced/index
   advanced/subsystems
   advanced/subsystem_states
   advanced/commands
   advanced/passive_subsystems
   advanced/enterable_states
   advanced/logging
