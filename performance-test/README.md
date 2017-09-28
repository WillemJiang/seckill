# SecKill Performance Test

Performance test is automated with [JMeter](http://jmeter.apache.org/download_jmeter.cgi). A great tutorial can be found
at [Guru99](https://www.guru99.com/jmeter-performance-testing.html) if you are not familiar with JMeter.

## How to Run Performance Test
To run performance test, execute the following JMeter command
```
jmeter -n -t seckill.jmx -l log.jtl
```

To generate test report from JMeter test log, run the following JMeter command
```
jmeter -g log.jtl -o <report folder>
```