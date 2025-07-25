2.3 - Writing Java Code
=======================

Before we write any Java code, we've got one very important question: *where*
do we write Java code?

For now, we're going to let VS Code take care of the behind-the-scenes
operations necessary to compile Java code.

Let's open up VS Code and go to a new folder with ``File`` > ``Open Folder``.
Create a new folder to store our first java code in.

I've call this folder ``examples``.

.. figure:: /_static/vs_code_new_folder.png

   Please ignore the source control setup; don't mind the 61 changes. They're
   not the point here. You don't need to setup source control in this project,
   but if you want you should because it's good pracitce.

Create a new file called ``Main.java`` (note the capital M).

.. image:: /_static/vs_code_new_main_java.png

We've now created a Java file with the extension ``.java``.

Copy and paste the following contents into the file:

.. literalinclude:: examples/Main.java
   :caption: Main.java
   :language: java

Currently, you aren't expected to know what *any* of this does. In the future,
we'll break down everything we're doing here.

To run the code, select the play button in the top right of the screen. This
runs your code.

.. important:: This will not work once we start writing real robot code. For
   now, this is a great way to run our code, but in the future, we won't be
   able to use this button. That'll be covered in much more detail in later
   chapters, however.

.. image:: /_static/vs_code_run_java.png 

This is a lot of output. However, we only care about the last line: ``Hello,
world!``. That's the output from our program. The first few lines are just the
instructions that VS Code gives to the computer to run the code.

To close this panel, select the garbage icon in the top right corner of the
pane.

Congratulations! You just ran your first bit of Java code. Now let's dive into
the wonderful world of Java in the rest of this chapter.
