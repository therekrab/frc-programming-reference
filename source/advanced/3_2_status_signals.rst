3.2 - Tips for CTRE Status Signals
==================================

When using CTRE devices over a CAN network, methods such as ``getVoltage()`` or
``getPosition()`` don't just return the value, but instead return
``StatusSignal``\s that wrap the internal value.

``StatusSignal``\s can be cached, refreshed, and they also carry data about
*how* the request went (i.e. success or failure).

Basic Status Signals
--------------------

A ``StatusSignal`` is just a class that represents the live data reported by a
device. They carry data about the value, but also latency, timestamp, units,
and any errors that were encountered, making them more versatile than just a
``DoubleSupplier``.

A CTRE device has many ``getXXX()`` methods which all return
``StatusSignal``\s. You can then use these status signals to query the data
that you're looking for.

For example, we can get the position of a motor like this:

.. code-block:: java

   StatusSignal<Angle> positionSignal = motor.getPosition();
   Angle position = positionSignal.getValue();

However, in times when performance is critical and we want to ensure that our
code is as efficient as possible, this code has a few issues. Most importantly,
the method ``motor.getPosition()`` not only retrieves a ``StatusSignal``
object, but it also automatically refreshes it. This isn't a problem, per say,
but it does leave some room for a bit of improvement. It turns out that
updating each ``StatusSignal`` you use in your robot program individually is
far less performant than updating them all at once. I can't really tell you
why; feel free to investigate yourself.

The takeaway here is that the robot program shouldn't call
``motor.getPosition()`` each loop, because it refreshes the status signal each
time the method is called. You can instead pass an optional ``boolean`` value
into the ``getXXX()`` method, which enables or disables the automatic refresh
behavior:

.. code-block:: java

   StatusSignal<Angle> positionSignal = motor.getPosition(false);
   // Now we have to update the signal ourselves:
   positionSignal.refresh();
   Angle position = positionSignal.getValue();

The advantage here is hard to see, but it is more clear if you're familiar with
the ``BaseStatusSignal`` class. That class provides a method ``refreshAll()``
which updates a group of status signals, all at once.

Because of how unintuitive the ``boolean`` argument is to the ``getXXX()``
method, sometime you will also see robot programs simply cache the
``StatusSignal`` object at the program initialization and keep calling
``BaseStatusSignal.refrehAll()`` on the same objects.

.. note:: There is no performance difference between calling
   ``motor.getXXX(false)`` and caching the output; the method ``getXXX(false)``
   simply returns a cached object anyways.

Updating Status Signals
-----------------------

There are two main ways to update a cached ``StatusSignal`` value:

1. Calling ``refresh`` on a single instance updates its value.

2. Calling ``BaseStatusSignal.refreshAll(StatusSignal[] signals)`` updates
   *all* signals in the array. All the status signals' associated devices must
   be on the same CAN network.

Although the first option is simpler, it can be better to use ``refreshAll()``
because it's more performant. Although we can group status signals by
subsystem, we get better performance if *all* status signals on one bus are
grouped together. Therefore, we need some global helper class to update all
signals.

Status Signal Helper
~~~~~~~~~~~~~~~~~~~~

Here's what a basic status signal class may look like that has two CAN buses.

.. code-block:: java
   :caption: StatusSignalUtil.java

   @SuppressWarnings("rawtypes")
   public class StatusSignalUtil {
     private static StatusSignal[] rioSignals = new StatusSignal[0];
     private static StatusSignal[] canivoreSignals = new StatusSignal[0];
 
     public static void registerRioSignals(StatusSignal... signals) {
       StatusSignal[] newSignals = new StatusSignal[rioSignals.length + signals.length];
       System.arraycopy(rioSignals, 0, newSignals, 0, rioSignals.length);
       System.arraycopy(signals, 0, newSignals, rioSignals.length, signals.length);
       rioSignals = newSignals;
     }
 
     public static void registerCANivoreSignals(StatusSignal... signals) {
       StatusSignal[] newSignals = new StatusSignal[canivoreSignals.length + signals.length];
       System.arraycopy(canivoreSignals, 0, newSignals, 0, canivoreSignals.length);
       System.arraycopy(signals, 0, newSignals, canivoreSignals.length, signals.length);
       canivoreSignals = newSignals;
     }
 
     public static void refreshAll() {
       if (rioSignals.length > 0) {
         BaseStatusSignal.refreshAll(rioSignals);
       }
       if (canivoreSignals.length > 0) {
         BaseStatusSignal.refreshAll(canivoreSignals);
       }
     }
   }

Subsystems can register all ``StatusSignal``\s that they want refreshed, and
then ``StatusSignalUtil.refreshAll()`` can be called every periodic loop in
``robotPeriodic()`` in ``Robot.java``.
