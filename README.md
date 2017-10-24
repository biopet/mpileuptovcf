TODO:
- Update this file
- Update docs/manual.md
- Update index.rst
- Update docs/installation.md
- Make sure all links are correct

#  MpileupToVcf
This tool is part of BIOPET tool suite that is developed at LUMC by [the SASC team](http://sasc.lumc.nl/). 
Each tool in the BIOPET tool suite is meant to offer a standalone function that can be used to perform a
dedicate data analysis task or added as part of [BIOPET pipelines](http://biopet-docs.readthedocs.io/en/latest/).

#  About this tool
This tool enables a user to extract a VCF file out a mpileup file generated from the BAM file using *samtools mpileup*,
for instance. The tool can also stream through STDin and STDout so that it is not necessary to store the mpileup file
on disk. Mpileup files can to be very large because they describe each covered base position in the genome on a per
read basis, so it is not desired to store them.

#  Documentation
For documentation and manuals visit the [readthedocs page](http://biopet-MpileupToVcf.readthedocs.io/en/latest/).


#  Contact

<p>
  <!-- Obscure e-mail address for spammers -->
For any question related to this tool, please use the github issue tracker or contact 
  <a href='http://sasc.lumc.nl/'>the SASC team</a> directly at: <a href='&#109;&#97;&#105;&#108;&#116;&#111;&#58;
 &#115;&#97;&#115;&#99;&#64;&#108;&#117;&#109;&#99;&#46;&#110;&#108;'>
  &#115;&#97;&#115;&#99;&#64;&#108;&#117;&#109;&#99;&#46;&#110;&#108;</a>.
</p>
