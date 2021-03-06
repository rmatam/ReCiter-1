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
package reciter.service.mongo.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reciter.database.mongo.model.ScopusArticleMongo;
import reciter.database.mongo.repository.ScopusRepository;
import reciter.model.scopus.ScopusArticle;
import reciter.service.mongo.ScopusService;

@Service("scopusService")
public class ScopusServiceImpl implements ScopusService {

	@Autowired
	private ScopusRepository scopusRepository;

	@Override
	public void save(Collection<ScopusArticle> scopusArticles) {
		List<ScopusArticleMongo> scopusArticleMongos = new ArrayList<>();
		for (ScopusArticle scopusArticle : scopusArticles) {
			ScopusArticleMongo scopusArticleMongo = new ScopusArticleMongo();
			scopusArticleMongo.setId(scopusArticle.getPubmedId());
			scopusArticleMongo.setScopusArticle(scopusArticle);
			scopusArticleMongos.add(scopusArticleMongo);
		}
		scopusRepository.save(scopusArticleMongos);
	}

	@Override
	public List<ScopusArticle> findByPmids(List<Long> pmids) {
		Iterator<ScopusArticleMongo> iterator = scopusRepository.findAll(pmids).iterator();
		List<ScopusArticle> scopusArticles = new ArrayList<>(pmids.size());
		while (iterator.hasNext()) {
			scopusArticles.add(iterator.next().getScopusArticle());
		}
		return scopusArticles;
	}
}
