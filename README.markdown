Resque Workers in Scala
=======================

Resque is the awesome queue system written by the github guys. It is a very polished piece of software that includes tremendous visibility in to the status of your workers and your queues. They include a full set of tools written in ruby, which is awesome. But, we're doing a lot of scala these days, and some of our background jobs *need* to be run by scala code. So, I hacked up some code to integrate in to the resque system from scala.

How it Works
------------

To write the code that runs a job, you need to create an object that inherits from com.protose.resque.Performable. Note that the performable must have a 0-args constructor:

    import com.protose.resque.Performable

    class MyAwesomeJob extends Performable {
      override def perform(args: List[String]) = println("I did something awesome with " + args)
    }

The name of your class (minus package) is how your performable will be identified. When you queue it up (presumably from ruby code), that name is what you need to use to make sure your Performable is found. In this example, that would like something like this:

    ## this is ruby code
    
    class MyAwesomeJob
      @queue = :my_queue
    end

    Resque.enqueue(MyAwesomeJob, "some arg")

Note that the MyAwesomeJob ruby class doesn't have a self.perform method. It doesn't need one, because it's really just a placeholder for your scala job which does the actual work.

Gotchas
-------

  1. Scala is statically typed, so, you can only pass arguments that can get parsed in to List[String]. Probably not a big deal in practice, but worth knowing about.
  2. There's currently no way to listen on multiple queues. This could be easily fixed.

Running It
----------

Grab the latest jar from the Downloads section, and all the dependencies from lib and create some kind of script to setup all the classpath nonsense and stuff. Once you have that, you can either put a config file in /etc/resque.conf or use the CONFIG environment variable to point to a custom location. If you don't provide a config file, it'll assume that redis is running locally.

In the config file, there are currently four parameters:

  redis.host   = "some.host"
  redis.port   = 12345
  queue	       = "the queue to listen on"
  performables = ["com.myco.MyJob"]

You can also set the queue with the QUEUE environment variable, and the performables with the PERFORMABLES env var. Performables must be a comma separated list of full paths to your performable classes.

There's an example config file in the examples directory.

The config is setup using Configgy (http://www.lag.net/configgy/). So, you can use Configgy.config from your Performables to get additional config parameters.

TODO
----

It should probably use actors for some concurrency. Currently, it processes one job at a time.

Patches
-------

This is my first open source scala project. I'm pretty noobish still, so I'd love some feedback, patches, etc. Please include specs with your patch where appropriate.

License
------

scala-resque-worker is copyright (c) 2009 James Golick, written for use at FetLife.com (NSFW), released under the terms of the MIT License. See the LICENSE file for details.


