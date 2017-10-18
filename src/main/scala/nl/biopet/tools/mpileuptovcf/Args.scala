package nl.biopet.tools.mpileuptovcf

import java.io.File

case class Args(input: File = null,
                output: File = null,
                sample: String = null,
                minDP: Int = 8,
                minAP: Int = 2,
                homoFraction: Double = 0.8,
                ploidy: Int = 2,
                seqError: Double = 0.005,
                refCalls: Boolean = false)
