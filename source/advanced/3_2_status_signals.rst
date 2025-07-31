3.2 - Tips for CTRE Status Signals
==================================

When using CTRE devices over a CAN network, methods such as ``getVoltage()`` or
``getPosition()`` don't just return the value, but instead return
``StatusSignal``\s that wrap the internal value.

``StatusSignal``\s can be cached, refreshed, and they also carry data about
*how* the request went (i.e. success or failure).

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
--------------------

Here's what a basic status signal class may look like that has two CAN buses.

.. code-block:: java
   :caption: StatusSignalUtil.java

   @SuppressWarnings("rawtypes")
   public class StatusSignalUtil {
     private static StatusSignal[] m_rioSignals = new StatusSignal[0];
     private static StatusSignal[] m_canivoreSignals = new StatusSignal[0];
 
     public static void registerRioSignals(StatusSignal... signals) {
       StatusSignal[] newSignals = new StatusSignal[m_rioSignals.length + signals.length];
       System.arraycopy(m_rioSignals, 0, newSignals, 0, m_rioSignals.length);
       System.arraycopy(signals, 0, newSignals, m_rioSignals.length, signals.length);
       m_rioSignals = newSignals;
     }
 
     public static void registerCANivoreSignals(StatusSignal... signals) {
       StatusSignal[] newSignals = new StatusSignal[m_canivoreSignals.length + signals.length];
       System.arraycopy(m_canivoreSignals, 0, newSignals, 0, m_canivoreSignals.length);
       System.arraycopy(signals, 0, newSignals, m_canivoreSignals.length, signals.length);
       m_canivoreSignals = newSignals;
     }
 
     public static void refreshAll() {
       if (m_rioSignals.length > 0) {
         BaseStatusSignal.refreshAll(m_rioSignals);
       }
       if (m_canivoreSignals.length > 0) {
         BaseStatusSignal.refreshAll(m_canivoreSignals);
       }
     }
   }

Subsystems can register all ``StatusSignal``\s that they want refreshed, and
then ``StatusSignalUtil.refreshAll()`` can be called every periodic loop in
``robotPeriodic()`` in ``Robot.java``.
