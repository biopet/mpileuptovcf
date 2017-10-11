package nl.biopet.tools.mpileuptovcf

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class MpileupToVcfTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      MpileupToVcf.main(Array())
    }
  }
}
