Intro To Advanced Design
========================

This section is exactly what it says. Advanced *Design*. This section is not
going to discuss software optimizations, the inner workings of Network Tables,
etc. This section also will use parts of WPILib that I may not have gone over
already, Instead, this is a *recommendation* as to how to structure projects
that use the command-based paradigm.

Here are the guiding principles, i.e. the reasons that the upcoming design
choices were made.

1. First and foremost, code that we write needs to be *highly malleable*.
   Introducing or removing features should be done without introducing side
   effects, nor should it require changes to the underlying architecture, even
   for more aggressive changes. Although we want an underlying design to remain
   constant, changes to the robot's behavior should be fluid and easy - even
   for newer programmers.

2. Secondly, it is important to write *safe* code. This means code that makes
   it difficult for a programmer to write code that breaks something. A great
   way to do this is by using types and other language features to ensure that
   invalid inputs break at compile time, or using visibility modifiers and
   design patterns to make sure what happens in code happens in the right
   place.

3. Thirdly, it's important to make sure that we remove as much boilerplate as
   possible. Boilerplate to setup or configure parts of code can lead to
   errors, which are never wanted. Too much boilerplate also turns our code
   from a declarative style (what we want the program to do) into more of an
   imperative style (how that works under the hood), thus reducing the
   effectiveness of programmers and introducing repeated code.

These goals are what guides the **Advanced** section, so keep them in mind
throughout the rest of the book.
else coming.
