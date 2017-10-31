# Brandenburg - API Proxy [![Build Status](https://travis-ci.org/leosilvadev/brandenburg.svg?branch=master)](https://travis-ci.org/leosilvadev/brandenburg) [![Coverage Status](https://coveralls.io/repos/github/leosilvadev/brandenburg/badge.svg?branch=master)](https://coveralls.io/github/leosilvadev/brandenburg?branch=master)
Brandenburg is a lightweight and simple API Proxy implementation. It is built over Vert.x, so its implementation is based on non-blocking IO.

### Features
- [x] Map your api's using the simple JSON configuration
- [x] Map specific endpoints of your api's with fine-grained JSON configuration
- [x] Define Timeouts in API or Endpoint level
- [x] Implement your own middlewares to intercept, validate or do whatever you want with the requests


### Current benchmarking / results
Brandenburg was tested in a real environment based on AWS. (fast api's responding around 20ms and slow api responding up to 5sec)
Running on AWS and working as proxy for 4 (four) internal api's and one server with static files.

AWS Instance for proxy: t2.micro
Result with Siege:
```
Lifting the server siege...
Transactions:		       11831 hits
Availability:		      100.00 %
Elapsed time:		       59.02 secs
Data transferred:	     1410.94 MB
Response time:		        0.76 secs
Transaction rate:	      200.46 trans/sec
Throughput:		       23.91 MB/sec
Concurrency:		      151.72
Successful transactions:       11831
Failed transactions:	           0
Longest transaction:	        3.02
Shortest transaction:	        0.00
```

### [How to use - Documentation](https://github.com/leosilvadev/brandenburg/wiki)

### How to contribute?
- Fork it
- Find bugs and open issues
- Implement tests
- Implement features
