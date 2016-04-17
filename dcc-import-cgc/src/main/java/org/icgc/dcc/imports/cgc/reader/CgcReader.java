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
package org.icgc.dcc.imports.cgc.reader;

import java.net.URL;
import java.util.Map;

import org.icgc.dcc.imports.core.util.AbstractMapReader;

import com.google.common.collect.ImmutableList;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CgcReader extends AbstractMapReader {

  /**
   * State.
   */
  @NonNull
  private final URL cgsUri;

  public CgcReader(URL cgsUri) {
    super(TAB_FIELD_SEPARATOR);
    this.cgsUri = cgsUri;
  }

  public Iterable<Map<String, String>> read() {
    log.info("Reading CGC...");
    val genes = ImmutableList.<Map<String, String>> builder();

    val cgc = readCgsStream();
    for (val cgcGene : cgc) {
      genes.add(cgcGene);
    }

    return genes.build();
  }

  @SneakyThrows
  private Iterable<Map<String, String>> readCgsStream() {
    return readRecords(cgsUri.openStream());
  }

}
