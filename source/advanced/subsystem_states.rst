Subsystem States
================

A common pattern is making calls like
``subsystem.setPosition(SubsystemConstants.POSITION_A)``. But this isn't great,
for a few reasons.

Firstly, this requires us to expose position constants outside of the
subsystem's specific package.

Secondly, it makes it too easy to input invalid positions (like ``-1`` or
``100``). There's no compiler checking that what we're entering is legal.

A simple fix would be to declare constants for each "valid" input, then provide
a method to reach each valid state.

.. code-block:: java

   private Command setPosition(double position) {
     return Commands.sequence(
         runOnce(() -> m_reference = position),
         Commands.waitUntil(atReference)); // atReference is a Trigger field
   }

   public Command goToA() {
     return setPosition(SubsystemConstants.A_POSITION);
   }

   public Command goToB() {
     return setPosition(SubsystemConstants.B_POSITION);
   }

   public Command goToC() {
     return setPosition(SubsystemConstants.C_POSITION);
   }

.. note:: The ``runOnce()`` call's inner body may be different depending on how
   the subsystem works, but that's not really the point of this page.

This code is pretty verbose, and due to the different method names, we have to
write different code later (because of the different method names) to reach
different setpoints.

We're also seeing a lot of repetition of code *that does the same thing*.

I will propose my favorite solution for solving this problem.

We could declare a constant enum ``ElevatorState`` that has the following
variants (using the 2025 game as an example):

.. code-block:: java

   public enum ElevatorState {
     Stow,
     L1,
     L2,
     L3,
     L4,
     Net,
     Prep,
     Ground,
     Zero;
   }

This provides an easy-to-use (and much less error-prone) interface for
communicating with the elevator. We just call
``elevator.setRererence(ElevatorState.Net)`` externally to set a reference.
Clean, simple, and most importantly, it eliminates the possibility of an
invalid reference at runtime.

We can also connect these enums to the actual position values they represent:

.. code-block:: java

   public enum ElevatorState {
     Stow(1.2),
     L1(2.0),
     L2(3.2),
     L3(3.8),
     L4(5.0),
     Net(5.5),
     Prep(2.4),
     Ground(0.1),
     Zero(0);

     protected final double position;

     private ElevatorState(double position) {
       this.position = position;
     }
   }

This associates each possible state with a position value that is inaccessible
from outside the elevator package. This ties the numeric constant to a defined
state in a very clean way that reduces much of the boilerplate code.

We can use this in ``Elevator`` like so:

.. code-block:: java

   public Command go(ElevatorState ref) {
     // this code can access ref.position to get a real reference position
     
     return Commands.sequence(
       runOnce(() -> m_reference = ref.position),
       Commands.waitUntil(atReference));
   }

This is the preferred way of dealing with subsystems, and promotes cleaner
code. It also makes it easier to add more reference states.
