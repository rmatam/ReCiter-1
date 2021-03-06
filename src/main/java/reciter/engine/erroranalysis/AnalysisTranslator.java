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
package reciter.engine.erroranalysis;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import reciter.model.article.ReCiterArticle;
import reciter.model.article.ReCiterAuthor;
import reciter.model.identity.Identity;
import reciter.model.scopus.Affiliation;
import reciter.model.scopus.Author;
import reciter.model.scopus.ScopusArticle;

public class AnalysisTranslator {

	public static AnalysisObject translate(
			ReCiterArticle reCiterArticle,
			StatusEnum status,
			String uid,
			Identity identity,
			boolean isClusterOriginator,
			long clusterId,
			int countOfArticleInCluster,
			boolean isClusterSelected) {

		AnalysisObject analysisObject = new AnalysisObject();
		analysisObject.setStatus(status);
		analysisObject.setUid(uid);
		analysisObject.setTargetName(
				identity.getPrimaryName().getFirstName() + " " + 
						identity.getPrimaryName().getMiddleName() + " " + 
						identity.getPrimaryName().getLastName());
		
//		analysisObject.setPubmedSearchQuery(identity.getPubmedSearchQuery());
		analysisObject.setPmid(reCiterArticle.getArticleId());
		analysisObject.setArticleTitle(reCiterArticle.getArticleTitle());
		analysisObject.setFullJournalTitle(reCiterArticle.getJournal().getJournalTitle());
		analysisObject.setPublicationYear(Integer.toString(reCiterArticle.getJournal().getJournalIssuePubDateYear()));

		StringBuilder scopusTargetAuthorAffiliation = new StringBuilder();
		StringBuilder scopusCoAuthorAffiliation = new StringBuilder();
		StringBuilder pubmedTargetAuthorAffiliation = new StringBuilder();
		StringBuilder pubmedCoAuthorAffiliation = new StringBuilder();

		// TODO Create a separate function to get the details from Scopus.
		ScopusArticle scopusArticle = reCiterArticle.getScopusArticle();
		if (scopusArticle != null) {
			for (Author scopusAuthor : scopusArticle.getAuthors()) {
				String scopusAuthorFirstName = scopusAuthor.getGivenName();
				String scopusAuthorLastName = scopusAuthor.getSurname();
				String targetAuthorLastName = identity.getPrimaryName().getLastName();
				if (StringUtils.equalsIgnoreCase(scopusAuthorLastName, targetAuthorLastName)) {
					String targetAuthorFirstInitial = identity.getPrimaryName().getFirstInitial();
					if (scopusAuthorFirstName != null && scopusAuthorFirstName.length() > 1) {
						if (scopusAuthorFirstName.substring(0, 1).equals(targetAuthorFirstInitial)) {

							Set<Integer> afidSet = scopusAuthor.getAfids();
							for (int afid : afidSet) {
								for (Affiliation affiliation : scopusArticle.getAffiliations()) {
									if (affiliation.getAfid() == afid) {
										scopusTargetAuthorAffiliation.append("[" + 
												scopusAuthor.getGivenName() + " " + scopusAuthor.getSurname() + "=" +
												affiliation.getAffilname() + " " + 
												affiliation.getAffiliationCity() + " " +
												affiliation.getAffiliationCountry() + "]");
										break;
									}
								}
//								Affiliation affiliation = scopusArticle.getAffiliationMap().get(afid);
//								if (affiliation != null) {
//									scopusTargetAuthorAffiliation.append("[" + 
//											scopusAuthor.getGivenName() + " " + scopusAuthor.getSurname() + "=" +
//											affiliation.getAffilname() + " " + 
//											affiliation.getAffiliationCity() + " " +
//											affiliation.getAffiliationCountry() + "]");
//								}
							}
						}
					}
				} else {
					Set<Integer> afidSet = scopusAuthor.getAfids();
					for (int afid : afidSet) {
						for (Affiliation affiliation : scopusArticle.getAffiliations()) {
							if (affiliation.getAfid() == afid) {
								scopusTargetAuthorAffiliation.append("[" + 
										scopusAuthor.getGivenName() + " " + scopusAuthor.getSurname() + "=" +
										affiliation.getAffilname() + " " + 
										affiliation.getAffiliationCity() + " " +
										affiliation.getAffiliationCountry() + "]");
								break;
							}
						}
//						Affiliation affiliation = scopusArticle.getAffiliationMap().get(afid);
//						if (affiliation != null) {
//							scopusCoAuthorAffiliation.append("[" + 
//									scopusAuthor.getGivenName() + " " + scopusAuthor.getSurname() + "=" +
//									affiliation.getAffilname() + " " + 
//									affiliation.getAffiliationCity() + " " +
//									affiliation.getAffiliationCountry() + "], ");
//						}
					}
				}
			}
		}

		for (ReCiterAuthor reCiterAuthor : reCiterArticle.getArticleCoAuthors().getAuthors()) {
			if (reCiterAuthor.getAuthorName().getLastName().equalsIgnoreCase(identity.getPrimaryName().getLastName())) {
				if (reCiterAuthor.getAffiliation() != null) {
					pubmedTargetAuthorAffiliation.append(reCiterAuthor.getAffiliation());
				}
			} else {
				if (reCiterAuthor.getAffiliation() != null) {
					pubmedCoAuthorAffiliation.append("[" +
							reCiterAuthor.getAuthorName().getFirstName() + " " +
							reCiterAuthor.getAuthorName().getMiddleName() + " " +
							reCiterAuthor.getAuthorName().getLastName() + "=" +
							reCiterAuthor.getAffiliation() + "]"
							);
				} else {
					pubmedCoAuthorAffiliation.append("[" +
							reCiterAuthor.getAuthorName().getFirstName() + " " +
							reCiterAuthor.getAuthorName().getMiddleName() + " " +
							reCiterAuthor.getAuthorName().getLastName() + "=" +
							"N/A]"
							);
				}
			}
		}

		analysisObject.setScopusTargetAuthorAffiliation(scopusTargetAuthorAffiliation.toString());
		analysisObject.setScopusCoAuthorAffiliation(scopusCoAuthorAffiliation.toString());
		analysisObject.setPubmedTargetAuthorAffiliation(pubmedTargetAuthorAffiliation.toString());
		analysisObject.setPubmedCoAuthorAffiliation(pubmedCoAuthorAffiliation.toString());

		analysisObject.setArticleKeywords(reCiterArticle.getArticleKeywords().toString());

		analysisObject.setClusterOriginator(isClusterOriginator);
		analysisObject.setClusterArticleAssignedTo(clusterId);
		analysisObject.setCountArticlesInAssignedCluster(countOfArticleInCluster);
		analysisObject.setClusterSelectedInPhaseTwoMatching(isClusterSelected);

		analysisObject.setEmailStrategyScore(reCiterArticle.getEmailStrategyScore());
		analysisObject.setDepartmentStrategyScore(reCiterArticle.getDepartmentStrategyScore());
		analysisObject.setKnownCoinvestigatorScore(reCiterArticle.getKnownCoinvestigatorScore());
		analysisObject.setAffiliationScore(reCiterArticle.getAffiliationScore());
		analysisObject.setScopusStrategyScore(reCiterArticle.getScopusStrategyScore());
		analysisObject.setCoauthorStrategyScore(reCiterArticle.getCoauthorStrategyScore());
		analysisObject.setJournalStrategyScore(reCiterArticle.getJournalStrategyScore());
		analysisObject.setCitizenshipStrategyScore(reCiterArticle.getCitizenshipStrategyScore());
		analysisObject.setBachelorsYearDiscrepancyScore(reCiterArticle.getBachelorsYearDiscrepancyScore());
		analysisObject.setDoctoralYearDiscrepancyScore(reCiterArticle.getDoctoralYearDiscrepancyScore());
		analysisObject.setArticleTitleStartWithBracket(reCiterArticle.isArticleTitleStartWithBracket());
		analysisObject.setEducationScore(reCiterArticle.getEducationStrategyScore());

		analysisObject.setDateInitialRun(identity.getDateInitialRun());
		analysisObject.setDateLastRun(identity.getDateLastRun());
		
		analysisObject.setTargetAuthorYearBachelorsDegree(identity.getDegreeYear().getBachelorYear());
		analysisObject.setTargetAuthorYearTerminalDegree(identity.getDegreeYear().getDoctoralYear());
		analysisObject.setDepartments(identity.getDepartments());
		analysisObject.setTargetAuthorKnownEmails(identity.getEmails());
		analysisObject.setTargetAuthorKnownNameAliases(identity.getAlternateNames());
		analysisObject.setTargetAuthorKnownAffiliations(identity.getInstitutions());
		analysisObject.setBachelorsYearDiscrepancy(reCiterArticle.getBachelorsYearDiscrepancy());
		analysisObject.setDoctoralYearDiscrepancy(reCiterArticle.getDoctoralYearDiscrepancy());
		
		analysisObject.setFrequentInstitutionalCollaborators(reCiterArticle.getFrequentInstitutionalCollaborators());
		analysisObject.setKnownRelationships(reCiterArticle.getKnownRelationships());
		
		analysisObject.setClusterId(clusterId);
		analysisObject.setMeshMajorStrategyScore(reCiterArticle.getMeshMajorStrategyScore());
		analysisObject.setOverlappingMeSHMajorNegativeArticles(reCiterArticle.getOverlappingMeSHMajorNegativeArticles());
		return analysisObject;
	}
}
