//> using scala "3.nightly"

def loadLines(path: String): Seq[String] =               // top-level definition
  io.Source.fromFile(path, "UTF-8").getLines.toSeq 

def selectFrom(path: String)(fromUntil: (String, String)): String = 
  val (from, until) = (fromUntil(0).trim, fromUntil(1).trim)  // apply on tuples
  val xs = loadLines(path).dropWhile(! _.trim.startsWith(from))
  (xs.take(1) ++ xs.drop(1).takeWhile(x => 
    if until.isEmpty then x.trim.nonEmpty                  // new control syntax
    else ! x.trim.startsWith(until)
  )).mkString("\n")

def createDirs(path: String): Boolean = 
  java.io.File(path).mkdirs()         // universal apply methods: new not needed

extension (s: String) def saveTo(path: String): Unit =      // extension methods
    val pw = java.io.PrintWriter(java.io.File(path), "UTF-8")
    try pw.write(s) finally pw.close()

enum Tag:          // scalable enums, from simple to generic algebraic datatypes
  case Document, Frame, Itemize, Enumerate, Paragraph, Code

export Tag.*            // tailor your namespace and api, no need for forwarders

open class Tree(var tag: Tag, var value: String):   //open classes allow extends 
  val sub = collection.mutable.Buffer[Tree]() 

extension (t: Tree)                              // collective extension methods
  def show: String = 
    def loop(t: Tree, level: Int): String = 
      val indent = "  " * level
      val node = s"""${t.tag}${if t.value.isEmpty then ":" else "(" + t.value + ")"}""" 
      val subnodes = t.sub.map(st => loop(st, level + 1)).mkString("","","")
      s"$indent$node\n$subnodes"
    loop(t, 0)

  def toLatex: String = Latex.fromTree(t)

  def toPdf(out: String = "out", dir: String = "target")(using Preamble): Unit = 
    Latex.make(t, out, dir)

case class Preamble(value: String)
object Preamble:
  given Preamble = loadPreamble()           //given values: cleaned-up implicits

  def loadPreamble() = Preamble(loadLines("preamble.tex").mkString("\n")) 
end Preamble                           //end markers are checked by the compiler

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
def code(text: String): TreeContext = leaf(Code, text)
def codeFrom(file: String)(fromUntil: (String, String)): TreeContext = 
  code(selectFrom(file)(fromUntil))

object Latex:
  def fromTree(tree: Tree): String =
    def loop(t: Tree): String =
      inline def tail: String = t.sub.map(loop).mkString
      t.tag match
        case Document  => env("document")(s"\\title{${t.value}}\\maketitle\n$tail")
        case Frame     => envArg("frame")("fragile")(t.value.replaceAllMarkers)(tail)
        case Paragraph => s"${t.value.replaceAllMarkers}$tail\n"
        case Itemize | Enumerate => 
          val body = t.sub.map(
            _ match 
              case st if st.tag == Paragraph => 
                  s"\\item ${st.value.replaceAllMarkers}\n" ++ st.sub.map(loop).mkString
              case st if st.tag == Itemize || st.tag == Enumerate => loop(st) 
              case st => throw Exception(s"illegal tag inside ${t.tag}: ${st.tag}")
          )
          val listEnv = if t.tag == Itemize then "itemize" else "enumerate"
          env(listEnv)(body.mkString)
        case Code => env("Scala")(t.value.minimizeMargin)
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
    s"\n\\begin{$environment}\n$body\n\\end{$environment}\n\n"

  def envArg(environment: String)(bracketArgs: String*)(braceArgs: String*)(body: String): String = 
    s"\n\\begin{$environment}${brackets(bracketArgs*)}${braces(braceArgs*)}\n$body\n\\end{$environment}\n\n"

  def beginEndPattern(s: String) = s"$s([^$s]*)$s".r

  val replacePatterns = Map[util.matching.Regex, (String, String)](
    beginEndPattern("\\*\\*") -> ("\\\\textbf{", "}"),
    beginEndPattern("\\*")    -> ("\\\\textit{", "}"),
    beginEndPattern("\\`")    -> ("\\\\texttt{", "}"),
  )

  extension (s: String) 
    def replaceAllMarkers: String = 
      var result = s
      for (pattern, (b, e)) <- replacePatterns do
        result = pattern.replaceAllIn(result, m => s"$b${m.group(1)}$e")
      result

    def minimizeMargin: String = 
      val xs = s.split("\n")
      val minMargin = xs.filter(_.nonEmpty).map(_.takeWhile(_.isWhitespace).length).minOption.getOrElse(0)
      xs.map(_.drop(minMargin)).mkString("\n")
  end extension
 
  def make(tree: Tree, out: String, workDir: String)(using pre: Preamble): Int = 
    import scala.sys.process.{Process  => OSProc}               // rename import
    createDirs(workDir)
    (pre.value ++ tree.toLatex).saveTo(s"$workDir/$out.tex")
    val wd = java.io.File(workDir)
      val proc = OSProc(Seq("latexmk", "-pdf", "-cd", "-halt-on-error", "-silent", s"$out.tex"), wd)
      val procOutputFile = java.io.File(s"$workDir/$out.log")
      val result = proc.#>(procOutputFile).run.exitValue
      if result == 0 then println(s"Latex output generated in $workDir")
      result
