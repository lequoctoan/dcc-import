package org.icgc.dcc.imports.pathway.reader;

import static org.apache.commons.lang.StringUtils.repeat;
import static org.icgc.dcc.imports.core.util.Importers.getRemoteReactomeHierarchyUri;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.icgc.dcc.imports.pathway.reader.PathwayHierarchyReader;
import org.junit.Test;
import org.xml.sax.SAXException;

@Slf4j
public class PathwayHierarchyReaderTest {

  @Test
  public void testRead() throws MalformedURLException, IOException, SAXException, ParserConfigurationException,
      URISyntaxException {
    val hierarchyFile = getRemoteReactomeHierarchyUri();
    log.info("Pathway URL {}", hierarchyFile);

    val parser = new PathwayHierarchyReader();
    val results = parser.read(hierarchyFile);

    for (val dbId : results.keySet()) {
      log.info("{}", repeat("-", 80));
      log.info("Pathway dbId: '{}'", dbId);
      log.info("{}", repeat("-", 80));
      val set = results.get(dbId);

      int index = 1;
      for (val list : set) {
        log.info("{}.", index);
        for (int i = 0; i < list.size(); i++) {
          val segment = list.get(i);
          log.info("  {}{} - {}", repeat("  ", i), segment.getReactomeName(), segment.isDiagrammed());
        }

        index++;
      }
    }
  }

}