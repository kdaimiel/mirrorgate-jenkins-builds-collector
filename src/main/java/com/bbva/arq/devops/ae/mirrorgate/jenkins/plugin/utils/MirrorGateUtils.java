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

package com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.utils;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.Run;
import java.io.IOException;
import jenkins.model.Jenkins;
import jenkins.plugins.mirrorgate.MirrorGatePublisher;

public class MirrorGateUtils {

    private MirrorGateUtils() {
    }

    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static String getBuildUrl(Run<?, ?> run) {
        return run.getParent().getAbsoluteUrl()
                + String.valueOf(run.getNumber()) + "/";
    }

    public static String getBuildNumber(Run<?, ?> run) {
        return String.valueOf(run.getNumber());
    }

    public static String getMirrorGateAPIUrl() {
        return Jenkins.getInstance().getDescriptorByType(
                MirrorGatePublisher.DescriptorImpl.class)
                .getMirrorGateAPIUrl();
    }

    public static String getMirrorGateUser() {
        return MirrorGateUtils.getUsernamePasswordCredentials() != null
                ? MirrorGateUtils.getUsernamePasswordCredentials()
                .getUsername() : null;
    }

    public static String getMirrorGatePassword() {
        return MirrorGateUtils.getUsernamePasswordCredentials() != null
                ? MirrorGateUtils.getUsernamePasswordCredentials()
                .getPassword().getPlainText() : null;
    }

    public static UsernamePasswordCredentials getUsernamePasswordCredentials() {
        String credentialsId = Jenkins.getInstance().getDescriptorByType(
                MirrorGatePublisher.DescriptorImpl.class)
                .getMirrorgateCredentialsId();
        return CredentialsUtils.getJenkinsCredentials(
                credentialsId, UsernamePasswordCredentials.class);
    }
}