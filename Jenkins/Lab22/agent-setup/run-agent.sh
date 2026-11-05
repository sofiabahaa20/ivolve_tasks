#!/bin/bash
# Run on the RHEL VM that hosts Jenkins (the private key is NOT stored in this repo)

# 1) SSH key pair for the agent (PEM format so Jenkins can read the private key)
ssh-keygen -t rsa -b 2048 -f ~/.ssh/jenkins_agent_key -N ""
ssh-keygen -p -m PEM -f ~/.ssh/jenkins_agent_key -P "" -N ""

# 2) Build the agent image (Java, Maven, Docker CLI, kubectl)
docker build --build-arg DOCKER_GID=$(stat -c %g /var/run/docker.sock) -t ivolve-agent .

# 3) Run the agent container (SSH server on host port 2222)
docker run -d --name jenkins-agent --restart unless-stopped \
  --security-opt label=disable \
  -p 2222:22 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -e JENKINS_AGENT_SSH_PUBKEY="$(cat ~/.ssh/jenkins_agent_key.pub)" \
  ivolve-agent

# 4) Check the tools inside the agent
docker exec -u jenkins jenkins-agent bash -c "java -version 2>&1 | head -1; mvn -v | head -1; docker ps | head -1; kubectl version --client | head -1"

# 5) Internal IP of the agent container (used as the Host of the Jenkins node, port 22)
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' jenkins-agent
