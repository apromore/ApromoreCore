FROM openjdk:8 as dev

RUN groupadd -r apromore && useradd --no-log-init -r -g apromore apromore

RUN apt-get -y update \
&& apt-get -y install maven \
&& apt-get -y install ant \
&& apt-get -y clean 


WORKDIR /opt/apromore/
COPY ./ ./ 

RUN chown -R apromore:apromore /opt/apromore/ \
&& chmod -R 777 /opt/apromore/

RUN mvn clean install
# RUN ant create-h2

CMD [ "ant", "start-virgo-nix"]


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
COPY --from=dev /opt/apromore/Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/ /opt/apromore/virgo-tomcat-server-3.6.4.RELEASE/
COPY --from=dev /opt/apromore/Apromore-Assembly/Event-Logs-Repository/ /opt/apromore/Event-Logs-Repository/
COPY --from=dev /opt/apromore/Apromore-Assembly/Filestore-Repository/ /opt/apromore/Filestore-Repository/
COPY --from=dev /opt/apromore/Apromore-Assembly/Lucene-Repository/ /opt/apromore/Lucene-Repository/

RUN chown -R apromore:apromore /opt/apromore/ \
&& chmod -R 777 /opt/apromore/

USER apromore
EXPOSE 9000

CMD [ "./virgo-tomcat-server-3.6.4.RELEASE/bin/startup.sh", "-clean" ]





