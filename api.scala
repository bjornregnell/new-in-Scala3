def loadLines(path: String): Seq[String] =               // top-level definition
  io.Source.fromFile(path, "UTF-8").getLines.toSeq 

def selectLines(path: String)(fromUntil: (String, String)): Seq[String] = 
  val (from, until) = (fromUntil(0).trim, fromUntil(1).trim)  // apply on tuples
  val xs = loadLines(path).dropWhile(! _.trim.startsWith(from))
  xs.take(1) ++ xs.drop(1).takeWhile(x => 
    if until.isEmpty then x.trim.nonEmpty                  // new control syntax
    else ! x.trim.startsWith(until)
  )

def createDirs(path: String): Boolean = 
  java.io.File(path).mkdirs()             // creator applicators, new not needed

extension (s: String) def save(path: String): Unit =        // extension methods
    val pw = java.io.PrintWriter(java.io.File(path), "UTF-8")
    try pw.write(s) finally pw.close()

enum Tag:          // scalable enums, from simple to generic algebraic datatypes
  case Document, Frame, Itemize, Enumerate, Paragraph

export Tag.*            // tailor your namespace and api, no need for forwarders

case class LatexPreamble(value: String)
object LatexPreamble:
  given LatexPreamble = simpleFrames        //given values: cleaned-up implicits

  def simpleFrames = LatexPreamble(s"""
    |\\documentclass{beamer}
    |
    |\\beamertemplatenavigationsymbolsempty
    |\\setbeamertemplate{footline}[frame number] 
    |\\setbeamercolor{page number in head/foot}{fg=gray} 
    |\\usepackage[swedish]{babel}
    |
    |\\usepackage[utf8]{inputenc}
    |\\usepackage[T1]{fontenc}
    |\\usepackage[scaled=0.95]{beramono} % inconsolata or beramono ???
    |\\usepackage[scale=0.9]{tgheros}
    |\\newenvironment{Frame}[2][]
    |  {\\begin{frame}[fragile,environment=Frame,#1]{#2}}
    |  {\\end{frame}} 
    |""".stripMargin
  ) 
end LatexPreamble                        //end markers are check by the compiler


open class Tree(var tag: Tag, var value: String):   //open classes allow extends 
  val sub = collection.mutable.Buffer[Tree]() 

type TreeContext = Tree ?=> Unit              // abstract over context functions

def root(tag: Tag, value: String)(body: TreeContext): Tree =   //builder-pattern
  given t: Tree = Tree(tag, value)
  body
  t

def leaf(tag: Tag, value: String): TreeContext = 
  summon[Tree].sub += Tree(tag, value)

def branch(tag: Tag, value: String = "")(body: TreeContext): TreeContext = 
  val subTree = Tree(tag, value)
  body(using subTree)
  summon[Tree].sub += subTree

def document(title: String)(body: TreeContext): Tree = root(Document, title)(body)
def itemize(body: TreeContext): TreeContext = branch(Itemize)(body)
def enumerate(body: TreeContext): TreeContext = branch(Enumerate)(body)
def frame(title: String)(body: TreeContext): TreeContext = branch(Frame, title)(body)
def p(text: String): TreeContext = leaf(Paragraph, text)

extension (t: Tree) 
  def show: String = 
    def loop(t: Tree, level: Int): String = 
      val indent = "  " * level
      val node = s"""${t.tag}${if t.value.isEmpty then ":" else "(" + t.value + ")"}""" 
      val subnodes = t.sub.map(st => loop(st, level + 1)).mkString("","","")
      s"$indent$node\n$subnodes"
    loop(t, 0)

  def toLatex: String = Latex.fromTree(t)
  def mkLatex(output: String = "output", workDir: String = "target/tex/"): Unit = 
    Latex.mk(t, output, workDir)

object Latex:
  def fromTree(tree: Tree): String =
    def loop(t: Tree): String =
      inline def tail: String = t.sub.map(loop).mkString
      t.tag match
        case Document  => Latex.env("document")(tail)
        case Frame     => Latex.envArg("Frame")(t.value)(tail)
        case Paragraph => s"${t.value}$tail\n"
        case Itemize | Enumerate => 
          val body = t.sub.map(
            _ match 
              case st if st.tag == Paragraph => 
                  s"\\item ${st.value}\n" ++ st.sub.map(loop).mkString
              case st if st.tag == Itemize || st.tag == Enumerate => loop(st) 
              case st => throw Exception(s"illegal tag inside ${t.tag}: ${st.tag}")
          )
          val listEnv = if t.tag == Itemize then "itemize" else "enumerate"
          Latex.env(listEnv)(body.mkString)
    loop(tree)

  def brackets(params: String*): String = 
    if params.isEmpty then "" else params.mkString("[",",","]")

  def braces(params: String*): String = 
    if params.isEmpty then "" else params.mkString("{",",","}")

  def cmd(command: String): String = s"\\$command"

  def cmdArg(command: String)(args: String*): String = 
      s"\\$command${braces(args*)}"

  def cmdOptArg(command: String)(opts: String*)(args: String*): String = 
    s"\\$command${brackets(opts*)}${braces(args*)}"

  def env(environment: String)(body: String): String = 
    val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
    s"\n\\begin{$environment}\n$body$newlineAfterBody\\end{$environment}"

  def envArg(environment: String)(args: String*)(body: String): String = 
    val newlineAfterBody = if body.endsWith("\n") then "" else "\n"
    s"\n\\begin{$environment}${braces(args*)}\n$body$newlineAfterBody\\end{$environment}"

  def mk(tree: Tree, out: String, workDir: String)(using LatexPreamble): Int = 
    import scala.sys.process.{Process  => OSProc}
    createDirs(workDir)
    (summon[LatexPreamble].value ++ tree.toLatex).save(s"$workDir/$out.tex")
    val wd = java.io.File(workDir)
    val proc = OSProc(Seq("latexmk", "-pdf", "-cd", "-halt-on-error", "-silent", s"$out.tex"), wd)
    val procOutputFile = java.io.File(s"$workDir/$out.log")
    val result = proc.#>(procOutputFile).run.exitValue
    if result == 0 then println(s"Latex output generated in $workDir")
    result
