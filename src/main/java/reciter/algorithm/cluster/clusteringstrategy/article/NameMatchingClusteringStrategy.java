package reciter.algorithm.cluster.clusteringstrategy.article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import reciter.algorithm.cluster.model.ReCiterCluster;
import reciter.model.article.ReCiterArticle;
import reciter.model.author.ReCiterAuthor;
import reciter.model.author.TargetAuthor;

public class NameMatchingClusteringStrategy extends AbstractClusteringStrategy {
	
	private final TargetAuthor targetAuthor;
	
	public NameMatchingClusteringStrategy(TargetAuthor targetAuthor) {
		this.targetAuthor = targetAuthor;
	}

	/**
	 * Select the first article from the list. Iterate through the remaining
	 * articles and assign article based on target author name match.
	 */
	@Override
	public Map<Integer, ReCiterCluster> cluster(List<ReCiterArticle> reCiterArticles) {
		
		// Reset ReCiterCluster's static id counter to 0, so that subsequent calls
		// to cluster method has ReCiterCluster id starts with 0.
		ReCiterCluster.getClusterIDCounter().set(0);
		
		Map<Integer, ReCiterCluster> clusters = new HashMap<Integer, ReCiterCluster>();
		boolean isFirstArticleSelected = false;

		for (ReCiterArticle article : reCiterArticles) {
			if (!isFirstArticleSelected) {
				// Select first article.
				ReCiterArticle firstArticle = null;
				if (reCiterArticles != null && reCiterArticles.size() > 0) {
					firstArticle = reCiterArticles.get(0);
				}  else {
					return clusters;
				}
				ReCiterCluster firstCluster = new ReCiterCluster();
				firstCluster.setClusterOriginator(firstArticle.getArticleId());
				firstCluster.add(firstArticle);
				clusters.put(firstCluster.getClusterID(), firstCluster);
				isFirstArticleSelected = true;
			} else {
				// Assign subsequent articles to a cluster.
				boolean foundCluster = false;
				for (Entry<Integer, ReCiterCluster> entry : clusters.entrySet()) {
			        ReCiterCluster reCiterCluster = entry.getValue();
			        for (ReCiterArticle reCiterArticle : reCiterCluster.getArticleCluster()) {

			          boolean isFirstNameMatch = isTargetAuthorNameMatch(article, reCiterArticle);
			          if (isFirstNameMatch) {
			            clusters.get(entry.getKey()).add(article);
			            foundCluster = true;
			            break;
			          }
			        }
			        if (foundCluster) break;
				}
				if (!foundCluster) {
					// create its own cluster.
					ReCiterCluster newReCiterCluster = new ReCiterCluster();
					newReCiterCluster.setClusterOriginator(article.getArticleId());
					newReCiterCluster.add(article);
					clusters.put(newReCiterCluster.getClusterID(), newReCiterCluster);
				}
			}
		}
		return clusters;
	}
	
	/**
	 * <p>
	 * First name matching in phase one clustering.
	 * <p>
	 * For more details, see https://github.com/wcmc-its/ReCiter/issues/59.
	 */
	private boolean isTargetAuthorNameMatch(ReCiterArticle newArticle, ReCiterArticle articleInCluster) {
		for (ReCiterAuthor reCiterAuthor : newArticle.getArticleCoAuthors().getAuthors()) {
			for (ReCiterAuthor clusterAuthor : articleInCluster.getArticleCoAuthors().getAuthors()) {
				if (reCiterAuthor.getAuthorName().firstInitialLastNameMatch(targetAuthor.getAuthorName()) &&
						clusterAuthor.getAuthorName().firstInitialLastNameMatch(targetAuthor.getAuthorName())) {

					// Check both first name and middle initial.
					if (reCiterAuthor.getAuthorName().getFirstName().equalsIgnoreCase(
							clusterAuthor.getAuthorName().getFirstName())
							&&
						reCiterAuthor.getAuthorName().getMiddleInitial().equalsIgnoreCase(
								clusterAuthor.getAuthorName().getMiddleInitial())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public TargetAuthor getTargetAuthor() {
		return targetAuthor;
	}

}