# MpileupToVcf


This tool enables a user to extract a VCF file out a mpileup file generated from the BAM file using *samtools mpileup*,
for instance. The tool can also stream through STDin so that it is not necessary to store the mpileup file
on disk. Mpileup files can to be very large because they describe each covered base position in the genome on a per
read basis, so it is not desired to store them.
    

# Documentation

For documentation and manuals visit our [github.io page](https://biopet.github.io/mpileuptovcf).

# About


MpileupToVcf is part of BIOPET tool suite that is developed at LUMC by [the SASC team](http://sasc.lumc.nl/).
Each tool in the [BIOPET tool suite](https://github.com/biopet/) is meant to offer a standalone function that can be used to perform a
dedicate data analysis task or added as part of a pipeline, for example the SASC team's [biowdl pipelines](https://github.com/biowdl).

All tools in the BIOPET tool suite are [Free/Libre](https://www.gnu.org/philosophy/free-sw.html) and
[Open Source](https://opensource.org/osd) Software.
    

# Contact


<p>
  <!-- Obscure e-mail address for spammers -->
For any question related to MpileupToVcf, please use the
<a href='https://github.com/biopet/mpileuptovcf/issues'>github issue tracker</a>
or contact
 <a href='http://sasc.lumc.nl/'>the SASC team</a> directly at: <a href='&#109;&#97;&#105;&#108;&#116;&#111;&#58;&#115;&#97;&#115;&#99;&#64;&#108;&#117;&#109;&#99;&#46;&#110;&#108;'>
&#115;&#97;&#115;&#99;&#64;&#108;&#117;&#109;&#99;&#46;&#110;&#108;</a>.
</p>

     

