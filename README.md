# JDBI examples

This repo contains some example code used to try out using [JDBI](http://jdbi.org) as a persistence framework
in Java projects.

The domain model used in the examples looks like this:

| Team | ---> | Person | -(cascade)-> | Address |

## Notes

### CGLIB issue with references

JDBI generates boilerplate code from annotated interfaces or abstract classes. When using
abstract classes it would be nice to di the following:

```java
@RegisterMapper(AddressMapper.class)
public abstract class AddressAbstractClassJdbiDao implements AddressDao {
}

...

public Foo {
    private AddressDao addressDao = dbi.onDemand(AddressAbstractClassJdbiDao.class);
}
```

However, this doesn't work. You will get a null pointer exception. Instead you must use
the abstract (implementation) class as reference, like this:


```java
@RegisterMapper(AddressMapper.class)
public abstract class AddressAbstractClassJdbiDao implements AddressDao {
}

...

public Foo {
    private AddressAbstractClassJdbiDao addressDao = dbi.onDemand(AddressAbstractClassJdbiDao.class);
}
```

### CGLIB issue with abstract classes

It seems that when using abstract classes with CGLIB you need to put them in a separate file,
otherwise you get the following error:

```
java.lang.IllegalArgumentException: Superclass has no null constructors but no arguments were given
	at org.skife.jdbi.cglib.proxy.Enhancer.emitConstructors(Enhancer.java:721)
	at org.skife.jdbi.cglib.proxy.Enhancer.generateClass(Enhancer.java:499)
```






