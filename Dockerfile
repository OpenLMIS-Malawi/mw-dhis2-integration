FROM openlmis/service-base:4

COPY build/libs/*.jar /service.jar
COPY build/consul /consul

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

COPY kuunika_certs/kuunika-all.crt /usr/local/share/ca-certificates/kuunika-all.crt
COPY kuunika_certs/kuunika-org.crt /usr/local/share/ca-certificates/kuunika-org.crt
COPY kuunika_certs/kuunika-org-second.crt /usr/local/share/ca-certificates/kuunika-org-second.crt


RUN echo yes | keytool -importcert -alias kuunika-all.crt -keystore \
    /opt/jdk/jdk1.8.0_92/jre/lib/security/cacerts -storepass openlmis -file kuunika_certs/kuunika-all.crt
RUN echo yes | keytool -importcert -alias kuunika-org.crt -keystore \
    /opt/jdk/jdk1.8.0_92/jre/lib/security/cacerts -storepass openlmis -file kuunika_certs/kuunika-org.crt
RUN echo yes | keytool -importcert -alias kuunika-org-second.crt -keystore \
    /opt/jdk/jdk1.8.0_92/jre/lib/security/cacerts -storepass openlmis -file kuunika_certs/kuunika-org-second.crt

RUN update-ca-certificates