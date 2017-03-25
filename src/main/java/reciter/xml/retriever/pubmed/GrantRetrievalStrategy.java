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
package reciter.xml.retriever.pubmed;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import reciter.model.identity.Identity;
import reciter.xml.retriever.pubmed.PubMedQuery.PubMedQueryBuilder;

@Component("grantRetrievalStrategy")
public class GrantRetrievalStrategy extends AbstractNameRetrievalStrategy {

	private static final String retrievalStrategyName = "GrantRetrievalStrategy";

	@Override
	public String getRetrievalStrategyName() {
		return retrievalStrategyName;
	}

	@Override
	protected String getStrategySpecificKeyword(Identity identity) {
		if (identity.getGrants() != null && !identity.getGrants().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (String grant : identity.getGrants()) {
				if (i != identity.getGrants().size() - 1) {
					sb.append(grant + "[Grant Number] OR ");
				} else {
					sb.append(grant + "[Grant Number]");
				}
				i++;
			}
			return "(" + sb.toString() + ")";
		} else {
			return null;
		}
	}

	@Override
	protected String buildNameQuery(String lastName, String firstName, Identity identity) {
		PubMedQueryBuilder pubMedQueryBuilder = 
				new PubMedQueryBuilder(getStrategySpecificKeyword(identity))
				.author(true, lastName, firstName);

		return pubMedQueryBuilder.build();
	}

	@Override
	protected String buildNameQuery(String lastName, String firstName, Identity identity, LocalDate startDate,
			LocalDate endDate) {
		PubMedQueryBuilder pubMedQueryBuilder = 
				new PubMedQueryBuilder(getStrategySpecificKeyword(identity))
				.author(true, lastName, firstName)
				.dateRange(true, startDate, endDate);

		return pubMedQueryBuilder.build();
	}
}
