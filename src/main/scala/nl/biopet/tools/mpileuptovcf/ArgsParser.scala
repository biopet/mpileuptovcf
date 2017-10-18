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
  }
  opt[Int]("minDP") action { (x, c) =>
    c.copy(minDP = x)
  }
  opt[Int]("minAP") action { (x, c) =>
    c.copy(minAP = x)
  }
  opt[Double]("homoFraction") action { (x, c) =>
    c.copy(homoFraction = x)
  }
  opt[Int]("ploidy") action { (x, c) =>
    c.copy(ploidy = x)
  }
  opt[Double]("seqError") action { (x, c) =>
    c.copy(seqError = x)
  }
  opt[Unit]("refCalls") action { (_, c) =>
    c.copy(refCalls = true)
  }
}
