April Tag Demo
==============

About
-----

This project involves writing code to aim at a specific april tag, using a
swerve drivetrain to rotate in place and face an AprilTag target.

Specifications
--------------

This project assumes that there is an AprilTag camera on the direct front of
the robot. The goal here is to rotate the robot such that the tag is in the
exact center of the frame, but if the camera is not in the center of the robot,
that task will not properly align the robot with the AprilTag.

We'll use PhotonVision to track AprilTags and report that data to the robot.

Implementation
--------------

This project has two main components:

1. Code to track an AprilTag and track it's relative angle to the robot

2. Code to handle that data and move the chassis in the correct direction.

.. hint:: This code is going to turn out to be very simple. This means that we
   *could* combine all of this into one united software component and avoid the
   dependency pattern we're building here.

   However, I chose to use this structure because it creates a layer of
   abstraction between the vision layer and the swerve drive layer, which means
   that if we want, we can sub out the AprilTag tracker for any other type of
   tracker (gamepiece, color, etc.). This is a pattern called dependency
   injection, which involves splitting up different components to allow for
   switching them out at runtime to achieve similar but different goals.

The logic to track a target could be part of a ``SimpleObjectTracker`` class, and
its output could be fed into the drivetrain to automatically orient the robot
towards the tag.

Writing the tracking class
~~~~~~~~~~~~~~~~~~~~~~~~~~

To create our ``SimpleObjectTracker`` class, we will simply create a class with the
following properties:

- Stores a camera object (using a ``CameraIO`` interface to improve simulation support)

- Stores an optional ``ExpirableTargetData`` object, which is just a record
  class with a ``Rotation2d`` field for the yaw, and a ``double`` field for the
  time the estimate was captured. This allows us to add an expiration period to
  the program.

- Provides a method ``update()`` which should be called every loop, which reads
  the latest value(s) from the camera object and updates the internal target
  data object.

- Provides another method ``getTargetData()`` which returns the internal
  ``ExpirableTargetData`` object (as an ``Optional``).

.. tip:: Instead of setting a variable to ``null``, use an ``Optional<T>``
   class to show that a variable may not take on a value.

Here's what that class may look like:
