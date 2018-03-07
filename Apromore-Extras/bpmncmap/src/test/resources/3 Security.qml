<?xml version="1.0" encoding="UTF-8"?>
<qml:QML xmlns:qml="http://www.processconfiguration.com/QML" author="Samia Mazhar" name="Airport international departure questionnaire" reference="">
  <Question id="q3" mapQF="#f5 #f6">
    <description>What scope of travel does the usage/configuration of the terminal building cater to?</description>
  </Question>
  <Question id="q12" mapQF="#f28 #f29">
    <description>Does airport policy require passengers to queue for preparation activities?</description>
  </Question>
  <Question id="q13" mapQF="#f30 #f31">
    <description>Are there enough business travellers to justify an express security queue?</description>
  </Question>
  <Question id="q14" mapQF="#f32 #f33">
    <description>Does security policy require pat-down or Explosive Trace Detection at metal objects screening point?</description>
  </Question>
  <Fact id="f5">
    <description>International</description>
    <mandatory>true</mandatory>
  </Fact>
  <Fact id="f6">
    <description>Domestic</description>
  </Fact>
  <Fact id="f28">
    <description>Yes</description>
  </Fact>
  <Fact id="f29">
    <description>No</description>
  </Fact>
  <Fact id="f30">
    <description>Yes</description>
  </Fact>
  <Fact id="f31">
    <description>No</description>
  </Fact>
  <Fact id="f32">
    <description>Pat-down check</description>
  </Fact>
  <Fact id="f33">
    <description>Explosive Trace Detection check</description>
  </Fact>
  <Constraints>(f5+f6).xor(f28,f29).xor(f30,f31).(f32+f33)</Constraints>
</qml:QML>
