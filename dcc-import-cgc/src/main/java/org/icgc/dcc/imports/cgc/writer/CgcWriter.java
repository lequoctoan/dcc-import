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
package org.icgc.dcc.imports.cgc.writer;

import static com.google.common.base.Stopwatch.createStarted;
import static org.icgc.dcc.common.core.model.ReleaseCollection.GENE_COLLECTION;
import static org.icgc.dcc.common.core.model.ReleaseCollection.GENE_SET_COLLECTION;

import java.util.Map;

import org.icgc.dcc.imports.core.util.AbstractJongoWriter;

import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.mongodb.MongoClientURI;

@Slf4j
public class CgcWriter extends AbstractJongoWriter<Iterable<Map<String, String>>> {

  public CgcWriter(@NonNull MongoClientURI mongoUri) {
    super(mongoUri);
  }

  @Override
  public void writeFiles(@NonNull Iterable<Map<String, String>> model) {
    val watch = createStarted();

    log.info("Writing gene sets to {}...", mongoUri);
    writeGeneSets(model);

    log.info("Writing gene gene sets to {}...", mongoUri);
    writeGeneGeneSets(model);

    log.info("Finished writing gene sets and gene gene sets in {}", watch);
  }

  private void writeGeneSets(Iterable<Map<String, String>> model) {
    new CgcGeneSetWriter(getCollection(GENE_SET_COLLECTION)).write();
  }

  private void writeGeneGeneSets(Iterable<Map<String, String>> model) {
    new CgcGeneGeneSetWriter(getCollection(GENE_COLLECTION)).write(model);
  }

}
