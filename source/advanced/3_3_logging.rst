3.3 - Logging
=============

It's incredibly helpful to be able to log values from the robot onto a USB
drive, and be able to load that up later. There are multiple ways to go about
doing this.

Epilogue (recommended)
----------------------

Epilogue is an annotation-based logging framework that ships with WPILib
automatically. It works by annotating fields or methods with ``@Logged``, and
automatically polls those fields/methods every robot loop to log data.

The full documentation for Epilogue can be found `here
<https://docs.wpilib.org/en/stable/docs/software/telemetry/robot-telemetry-with-annotations.html>`_.

Configuration
~~~~~~~~~~~~~

You configure Epilogue in the ``Robot.java`` class, in the constructor for
``Robot``. Here is an example from the docs that shows how to configure
Epilogue (I've highlighted the most important lines):

.. code-block:: java
  :emphasize-lines: 6,11,15,20,22

  public class Robot extends TimedRobot {
    public Robot() {
      Epilogue.configure(config -> {
        // Log only to disk, instead of the default NetworkTables logging
        // Note that this means data cannot be analyzed in realtime by a dashboard
        config.backend = new FileBackend(DataLogManager.getLog());

        if (isSimulation()) {
          // If running in simulation, then we'd want to re-throw any errors that
          // occur so we can debug and fix them!
          config.errorHandler = ErrorHandler.crashOnError();
        }

        // Change the root data path
        config.root = "Telemetry";

        // Only log critical information instead of the default DEBUG level.
        // This can be helpful in a pinch to reduce network bandwidth or log file size
        // while still logging important information.
        config.minimumImportance = Logged.Importance.CRITICAL;
      });
      Epilogue.bind(this);
    }
  }

Typically, it's advisable to only log to disk rather than NT, because the
network writes can take a long time (relative to the robot's loops).

Usage
~~~~~

To use the annotation-based logging, simply provide a decorator before a method
or field in a class to automatically register it with the logging tools, and
you're set!

Best Practices
~~~~~~~~~~~~~~

The actual logging of data is not very computationally expensive. Instead, what
*can* cause Epilogue to take significant time is by reading expensive methods.
When you log a method in your project, Epilogue with call that method once a
loop to get the latest data. This is concerning, especially if your methods
read anything from the network or if the method(s) read anything from
mechanisms.

Instead, it will generally perform better to log *fields* rather than methods.
It turns out that we've actually already built a project structure that works
well for this: our ``IOInputs`` classes are *only* fields!

Here's how we could modify a basic ``IOInputs`` class to simply log everything.

.. code-block:: java
   :emphasize-added: 1,5

   @Logged
   public class ShooterIOInputs {
     public Voltage voltage;
     public Amps current;
     @Logged(name="Motor Temperature") // This sets a specific name for the field rather than the variable's name
     public Temperature motorTemp;
     /* snip */
   }

It's so simple, yet so effective! Adding the ``@Logged`` annotation to a class
automatically logs all non-static fields on the class!

You can also log ``Trigger``\s and enums, meaning that it's *very* easy to log
the ``StateManager`` class:

.. code-block:: java
   :emphasize-added: 1

   @Logged(name="Robot-wide State")
   public class StateManager {
     public Trigger shootReady() {
       /* snip */
     }

     public PieceStatus pieceStatus() {
       /* snip */
     }
   }

As long as the methods in ``StateManager`` aren't incredibly computationally
expensive (i.e. motor reads or NT reads), calling them all in your code isn't
an issue.

Write your own logger
---------------------

Using WPILib' ``DataLog`` class (and its other related classes), you can write
your own simple logger which logs data values to ``wpilog`` files.

This is a very fun activity, and ensures compatibility with anything you can
think of - because you write the logger yourself. However, it's not very
practical and I can't really recommend it for anything more than a side-quest
for fun. Definitely don't use this for competiions or a real robot.

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
