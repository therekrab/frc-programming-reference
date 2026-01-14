3.1.5 - State, Applications
===========================

This page focuses on how robot-wide state can be incorporated into a robot
program, and shares my current favorite implementation to do so.

The problem
-----------

Effectively, the problem is rather simple. Because command-builders have the
sole responsibility to construct commands, they generally have to have access
to robot-wide state. Because robot-wide state is deterministically dependent on
the individual state variables which can be obtained through each subsystem in
the ``subsystems`` field of the ``Superstructure`` class, the logic for the
calculation of these state variables isn't really important. What is more
difficult, however, is deciding where best to put the code to calculate each
state variable based on subsystem variables: we want to select somewhere
generally accessible, but we also need to make sure that where ever we put the
code, it needs to have access to the subsystems themselves as well. Given the
rather limited availability of the ``subsystems`` object, this proves to be
challenging (not to code, but just to design).

The solution(s)
---------------

A basic approach
~~~~~~~~~~~~~~~~

Naively, we could just include the derivations for each robot-wide state
variable (which will just be referred to as state for the rest of this page)
could be included in each command builder class where it is necessary. However,
this opens the door for discrepancies between command builders, where their
implementations of this 'state calculation' process differ for the same state
variable. This can lead to problems because they may be expected to be
referring the same state variable when, in fact, they are not.

This approach is also potentially too restrictive; we used the whole
*only-command-builders-can-access-subsystems* pattern to prevent access to the
command factories on each subsystem. We don't really mind if code outside of
the command builder classes can read state variables. For example, some vision
code may need to know the current position of the robot. While we could simply
pass around a ``Supplier<Pose2d>`` from the drivetrain to the vision code, this
sort of sidesteps the issue. What if the same code also needs to know something
else about the robot's state, such as whether the robot has a gamepiece
(unlikely for vision, but not a bad example in general). How do we go about
giving that vision class the ability to read that state variable as well? Do we
pass in *another* ``Supplier``? This could get out of hand. It would be better
if we could just separate the state calculation code from the highly protected
subsystem objects.

Instead, we could move all of the state calculation logic to the
``Subsystems`` class. This would result in one implementation of each state
calculation implementation, but that doesn't mean we're in the clear yet. This
approach still has its own issues, mainly that it continues to be highly
restrictive of what parts of code can read state variables. Additionally, it
forces a lot of extra code into the ``Subsystems`` class that we explicitly
*don't* want strongly correlated with the subsystems themselves (for protection
reasons).

A better approach
~~~~~~~~~~~~~~~~~

Another possible implementation is moving the unified state calculation code to
the ``Superstructure`` class. As part of the superstructure, it would have
access to the ``subsystems`` object, which means it would be able to read all
the internal state variables it needs, but because it's at the same access
level as the ``superstructure`` object, the ``superstructure`` object can be
passed around safely, and thus state variables would become more accessible.

However, this becomes a little trickier for another reason: command builders
(as of their implementation so far described) don't have access to the
superstructure class, only the ``subsystems`` record. So, we would have to add
another parameter for the ``CommandBuilder.build()`` method to also take in the
superstructure class. It's not a dealbreaker by any means, but it is
noteworthy. It means that there's a distinct difference between the
subsystem-specific state variables and those which are robot-wide. Programmers
would have to be able to know exactly which object (``superstructure`` or
``subsystems``) holds the method they need to read whatever state they'd like.
This is kind of strange, and makes it a little more difficult to write code
that makes inherent sense. On the other hand, the argument could be made
(successfully, in my opinion) that there *is* an intrinsic difference between
subsystem-specific state and robot-wide state. For example, subsystem state is
likely to be helpful in building up inter-subsystem commands, but robot-wide
state variables are more likely to be helpful in a different capacity. So this
is less of a problem than anything else mentioned so far.

Additionally, this has the same issue as putting the state calculations in the
``Subsystem`` class; that is, it will fill up the file with methods that aren't
directly a part of the class's original intent. The ``Superstructure`` class is
meant to be a limited communication point between individual subsystems and the
rest of the robot code.

So why not move the state calculations to another class? Because Java treats
non-primative variables as *references*, not values, we could pass the
``subsystems`` record to another class, ``StateManager`` (or some better name
which I haven't come up with yet). This class would be responsible for holding
all of the state calculation code, and would have *internal*, but not exposed,
access to the ``subsystems`` record. The ``Superstructure`` class could
construct a ``final`` instance of this class during its initialization, and
simply allow public access to the field to get a ``state`` object which could
be queried for information about the robot's state.

This seems to solve most of our issues so far: it separates the state
calculation code into a place separate from the ``Superstructure`` or
``Subsystems`` classes, and also is just about generally available. Thus, this
is the solution I propose for most robot projects. Use the ``Superstructure``
class as a class which can be passed to other parts of the robot code safely,
and a ``state`` field which is responsible for providing robot-wide state
variables which are likely necessary in other parts of the program.

Summary
-------

Here's a "graphical" representation of the structure of the robot program that
uses this design:

.. code-block:: text
   :caption: Structure of Superstructure

   Superstructure
    - (public) StateManager instance
      - (private) Subsystems instance
    - (private) Subsystems instance

Note that there are going to be two variables called `subsystems`, however
they'll both be references to the same object. We don't have two different
copies of the ``Subsystems`` record.

Here's how the system works:

1. The ``RobotContainer`` class instantiates the ``Superstructure`` class,
   which creates the ``Subsystems`` record and shares it with the
   ``StateManager`` instance.

2. The ``StateManager`` instance is simply a final, public field ``state`` on
   the ``Superstructure`` field. Anywhere in code that requires robot-wide
   state uses this object to query the robot's state.

3. ``CommandBuilder``\s' ``build()`` method accepts both a ``Subsystems``
   object *and* a ``StateManager`` object. This lets them work with both
   robot-wide *and* subsystem-specific state data to form complex commands.

4. ``Binder``\s can access ``Trigger``\s corresponding to robot state because
   their ``bind()`` method accepts the ``Superstructure`` object, whose public
   field ``state`` allows the ``Binder``\s access to robot state.

5. Other classes, like vision, can also use the ``StateManager`` object to
   query robot state, such as the robot pose.

Repeated functions
------------------

It's not unlikely you'll find yourself writing robot-wide state-getters which
simply echo the state of one subsystem. Although this code can be a bit
repetitive, it's not really too bad; it's also rather unavoidable if you don't
expose subsystem objects themselves but what to explicitly expose the result of
some of their functions.

Luckily, if you've been following the principles I've set in this document,
it's unlikely you'll have to write much code that actually does any form of
'conversion' or 'sanitization' on the output of those methods; you should be
able to simply just return the result of calling the subsystem method. If this
isn't true, then it's likely that you haven't followed best practices when
creating that method.

