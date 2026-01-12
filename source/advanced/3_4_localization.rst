3.4 - Localization
==================

.. note:: This page deals mainly with localization using PhotonVision. Any
   other vision system (e.g. Limelight) is not covered here.

In many FRC games, knowing the robot's position on the field is critical to
solid gameplay. The most powerful way to do this is by utilizing April Tags,
which are posted around the field at known locations. Cameras on the robot can
detect these tags and, depending on the size of the tag in frame and the
position of it, the robot can determine its position to a relatively high
degree of accuracy.

Setup PhotonVision
------------------

The PV docs give a good idea as to how to setup :term:`PV`, but here's the
basic list of steps: 

1. Download the latest PhotonVision disk image for the hardware you will use
   and flash it to a USB.

   It's recommended to connect to this device and change the SSH username and
   password to something that you won't forget. Also consider setting the
   internal clock to the correct time.

2. Plug in the cameras to the device (see section on ArduCams below) and ensure
   that PV sees all of them.

3. Decide on a frame rate and resolution (advice on deciding this further
   down).

4. Calibrate each camera for 3D using at the same resolution, accoring to the
   procedures listed in the PV docs.

Using Arducams
~~~~~~~~~~~~~~

If you're using ArduCams, use the ArduCam Serial Number Tool to give the
devices recognizable names. Things can act weird if multiple devices have the
same exact name.

Deciding a frame rate
~~~~~~~~~~~~~~~~~~~~~

It's not easy to decide on a frame rate and resolution that works best for your
system. However, here's some advice on how to choose:

1. All cameras don't need to be the same resolution. Different cameras in
   different locations can be different resolutions, even if they are expected
   to see the same thing on the robot.

2. High resolution often means low processing speed, but higher accuracy -
   especially at a distance. If a camera is expected to need to see far away,
   then consider a high resolution for that camera.

3. Low resolution means a fast frame rate, so multiple updates faster. Low
   resolution isn't really a problem when the cameras only require accuracy
   when very close to the tag. However, avoid very slow resolutions, because
   they can have frame rates that are *too* fast. You don't need the robot to
   process hundreds of updates per second. There's not enough gain to support
   the super-low resolutions.

Code structure
--------------

Let's now focus on how we can deal with :term:`PV` in code.

Firstly, do we want vision to act as a subsystem, just like all the other
mechanisms on the robot? Many teams take this approach, but I don't believe
that this is the way to go. For one, vision doesn't need the requirements
features that subsystems get. Also, we may not want our vision updates to occur
synchronously with all the other ``periodic()`` methods in code. Maybe we'd like
another thread for that instead, if we have lots of vision updates per second.

Structure overview
~~~~~~~~~~~~~~~~~~

Here's a brief overview of the structure that we're going to be developing
throughout the rest of this chapter.

- Cameras are represented with the ``CameraIO`` interface.

- Each camera has an associated ``SingleInputPoseEstimator`` (SIPO) to handle
  localization values from *that* camera. These SIPOs also generate a standard
  deviations matrix for each estimate based on the data from the estimate as
  well as the camera's initial trust values.

- All SIPOs are controlled by a single ``AprilTagVisionHandler`` which is
  responsible for polling each SIPO when it runs. The ``AprilTagVisionHandler``
  is also responsible for cross-referencing outputs from each SIPO to ensure
  valid estimates, through a ``MultiInputFilter``.

- The ``AprilTagVisionHandler`` reports valid estimates to the drivetrain
  through a consumer of the estimates (which are simple
  ``TimestampedPoseEstimate`` records).

``CameraIO`` interface
~~~~~~~~~~~~~~~~~~~~~~

Because cameras can either be real cameras (``CameraIOHardware``) or simulated
(``CameraIOAprilTagSimulation``). Either way, they must implement the following
interface:

.. code-block:: java
   :caption: CameraIO.java

   public interface CameraIO {
     void updateInputs(CameraIOInputs);

     public class CameraIOInputs {
       public boolean connected;
       public List<PhotonPipelineResult> unreadResults = new ArrayList<>();
     }
   }

.. note:: The ``CameraIOInputs`` class is so small it doesn't need its own
   file.

``TimestampedPoseEstimate`` record
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This record type is used to store the following information about an estimate:

- Estimated pose, as a ``Pose2d``.

- Estimated standard deviations deviations matrix, as a ``Matrix<N3, N1>``

- The timestamp the estimate was received at, as a ``double``.


``SingleInputPoseEstimator`` (SIPO)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Each camera is passed to a SIPO with other parameters, such as the camera's
robot-relative offset, and the camera's initial standard deviations matrix
(initial trust). The constructor also takes a consumer of a
``TimestampedPoseEstimate`` which it uses to send estimates after it processes
them. Finally, it also accepts a ``MultiInputFilter``.

The ``SingleInputPoseEstimator`` class exposes a method ``refresh(Pose2d
robotPose)`` which uses its internal ``CameraIO`` object to update the
``CameraIOInputs`` value. If the camera is disconnected, display a warning
using an ``Alert``. Finally, this method updates the ``MultiInputFilter`` with
the tag IDs that were seen.

Then, the SIPO also exposes a ``run()`` method, which does the following steps.

1. For each unread result, it filters any results that:

   - Don't have a tag in view

   - Latency or ambiguity is too high

   - Estimated pose is unreasonable (off-field or not on the ground)

   - ``MultiInputFilter`` deems a result unreasonable.

2. Groups all results that passed inspection

3. Uses the pose estimator to get an estimated pose. You should begin by trying
   to do a multitag estimation, and if that's not available, then move to
   another solution. Testing is required to determine the best solution for
   single-tag estimation in your use case.

4. Calculates the estimated standard deviations matrix for each estimate.

5. Constructs and sends off (to the consumer) a ``TimestampedPoseEstimate`` for
   each result that has made it to the end.

``AprilTagVisionHandler``
~~~~~~~~~~~~~~~~~~~~~~~~~

This is the class that is responsible for instantiating each SIPO and its
camera objects. It also exposes a method ``run()`` which does the following steps:

1. Resets the ``MultiInputFilter`` so that old results are ignored.

2. Polls the current robot pose, and runs ``refresh()`` on each SIPO.

3. Calls ``run()`` on each SIPO.

``MultiInputFilter``
~~~~~~~~~~~~~~~~~~~~

By recording each tag seen - and the camera that saw it - each loop, the filter
is able to disregard nonsensical results that aren't considered invalid by a
SIPO. For example, a result may look good to one pose estimator, but if it
would mean the robot's in a position where a *different* camera wouldn't have
been able to see the tags that it did, that result can be invalidated by this
filter.

This can be computationally intensive with lots of results, so should be
avoided when the robot is enabled.

Tips
----

A lot of this page especially is oddly abstract and vague for this book, but
that's because vision requires a lot of testing to determine the optimal
configuration. This is just a recommendation on *structure*, not
*implementation*.

Mostly, do the following to get the highest chance of success with vision:

1. Test stuff. Even in simulation, you can see what works best for your system.

2. Read OA posts on Chief Delphi. You'll see what a lot of successful teams
   have chosen to do, and maybe it'll inspire you.

3. Don't try to over-optimize. In 2025, team 2910 didn't have the advanced
   logic here - i.e. std. devs. They just set their pose to the value of an
   estimate when a new result came in. And they won the whole thing.
