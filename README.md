# New in Scala3

A quick tour of selected new stuff in Scala 3.

# How to build the slides

* Clone this repo or download the `.scala` files.

* Install scala-cli from https://scala-cli.virtuslab.org/install

* Make sure you have `latexmk` on your path. See: https://www.latex-project.org/get/

* Run this in terminal:
```
scala-cli run --watch . -S 3.nightly -language:experimental.fewerBraces  
```

* If you have `latexmk` on your path then you can now find the slides in `target/tex/output.pdf`

* Look at the source to see Scala 3 in action.

