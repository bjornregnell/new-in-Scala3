//run in terminal with scala-cli from https://scala-cli.virtuslab.org/install
//scala-cli run --watch . -S 3.nightly -language:experimental.fewerBraces  

@main def run = 
  println(slides.show)
  slides.mkLatex()

def slides: Tree = document("New in Scala 3"):
  frame("New in Scala 3"):
    itemize:
      p("My favorite new stuff in Scala 3")
      p("another paragraph")


