1.1 - Version Control
=====================

Programming is just writing text. We'll get to how to write these programs in a
later section, but from a high level standpoint, we're really only writing text
and working to make sure that our text reads to the computer in the way we
intend.

So, let's picture an essay for English class. That essay can represent our
program code. The contents of the essay aren't the most important thing here,
so I'll leave that up to your imagination.

Let's now introduce an abstract idea of some system called **version control**.
For now, all we need to know is that version control is a tool that lets us
*control*, or modify, the *version* of our document that we're working with.
Don't worry if this sounds scary or doesn't make sense. Hopefully this will
become clearer as the page continues, but what we're going to try to do it lay
out a list of common problems we may have while we write code (our essay) and
how version control solves those problems.

Saving instances of code at a point in time
-------------------------------------------

Imagine we're writing code and we want to try to make a new feature. Let's say
this correlates to wanting to add some new section of our essay. However, we're
not sure if the essay will work nicely with the new section, so we want some
way to make sure we can revert our changes back to the original version -
before the addition (or deletion; it doesn't matter).

This is common in code. We make some modification, but we want to leave
ourselves protected if something doesn't work out and we introduce a **bug** -
a mistake in our code that makes it act in some unexpected or undesired way.

.. admonition:: Example of a bug

   Let's pretend we wrote some code for an adding machine. It adds two numbers
   together. A **bug** occurs when the result our program gets isn't the result
   that we expect. We enter the numbers ``6`` and ``2``, but the program spits
   out ``12`` instead of ``8``. We've got an issue in our code - a bug.

We want some way to effectively take a "snapshot" of the current program state
and save it in case we want to return here later.

This is a feature of a version control system: we can create these "snapshots"
of our code that we can refer to later. Each snapshot must be independent from
the others, which means that if we take a snapshot today and make some changes
to our code tomorrow, the snapshot we made today will be unaffected.

We call these "snapshots" of code **commits**. Commits are exactly what we've
defined - they capture the code right now and don't ever change. We can jump
around in the program's history as well, allowing us to see the program at
different times and states.

This ensures that we don't ever lose code. We can always just roll back
(revert) whatever changes we make if they don't work out, or we can look back
at old code to see how something worked.

These **commits** also carry **metadata** - data about the commits themselves,
not the code at this point. This information includes the date and time of the
commit, as well as the author who made the commit. This means we can look back
in code to see who did what - and when.

Commits also carry **commit messages** - brief segments of plain text that
describe what changes are in a commit. This makes it easy to go back in time
through our code - we can get quick overviews of each snapshot, in English,
that let us tell what happened. This is much faster than looking at the code at
every commit to find when a change was introduced.

Different versions of code
--------------------------

Okay, let's get back to our example essay. Now let's say we've been struck by
creativity and we've got a new idea for a new way to rephrase our essay.

We could simply start from the latest version (the most recent **commit**) and
make our change. If we don't like how the change seems, we could revert back to
the previous commit and be good to go.

However, this has a few flaws. The major issue is that there may be two
different things happening at the same time. Let's say that we want to try the
rewording at the same time that we are writing our conclusion. If we rephrase
some bit, then write the conclusion, we've got a problem if we want to revert
back to the "original version". We would not only throw away the rephrasing
changes, but we have no way to keep the important conclusion! We'd have to
rewrite the conclusion again!

There's another issue with this approach. This issue is less practical, but
still is important to consider. Imagine if we *had* decided to do the rewrite
then add the conclusion.

In the commit history, we would see something that looked like this:

.. code-block:: text
   :caption: Commit history

   (snip)
   4. Rewrite some bit
   5. Add conclusion

This doesn't accurately reflect our intentions here. This looks like we
performed the rephrasing before we did the conclusion. We've forced ourself
into a straight line for our history, even though it would be more accurate to
say that we were working on two things at the same time.

.. tip:: Often in programming, a solution doesn't just need to work. Instead,
   it's best if the solution *makes sense* and accurately mirrors the
   real-world situation the code is meant to reflect. Programming to make the
   code work is good, but it's best if the code actually is an accurate
   representation of the real phenomenon that it depends on. This will be
   important throughout nearly all of this book.

If only there was some way to tell the version control system that we didn't do
A *then* B, but instead A *and* B *simultaneously*.

Alas, this situation is so common that version control systems have a feature
for this! Version control systems can "split" a project's history at some point
into two separate versions, each independent of each other. For our example, we
could continue on the regular version for our conclusion, but split off right
before adding that to do the rewrite in an isolated environment.

These "parallel worlds" of code are called **branches**. Multiple branches can
exist at once, which means that we can very easily add in multiple features.

Branches also have to be reconciled back into one, however, otherwise there's
really no point! We call this process **merging**.

.. note:: Merging branches is so important that it's got it's own page coming
   up!

Let's try a new visualization of the same commit history we saw last time:

.. code-block:: text
   :caption: Commit history

   --- (last change) --- ( rephrase ) --- ( add conclusion )

.. admonition:: How to read this visual

   This shows commits from oldest to newest, from left to right. Each commit is
   directly "attached" to its previous and next commit, showing which versions
   of the code came from which original states.

   For example, we see that the ``add conclusion`` commit made changes to the
   state of ``rephrase``. If we want to get rid of rephrase, how would we do
   that while keeping the ``add conclusion`` commit? It's original version is
   now gone, too, so how would we know what changes to make?

This shows what we saw earlier - the rephrase commit preceded the commit to add
the conclusion, and the ``add conclusion`` commit came from the ``rephrase``
commit, so you can't jump to before the ``rephrase`` commit without losing the
``add conclusion`` part as well.

Here's a version that utilizes branches to work on different parts of code in parallel:

.. code-block:: text
   :caption: Commit history

                      -- ( rephrase ) ---------
                     / 
   --- (last change) --- ( add conclusion ) ---

.. admonition:: What this means

   This visual shows a different story. Here, both the ``rephrase`` commit and
   the ``add conclusion`` commit stem from the same version of code. Here,
   either commit can be removed without cutting off the other. This also shows
   how they're independent. Changes after one commit don't affect the other
   parallel version (branch).

Now we see that the ``rephrase`` and ``add conclusion`` commits *aren't*
directly sequenced after each other. That's great, and exactly what we want to
see!

Tracking multiple files
-----------------------

As our code increases in size, we will move some parts of it to different files
to organize it. Version control systems don't just have to "track" (save
changes for) a single file, but rather can save entire folders. These folders
are referred to as **repositories** and hold the version control information
for the entire folder! This means changes across multiple files can be saved in
the same history.

Saving code remotely to collaborate
-----------------------------------

It's helpful to write code with other people well. Version control systems can
also push all of the commits in a repository to a **remote** location. Here,
remote means that the location isn't on our computers - instead, it's on the
internet and we can access it from anywhere, like an essay in Google Docs.

Think of this like a shared folder in Google Drive. Anybody can make changes,
and then everybody else can see those changes. But version control systems are
*way* more powerful, as we've already seen. We don't just have access to the
current version - instead, we can see *every* "snapshot" (commit) of the
repository by everybody throughout its entire history.

.. note:: There are a few nuances when it comes to writing code across devices,
   but those don't belong in this page. Rest assured, they will be covered,
   however.
