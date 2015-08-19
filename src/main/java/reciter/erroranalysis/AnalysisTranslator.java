package reciter.erroranalysis;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import reciter.model.article.ReCiterArticle;
import reciter.model.author.ReCiterAuthor;
import reciter.model.author.TargetAuthor;
import xmlparser.pubmed.PubmedXmlFetcher;
import xmlparser.scopus.model.Affiliation;
import xmlparser.scopus.model.Author;
import xmlparser.scopus.model.ScopusArticle;

public class AnalysisTranslator {

	public static AnalysisObject translate(
			ReCiterArticle reCiterArticle,
			StatusEnum status,
			String cwid,
			TargetAuthor targetAuthor,
			boolean isClusterOriginator,
			int clusterId,
			int countOfArticleInCluster,
			boolean isClusterSelected) {
		AnalysisObject analysisObject = new AnalysisObject();

		analysisObject.setStatus(status);
		analysisObject.setCwid(cwid);
		analysisObject.setTargetName(
				targetAuthor.getAuthorName().getFirstName() + " " + 
						targetAuthor.getAuthorName().getMiddleName() + " " + 
						targetAuthor.getAuthorName().getLastName());
		analysisObject.setPubmedSearchQuery(PubmedXmlFetcher.getPubMedSearchQuery(
				targetAuthor.getAuthorName().getLastName(),
				targetAuthor.getAuthorName().getFirstName()));
		analysisObject.setPmid(Integer.toString(reCiterArticle.getArticleId()));
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
			for (Author scopusAuthor : scopusArticle.getAuthors().values()) {
				String scopusAuthorFirstName = scopusAuthor.getGivenName();
				String scopusAuthorLastName = scopusAuthor.getSurname();
				for (ReCiterAuthor reCiterAuthor : reCiterArticle.getArticleCoAuthors().getAuthors()) {
					String reCiterAuthorLastName = reCiterAuthor.getAuthorName().getLastName();
					if (StringUtils.equalsIgnoreCase(scopusAuthorLastName, reCiterAuthorLastName)) {
						String reCiterAuthorFirstInitial = reCiterAuthor.getAuthorName().getFirstInitial();
						if (scopusAuthorFirstName != null && scopusAuthorFirstName.length() > 1) {
							if (scopusAuthorFirstName.substring(0, 1).equals(reCiterAuthorFirstInitial)) {

								Set<Integer> afidSet = scopusAuthor.getAfidSet();
								for (int afid : afidSet) {
									Affiliation affiliation = scopusArticle.getAffiliationMap().get(afid);
									if (affiliation != null) {
										scopusTargetAuthorAffiliation.append("[" + affiliation.getAffilname() + " " + 
												affiliation.getAffiliationCity() + " " +
												affiliation.getAffiliationCountry() + "], ");
									}
								}
							}
						}
					} else {
						Set<Integer> afidSet = scopusAuthor.getAfidSet();
						for (int afid : afidSet) {
							Affiliation affiliation = scopusArticle.getAffiliationMap().get(afid);
							if (affiliation != null) {
								scopusCoAuthorAffiliation.append("[" + 
										scopusAuthor.getGivenName() + " " + scopusAuthor.getSurname() + "=" +
										affiliation.getAffilname() + " " + 
										affiliation.getAffiliationCity() + " " +
										affiliation.getAffiliationCountry() + "], ");
							}
						}
					}
				}
			}
		}

		for (ReCiterAuthor reCiterAuthor : reCiterArticle.getArticleCoAuthors().getAuthors()) {
			if (reCiterAuthor.getAuthorName().getLastName().equalsIgnoreCase(targetAuthor.getAuthorName().getLastName())) {
				if (reCiterAuthor.getAffiliation() != null) {
					pubmedTargetAuthorAffiliation.append(reCiterAuthor.getAffiliation().getAffiliationName());
				}
			} else {
				if (reCiterAuthor.getAffiliation() != null) {
					pubmedCoAuthorAffiliation.append("[" +
							reCiterAuthor.getAuthorName().getFirstName() + " " +
							reCiterAuthor.getAuthorName().getMiddleName() + " " +
							reCiterAuthor.getAuthorName().getLastName() + "=" +
							reCiterAuthor.getAffiliation().getAffiliationName() + "]"
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
		analysisObject.setNameMatchingScore(0);

		analysisObject.setClusterOriginator(isClusterOriginator);
		analysisObject.setJournalSimilarityPhaseOne(0);
		analysisObject.setCoauthorAffiliationScore(0);
		analysisObject.setTargetAuthorAffiliationScore(0);
		analysisObject.setKnownCoinvestigatorScore(0);
		analysisObject.setFundingStatementScore(0);
		analysisObject.setTerminalDegreeScore(0);
		analysisObject.setDefaultDepartmentJournalSimilarityScore(0);
		analysisObject.setDepartmentOfAffiliationScore(0);
		analysisObject.setKeywordMatchingScore(0);
		analysisObject.setPhaseTwoSimilarityThreshold(0);
		analysisObject.setClusterArticleAssignedTo(clusterId);
		analysisObject.setCountArticlesInAssignedCluster(countOfArticleInCluster);
		analysisObject.setClusterSelectedInPhaseTwoMatching(isClusterSelected);
		analysisObject.setAffiliationSimilarity(0);
		analysisObject.setKeywordSimilarity(0);
		analysisObject.setJournalSimilarityPhaseTwo(0);

		return analysisObject;
	}
}
