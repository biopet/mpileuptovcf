package nl.biopet.tools.mpileuptovcf

import java.io.File

import nl.biopet.utils.tool.AbstractOptParser

class ArgsParser(cmdName: String) extends AbstractOptParser[Args](cmdName) {
  opt[File]('I', "input") valueName "<file>" action { (x, c) =>
    c.copy(input = x)
  } text "input, default is stdin"
  opt[File]('o', "output") required () valueName "<file>" action { (x, c) =>
    c.copy(output = x)
  } text "out is a required file property"
  opt[String]('s', "sample") required () action { (x, c) =>
    c.copy(sample = x)
  } text ""
  opt[Int]("minDP") action { (x, c) =>
    c.copy(minDP = x)
  } text ""
  opt[Int]("minAP") action { (x, c) =>
    c.copy(minAP = x)
  } text ""
  opt[Double]("homoFraction") action { (x, c) =>
    c.copy(homoFraction = x)
  } text ""
  opt[Int]("ploidy") action { (x, c) =>
    c.copy(ploidy = x)
  } text "Specify the ploidy as a number: '1' for haploid, '2' for diploid etc."
  opt[Double]("seqError") action { (x, c) =>
    c.copy(seqError = x)
  } text ""
  opt[Unit]("refCalls") action { (_, c) =>
    c.copy(refCalls = true)
  } text ""
}
