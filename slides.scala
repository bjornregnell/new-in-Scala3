// run with scala-cli from https://scala-cli.virtuslab.org/install
// by typing this command in terminal
// scala-cli run --watch . -S 3.nightly -language:experimental.fewerBraces  

@main def run = 
  println(slides.show)
  slides.toPdf()

def slides: Tree = document("New in Scala 3"):
  frame("New in `Scala 3`"):
    p("Important for *beginner* `programmer` **teaching** ")
    itemize:
      p("My favorite new stuff *in* **Scala** `3`")
      p("another item")
      enumerate:
        p("subitem *one*")
        p("subitem **two**")
  frame("Scala Teachers Summit 2022 March 29"):
    p("Agenda:")
    itemize:
      p("Scala teachers get introduced to each others")
      p("Present the common needs for teachers, in terms of resources or tooling, as identified through our round of interviews")  
      p("Present and discuss the next steps envisioned by the Scala Center")
      itemize:
        p("create a new page on the Scala website to promote Scala as a vehicle for teaching programming")
        p("create a public discussion forum like contributors.scala-lang.org but for teachers")
        p("create a tool to define “subsets” of Scala by selecting specific language features")
        p("improve online development environments (scastie, notebooks)")

  frame("Some thoughts and suggestions"):
    itemize:
      p("Be explicit about that Scala is general purpose a tool for teaching - universities teach a subject not a language")
      p("Some different teaching contexts:")
      itemize:
        p("pre-university education")
        itemize:
          p("explain why Scala is a good teaching tool for computational thinking compared to Python and Javascript")
        p("university-level education"); itemize:
          p("beginner programming in computer science programs")
          p("beginner programming in non-computer science programs")
          p("data engineering specializations")

        p("professional training")
        itemize:
          p("unemployment re-training")
          p("on-the-job training")

  frame("Scala as a tool for advanced level concepts"):
    p("Explain why Scala is a good teaching tool for advanced level topics, such as:")
    itemize:
      p("concurrency")
      p("efficient data structures and algorithms")
      p("software engineering tools and methods")
      p("compiler technology")
      p("graphics")
      p("AI and ML")
      p("...")

  frame("Progression of teaching with Scala as first language at Lund University: (16 weeks at 1/4 speed, fall semester)"):
    enumerate:
      p("programs and control structures")
      p("functions and abstraction")
      p("objects and encapsulation")
      p("classes and data modelling")
      p("patterns and error management")
      p("sequences and enumerations")
      p("nested structures and generic structures")
      p("sets and tables")
      p("inheritance and composition")
      p("contextual abstraction and api usability")

