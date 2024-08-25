FROM openjdk:8-jre-alpine3.9
MAINTAINER Salvio Furbino Elias <salviof@gmail.com>
COPY ws-1.0-SNAPSHOT.one-jar.jar /home/git/publicados/agenciaForms/agenciaFormsws.jar
COPY ./app/ /home/git/publicados/agenciaForms/paginas/
workdir WORKDIR /home/git/publicados/agenciaForms/
USER root
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#RUN fc-cache -f -v
EXPOSE 8666
CMD ["java", "-jar", "/home/git/publicados/agenciaForms/agenciaFormsws.jar"]