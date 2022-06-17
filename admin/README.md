# Augeil Admin Portal
*Administration portal for Augeil*

## Environment Configuration

Define and set the following environment variables:
```bash
# Basic authentication
SPRING_SECURITY_USERNAME=...
SPRING_SECURITY_PASSWORD=...

# Azure blob storage
AZURE_BLOB_STORAGE_ACCOUNT_NAME=...
AZURE_BLOB_STORAGE_ACCOUNT_KEY=...
AZURE_BLOB_STORAGE_CONTAINER_NAME=...

# Spring active profile (local or prod)
spring.profiles.active=...
```

## Build and run with docker
```
$ docker build -t augeil .
$ docker run -p 8080:8080 --env-file env augeil
```

## Deploy to Azure
```
$ az login

# Build docker image and upload to Azure Container Registry:
$ az acr build --file Dockerfile --registry augeilcontainerregistry --image augeil .


```