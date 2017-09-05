## Architecture of Command Micro-Service
* SecKillCommandRestController<br />
Accept customer coupon grab request
* SecKillPromotionBootstrap<br />
Load unpublish promotion and activate it when PublishTime reached
* Active Promotion Components<br />
1.SecKillCommandService : try lock coupon<br />
2.Mem Block Queue : cache and control coupon limit<br />
3.SecKillEventPersistentRunner : persist event and publish event message<br />
* SecKillMessagePublisher<br />
send event message to message broker

![Alt text](https://github.com/ServiceComb/seckill/blob/master/etc/CommandServiceImpl.png)

