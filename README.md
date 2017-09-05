# ServiceComb Demo - SecKill [![Build Status](https://travis-ci.org/ServiceComb/seckill.svg?branch=master)](https://travis-ci.org/ServiceComb/seckill)[![Coverage Status](https://coveralls.io/repos/github/ServiceComb/seckill/badge.svg)](https://coveralls.io/github/ServiceComb/seckill)[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Purpose
In order for users to better understand how to develop micro-services using ServiceComb, and learning event sourcing.

## Architecture of SecKill
* Admin Micro-Service (Promotion Management)
* Command Micro-Service (Active Promotion and Accept Customer Grab Coupon then publish event to Message broker)<br />
More detail see : [Command Micro-Service Architecture][cmsa]
* Event Micro-Service (Consume Event Message from Message broker)
* Query Micro-Service (Customer can query current active promotion and acquired coupons)

![Alt text](https://github.com/ServiceComb/seckill/blob/master/etc/EventSourcing.png)

[cmsa]: https://github.com/ServiceComb/seckill/tree/master/seckill-command-service/README.md

## Prerequisites
You will need:
1. [Oracle JDK 1.8+][jdk]
2. [Maven 3.x][maven]
3. [MySQL CE 5.x][mysql]
4. [Docker][docker]
5. [Docker machine(optional)][docker_machine]
6. [curl][curl] or [Postman][postman]

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[maven]: https://maven.apache.org/install.html
[mysql]: https://www.mysql.com/downloads/
[docker]: https://www.docker.com/get-docker
[docker_compose]: https://docs.docker.com/compose/install/
[docker_machine]: https://docs.docker.com/machine/install-machine/
[curl]: https://curl.haxx.se
[postman]: https://www.getpostman.com/

## Run Services
You can run services follow this steps:

First Build all service images using command `mvn package -Pdocker`

If run jar mode locally you can change application.properties and then `java -jar target/seckill/seckill-xxx-service-xxx-exec.jar`

Also you can run all service images using command `docker-compose up`

If you are using [Docker Toolbox](https://www.docker.com/products/docker-toolbox), please add an extra profile `-Pdocker-machine`.

```mvn package -Pdocker -Pdocker-machine```

## Run Integration Tests

```
mvn verify -Pdocker -Pdocker-machine
```

## Verify services
You can verify the services using Postman by the following steps:
1. Create promotion
![Alt text](https://github.com/ServiceComb/seckill/blob/master/etc/CreatePromotion.png)
2. Query active promotions
![Alt text](https://github.com/ServiceComb/seckill/blob/master/etc/QueryActivePromotions.png)
3. Request grab coupon
![Alt text](https://github.com/ServiceComb/seckill/blob/master/etc/RequestGrabCoupon.png)
4. Query acquired coupons
![Alt text](https://github.com/ServiceComb/seckill/blob/master/etc/QueryAcquiredCoupons.png)