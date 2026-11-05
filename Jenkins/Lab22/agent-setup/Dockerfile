FROM jenkins/ssh-agent:jdk21
USER root
ARG DOCKER_GID=0
RUN apt-get update && apt-get install -y docker.io maven curl && rm -rf /var/lib/apt/lists/*
RUN curl -fsSLO "https://dl.k8s.io/release/$(curl -fsSL https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
    && install -m 0755 kubectl /usr/local/bin/kubectl && rm kubectl
RUN groupadd -g ${DOCKER_GID} dockerhost 2>/dev/null || true; \
    usermod -aG $(getent group ${DOCKER_GID} | cut -d: -f1) jenkins
