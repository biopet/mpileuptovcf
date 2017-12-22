/*
 * Copyright (c) 2014 Sequencing Analysis Support Core - Leiden University Medical Center
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.biopet.tools.mpileuptovcf

import java.io.File

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]('I', "input") valueName "<file>" action { (x, c) =>
    c.copy(input = x)
  } text "input, default is stdin"
  opt[File]('o', "output") required () valueName "<file>" action { (x, c) =>
    c.copy(output = x)
  } text "output file (required)"
  opt[String]('s', "sample") required () action { (x, c) =>
    c.copy(sample = x)
  } text "Sample name in the vcf file"
  opt[Int]("minDP") action { (x, c) =>
    c.copy(minDP = x)
  } text "Minimal total depth"
  opt[Int]("minAP") action { (x, c) =>
    c.copy(minAP = x)
  } text "Minimal alternative depth"
  opt[Double]("homoFraction") action { (x, c) =>
    c.copy(homoFraction = x)
  } text "If alleles are above this fraction it's being seen as homozygous. Default if 0.8"
  opt[Int]("ploidy") action { (x, c) =>
    c.copy(ploidy = x)
  } text "Specify the ploidy as a number: '1' for haploid, '2' for diploid etc."
  opt[Double]("seqError") action { (x, c) =>
    c.copy(seqError = x)
  } text "Expected sequencing error rate, default is 0.005"
  opt[Unit]("refCalls") action { (_, c) =>
    c.copy(refCalls = true)
  } text "If set refcalls are also writen. Warning: This will results in a very large vcf file"
}
