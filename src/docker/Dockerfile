#################################################################
#
# Microservice Container Template
# (C) MetaMagic Global Inc, NJ, USA, 2022
# Apache 2.0 License
#
# Container Name: ms-cache-service
#
# Microservices Containers - SpringBoot 3.2.x / Java 21
#
# Version 0.2
#################################################################
# Base Version
FROM eclipse-temurin:21-jre-alpine

LABEL author="Araf Karsh Hamid <araf.karsh@metamagic.in>"

# Create non-root user and required directories (previously provided by metamagic/java base image)
RUN addgroup -S poduser && adduser -S poduser -G poduser \
    && mkdir -p /Softwares/service/libs /home/poduser/libs \
    && chown -R poduser:poduser /Softwares /home/poduser

ARG SPRINGBOOT_JAR=ms-cache-service-spring-boot.jar
ARG MAVEN_JAR=ms-cache-service-thin.jar

# Copy Service (Install)
# COPY --chown=poduser:poduser StartService /Softwares/service/
COPY --chown=poduser:poduser application.properties /home/poduser/
COPY --chown=poduser:poduser application.properties /Softwares/service/
# SpringBoot Fat Jar
COPY --chown=poduser:poduser ${SPRINGBOOT_JAR} /Softwares/service/

# Maven Thin Jar
#COPY --chown=poduser:poduser ${MAVEN_JAR} /Softwares/service/
#COPY --chown=poduser:poduser /libs /Softwares/service/libs/
#COPY --chown=poduser:poduser /libs /home/poduser/libs/

# Change the Mode
RUN chmod -R 750 /Softwares/service/*

# Expose Microservice Port
EXPOSE 9090

USER poduser

# Start Microservice
#ENTRYPOINT ["java","-jar","/Softwares/service/ms-cache-service-thin.jar"]
ENTRYPOINT ["java","-jar","/Softwares/service/ms-cache-service-spring-boot.jar"]

