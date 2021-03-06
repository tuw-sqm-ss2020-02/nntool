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

package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;

/**
 * @author alladin-IT GmbH (bp@alladin.at)
 */
@Mapper(componentModel = "spring")
public interface DateTimeMapper {

    default org.joda.time.LocalDateTime map(java.time.LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }

        return new org.joda.time.LocalDateTime(
                ldt.getYear(),
                ldt.getMonthValue(),
                ldt.getDayOfMonth(),
                ldt.getHour(),
                ldt.getMinute(),
                ldt.getSecond(),
                ldt.getNano() / 1000000 // ?
        );

        //return new org.joda.time.LocalDateTime(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    default java.time.LocalDateTime map(org.joda.time.LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }

        return java.time.LocalDateTime.of(
                ldt.getYear(),
                ldt.getMonthOfYear(),
                ldt.getDayOfMonth(),
                ldt.getHourOfDay(),
                ldt.getMinuteOfHour(),
                ldt.getSecondOfMinute(),
                ldt.getMillisOfSecond() * 1000000 // ?
        );
    }
}
