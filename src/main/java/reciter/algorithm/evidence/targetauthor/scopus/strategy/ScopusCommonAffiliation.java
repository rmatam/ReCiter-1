/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package reciter.algorithm.evidence.targetauthor.scopus.strategy;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import reciter.algorithm.evidence.targetauthor.AbstractTargetAuthorStrategy;
import reciter.engine.Feature;
import reciter.model.article.ReCiterArticle;
import reciter.model.article.ReCiterAuthor;
import reciter.model.identity.Identity;
import reciter.model.scopus.Affiliation;
import reciter.model.scopus.Author;
import reciter.model.scopus.ScopusArticle;

/**
 * ScopusCommonAffiliation strategy
 * 
 * @author Jie
 *
 */
public class ScopusCommonAffiliation extends AbstractTargetAuthorStrategy {

	@Override
	public double executeStrategy(ReCiterArticle reCiterArticle, Identity identity) {
		
		double score = 0;
		ScopusArticle scopusArticle = reCiterArticle.getScopusArticle();

		if (scopusArticle != null) {
			boolean containsWeillCornellFromScopus = containsWeillCornellFromScopus(scopusArticle, identity);

			if (containsWeillCornellFromScopus) {
				for (ReCiterAuthor reCiterAuthor : reCiterArticle.getArticleCoAuthors().getAuthors()) {

					boolean isFirstNameMatch = StringUtils.equalsIgnoreCase(
							reCiterAuthor.getAuthorName().getFirstInitial(), identity.getPrimaryName().getFirstInitial());

					if (isFirstNameMatch) {
						score += 1;
					}
				}
			}
		}
		reCiterArticle.setScopusStrategyScore(score);
		return score;
	}

	@Override
	public double executeStrategy(List<ReCiterArticle> reCiterArticles, Identity identity) {
		int sum = 0;
		for (ReCiterArticle reCiterArticle : reCiterArticles) {
			sum += executeStrategy(reCiterArticle, identity);
		}
		return sum;
	}

	/**
	 * Check affiliation exists in Scopus Article.
	 * @param scopusArticle
	 * @param identity
	 * @return
	 */
	public boolean containsWeillCornellFromScopus(ScopusArticle scopusArticle, Identity identity) {
		if (scopusArticle != null) {
			for (Author scopusAuthor : scopusArticle.getAuthors()) {
				if (StringUtils.equalsIgnoreCase(scopusAuthor.getSurname(), identity.getPrimaryName().getLastName())) {
					Set<Integer> afidSet = scopusAuthor.getAfids();
					for (int afid : afidSet) {
						for (Affiliation affiliation : scopusArticle.getAffiliations()) {
							if (affiliation.getAfid() == afid) {
								String affilName = affiliation.getAffilname();
								if (containsWeillCornell(affilName)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if the ReCiterArticle's affiliation information contains the phrase 
	 * "weill cornell", "weill-cornell", "weill medical" using case-insensitive
	 * string matching.
	 * 
	 * @param reCiterArticle
	 * @return
	 */
	protected boolean containsWeillCornell(ReCiterArticle reCiterArticle) {
		for (ReCiterAuthor author : reCiterArticle.getArticleCoAuthors().getAuthors()) {
			if (author.getAffiliation() != null) {
				String affiliation = author.getAffiliation();
				if (containsWeillCornell(affiliation)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean containsWeillCornell(String affiliation) {
		return 	StringUtils.containsIgnoreCase(affiliation, "weill cornell") || 
				StringUtils.containsIgnoreCase(affiliation, "weill-cornell") || 
				StringUtils.containsIgnoreCase(affiliation, "weill medical") || 
				StringUtils.containsIgnoreCase(affiliation, "cornell medical center") || 
				StringUtils.containsIgnoreCase(affiliation, "Memorial Sloan-Kettering Cancer Center") ||
				StringUtils.containsIgnoreCase(affiliation, "Sloan-Kettering") ||
				StringUtils.containsIgnoreCase(affiliation, "Sloan Kettering");
	}

	@Override
	public void populateFeature(ReCiterArticle reCiterArticle, Identity identity, Feature feature) {
		ScopusArticle scopusArticle = reCiterArticle.getScopusArticle();

		if (scopusArticle != null) {
			boolean containsWeillCornellFromScopus = containsWeillCornellFromScopus(scopusArticle, identity);

			if (containsWeillCornellFromScopus) {
				for (ReCiterAuthor reCiterAuthor : reCiterArticle.getArticleCoAuthors().getAuthors()) {

					boolean isFirstNameMatch = StringUtils.equalsIgnoreCase(
							reCiterAuthor.getAuthorName().getFirstInitial(), identity.getPrimaryName().getFirstInitial());

					if (isFirstNameMatch) {
						feature.setContainsWeillCornellFromScopus(1);
					}
				}
			}
		}
	}
}
