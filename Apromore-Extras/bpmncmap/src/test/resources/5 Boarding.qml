<?xml version="1.0" encoding="UTF-8"?>
<qml:QML xmlns:qml="http://www.processconfiguration.com/QML" author="Samia Mazhar" name="Airport international departure questionnaire" reference="">
  <Question id="q1" mapQF="#f1 #f2">
    <description>What type of service does the airport offer?</description>
  </Question>
  <Question id="q2" mapQF="#f3 #f4">
    <description>Does security screening precede Customs checks?</description>
  </Question>
  <!--
  <Question id="q3" mapQF="#f5 #f6">
    <description>What scope of travel does the usage/configuration of the terminal building cater to?</description>
  </Question>
  <Question id="q4" mapQF="#f7 #f8 #f10">
    <description>What transport facilities are available to the airport?</description>
  </Question>
  <Question id="q5" mapQF="#f13 #f12">
    <description>Is there a bag-weighing facility present?</description>
  </Question>
  <Question id="q6" mapQF="#f14 #f15 #f16">
    <description>What check in types are provided by the airlines?</description>
  </Question>
  <Question id="q7" mapQF="#f17 #f18">
    <description>Can minor name change occur at check in?</description>
  </Question>
  <Question id="q8" mapQF="#f19 #f20">
    <description>Can TRS and restricted items be checks be arranged around check in?</description>
  </Question>
  <Question id="q9" mapQF="#f23 #f21 #f22">
    <description>With whom should passenger resolve excess baggage/payment?</description>
  </Question>
  <Question id="q10" mapQF="#f24 #f25">
    <description>Who is responsible for providing the boarding pass?</description>
  </Question>
  <Question id="q11" mapQF="#f26 #f27">
    <description>Does airline check in policy include inquiry on prohibited/restricted items?</description>
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
  <Question id="q15" mapQF="#f34 #f35">
    <description>What type of passenger is primarily being served?</description>
    <guidelines></guidelines>
  </Question>
  <Question id="q16" mapQF="#f36 #f37">
    <description>Is a private security pat down area present?</description>
  </Question>
  -->
  <Question id="q17" mapQF="#f38 #f39">
    <description>Are there airline lounges at the airport?</description>
  </Question>
  <Question id="q18" mapQF="#f40 #f41">
    <description>Is there a gate dedicated for a wheelchair lift?</description>
  </Question>
  <!--
  <Question id="q19" mapQF="#f43 #f44 #f45 #f46 #f47 #f48 #f49 #f42">
    <description>What selection of facilities/services are available on entering the terminal?</description>
  </Question>
  <Question id="q20" mapQF="#f50 #f51">
    <description>Can TRS validity be assessed in a Customs office before check-in?</description>
  </Question>
  <Question id="q21" mapQF="#f53 #f54 #f52">
    <description>What selection of facilities/services are available in the non-sterile area on receiving the boarding pass?</description>
    <guidelines></guidelines>
  </Question>
  <Question id="q22" mapQF="#f55 #f56">
    <description>Are facilities/services offered prior to final security LAGs check?</description>
  </Question>
  <Question id="q23" mapQF="#f58 #f59 #f60 #f61 #f62 #f63 #f57">
    <description>What selection of facilities/services are available prior to boarding?</description>
  </Question>
  <Question id="q24" mapQF="#f64 #f65">
    <description>What purchase-related checks are present prior to boarding?</description>
  </Question>
  -->
  <Fact id="f1">
    <description>Regular civilian</description>
  </Fact>
  <Fact id="f2">
    <description>Chartered military</description>
  </Fact>
  <Fact id="f3">
    <description>Yes</description>
  </Fact>
  <Fact id="f4">
    <description>No</description>
  </Fact>
  <!--
  <Fact id="f5">
    <description>International</description>
    <mandatory>true</mandatory>
  </Fact>
  <Fact id="f6">
    <description>Domestic</description>
  </Fact>
  <Fact id="f7">
    <description>Bus</description>
  </Fact>
  <Fact id="f8">
    <description>Train</description>
  </Fact>
  <Fact id="f9">
    <description>Taxi/car with driver</description>
  </Fact>
  <Fact id="f10">
    <description>Self-driven car</description>
  </Fact>
  <Fact id="f11">
    <description>Chartered truck/bus</description>
  </Fact>
  <Fact id="f12">
    <description>Yes</description>
  </Fact>
  <Fact id="f13">
    <description>No</description>
  </Fact>
  <Fact id="f14">
    <description>Regular check in  (traditional)</description>
  </Fact>
  <Fact id="f15">
    <description>Premium check in  (traditional)</description>
  </Fact>
  <Fact id="f16">
    <description>Bag drop only for internet check ins (traditional)</description>
  </Fact>
  <Fact id="f17">
    <description>Yes</description>
  </Fact>
  <Fact id="f18">
    <description>No</description>
  </Fact>
  <Fact id="f19">
    <description>Yes</description>
  </Fact>
  <Fact id="f20">
    <description>No</description>
  </Fact>
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
  <Fact id="f26">
    <description>Yes</description>
  </Fact>
  <Fact id="f27">
    <description>No</description>
    <guidelines></guidelines>
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
  <Fact id="f34">
    <description>Regular civilian</description>
  </Fact>
  <Fact id="f35">
    <description>Chartered military</description>
  </Fact>
  <Fact id="f36">
    <description>Yes</description>
  </Fact>
  <Fact id="f37">
    <description>No</description>
    <guidelines></guidelines>
  </Fact>
  -->
  <Fact id="f38">
    <description>Yes</description>
  </Fact>
  <Fact id="f39">
    <description>No</description>
  </Fact>
  <Fact id="f40">
    <description>Yes</description>
  </Fact>
  <Fact id="f41">
    <description>No</description>
  </Fact>
  <!--
  <Fact id="f43">
    <description>Currency exchange</description>
  </Fact>
  <Fact id="f44">
    <description>ATM</description>
  </Fact>
  <Fact id="f45">
    <description>Travel insurance</description>
  </Fact>
  <Fact id="f46">
    <description>Wireless internet</description>
  </Fact>
  <Fact id="f47">
    <description>Internet kiosk</description>
    <guidelines></guidelines>
  </Fact>
  <Fact id="f48">
    <description>Unaccompanied baggage</description>
  </Fact>
  <Fact id="f49">
    <description>Bag wrapping</description>
  </Fact>
  <Fact id="f42">
    <description>Airline service desk</description>
  </Fact>
  <Fact id="f50">
    <description>Yes</description>
    <guidelines></guidelines>
  </Fact>
  <Fact id="f51">
    <description>No</description>
  </Fact>
  <Fact id="f52">
    <description>Wireless internet</description>
  </Fact>
  <Fact id="f53">
    <description>Internet kiosk</description>
  </Fact>
  <Fact id="f54">
    <description>Unaccompanied baggage</description>
  </Fact>
  <Fact id="f55">
    <description>Yes</description>
  </Fact>
  <Fact id="f56">
    <description>No</description>
  </Fact>
  <Fact id="f57">
    <description>Public phone</description>
  </Fact>
  <Fact id="f58">
    <description>Currency exchange</description>
  </Fact>
  <Fact id="f59">
    <description>ATM</description>
  </Fact>
  <Fact id="f60">
    <description>Travel insurance</description>
    <guidelines></guidelines>
  </Fact>
  <Fact id="f61">
    <description>Wireless internet</description>
  </Fact>
  <Fact id="f62">
    <description>Internet kiosk</description>
  </Fact>
  <Fact id="f63">
    <description>Prayer room</description>
  </Fact>
  <Fact id="f64">
    <description>Tourist Refund Scheme</description>
  </Fact>
  <Fact id="f65">
    <description>Docket collector</description>
  </Fact>
  -->
  <Constraints>xor(f1,f2).xor(f3,f4).xor(f38,f39).xor(f40,f41)</Constraints>
</qml:QML>
