/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.imports.pathway.reader;

import static org.icgc.dcc.common.core.util.Formats.formatCount;

import java.net.URL;

import org.icgc.dcc.common.core.model.FieldNames;
import org.icgc.dcc.imports.core.util.AbstractMapReader;
import org.icgc.dcc.imports.pathway.model.PathwayUniprot;

import com.google.common.collect.ImmutableList;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PathwayUniprotReader extends AbstractMapReader {

  /**
   * Constants.
   */
  // @formatter:off
  private static final String HOMO_SAPIEN = "Homo sapiens";
  private static final String[] CSV_HEADER = { 
          FieldNames.PATHWAY_UNIPROT_ID, 
          FieldNames.PATHWAY_REACTOME_ID, 
          FieldNames.PATHWAY_URL, // Not used
          FieldNames.PATHWAY_NAME, 
          FieldNames.PATHWAY_EVIDENCE_CODE, 
          FieldNames.PATHWAY_SPECIES
      };
  // @formatter:on

  public PathwayUniprotReader() {
    super(TAB_FIELD_SEPARATOR);
  }

  @SneakyThrows
  public Iterable<PathwayUniprot> read(URL uniprotFile) {
    log.info("Reading pathway-uniprots from {}...", uniprotFile);

    val uniprots = ImmutableList.<PathwayUniprot> builder();

    for (val record : readRecords(CSV_HEADER, uniprotFile.openStream())) {
      val human = record.get(FieldNames.PATHWAY_SPECIES).equals(HOMO_SAPIEN);
      if (!human) {
        continue;
      }

      val uniprot = record.get(FieldNames.PATHWAY_UNIPROT_ID).trim();
      val reactomeId = record.get(FieldNames.PATHWAY_REACTOME_ID).trim();
      val evidenceCode = record.get(FieldNames.PATHWAY_EVIDENCE_CODE).trim();
      val name = record.get(FieldNames.PATHWAY_NAME).trim();

      val pathwyUniprot = PathwayUniprot.builder()
          .uniprot(uniprot)
          .reactomeId(reactomeId)
          .evidenceCode(evidenceCode)
          .name(name)
          .build();

      uniprots.add(pathwyUniprot);
    }

    log.info("Finished reading {} pathway-uniprots", formatCount(uniprots.build()));
    return uniprots.build();
  }

}
