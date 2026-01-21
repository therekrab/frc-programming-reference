3.5.4 - Debugging Tips & Tricks
===============================

Here's a list of tips and advice that can come in helpful when debugging robot
code.

1. Log as much as possible. The more data we log, the quicker we can pinpoint
   what component of the robot code caused an issue.

2. Don't make any assumptions about values in code. If you don't know what a
   value was when the robot made some decision or action, that value should be
   logged.

3. Never, ever let anything slide via "it's not breaking anything". Letting
   small things through initial testing is a gateway to unexpected behavior,
   which makes it much easier to write bugs.

4. If you really can't figure out what's going on, start from the beginning.
   Write a small new program that does what you need to test - and nothing
   more. It's easier to debug software if you have a small amount of code to
   work with.

5. Reading the documentation for vendor libraries is incredibly important and
   will *always* save time in the long run.

6. Testing regularly is never a bad idea. Ensuring a small change doesn't break
   the robot is rarely impossible or difficult, and its rewards of catching
   bugs early is far greater than the possible time cost - in most cases.

7. Learn how to use sim, and when it's applicable. Simulation is helpful for
   ensuring that the robot components work together well, and testing higher
   abstraction-level software, but it isn't always great when it comes to
   diagnosing issues with real hardware.

8. If applicable, check your units. Unit conversion errors can result in a
   variety of unexpected behavior.

9. Debugging is hard, but never is impossible. You'll get better with
    practice, but it takes time to learn common issues with domain-specific
    technologies and systems (e.g. CTRE devices have their own common
    problems).

10. Write your code with debugging in mind. Good organization and fixing
    unnecessarily tight coupling can help to make sure that code is easy to
    change and test in the future.

11. Sometimes, it's easy to identify the issue, but harder to write the code
    that solves it. In these cases, you may be tempted to use :term:`bandaid
    code`, but avoid these urges unless absolutely necessary. Often, taking the
    additional time to write the *correct* solution ensures no adverse
    side-effects occur.

12. Understand that assumptions are the *only* thing that can get in the way
    between expected behavior and actual behavior. Thus, verify all your
    assumptions - when you turn assumptions about reality into either *true* or
    *false* realities, you *ensure* that you can logically follow the robot's
    path of operation until you find where the actual behavior diverges from
    the expected behavior.
