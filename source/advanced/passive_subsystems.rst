Passive Subsystems
==================

The **passive subsystem** is an extension of the regular subsystem. It has all
the same features, but it has a default command that is prebuilt off of a
method called ``passive()``, which the user must implement.

PassiveSubsystems give more control over *when* a default command can run,
through the use of a few methods:

``take()`` tells the default command that, even if no command is currently
using the subsystem, the default command should not be ran.

``release()`` tells the default command that, when it can run, it should do so.
The subsystem is "released", or free to be used.

``conditionalRelease()`` accepts a boolean as an argument, and if the boolean
is ``true``, calls ``release()``. This is convenient if you want a subsystem to
return to default behavior if a command is interrupted, but not allow the
default command to run if the command ends regularly. Typically, this method is
seen in cases like ``.finallyDo(subsystem::conditionalRelease)`` to
automatically accept the ``interrupted`` boolean.

Because the default command has logic that worries about the state of the
subsystem, it should not be set manually. Instead, define a method
``passive()`` and let the automatic default command handle the logic for
switching between released and taken states.

Implementation
--------------

Here you will find a reference implementation for the ``PassiveSubsystem``
class, which extends ``SubsystemBase`` for easy use.

.. code-block:: java

   public abstract class PassiveSubsystem extends SubsystemBase { 
     private boolean m_taken;

     public PassiveSubsystem() {
       super();
       m_taken = false;
       enablePassiveBehavior();
     }

     public void take() {
       m_taken = true;
     }

     public void release() {
       m_taken = false;
     }

     public void conditionalRelease(boolean shouldRelease) {
       if (shouldRelease) {
         release();
       }
     }

     private void enablePassiveBehavior() {
       setDefaultCommand(run(() -> {
         if (!m_taken) {
           passive();
         }
       }))
     }

     protected abstract void passive();
   }

The class itself is quite simple, it's just helpful to use because of its
increased control over when it can run.
