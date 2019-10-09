
FROM openjdk:8 as intial

RUN groupadd -r apromore && useradd --no-log-init -r -g apromore apromore

RUN apt-get -y update \
&& apt-get -y install vim \
&& apt-get -y install git \
&& apt-get -y install python3 \
&& apt-get -y install python3-pip \
&& apt-get -y install xvfb \
&& apt-get -y install python3-tk \
&& apt-get -y install maven \
&& apt-get -y install ant \
&& apt-get -y clean 


WORKDIR /opt/
RUN mkdir /opt/apromore
COPY . /opt/apromore/

RUN chown -R apromore:apromore /opt/apromore \
&& chmod -R 777 /opt/apromore/Apromore-Assembly/

WORKDIR /opt/apromore
RUN mvn clean install \
&& ant create-h2 \
&& ant set-permissions\ 
&& ant copy-virgo-nix

USER apromore
EXPOSE 9000
#CMD [ "./Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/bin/startup.sh", "-clean" ]
CMD [ "ant start-virgo-nix"]


