# Programming Reference Book

This book is intended to be a living document; that is, edits to this book are
not only encouraged, but also necessary to keep this a usable tool in
introducing students to FRC.

## Why not WPILib docs?

The WPILib documentation is probably the single best resource for programming
in FRC. However, there are a few reasons this document is different and used:

1. This is more of a follow-along guide. It isn't intended to completely
   replace the docs - only supplement them. Although this does mean there will
   be some crossover between the WPILib docs and this book, this book offers a
   different perspective, with more big-project examples rather than just
   reference code.

2. This also includes an introduction to programming in general. This isn't
   covered in the WPILib docs - for good reason. It's reference material to
   WPILib, not the entire process of programming a robot. This book also covers
   an introduction to git.

3. This book offers advice (i.e. opinions) as to how to structure robot code
   that slightly differs from the recommendations put in place by WPILib.
   Rather than fight them, this book instead offers a different perspective and
   highlights how multiple different structures can solve the same problem in
   different ways - and touches on the differences between them.

4. This is more malleable and flexible to our team; that is, we can put basic
   tips for our workflow (CTRE, WPILib, etc.) to make it easier to use.

## Modification Guide

This section lays out the fundamentals of how modifications to this book should
be handled.

### Tech Stack

This book is built with Sphinx and the `sphinxawesome` theme. It uses
ReStructured Text instead of markdown. Unless something needs to fundamentally
change with the website, this structure should remain untouched. Although
*slightly* tricker than markdown, RST is much more powerful and works well with
Sphinx.

Theme configurations should also remain constant unless extremely necessary.

### Book Structure

Unless it becomes absolutely necessary, the general structure and division of
concepts of this book should remain untouched. If a change to the structure of
the book is proposed, it should come to a team consensus and the mentors should
be consulted immediately.

If unsure, err on the side of less drastic changes to the structure of the book.

### Content

For a variety of reasons, content in this book may become outdated. If so,
please feel free to take action to change this. GitHub issues are good; pull
requests are even better.

Content changes should also reflect the trends of how team 3414 programs. Even
if WPILib doesn't change, but a new paradigm begins to appear in the team's
code (and it is preferred for a good reason), the book should be updated to
reflect this.

### Style

When contributing to this book, please attempt to follow the proceeding "style
guide" for written language:

1. Avoid formality without acting too informal. Phrases such as "one should"
   can be replaced with "you should" or "we should". Use "we" rather than "I"
   or "you" in most cases, because it's more inclusive. Slang or informal
   acronyms should be avoided, and proper American English grammar should be
   followed. Citations are best done in a very informal format, such as the
   following example:

   > The [WPILib Docs](https://docs.wpilib.org) say...

2. Avoid the use of acronyms unless they are already defined earlier in the
   book, even if they seem commonplace in FRC or programming in general. For
   example, prefer "undefined behavior" over UB.

3. Following the last point, remember that this book is supposed to be used as
   reference for teaching *brand new* programmers at the start of the
   preseason. Don't use complex language without explaining it, and make sure
   that any and all design choices you make in code (no matter how simple they
   may seem) are well documented. This doesn't mean verbosity in repeating
   yourself every time you declare a variable. Assume that the reader is
   familiar with major content already in the book, but minor asides can be
   repeated to ensure complete understanding.

   Rule of thumb: If you introduce an idea and don't elaborate, the reader
   should be able to look at the table of contents to see where the topic is
   defined. If it's not clear, introduce a definition or link directly to the
   correct page and section.

4. Sections and pages each have numbers, starting from 1 and going up as high
   as necessary. Each section's pages start at 1 and are independent of pages
   from another section. Pages should be titled with the format `SECTION.PAGE -
   TITLE`.
