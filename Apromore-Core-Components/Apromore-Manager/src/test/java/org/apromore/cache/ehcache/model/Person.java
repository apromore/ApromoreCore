package org.apromore.cache.ehcache.model;

import java.util.Objects;

// tag::personClass[]
public class Person {

  String name;
  int age;
  Description desc;

  public Person() {}

  public Person(String name, int age, Description desc) {
    this.name = name;
    this.age = age;
    this.desc = desc;
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) return true;
    if(other == null) return false;
    if(!(other instanceof Person)) return false;

    Person that = (Person)other;
    if(age != that.age) return false;
    if(!Objects.equals(name, that.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + age;
    result = 31 * result + (name == null ? 0 : name.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return name + ";" + age + "::" + desc;
  }
}
// end::personClass[]
