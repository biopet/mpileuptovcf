package nl.biopet.tools.mpileuptovcf

import java.io.File

import htsjdk.samtools.reference.IndexedFastaSequenceFile
import htsjdk.variant.variantcontext.Allele
import htsjdk.variant.vcf.VCFFileReader
import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

import scala.collection.JavaConversions._

class MpileupToVcfTest extends BiopetTest {
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
    val args = Array("-I", pileup, "--sample", "test", "-o", tmp.getAbsolutePath)

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
        sequenceFile.getSubsequenceAt(record.getContig, record.getStart, record.getEnd).getBases,
        true)

      for (ref <- refAlleles) {
        record.extraStrictValidation(ref, realRef, Set(""))
      }
    }
  }
}
