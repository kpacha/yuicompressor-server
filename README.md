yuicompressor-server
====================

A pure java HTTP service with an embedded yuicompressor engine

[![Build Status](https://travis-ci.org/kpacha/yuicompressor-server.png?branch=master)](https://travis-ci.org/kpacha/yuicompressor-server)

Powered by [jetty](http://www.eclipse.org/jetty/), [ehcache](http://ehcache.org/) and [yui-compressor](http://yui.github.com/yuicompressor/) and inspired on [fotonaut's yuicompressor-server](https://github.com/fotonauts/yuicompressor-server/)

#Requirements

* git
* jdk 1.7
* mvn 3

#Install

Clone the project and install it!

	$ git clone https://github.com/kpacha/yuicompressor-server.git
	$ cd yuicompressor-server
	$ mvn install

And, after testing the code, the yuicompressor-server-0.2.1-SNAPSHOT.jar should be on your local maven repo

#Run with maven

If the first test is hard, people usually quit the test... So check this out! Just type one more maven command and done! 

	$ mvn clean compile exec:java

Your service should be waiting for you at port `8080`. Nice, uh?

#Build and Run the fat-jar

So, let's build it for real, deploy it to an actual server and run it!

	$ mvn clean compile assembly:single
	# ...and you are ready for deploy the fat-jar! (it's placed at `target/`)

	# ... your deployment process here ...

	# start the yuicompressor service
	$ java -jar yuicompressor-server-0.2.1-SNAPSHOT-jar-with-dependencies.jar [<PORT> [<ALGORITHM>]]

Note the optional arguments!

* `PORT` allows you to set the service port. Default value is `8080`
* `ALGORITHM` allows you to set the hashing algorithm. Default: `SHA-1`

And you already have a yuicompressor-server running!

#Usage

Just send your javascript and css files as a post request to your service.

Simple demo with curl:

	$ curl -H "Content-Type:text/css; charset=utf-8" -X POST -id 'a {}       c{ color=red;      }' http://localhost:8080/
	# or
	$ curl -H "Content-Type:text/css; charset=utf-8" -X POST -id @src/test/resources/background-position.css http://localhost:8080/

Do not forget to set the `Content-Type` header with the right charset or get ready to die a painful, lonely death!

#Why?

Supose you have several hosts where you have to compress your javascript and css files. Why would you spend so much time doing the same operation again and again? And are your files different from one host to another or are they almost the same? How often do you deploy? Just take the DRY pattern to the next abstraction level and delegate that process to a dedicated service!

	$ cd src/test/resources
	$ ab -c 10 -n 100 -H "Content-Type:text/css; charset=utf-8" -p background-position.css -T "text/css" http://localhost:8080/
	This is ApacheBench, Version 2.3 <$Revision: 1430300 $>
	Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
	Licensed to The Apache Software Foundation, http://www.apache.org/

	Benchmarking localhost (be patient).....done


	Server Software:        Jetty(9.1.z-SNAPSHOT)
	Server Hostname:        localhost
	Server Port:            8080

	Document Path:          /
	Document Length:        52 bytes

	Concurrency Level:      10
	Time taken for tests:   0.087 seconds
	Complete requests:      100
	Failed requests:        0
	Write errors:           0
	Total transferred:      27900 bytes
	Total body sent:        22900
	HTML transferred:       5200 bytes
	Requests per second:    1143.80 [#/sec] (mean)
	Time per request:       8.743 [ms] (mean)
	Time per request:       0.874 [ms] (mean, across all concurrent requests)
	Transfer rate:          311.64 [Kbytes/sec] received
	                        255.79 kb/s sent
	                        567.43 kb/s total

	Connection Times (ms)
	              min  mean[+/-sd] median   max
	Connect:        0    0   0.5      0       2
	Processing:     2    8   4.9      7      31
	Waiting:        2    8   4.6      7      31
	Total:          2    8   5.0      7      31

	Percentage of the requests served within a certain time (ms)
	  50%      7
	  66%      8
	  75%     10
	  80%     12
	  90%     16
	  95%     19
	  98%     23
	  99%     31
	 100%     31 (longest request)



Was that enough?

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/kpacha/yuicompressor-server/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
