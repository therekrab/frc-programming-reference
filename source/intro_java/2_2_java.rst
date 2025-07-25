2.2 - What is Java?
===================

There are a *lot* of programming languages. Java is just one. Each programming
language has its own rules for how the language must be written, and Java is no
different. The rest of this chapter will focus solely on the Java programming
language.

Compiled vs. interpreted languages
----------------------------------

There are lots of ways to classify programming languages. One of them is the
idea of **compiled** languages vs **interpreted** languages.

A **compiled** language is a language whose source files are turned into some
other kind of file - an **executable**. Executable files aren't meant to be
read by humans. If you've ever seen a ``.exe`` file on Windows, that's an
executable. Executables contain the machine-readable instructions that tell the
computer *exactly* what to do.

Compiled languages **compile**, or translate, the human-readable code into this
machine format before the program is ran. This results in the final file not
having any source code in it, and it is ready to run.

On the other hand, there are **interpreted** languages. These languages don't
spit out another file - instead, they just run your code. They skip the step
where they turn it into machine code, and only do that at **runtime** - when
you run the program.

The advantage of this is that you don't get another file you have to deal with.
However, the disadvantage is that it's easier for computers to read a machine
code file (like that which compiled languages create) than process a
human-readable file format. This means the computer has to do more work while
the program is running to execute the steps of the program.

This can cause reductions in performance because it's harder for the computer
to translate the human-readable format into machine code on the fly. Compiled
langauges fix this problem by doing that translation as a different step, so
the hard work is done before the program is even ran. This can make the code
run faster.

Both language types are helpful, but for performance-critical applications,
like FRC, compiled languages offer more appeal.

Java is a compiled language. It turns ``.java`` files into a ``.class`` file,
which is easier for the computer to read at runtime.

Compile time vs Runtime
-----------------------

It's important to distinguish between **compile time** and **runtime** when
talking about compiled programming languages.

**Compile time** refers to the time when the program is being compiled -
translated from human code to machine code. This is when Java will make sure
that your program is syntactically correct and doesn't break the rules of the
language. But *none of your code* is actually ran.

**Runtime**, as the name implies, referes to when the machine code is actually
running. This is when your code runs.

An understanding of the difference between the two is important to know.

That's enough about the ideas behind programming languages. Let's dive in and
start writing some code.
