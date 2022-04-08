// run with scala-cli from https://scala-cli.virtuslab.org/install
// by typing this command in terminal
// scala-cli run --watch . -S 3.nightly -language:experimental.fewerBraces  

@main def run = slides.toPdf()

def slides = document("New in Scala 3"):
  frame("Goals"):
    itemize:
     p("Showcase interesting new things in Scala")
     p("Help you get started with Scala 3")
     p("Illustrated through a DSL for slides (these slides...)")

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


