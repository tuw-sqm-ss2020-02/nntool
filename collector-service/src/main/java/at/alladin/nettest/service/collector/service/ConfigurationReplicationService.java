/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.service.collector.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * @author alladin-IT GmbH (bp@alladin.at)
 */
@Service
public class ConfigurationReplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationReplicationService.class);

    private static final String SETTINGS_INDEX = "nntool_settings";
    private static final String TRANSLATION_INDEX = "nntool_translation";

    private static final String TRANSLATION_INSERT_SQL = "INSERT INTO translations (language_code, json) VALUES (?, ?::jsonb) ON CONFLICT (language_code) DO UPDATE SET json = EXCLUDED.json;";

    @Autowired
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    @Qualifier("elasticSearchClient")
    private RestHighLevelClient elasticSearchClient;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 5000) // Every 10 minutes
    public void replicateConfiguration() {
        LOGGER.info("Replicating configuration. ({}, {})", elasticSearchClient, jdbcTemplate);
        initialize();

        replicateTranslations();

        LOGGER.info("Replication done.");
    }

    private void initialize() {
        if (elasticSearchClient != null) {
            createElasticsearchIndexIfNotExists(SETTINGS_INDEX);
            createElasticsearchIndexIfNotExists(TRANSLATION_INDEX);
        }
    }

    private void createElasticsearchIndexIfNotExists(String index) {
        try {
            final boolean indexExists = elasticSearchClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);

            if (!indexExists) {
                LOGGER.debug("Creating {} index", index);

                final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                createIndexRequest.mapping("{\n"
                        + "      \"dynamic_templates\": [\n"
                        + "        {\n"
                        + "          \"strings\": {\n"
                        + "            \"match_mapping_type\": \"string\",\n"
                        + "            \"path_match\": \"*\",\n"
                        + "            \"mapping\": {\n"
                        + "              \"type\": \"object\",\n"
                        + "              \"index\": \"not_analyzed\",\n"
                        + "              \"enabled\": false\n"
                        + "            }\n"
                        + "          }\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }", XContentType.JSON);
                elasticSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception ex) {
            LOGGER.error("Could not create Elasticsearch index with name {}", index, ex);
        }
    }

    private void replicateTranslations() {
        LOGGER.info("Replicating translations.");

        final Map<Locale, Map<String, String>> translations = storageService.getTranslations();

        if (elasticSearchClient != null) {
            final BulkRequest bulkRequest = new BulkRequest();

            translations.forEach((k, v) ->
                bulkRequest.add(
                        new IndexRequest(TRANSLATION_INDEX)
                                .id(k.getLanguage())
                                .source(v)
                )
            );

            try {
                final BulkResponse bulkResponse = elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    throw new IllegalArgumentException(bulkResponse.buildFailureMessage());
                }
            } catch (Exception ex) {
                LOGGER.error("Could update translations in Elasticsearch.", ex);
            }
        }

        if (jdbcTemplate != null) {
            final List<Object[]> params = new ArrayList<>();

            translations.forEach((k, v) -> {
                try {
                    params.add(new Object[]{k.getLanguage(), objectMapper.writeValueAsString(v)});
                } catch (JsonProcessingException e) {
                    LOGGER.trace("Could not add paramter", e);
                }
            });

            try {
                jdbcTemplate.batchUpdate(TRANSLATION_INSERT_SQL, params);
            } catch (Exception ex) {
                LOGGER.error("Could update translations in PostgreSQL.", ex);
            }
        }
    }
}
