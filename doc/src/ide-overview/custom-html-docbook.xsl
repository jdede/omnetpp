<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="1.0">

<!--
  For a complete guide of DocBook customizations see
    http://www.sagehill.net/docbookxsl
 -->


<!-- The local path to Norm Walsh's DocBook FO stylesheets -->
<xsl:import href="@DOCBOOK_STYLESHEET@"/>

<!-- The local path to your common customization stylesheet
<xsl:import href="file:///not set"/>
-->

<!-- ***************  Additional  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Admonitions  *********************  -->
<!-- ***************************************************  -->

<!-- If true (non-zero), admonitions are presented in an alternate style that uses a graphic. Default graphics are provided in the distribution. -->
<xsl:param name="admon.graphics" select="1"/>

<!-- How do you want admonitions styled? Set the font-size, weight, etc. to the style required. -->
<xsl:attribute-set name="admonition.properties">
 <xsl:attribute name="font-size">10pt</xsl:attribute>
</xsl:attribute-set>

<!-- ***************  Automatic labelling  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Bibliography  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Callouts  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Cross References  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  EBNF  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Font Families  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Glossary  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Graphics  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Linking  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Lists  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Localization  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Meta/*Info  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Miscellaneous  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Pagination and General Styles  *********************  -->
<!-- ***************************************************  -->

<!--  Specifies the default point size for body text. The body font size is specified in two parameters (body.font.master and body.font.size) so that math can be performed on the font size by XSLT. -->
<xsl:param name="body.font.master">10</xsl:param>
<xsl:param name="body.font.family">Bookman</xsl:param>
<xsl:param name="title.font.family">Bookman</xsl:param>


<!-- Select the paper type. The paper type is a convenient way to specify the paper size. The list of known paper sizes includes USletter and most of the A, B, and C sizes. See page.width.portrait, for example.  -->
<xsl:param name="paper.type">A4</xsl:param>

<!-- ***************  Prepress  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Processor Extensions  *********************  -->
<!-- ***************************************************  -->

<!-- If non-zero, extensions for FOP version 1 and later will be used. This parameter can also affect which graphics file formats are supported. The original fop.extensions should still be used for FOP version 0.20.5 and earlier. -->
<xsl:param name="fop1.extensions" select="1"/>


<!-- ***************  Profiling  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Property Sets  *********************  -->
<!-- ***************************************************  -->

<!--  Style the title element of formal object such as a figure. Specify how the title should be styled. Specify the font size and weight of the title of the formal object. -->
<xsl:attribute-set name="formal.title.properties" use-attribute-sets="normal.para.spacing">
  <xsl:attribute name="font-weight">bold</xsl:attribute>
  <xsl:attribute name="font-size">
    <xsl:value-of select="$body.font.master">
    </xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
  <xsl:attribute name="space-after.minimum">0.4em</xsl:attribute>
  <xsl:attribute name="space-after.optimum">0.6em</xsl:attribute>
  <xsl:attribute name="space-after.maximum">0.8em</xsl:attribute>
  <xsl:attribute name="text-align">center</xsl:attribute>
</xsl:attribute-set>


<!--  The properties of level-1 section titles. -->
<xsl:attribute-set name="section.title.level1.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.5 "></xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
</xsl:attribute-set>

<!--  The properties of level-2 section titles. -->
<xsl:attribute-set name="section.title.level2.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.2"></xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
</xsl:attribute-set>

<!--  The properties of level-3 section titles. -->
<xsl:attribute-set name="section.title.level3.properties">
  <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master"></xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
</xsl:attribute-set>

<!-- ***************  QAndASet  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Header/Footer (Andras) ********************  -->
<!-- ***************************************************  -->
<xsl:param name="header.column.widths">1 4 1</xsl:param>  <!-- avoid line breaks in the header -->

<!-- ***************  Reference Pages  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Stylesheet Extensions  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Tables  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  ToC/LoT/Index Generation  *********************  -->
<!-- ***************************************************  -->
<xsl:param name="generate.toc">
article title
</xsl:param>

<!-- ***************  XSLT Processing  *********************  -->
<!-- ***************************************************  -->

<!-- ***************  Figure, table, and other titles  *********************  -->
<!-- ***************************************************  -->
<xsl:param name="formal.title.placement">
figure before
example before
equation before
table before
procedure before
</xsl:param>

<xsl:param name="local.l10n.xml" select="document('')"/>
<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
  <l:l10n language="en">
    <l:context name="title">
      <l:template name="figure" text="%t"/>
    </l:context>
  </l:l10n>
</l:i18n>

<xsl:template match="table" mode="label.markup"/>
</xsl:stylesheet>
