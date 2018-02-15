/*
 * Copyright (c) 2014 Biopet
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

import htsjdk.samtools.reference.IndexedFastaSequenceFile
import htsjdk.variant.variantcontext.Allele
import htsjdk.variant.vcf.VCFFileReader
import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

import scala.collection.JavaConversions._

class MpileupToVcfTest extends ToolTest[Args] {
  def toolCommand: MpileupToVcf.type = MpileupToVcf
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      MpileupToVcf.main(Array())
    }
  }

  val pileup: String = resourcePath("/paired01.pileup")

  @Test
  def testMain(): Unit = {
    val tmp = File.createTempFile("mpileup", ".vcf")
    tmp.deleteOnExit()
    val args =
      Array("-I", pileup, "--sample", "test", "-o", tmp.getAbsolutePath)

    MpileupToVcf.main(args)
  }

  @Test
  def validateOutVcf(): Unit = {
    val tmp = File.createTempFile("mpileup", ".vcf")
    tmp.deleteOnExit()
    val args = Array("-I",
                     pileup,
                     "--sample",
                     "test",
                     "-o",
                     tmp.getAbsolutePath,
                     "--minDP",
                     "1",
                     "--minAP",
                     "1")
    MpileupToVcf.main(args)

    val vcfReader = new VCFFileReader(tmp, false)

    // VariantContexts validate on creation
    // therefore we just have to loop through them

    vcfReader.foreach(_ => 1)

  }

  @Test
  def extraValidateOutVcf(): Unit = {
    val tmp = File.createTempFile("mpileup", ".vcf")
    tmp.deleteOnExit()
    val args = Array("-I",
                     pileup,
                     "--sample",
                     "test",
                     "-o",
                     tmp.getAbsolutePath,
                     "--minDP",
                     "1",
                     "--minAP",
                     "1")
    MpileupToVcf.main(args)

    val vcfReader = new VCFFileReader(tmp, false)

    val fasta = resourcePath("/chrQ_allN.fa")

    val sequenceFile = new IndexedFastaSequenceFile(new File(fasta))

    for (record <- vcfReader) {
      val alleles = record.getAlleles.toSet
      val refAlleles = alleles -- record.getAlternateAlleles.toSet

      refAlleles.size should be >= 1

      val realRef = Allele.create(
        sequenceFile
          .getSubsequenceAt(record.getContig, record.getStart, record.getEnd)
          .getBases,
        true)

      for (ref <- refAlleles) {
        record.extraStrictValidation(ref, realRef, Set(""))
      }
    }
  }
}
