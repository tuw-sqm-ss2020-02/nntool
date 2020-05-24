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

package at.alladin.nettest.service.loadbalancer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lb@alladin.at
 */
public class LoadApiReport {

    @JsonProperty("last_response")
    private LoadApiResponse lastResponse;

    @JsonProperty("last_attempt")
    private Long lastAttempt;

    @JsonProperty("last_successful_attempt")
    private Long lastSuccessfulAttempt = -1L;

    @JsonProperty("fails_since_last_attempt")
    private Integer failesSinceLastAttempt = 0;

    @JsonProperty("measurement_server_identifier")
    private String measurementServerIdentifier;

    public LoadApiResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(LoadApiResponse lastResponse) {
        this.lastResponse = lastResponse;
    }

    public Long getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(Long lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public Long getLastSuccessfulAttempt() {
        return lastSuccessfulAttempt;
    }

    public void setLastSuccessfulAttempt(Long lastSuccessfulAttempt) {
        this.lastSuccessfulAttempt = lastSuccessfulAttempt;
    }

    public Integer getFailesSinceLastAttempt() {
        return failesSinceLastAttempt;
    }

    public void setFailesSinceLastAttempt(Integer failesSinceLastAttempt) {
        this.failesSinceLastAttempt = failesSinceLastAttempt;
    }

    public String getMeasurementServerIdentifier() {
        return measurementServerIdentifier;
    }

    public void setMeasurementServerIdentifier(String measurementServerIdentifier) {
        this.measurementServerIdentifier = measurementServerIdentifier;
    }
}
