/* ********************************************************************
    Appropriate copyright notice
*/
package org.bedework.dependencyChecker;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: mike Date: 1/10/25 Time: 22:40
 */
public class DependencyChecker implements Logged {
  private final Set<Dependency> dependenciesSet = new HashSet<>();

  static class JarInfo {
    String groupId;
    String artifactId;
    String version;

    public JarInfo(final String groupId,
                   final String artifactId,
                   final String version) {
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.version = version;
    }
  }

  static List<JarInfo> needUpdates = new ArrayList<>();

  static {
    //needUpdates.add(new JarInfo(""));
  }

  final String[] needMigration = {
          "com.sun.mail",
          "javax.activation",
          "javax.annotation",
          "javax.authentication",
          "javax.authorization",
          "javax.batch",
          "javax.ejb",
          "javax.el",
          "javax.enterprise",
          "javax.enterprise.concurrent",
          "javax.faces",
          "javax.inject",
          "javax.interceptor",
          "javax.jms",
          "javax.json",
          "javax.json.bind",
          "javax.mail",
          "javax.mvc",
          "javax.pages",
          "javax.resource",
          "javax.servlet"
  };

  public static void main(final String[] args) {
    final var dc = new DependencyChecker();
    try {
      final Dependency d = dc.parseJson(dc.mungeFile(args[0]));
      if (d == null) {
        dc.warn("Unable to parse dependency file: " + args[0]);
        System.exit(1);
      }
      dc.outTree(d, "");
    } catch (final Throwable t) {
      System.exit(1);
    }
    System.exit(0);
  }

  private void outTree(final Dependency d,
                       final String indent) {
    info(indent + d);
    if (d.getChildren() != null) {
      for (final Dependency c : d.getChildren()) {
        outTree(c, indent + "  ");
      }
    }
  }

  private Reader mungeFile(final String name) {
    try {
      final LineNumberReader lnr = new LineNumberReader(
              new FileReader(name));

      final var outWtr = new StringWriter();
      var hadCurly = false;

      while (lnr.ready()) {
        final var s = lnr.readLine();

        if (s.equals("}")) {
          // If it's the end add array terminator

          String next = "";
          if (lnr.ready()) {
            next = lnr.readLine();
          }

          if (next.isEmpty()) {
            if (hadCurly) {
              outWtr.append("}\n");
              outWtr.append("  ]\n");
            }
            break;
          } else {
            // either end of parent or module
            if (hadCurly) {
              // module
              outWtr.append("  },\n");
            } else {
              outWtr.append("  , \"children\": [\n");
              hadCurly = true;
            }
            outWtr.append(next);
          }
        } else if (hadCurly) {
          outWtr.append("  ").append(s);
        } else {
          outWtr.append(s);
        }
        outWtr.append("\n");
      }

      outWtr.append("  \n}\n");
      outWtr.close();

      return new StringReader(outWtr.toString());
    } catch (final Throwable t) {
      error(t);
      return null;
    }
  }

  private Dependency parseJson(final Reader rdr) {
    if (rdr == null) {
      return null;
    }

    try {
      final ObjectMapper mapper = new ObjectMapper();

      final Dependency d =
              mapper.readValue(rdr, Dependency.class);

      return d;
    } catch (final Throwable t) {
      error(t);
      return null;
    }
  }

  /* ====================================================================
   *                   Logged methods
   * ==================================================================== */

  private BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
