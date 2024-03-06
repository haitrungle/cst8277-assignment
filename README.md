# Spring Microservices on Kubernetes

That title has a buzzwords ratio of 75%, pretty good eh.

You can load this project inside a dev container or open it on Github Codespaces. If not, make sure you have the following:
- Java, Maven
- Docker
- k3d, kubectl

`k3d` runs `k3s` under the hood, a Kubernetes distribution by Rancher useful for educational purposes and local testing. The most popular lightweight k8s currently seems to be `minikube`, but I couldn't make it work on GitHub Codespaces, which is why I switched to `k3d`.

## Deploy locally

1. Create a Kubernetes cluster (see [Exposing Services - k3d](https://k3d.io/v5.6.0/usage/exposing_services/))
```bash
k3d cluster create --api-port 6550 -p 8081:80@loadbalancer
```

- `--api-port 6550`: the API server of the control plane is on port 6550
- `-p 8081:80@loadbalancer`: map port 8081 of the host to port 80 in the cluster

2. Deploy mysql, the two services, and the ingress controller
```bash
kubectl apply -k .
```

If you updated the code, you will need to rebuild the Docker images, push them, and modify the corresponding YAML files in "k8s/".

## Run it without k8s

1. Create a MySQL instance
```bash
docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 -d mysql:8.2
```

Wait for the container to be ready before starting the Spring services, or else they will crash.

2. In two separate terminal windows, run
```bash
cd twitter-clone && ./mvnw spring-boot:run
```

and

```bash
cd user-management && ./mvnw spring-boot:run
```
