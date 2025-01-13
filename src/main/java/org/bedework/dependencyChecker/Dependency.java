/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.dependencyChecker;

import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Util;

import java.util.List;

/**
 * User: mike Date: 1/13/25 Time: 13:22
 */
class Dependency implements Comparable<Dependency> {
  private Dependency parent;

  private String groupId;

  private String artifactId;

  private String version;

  private String type;

  private String scope;

  private String classifier;

  private boolean optional;

  private List<Dependency> children;
  private boolean seen;

  public Dependency() {
  }

  public void setParent(final Dependency val) {
    parent = val;
  }

  public void setGroupId(final String val) {
    groupId = val;
  }

  public void setArtifactId(final String val) {
    artifactId = val;
  }

  public void setVersion(final String val) {
    version = val;
  }

  public void setType(final String val) {
    type = val;
  }

  public void setScope(final String val) {
    scope = val;
  }

  public void setClassifier(final String val) {
    classifier = val;
  }

  public void setOptional(final boolean val) {
    optional = val;
  }

  public void setChildren(final List<Dependency> val) {
    children = val;
    for (final var c: children) {
      c.setParent(this);
    }
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getType() {
    return type;
  }

  public String getScope() {
    return scope;
  }

  public String getClassifier() {
    return classifier;
  }

  public void addChild(final Dependency val) {
    val.parent = this;
    children.add(val);
  }

  public List<Dependency> getChildren() {
    return children;
  }

  public void setSeen(final boolean val) {
    seen = val;
  }

  public boolean isSeen() {
    return seen;
  }

  public int compareTo(final Dependency dependency) {
    int result = groupId.compareTo(
            dependency.getGroupId());
    if (result != 0) {
      return result;
    }

    result = artifactId.compareTo(
            dependency.getArtifactId());
    if (result != 0) {
      return result;
    }

    result = type.compareTo(dependency.getType());
    if (result != 0) {
      return result;
    }

    result = Util.cmpObjval(classifier,
                            dependency.getClassifier());
    if (result != 0) {
      return result;
    }

    return Util.cmpObjval(version,
                          dependency.getVersion());
  }

  public String toString() {
    final var ts = new ToString(this);
    ts.append("groupId", groupId);
    ts.append("artifactId", artifactId);
    ts.append("type", type);
    ts.append("classifier", classifier);
    ts.append("version", version);
    ts.append("scope", scope);
    ts.append("optional", optional);
    return ts.toString();
  }
}
