Intro To Advanced Design
========================

This section is exactly what it says. Advanced *Design*. I'm not going to
discuss software optimizations, the inner workings of Network Tables, etc.
Instead, I offer advice on how *I* like to structure projects that use the
command-based paradigm.

My priorities when writing code with this style are as follows, in order:

1. I strive to remove repetition in ideas, and also boilerplate code. I'm okay
   with a little verbosity, as long as it serves a real purpose. If I have to
   implement the same thing twice, and *they mean the same thing*, I will look
   for a better way to do it. I want to make room for change in the future.

2. I want to add as much safety as I can. Whether this is clever use of types
   to ensure that compile-time checks can prevent bugs, or limiting access to
   something in a novel way, I want to prevent myself and others from being
   able to write code that can be dangerous or exhibit undefined behavior. 

3. Writing code that reads like it performs is a goal here. I don't want to
   worry about the inner workings of the system I build, but rather only write
   what the program should do - not how it should happen under-the-hood.

These goals are what guides the **Advanced** section. Now you know.
