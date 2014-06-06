<?xml version="1.0" encoding="UTF-8"?>
<qml:QML xmlns:qml="http://www.processconfiguration.com/QML" author="Samia Mazhar" name="Airport international departure questionnaire" reference="">
  <Question id="q9" mapQF="#f21 #f22 #f23">
    <description>With whom should passenger resolve excess baggage/payment?</description>
  </Question>
  <Question id="q10" mapQF="#f24 #f25">
    <description>Who is responsible for providing the boarding pass?</description>
  </Question>
  <Fact id="f21">
    <description>Airline premium check in counter</description>
  </Fact>
  <Fact id="f22">
    <description>Airport service desk</description>
  </Fact>
  <Fact id="f23">
    <description>Qantas sales desk</description>
  </Fact>
  <Fact id="f24">
    <description>Airport</description>
    <mandatory>true</mandatory>
    <impact></impact>
  </Fact>
  <Fact id="f25">
    <description>Airline</description>
    <guidelines></guidelines>
  </Fact>
  <Constraints>(f21+f22+f23).(f24+f25)</Constraints>
</qml:QML>
