yuicompressor-server
====================

A pure java HTTP service with an embedded yuicompressor engine

[![Build Status](https://travis-ci.org/softonic/yuicompressor-server.png?branch=master)](https://travis-ci.org/softonic/yuicompressor-server)

Powered by [jetty](http://www.eclipse.org/jetty/), [ehcache](http://ehcache.org/) and [yui-compressor](http://yui.github.com/yuicompressor/) and inspired on [fotonaut's yuicompressor-server](https://github.com/fotonauts/yuicompressor-server/)

#Requirements

* git
* jdk 1.7
* mvn 3

#Install

Clone the project and install it!

	$ git clone https://github.com/softonic/yuicompressor-server.git
	$ cd yuicompressor-server
	$ mvn install

And, after testing the code, the yuicompressor-server-0.2.2-SNAPSHOT.jar should be on your local maven repo

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
	$ java -jar yuicompressor-server-__VERSION__-full.jar [-p <port>] [-c] [-m <max_input_size>]

And you already have a yuicompressor-server running!

The options are:

	-p <arg>   port (default 8080)
	-c         enable cache
	-m <arg>   max input size (default 1024000 bytes)

#Usage

Just send your javascript and css files as a post request to your service.
The server checks the integrity of the request using the Content-MD5 header, so you need to add this header with the MD5 of the file sent.

Simple demo with curl:

	$ curl -H "Content-MD5: acf0ae0101e6762933ee7036297851fd" --data-urlencode "type=css" --data-urlencode "files=main.css" --data-urlencode "input=a {}       c{ color=red;      }" http://localhost:8080/
	# or
	$ curl -H "Content-MD5: <md5_of_file_sended>" --data-urlencode "type=js" --data-urlencode "files=main.js" --data-urlencode "input@main.js" http://localhost:8080/

#Why?

Suppose you have several hosts where you have to compress your javascript and css files. Why would you spend so much time doing the same operation again and again? And are your files different from one host to another or are they almost the same? How often do you deploy? Just take the DRY pattern to the next abstraction level and delegate that process to a dedicated service!

Was that enough?
