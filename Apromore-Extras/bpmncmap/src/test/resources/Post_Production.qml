<?xml version="1.0" encoding="UTF-8"?>
<qml:QML xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:qml="http://www.processconfiguration.com/QML" xsi:schemaLocation="http://www.processconfiguration.com/QML qml.xsd" author="M. La Rosa, J. Lux" name="Picture post-production" reference="www.processconfiguration.com">
  <Question id="q1" mapQF="#f1 #f2 #f3">
    <description>What is the allocated budget for the project?</description>
    <guidelines>The budget of the project has influence on the post-production process. In general, the quality of the outcome is related to the available budget. However, the budget for the post-production is often underestimated and therefore should be planned well. Low-Budget productions generally shouldn't shoot on film due to the accompanied necessary equipment and additional logistic jobs. If there are problems of time, it should be considered that shortening the duration of the post-production normally requires a higher budget.</guidelines>
  </Question>
  <Question id="q2" mapQF="#f4 #f5 #f6 #f7 #f8">
    <description>What are the primary distribution channels?</description>
  </Question>
  <Question id="q3" partiallyDepends="#q1 #q2" mapQF="#f9 #f10">
    <description>Which shooting media have been used?</description>
    <guidelines>The type of shooting is highly relevant for the post-production process. While film is often considered to be still the medium with the highest quality, it requires some special treatments that make the work with film unhandy and expensive.</guidelines>
  </Question>
  <Question id="q4" partiallyDepends="#q1 #q2" mapQF="#f11 #f12">
    <description>How is the editing to be performed?</description>
    <guidelines>A decision regarding the high-resolution edit should consider the availability of the respective facilities and the given time frame.</guidelines>
  </Question>
  <Question id="q5" partiallyDepends="#q1 #q2" mapQF="#f13 #f14 #f15">
    <description>Which are the expected deliverables?</description>
    <guidelines>The expected deliverables affect the post-edit. Decisions should consider the booking of post-processing facilities needed for the respective deliverable.</guidelines>
  </Question>
  <Question id="q6" fullyDepends="#q3" mapQF="#f16 #f17 #f18">
    <description>What Tape format has been shot?</description>
    <guidelines>The type of tape has impact on the required facilities and therefore on the budget. Standard Definition (SD) digital tape is the default option.</guidelines>
  </Question>
  <Question id="q7" fullyDepends="#q3" mapQF="#f19 #f20 #f21">
    <description>What Film format has been shot?</description>
    <guidelines>Film has international standards, however there is a distinction between the three basic formats.</guidelines>
  </Question>
  <Fact id="f1">
    <description>Low (≤ 250,000 US)</description>
    <mandatory>true</mandatory>
    <guidelines>Low-budget productions generally should not shoot on film or on High-Definition tape.</guidelines>
  </Fact>
  <Fact id="f2">
    <description>Medium (> 250,000 US, ≤ 1.5mil US)</description>
    <default>true</default>
    <mandatory>true</mandatory>
    <guidelines>Medium-budget productions generally should not shoot on film due to the accompanied necessary equipment and additional logistic jobs.</guidelines>
  </Fact>
  <Fact id="f3">
    <description>High (> 1.5mil US)</description>
    <mandatory>true</mandatory>
    <guidelines>A high budget does not restrict subsequent choices.</guidelines>
  </Fact>
  <Fact id="f4">
    <description>Cinema</description>
    <default>true</default>
    <guidelines>Cinema distribution is not suitable for Low-budget projects, as it requires to be finished on Film.</guidelines>
  </Fact>
  <Fact id="f5">
    <description>TV</description>
    <guidelines>TV distribution requires to be finished on Tape.</guidelines>
  </Fact>
  <Fact id="f6">
    <description>Home</description>
    <default>true</default>
    <guidelines>Home distribution requires to be finished on Tape and/or New Media.</guidelines>
  </Fact>
  <Fact id="f7">
    <description>Mobile</description>
    <guidelines>Mobile distribution requires to be finished on New Media.</guidelines>
  </Fact>
  <Fact id="f8">
    <description>Internet</description>
    <guidelines>Internet distribution requires to be finished on New Media.</guidelines>
  </Fact>
  <Fact id="f9">
    <description>Tape shooting</description>
    <default>true</default>
    <mandatory>true</mandatory>
    <guidelines>A tape shoot allows a quicker look on the footage, but still lacks quality compared to an exposed film.</guidelines>
  </Fact>
  <Fact id="f10">
    <description>Film shooting</description>
    <mandatory>true</mandatory>
    <guidelines>Film footage must be processed by a film laboratory before it is brought to the post-production facility.</guidelines>
  </Fact>
  <Fact id="f11">
    <description>Online cut</description>
    <default>true</default>
    <guidelines>An online editing suite can vary strongly in terms of processing power and the editing options. The cut in the online suite therefore can be a plus or a minus to the budget, depending on what type of system is used.</guidelines>
  </Fact>
  <Fact id="f12">
    <description>Film-based cut</description>
    <guidelines>A film-based cut generally takes longer due to the handcraft-style of the work.</guidelines>
  </Fact>
  <Fact id="f13">
    <description>Tape finish</description>
    <default>true</default>
    <mandatory>true</mandatory>
    <guidelines>Tape is the common broadcast format for TV.</guidelines>
  </Fact>
  <Fact id="f14">
    <description>Film finish</description>
    <default>true</default>
    <mandatory>true</mandatory>
    <guidelines>The traditional finish on a film roll for cinema projection. Expensive.</guidelines>
  </Fact>
  <Fact id="f15">
    <description>New Media finish</description>
    <default>false</default>
    <mandatory>true</mandatory>
    <guidelines>New Media includes finishing on Disc and on file, e.g. for Internet and Mobile distribution. In general not expensive.</guidelines>
  </Fact>
  <Fact id="f16">
    <description>Analogue tape</description>
    <guidelines>The traditional tape format. Less expensive in procurement but needs to be digitized during the preparations for the edit.</guidelines>
  </Fact>
  <Fact id="f17">
    <description>SD Digital tape</description>
    <default>true</default>
    <guidelines>Digital tape. It needs new (digital processing) equipment for shoot and post-production.</guidelines>
  </Fact>
  <Fact id="f18">
    <description>HD Digital tape</description>
    <guidelines>High-definition digital tape. It needs new and expensive equipment for shoot and especially the post-production to work on the high quality in the edit.</guidelines>
  </Fact>
  <Fact id="f19">
    <description>16mm film</description>
    <guidelines>Most frequently used film globally overall. In general cheaper.</guidelines>
  </Fact>
  <Fact id="f20">
    <description>35mm film</description>
    <guidelines>Favorite film format for US high-budget film productions.</guidelines>
  </Fact>
  <Fact id="f21">
    <description>65mm film</description>
    <guidelines>The format for IMAX-movie productions. Proprietary standard that needs special IMAX-equipment for shoot and projection and also needs 65mm film throughout the whole post-production.</guidelines>
  </Fact>
  <Constraints>xor(f1, f2, f3).(f1 => -(f10 + f14)).(f2 => -f10).(f4 + f5 + f6 + f7 + f8).(f4 => f14).(f5 => f13).(f6 => (f13 + f15)).((f7 + f8) => f15).(f9 + f10).(f11 + f12).(-f10 => -f12).(f13 + f14 + f15).(f12 => f14).(xor(f16, f17, f18) = f9).(nor(f16, f17, f18) = -f9).(xor(f19, f20, f21) = f10).(nor(f19, f20, f21) = -f10)</Constraints>
</qml:QML>