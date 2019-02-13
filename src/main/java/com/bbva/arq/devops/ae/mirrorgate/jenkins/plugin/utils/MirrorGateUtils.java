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

import com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.model.BuildDTO;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.model.Run;
import jenkins.model.Jenkins;
import jenkins.plugins.mirrorgate.MirrorGateRecorder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MirrorGateUtils {

    private static final String BASE64IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAWJAAAFiQFtaJ36AAAAB3RJTUUH4QUQCwwbo7DTaQAAAyVJREFUOMt9U2lMVGcUPd+8NzDzHgNVBAYHRerCYF1Ghakpo5jaGGraatziQiNElMQlheCCaWLQ1FSo2KY/iDHBkmoixlQNQdPEGJcmVEWN1VaL4ogSNofFDovMzHvv+AOdSGM8f+69yT0nJ3cReAuS/WPoHddhHpeVbJ7gmmeyREns720Y+LOyEYCOd8D0JpHt7mFy2vLNUrKr2fB3HdPavdWIUP5JW1GqzcnOP733YFUSAJG/4+BIFdnuHo4pi8rMM3MoJ86l5JhPMT6b1rRVtMzIY1F5DUkatRfqT73LCazOL8qVzK1Upi5hxeETPHvlL/7d6qeank/r9PWMnLmJvh4/SfLzr3eVjSArzsVjJxaepuwp5IkrD9gbJHWS62p7OX7Zd7ROy6Eyu4D/ettJkjUXbhEwjQnPICIm7rAUaUGWJwOr5zuhG8DNDh3xPffR3toBgKChw6Zaht3GJuCztcV1ACAiZ2xIMEXbOkT/f1Acyfh0ogJFVXC+sQ8Dbc3QuttBWcGarxag+kABWgaBZw+bMHlcrP5NyQ/xMozBIvi6kbquCD1P7uPMtbsQRgB4/hi6ISDMKn6t2Iw1X86Dtw84e6cLpgCQ6RolebI++V42/G1BXQvCW1eFIVgg/M8R6noKNcqGb3dsRMmW1egOSqhrAeobfXDoPnimJgIAbvXIo4VpzKyxkkVtNTQN4gM79K5n+ChtEu5dPYmQAVQ/MHD5XgseXqqD3eXB0vhuTHN+iBsvVO47VJUMAEjKLr5oX7KXIt5NEZfON9heT6YWVFFduJPKrLW0zFnP3eVH2dTpZ27h/rsAIAPAy97OPFOMqZm6JlKnpITX673TgOabf8DwPQKHBhChRiHWfQDQQ6j+6eeFI6/RkfmjiEunOdEddnDyto8wJ9HqXMyt20vZ2PaCxWceMXdbacXbFwwpIQMAIOLSaxA7m6s2lIRF+gaGGAwESJKnHpNztx2pxHsxanpKtGtl0/5ffg+FjGGRQY0890Qz8ipqj/y/XYx454QM6J0Nr6uY6JJDlQ6bzZbbH9CGjAjlaNmm7KfL9xzHb/tywpxXPR+ClBpZQGEAAAAASUVORK5CYII=";

    private MirrorGateUtils() {
    }

    public static String getBase64image() {
        return BASE64IMAGE;
    }

    public static String convertObjectToJson(Object object) throws IOException {
        ObjectMapper mapper = new CustomObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    public static String getBuildUrl(Run<?, ?> run) throws UnsupportedEncodingException {
        return URLDecoder.decode(run.getParent().getAbsoluteUrl()
                + run.getNumber() + "/", "UTF-8");
    }

    public static String getBuildNumber(Run<?, ?> run) {
        return String.valueOf(run.getNumber());
    }

    public static String getMirrorGateAPIUrl() {
        return Jenkins.get().getDescriptorByType(
                MirrorGateRecorder.DescriptorImpl.class)
                .getMirrorGateAPIUrl();
    }

    public static String getExtraUrls() {
        return Jenkins.get().getDescriptorByType(
                MirrorGateRecorder.DescriptorImpl.class).getExtraURLs();
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
        String credentialsId = Jenkins.get().getDescriptorByType(
                MirrorGateRecorder.DescriptorImpl.class)
                .getMirrorgateCredentialsId();
        return CredentialsUtils.getJenkinsCredentials(
                credentialsId, UsernamePasswordCredentials.class);
    }

    public static List<String> getURLList() {

        String commaSeparatedList = getExtraUrls();
        String[] urlArray = commaSeparatedList.split(",");

        return Arrays.stream(urlArray)
                .map(String::trim)
                .filter(u -> !u.isEmpty())
                .collect(Collectors.toList());
    }

    public static void parseBuildUrl(String buildUrl, BuildDTO request) {
        List<String> keywords = new ArrayList<>();
        String[] buildInfo = buildUrl.split("/job/");
        String projectName = buildInfo[1].split("/")[0];

        request.setBuildUrl(buildUrl);
        request.setProjectName(projectName);
        keywords.add(buildUrl);
        keywords.add(projectName);

        if (buildInfo.length >= 3) {
            String repoName = buildInfo[buildInfo.length - 2].split("/")[0];
            String branch = buildInfo[buildInfo.length - 1].split("/")[0];
            request.setRepoName(repoName);
            request.setBranch(branch);
            keywords.add(repoName);
            keywords.add(branch);
        }

        request.setKeywords(keywords);
    }
}