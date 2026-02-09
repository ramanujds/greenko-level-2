## Build images

### Asset Service
```bash

docker build -t asset-service .

```

### Telemetry Service
```bash

docker build -t telemetry-service .

```

### Run Asset Service

```bash

docker run -d -p 8081:8080 \
    --name asset-service \
    asset-service

```

### Run Asset Service

```bash

docker run -d -p 8082:8080 \
    --name telemetry-service \
    -e ASSET_SERVICE_URL=http://asset-service:8080 \
    telemetry-service

```

### Docker network commands

```
docker network create greenko

docker network connect greenko asset-service

docker network connect greenko telemetry-service

```