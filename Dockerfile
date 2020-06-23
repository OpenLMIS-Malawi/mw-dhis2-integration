FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/consul /consul

RUN update-ca-certificates

COPY certs/cacerts /opt/jdk1.8.0_92/jre/lib/security/cacerts