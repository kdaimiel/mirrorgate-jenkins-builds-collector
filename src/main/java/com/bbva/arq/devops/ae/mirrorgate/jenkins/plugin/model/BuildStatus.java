/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Enumeration of build status.
 *
 */
public enum BuildStatus {
    Deleted,
    Success,
    Failure,
    Unstable,
    Aborted,
    InProgress,
    NotBuilt,
    Unknown;

    private static final Map<String, BuildStatus> MAPPING = new HashMap<>();

    static {
        MAPPING.put("not_built", NotBuilt);
        for (BuildStatus buildStatus : values()) {
            MAPPING.put(buildStatus.toString().toLowerCase(Locale.ENGLISH), buildStatus);
        }
    }

    public static BuildStatus fromString(String value) {
        String key = value.toLowerCase(Locale.ENGLISH);
        return MAPPING.getOrDefault(key, Unknown);
    }

}
