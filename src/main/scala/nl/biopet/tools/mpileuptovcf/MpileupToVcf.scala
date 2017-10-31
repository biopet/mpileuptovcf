package nl.biopet.tools.mpileuptovcf

import java.io.PrintWriter

import cern.jet.random.Binomial
import cern.jet.random.engine.RandomEngine
import nl.biopet.utils.tool.ToolCommand

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.math.{floor, round}

object MpileupToVcf extends ToolCommand[Args] {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

    logger.info("Start")

    if (cmdArgs.input != null && !cmdArgs.input.exists)
      throw new IllegalStateException("Input file does not exist")

    val writer = new PrintWriter(cmdArgs.output)
    writer.println("##fileformat=VCFv4.1")
    writer.println("##ALT=<ID=REF,Description=\"Placeholder if location has no ALT alleles\">")
    writer.println("##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Total Depth\">")
    writer.println(
      "##INFO=<ID=AF,Number=A,Type=Float,Description=\"Allele Frequency, for each ALT allele, in the same order as listed\">")
    writer.println("##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Total Depth\">")
    writer.println("##FORMAT=<ID=AD,Number=.,Type=Integer,Description=\"Total Allele Depth\">")
    writer.println("##FORMAT=<ID=FREQ,Number=A,Type=Float,Description=\"Allele Frequency\">")
    writer.println(
      "##FORMAT=<ID=RFC,Number=1,Type=Integer,Description=\"Reference Forward Reads\">")
    writer.println(
      "##FORMAT=<ID=RRC,Number=1,Type=Integer,Description=\"Reference Reverse Reads\">")
    writer.println(
      "##FORMAT=<ID=AFC,Number=A,Type=Integer,Description=\"Alternative Forward Reads\">")
    writer.println(
      "##FORMAT=<ID=ARC,Number=A,Type=Integer,Description=\"Alternative Reverse Reads\">")
    writer.println(
      "##FORMAT=<ID=SEQ-ERR,Number=.,Type=Float,Description=\"Probability to not be a sequence error with error rate " + cmdArgs.seqError + "\">")
    writer.println(
      "##FORMAT=<ID=MA-SEQ-ERR,Number=1,Type=Float,Description=\"Minimal probability for all alternative alleles to not be a sequence error with error rate " + cmdArgs.seqError + "\">")
    writer.println("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">")
    writer.println("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t" + cmdArgs.sample)
    val inputStream = if (cmdArgs.input != null) {
      Source.fromFile(cmdArgs.input).getLines()
    } else {
      logger.info("No input file as argument, waiting on stdin")
      Source.stdin.getLines()
    }
    class Counts(var forward: Int, var reverse: Int)
    for (line <- inputStream;
         values = line.split("\t") if values.size > 5) {
      val chr = values(0)
      val pos = values(1)
      val ref = values(2) match {
        case "A" | "T" | "G" | "C" => values(2)
        case "a" | "t" | "g" | "c" => values(2).toUpperCase
        case "U" | "u" => "T"
        case _ => "N"
      }
      val reads = values(3).toInt
      val mpileup = values(4)
      //val qual = values(5)

      val counts: mutable.Map[String, Counts] = mutable.Map(ref.toUpperCase -> new Counts(0, 0))

      def addCount(s: String) {
        val upper = s.toUpperCase
        if (!counts.contains(upper)) counts += upper -> new Counts(0, 0)
        if (s(0).isLower) counts(upper).reverse += 1
        else counts(upper).forward += 1
      }

      var t = 0
      var dels = 0
      while (t < mpileup.length) {
        mpileup(t) match {
          case ',' =>
            addCount(ref.toLowerCase)
            t += 1
          case '.' =>
            addCount(ref.toUpperCase)
            t += 1
          case '^' => t += 2
          case '$' => t += 1
          case '*' =>
            dels += 1
            t += 1
          case '+' | '-' =>
            t += 1
            var size = ""
            var insert = ""
            while (mpileup(t).isDigit) {
              size += mpileup(t)
              t += 1
            }
            for (c <- t until t + size.toInt) insert = insert + mpileup(c)
            t += size.toInt
          case 'a' | 'c' | 't' | 'g' | 'A' | 'C' | 'T' | 'G' =>
            addCount(mpileup(t).toString)
            t += 1
          case _ => t += 1
        }
      }

      val binomial = new Binomial(reads, cmdArgs.seqError, RandomEngine.makeDefault())
      val info: ArrayBuffer[String] = ArrayBuffer("DP=" + reads)
      val format: mutable.Map[String, Any] = mutable.Map("DP" -> reads.toString)
      val alt: ArrayBuffer[String] = new ArrayBuffer
      var maSeqErr: Option[Double] = None
      format += ("RFC" -> counts(ref.toUpperCase).forward.toString)
      format += ("RRC" -> counts(ref.toUpperCase).reverse.toString)
      format += ("AD" -> (counts(ref.toUpperCase).forward + counts(ref.toUpperCase).reverse).toString)
      format += ("SEQ-ERR" -> (1.0 - binomial.cdf(
        counts(ref.toUpperCase).forward + counts(ref.toUpperCase).reverse)).toString)
      if (reads >= cmdArgs.minDP)
        for ((key, value) <- counts if key != ref.toUpperCase
             if value.forward + value.reverse >= cmdArgs.minAP) {
          alt += key
          format += ("AD" -> (format("AD") + "," + (value.forward + value.reverse).toString))
          val seqErr = 1.0 - binomial.cdf(value.forward + value.reverse)
          maSeqErr match {
            case Some(x) if x < seqErr =>
            case _ => maSeqErr = Some(seqErr)
          }
          format += ("SEQ-ERR" -> (format("SEQ-ERR") + "," + seqErr.toString))
          format += ("AFC" -> ((if (format.contains("AFC")) format("AFC") + "," else "") + value.forward))
          format += ("ARC" -> ((if (format.contains("ARC")) format("ARC") + "," else "") + value.reverse))
          format += ("FREQ" -> ((if (format.contains("FREQ")) format("FREQ") + "," else "") +
            round((value.forward + value.reverse).toDouble / reads * 1E4).toDouble / 1E2))
        }

      maSeqErr match {
        case Some(x) => format += ("MA-SEQ-ERR" -> x)
        case _ =>
      }

      if (alt.nonEmpty || cmdArgs.refCalls) {
        val ad = for (ad <- format("AD").toString.split(",")) yield ad.toInt
        var left = reads - dels
        val gt = ArrayBuffer[Int]()

        for (p <- 0 to alt.size if gt.size < cmdArgs.ploidy) {
          var max = -1
          for (a <- ad.indices if ad(a) > (if (max >= 0) ad(max) else -1) && !gt.contains(a))
            max = a
          val f = ad(max).toDouble / left
          for (_ <- 0 to floor(f).toInt if gt.size < cmdArgs.ploidy) gt.append(max)
          if (f - floor(f) >= cmdArgs.homoFraction) {
            for (_ <- p to cmdArgs.ploidy if gt.size < cmdArgs.ploidy) gt.append(max)
          }
          left -= ad(max)
        }
        writer.println(
          Array(
            chr,
            pos,
            ".",
            ref.toUpperCase,
            if (alt.nonEmpty) alt.mkString(",") else "<REF>",
            ".",
            ".",
            info.mkString(";"),
            "GT:" + format.keys.mkString(":"),
            gt.sortWith(_ < _).mkString("/") + ":" + format.values.mkString(":")
          ).mkString("\t"))
      }
    }
    writer.close()
    
    logger.info("Done")
  }
}
