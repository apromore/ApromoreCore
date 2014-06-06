<?xml version="1.0" encoding="UTF-8"?>
<qml:QML xmlns:qml="http://www.processconfiguration.com/QML" author="Samia Mazhar" name="Airport international departure questionnaire" reference="">
  <Question id="q11" mapQF="#f26 #f27">
    <description>Does airline check in policy include inquiry on prohibited/restricted items?</description>
  </Question>
  <Fact id="f26">
    <description>Yes</description>
  </Fact>
  <Fact id="f27">
    <description>No</description>
    <guidelines></guidelines>
  </Fact>
  <Constraints>xor(f26,f27)</Constraints>
</qml:QML>
