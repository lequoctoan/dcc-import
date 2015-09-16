/*
 * Copyright (c) 2014 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.imports.pathway.util;

import static java.util.Collections.disjoint;
import static org.icgc.dcc.imports.core.util.Genes.getGeneUniprotIds;
import static org.icgc.dcc.imports.geneset.model.GeneSetAnnotation.DIRECT;
import static org.icgc.dcc.imports.geneset.model.GeneSetAnnotation.INFERRED;
import static org.icgc.dcc.imports.geneset.model.GeneSetType.PATHWAY;

import java.util.Set;

import org.icgc.dcc.imports.geneset.model.gene.GeneGeneSet;
import org.icgc.dcc.imports.pathway.core.PathwayModel;
import org.icgc.dcc.imports.pathway.model.Pathway;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;

@Slf4j
@RequiredArgsConstructor
public class PathwayGeneGeneSetsBuilder {

  @NonNull
  private final PathwayModel model;

  public Set<GeneGeneSet> build(@NonNull ObjectNode gene) {
    val geneSets = Sets.<GeneGeneSet> newHashSet();
    val geneUniprotIds = getGeneUniprotIds(gene);

    val inferredPathways = getInferredPathways(geneUniprotIds);
    for (val inferredPathway : inferredPathways) {
      val direct = isDirect(geneUniprotIds, inferredPathway);
      log.debug("{} ", inferredPathway);
      val geneSet = GeneGeneSet.builder()
          .id(inferredPathway.getReactomeId())
          .name(inferredPathway.getReactomeName())
          .type(PATHWAY)
          .annotation(direct ? DIRECT : INFERRED)
          .build();

      geneSets.add(geneSet);
    }

    return geneSets;
  }

  private Set<Pathway> getInferredPathways(Set<String> geneUniprotIds) {
    val uniqueInferredPathways = Sets.<Pathway> newHashSet();

    for (val geneUniprotId : geneUniprotIds) {
      for (val uniprotPathway : model.getPathways(geneUniprotId)) {
        // Hierarchy doesn't include "self"
        uniqueInferredPathways.add(uniprotPathway);

        // These are strictly non-"self"
        val uniprotHierarchy = model.getHierarchy(uniprotPathway.getReactomeId());
        for (val path : uniprotHierarchy) {
          for (val pathSegment : path) {
            val inferredPathway = model.getPathway(pathSegment.getReactomeId());

            uniqueInferredPathways.add(inferredPathway);
          }
        }
      }
    }

    return uniqueInferredPathways;
  }

  private static boolean isDirect(Set<String> geneUniprotIds, Pathway inferredPathway) {
    return !disjoint(inferredPathway.getUniprots(), geneUniprotIds);
  }

}