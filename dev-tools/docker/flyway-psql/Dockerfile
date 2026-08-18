FROM --platform=linux/arm64 flyway/flyway:latest

USER root

# Asenna postgresql-client paketti
RUN apt-get update && \
    apt-get install -y postgresql-client && \
    apt-get clean

RUN useradd -ms /bin/bash flyway

USER flyway