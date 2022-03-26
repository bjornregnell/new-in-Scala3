// run with scala-cli from https://scala-cli.virtuslab.org/install
// by typing this command in terminal
// scala-cli run --watch . -S 3.nightly -language:experimental.fewerBraces  

@main def run = 
  println(slides.show)
  slides.toPdf()

def slides: Tree = document("New in Scala 3"):
  frame("New in Scala 3"):
    itemize:
      p("My favorite new stuff in Scala 3")
      p("another item")
      enumerate:
        p("subitem one")
        p("subitem two")
