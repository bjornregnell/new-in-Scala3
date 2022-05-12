//> using scala "3.nightly"

import scala.language.experimental.fewerBraces

@main def run = slides.toPdf()

def slides = document("Scala 3 goodies"):
  frame("Goals"):
    itemize:
      p("Showcase cool new stuff in Scala 3")
      p("Help you get started with Scala")
      p("Illustrated by Scala 3 DSL (for these slides...)")
      p("https://github.com/bjornregnell/new-in-Scala3/")

  frame("A slide DSL embedded in Scala 3"):
    codeFrom("slides.scala"):
      "frame" -> """frame("Background"""

  frame("Background: What is Scala?"):
    itemize:
      p("A general-purpose programming language")
      itemize:
        p("statically typed: find bugs earlier")
        p("multi-paradigm: imperative, object-oriented, functional")
        p("scalable from small scripts to large production systems")

      p("Scala in comparison to Java")
      itemize:
        p("more concise syntax")
        p("more object-oriented")
        p("more functional")

      p("Multiple execution platforms")
      itemize:
        p("Java Virtual Machine and Graal VM with Java interop")
        p("ScalaJS (both browser and nodeJS)")
        p("Scala Native (pre-compiled binaries, with C/C++ interop)")

    p("A main-stream language with a large open-source library ecosystem and wide-spread use in industry")
      itemize:
        p("Netflix, Twitter, LinkedIn, Spotify, Klarna, Zalando, ...")

  frame("Goals of Scala 3"):
    itemize:
      p("Clean it up based on lessons learnt")
      itemize:
        p("Simpler")
        p("Safer")
        p("More regular")
        p("More expressive")
      p("Stronger foundations: improved type system")
      itemize:
        p("DOT calculus")
        p("Intersection types")
        p("Union types")
        p("Type lambas")
        p("Context functions")
      p("Make migration as smooth as possible")
    p("https://docs.scala-lang.org/scala3/reference/")

  frame("Scala 3 goodies for learners (and you?)"):
    p("An even better tool for teaching and learning:")
    itemize:
      p("Optional braces")
      p("Top-level definitions")
      p("New control syntax")
      p("Enumerations")
      p("Extension methods")
      p("@main functions with type-safe parameters")
      p("Universal apply")
      p("Export")
    p("https://docs.scala-lang.org/scala3/reference/")

  frame("Cleaned-up implicits"):
    p("Implicits in Scala 2: very powerful but criticized")
    itemize:
      p("over-used keyword: **`implicit`**")
      p("encoding by mechanism rather than intent")
      p("implicit conversions can be risky")
    p("Scala 3 replaces `implicit` by:")
    itemize:
      p("**`given`** values and **`using`** parameters")
      p("**`extension`** methods")
      p("Explicit **`import`** of **`given`** values")
      p("A specific type class for conversions")

  frame("Principled Meta-programming"):
    itemize:
      p("The new **`inline`** modifier *guarantees* inlining")
      p("The `scala.compiletime` package provides compile-time ops over values")
      p("Macros with quotes and splices")
      p("Multi-stage programming")
      p("TASTy reflection")

  frame("Scala in the future"):
    p("On-going developments:")
    itemize:
      p("Explicit Nulls and limited flow typing")
      p("Safer Exceptions: CanThrow capabilities **`throws`**")
      p("Capture checking: generalized capabilities")
      itemize:
        p("Impure function type: `A => B`")
        p("Pure function type: ~~~~`A -> B`")
        p("Can capture a capability: \\texttt{\\{c\\} A -> B}")
      p("...")
    p("https://dotty.epfl.ch/docs/reference/experimental/")

  frame("Final Slide: Thank you!"):
    itemize:
      p("https://www.scala-lang.org/")
      p("https://scala-cli.virtuslab.org/")
      p("https://www.scala-js.org/")
      p("https://scala-native.readthedocs.io/en/latest/")
      p("https://docs.scala-lang.org/scala3/")
      p("https://dotty.epfl.ch/docs/reference/experimental/")
