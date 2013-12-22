JDBI examples
-------------

This repo contains some example code used to try out using [JDBI](http://jdbi.org) as a persistence framework
in Java projects.

The domain model used in the examples looks like this:

| Team | ---> | Person | -(cascade)-> | Address |
