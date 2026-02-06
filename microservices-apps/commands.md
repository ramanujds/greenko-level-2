# Docker Commands

## Run Containers

### Run Spring Boot
```bash
docker run -d -p 8080:8080 --name=spring-boot ram1uj/spring-boot
```
Starts a container in detached mode from the `ram1uj/spring-boot` image, maps host port `8080` to container port `8080`, and names the container `spring-boot`\.

### Run Nginx
```bash
docker run -d --name=web nginx
```
Starts an Nginx container in detached mode and names it `web`\.

## Networking

### Create a user\-defined network
```bash
docker network create my-app-network
```
Creates a user\-defined Docker bridge network named `my-app-network` for container\-to\-container communication\.

### Connect Spring Boot to the network
```bash
docker network connect my-app-network spring-boot
```
Attaches the `spring-boot` container to `my-app-network`\.

### Connect Nginx to the network
```bash
docker network connect my-app-network web
```
Attaches the `web` container to `my-app-network`\.

## Test Connectivity

### Call Spring Boot from inside Nginx
```bash
docker exec -it web curl spring-boot:8080/message
```
Runs `curl` inside the `web` container to call the `spring-boot` container (resolved by container name on the same network) at `/message`\.

## Container Management

### List running containers
```bash
docker ps
```
Lists currently running containers\.

### List all containers
```bash
docker ps -a
```
Lists all containers (running and stopped)\.

### Stop a container
```bash
docker stop web
```
Stops the running `web` container\.

### Remove a container
```bash
docker rm web
```
Removes (deletes) the `web` container (must be stopped first)\.

## Image Management

### List local images
```bash
docker images
```
Lists locally available Docker images\.

### Remove an image
```bash
docker rmi nginx
```
Removes the local `nginx` image (fails if it is still used by any existing container)\.
```