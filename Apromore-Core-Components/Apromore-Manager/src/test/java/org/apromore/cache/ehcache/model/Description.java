package org.apromore.cache.ehcache.model;

// tag::descriptionClass[]
public class Description {

  String alias;
  int id;

  public Description() {}

  public Description(final String alias, final int id) {
    this.alias = alias;
    this.id = id;
  }

  @Override
  public boolean equals(final Object obj) {
    if(this == obj) return true;
    if(obj == null || this.getClass() != obj.getClass()) return false;

    Description other = (Description)obj;
    if(id != other.id) return false;
    if ((alias == null) ? (alias != null) : !alias.equals(other.alias)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + id;
    result = 31 * result + (alias == null ? 0 : alias.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return alias + ";" + id;
  }
}
// end::descriptionClass[]
