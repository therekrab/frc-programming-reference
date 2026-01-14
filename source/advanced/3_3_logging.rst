3.3 - Logging
=============

It's incredibly helpful to be able to log values from the robot onto a USB
drive, and be able to load that up later. There are multiple ways to go about
doing this.

Write your own logger
---------------------

Using WPILib' ``DataLog`` class (and its other related classes), you can write
your own simple logger which logs data values to ``wpilog`` files.

This is a very fun activity, and ensures compatibility with anything you can
think of - because you write the logger yourself.

Using DogLog
------------

`DogLog <https://docglog.dev>`_ is another logger for FRC which is simpler than
AdvantageKit and easier to use than the ``DataLog`` classes.

Using AdvantageKit
------------------

`Advantage Kit <https://docs.advantagekit.org/>`_ is a powerful tool developed
by team 6328. It does much more than logging, but instead can shape the entire
framework of your robot code. Whether this is a good thing or not, I'll leave
up to you.

It's definitely not necessary for powerful logging, but some teams swear by it.

Viewing log data
----------------

When it comes time to actually *view* log data, the best option is `Advantage
Scope <https://docs.advantagescope.org/>`_ - by no small margin.

Logging to USB
--------------

With most logging tools, logging to a USB drive is automatic if there is a
valid drive present. For the RoboRIO 2.0, any drives must be formatted with
``FAT32`` and cannot exceed ``32GB`` in size.
