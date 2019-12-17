FROM openjdk:8

RUN groupadd -r apromore && useradd --no-log-init -r -g apromore apromore

RUN apt-get -y update \
&& apt-get -y install vim \
&& apt-get -y clean 


Copy ./Supplements/liblpsolve55/* /usr/local/lib/
WORKDIR /usr/local/lib/
RUN ldconfig \
&& chmod 755 /usr/local/lib/liblpsolve55j.so
ENV LD_LIBRARY_PATH /usr/local/lib


WORKDIR /opt/apromore/
COPY ./Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/ /opt/apromore/virgo-tomcat-server-3.6.4.RELEASE/
COPY ./Apromore-Assembly/Event-Logs-Repository/ /opt/apromore/Event-Logs-Repository/
COPY ./Apromore-Assembly/Filestore-Repository/ /opt/apromore/Filestore-Repository/
COPY ./Apromore-Assembly/Lucene-Repository/ /opt/apromore/Lucene-Repository/

RUN chown -R apromore:apromore /opt/apromore/ \
&& chmod -R 777 /opt/apromore/

USER apromore
EXPOSE 9000

CMD [ "./virgo-tomcat-server-3.6.4.RELEASE/bin/startup.sh", "-clean" ]





