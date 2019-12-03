package org.apromore.cache.ehcache.model;

// tag::employeeClass[]
public class Employee extends Person {

  long employeeId;

  public Employee() {}

  public Employee(long employeeId, String name, int age, Description desc) {
    super(name, age, desc);
    this.employeeId = employeeId;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!super.equals(obj)) return false;
    if(!(obj instanceof Employee)) return false;
    
    Employee other = (Employee)obj;
    if(employeeId != other.employeeId) return false;
    
    return true;
  }

  @Override
  public int hashCode() {
    return (31 * (int)employeeId) +  super.hashCode();
  }

  @Override
  public String toString() {
    return employeeId + ";" + super.toString();
  }
}
// end::employeeClass[]
