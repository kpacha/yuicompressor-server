yuicompressor-server
====================

A pure java HTTP service with an embeded yuicompressor engine

[![Build Status](https://travis-ci.org/kpacha/yuicompressor-server.png?branch=master)](https://travis-ci.org/kpacha/yuicompressor-server)

Powered by [jetty](http://www.eclipse.org/jetty/) and [yui-compressor](http://yui.github.com/yuicompressor/) and inspired on [fotonaut's yuicompressor-server](https://github.com/fotonauts/yuicompressor-server/)

#Requirements

* git
* jdk 1.5 or newer
* mvn 2 or greatter

#Install

Clone the project and install it!

	$ git clone https://github.com/kpacha/yuicompressor-server.git
	$ cd yuicompressor-server
	$ mvn install

And, after testing the code, the yuicompressor-server.jar should be on your local maven repo

#Run with maven

	$ mvn clean compile exec:java

#Build and Run the fat-jar

	$ mvn clean compile assembly:single
	# ...and you are ready for deploy the fat-jar!

	# start your yuicompressor service
	$ java -jar target/yuicompressor-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar [<PORT> [<ALGORITHM>]]

Note the optional arguments!

* `PORT` allows you to set the service port. Default value is 8080
* `ALGORITHM` allows you to set the hashing algorithm. Default: `SHA-1`

#Usage

Just send your javascript and css files as a post request to your service.

Simple demo with curl:

	$ curl -H "Content-Type:text/css; charset=utf-8" -X POST -id 'a {}       c{ color=red;      }' http://localhost:8080/
	# or
	$ curl -H "Content-Type:text/css; charset=utf-8" -X POST -id @src/test/resources/test.css http://localhost:8080/

Do not forget to set the `Content-Type` header with the right charset or get ready to die a painful, lonely death!

#Why?

Supose you have several hosts where you have to compress your javascript and css files. Why would you spend so much time doing the same operation again and again? And are your files different from one host to another or are they almost the same? Just take the DRY pattern to the next abstraction level and delegate that process to a dedicated service!

	$ ab -c 100 -n 1000 -H "Content-Type:text/css; charset=utf-8" -p src/test/resources/test.css -T "text/css" http://localhost:8080/
	This is ApacheBench, Version 2.3 <$Revision: 1430300 $>
	Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
	Licensed to The Apache Software Foundation, http://www.apache.org/

	Benchmarking localhost (be patient)
	Completed 100 requests
	Completed 200 requests
	Completed 300 requests
	Completed 400 requests
	Completed 500 requests
	Completed 600 requests
	Completed 700 requests
	Completed 800 requests
	Completed 900 requests
	Completed 1000 requests
	Finished 1000 requests


	Server Software:        Jetty(9.1.1.v20140108)
	Server Hostname:        localhost
	Server Port:            8080

	Document Path:          /
	Document Length:        52 bytes

	Concurrency Level:      100
	Time taken for tests:   0.752 seconds
	Complete requests:      1000
	Failed requests:        0
	Write errors:           0
	Total transferred:      280000 bytes
	Total body sent:        229000
	HTML transferred:       52000 bytes
	Requests per second:    1330.29 [#/sec] (mean)
	Time per request:       75.172 [ms] (mean)
	Time per request:       0.752 [ms] (mean, across all concurrent requests)
	Transfer rate:          363.75 [Kbytes/sec] received
	                        297.50 kb/s sent
	                        661.25 kb/s total

	Connection Times (ms)
	              min  mean[+/-sd] median   max
	Connect:        0    1   2.4      0      13
	Processing:     1   47  29.5     42     280
	Waiting:        1   46  29.4     42     280
	Total:          1   47  29.0     42     280

	Percentage of the requests served within a certain time (ms)
	  50%     42
	  66%     53
	  75%     59
	  80%     64
	  90%     81
	  95%    101
	  98%    129
	  99%    142
	 100%    280 (longest request)

Was that enough?
