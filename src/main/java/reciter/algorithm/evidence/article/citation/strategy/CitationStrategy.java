package reciter.algorithm.evidence.article.citation.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import reciter.algorithm.evidence.article.AbstractReCiterArticleStrategy;
import reciter.model.article.ReCiterArticle;

public class CitationStrategy extends AbstractReCiterArticleStrategy {

	@Override
	public double executeStrategy(ReCiterArticle reCiterArticle, ReCiterArticle otherReCiterArticle) {

		// check citation references in both ways.
		if (checkCitationReference(reCiterArticle, otherReCiterArticle) == 0) {
			return checkCitationReference(otherReCiterArticle, reCiterArticle);
		} else {
			return 1;
		}
	}

	private double checkCitationReference(ReCiterArticle reCiterArticle, ReCiterArticle otherReCiterArticle) {
		if (reCiterArticle.getCommentsCorrectionsPmids() != null && 
				reCiterArticle.getCommentsCorrectionsPmids().contains(otherReCiterArticle.getArticleId())) {

			reCiterArticle.setClusterInfo(reCiterArticle.getClusterInfo() + "[article " + reCiterArticle.getArticleId() +
					" references article " + otherReCiterArticle.getArticleId());
			return 1;
		}
		return 0;
	}

	private double checkCoCitationReference(ReCiterArticle reCiterArticle, ReCiterArticle otherReCiterArticle) {
		int count = 0;
		Set<Integer> sharedPmids = new HashSet<Integer>();
		if (reCiterArticle.getCommentsCorrectionsPmids() != null && otherReCiterArticle.getCommentsCorrectionsPmids() != null) {

			Set<Integer> pmids = reCiterArticle.getCommentsCorrectionsPmids();
			for (int pmid : pmids) {
				if (otherReCiterArticle.getCommentsCorrectionsPmids().contains(pmid)) {
					count++;
					sharedPmids.add(pmid);
				}
			}
		}
		
		reCiterArticle.setClusterInfo(reCiterArticle.getClusterInfo() + "[article " + reCiterArticle.getArticleId() +
				" and article " + otherReCiterArticle.getArticleId() + " share " + count + " references and those are + " + sharedPmids + "], ");
		return count;
	}

	@Override
	public double executeStrategy(List<ReCiterArticle> reCiterArticles, ReCiterArticle otherReCiterArticle) {
		// TODO Auto-generated method stub
		return 0;
	}

}