# E-commerce Microservice Project

## Description

This project is a microservice project that simulates an e-commerce system created for study purpose. It is composed of the following services:
- **Product Service**: responsible for managing products.
- **Order Service**: responsible for managing orders.
- **Customer Service**: responsible for managing customers.
- **Payment Service**: responsible for managing payments.
- **Notification Service**: responsible for managing notifications.
- **Gateway Service**: responsible for routing requests to the services and managing security.
- **Discovery Service**: responsible for service discovery.
- **Config Service**: responsible for managing configurations.

## Execution

### **Docker Compose**

The project dependencies can be executed using docker-compose. To do this, run the following command in the root directory of the project:

```shell
$ docker-compose up -d
```
### **Keycloak**

The project uses Keycloak for authentication and authorization. To access the Keycloak admin console, access the following URL: http://localhost:9098/admin/master/console. The default credentials are:
- **Username**: admin
- **Password**: admin

#### **Create a realm**

To create a realm, follow the steps below:
1. Access the Keycloak admin console.
2. Click on the "Add realm" button.
3. Enter the realm name "ecommerce" and click on the "Create" button.
4. Click on the "Create" button.

#### **Create a client**

To create a client, follow the steps below:
1. Access the Keycloak admin console.
2. Click on the "Clients" menu.
3. Click on the "Create Client" button.
4. Enter the client ID "ecommerce-api" and click on the "Next" button.
5. Enable "Client authentication" and "Authorization" and click on the "Next" button.
6. Click on the "Save" button.

### **Run Services**

The services must be started in the following order:
- **Config Service**
- **Discovery Service**
- **Gateway Service**
- **Product Service**
- **Order Service**
- **Customer Service**
- **Payment Service**
- **Notification Service**

## Possible problems

### 1 - Error on access to the keycloak admin console on localhost (https required)
```shell
$ docker ps | grep keycloak
$ docker exec -it {containerID} bash
container$ cd /opt/jboss/keycloak/bin
container$ ./kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin
container$ ./kcadm.sh update realms/master -s sslRequired=NONE
```
