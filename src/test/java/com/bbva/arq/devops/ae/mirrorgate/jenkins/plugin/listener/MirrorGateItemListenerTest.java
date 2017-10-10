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

package com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.service.MirrorGateService;
import com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.utils.MirrorGateResponse;
import com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.utils.MirrorGateUtils;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import java.util.Arrays;
import java.util.Collection;
import jenkins.model.Jenkins;
import jenkins.plugins.mirrorgate.MirrorGatePublisher;
import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Job.class, Jenkins.class, MirrorGateUtils.class})
public class MirrorGateItemListenerTest extends TestCase {

    @Mock
    Jenkins jenkins;

    @Mock
    MirrorGatePublisher.DescriptorImpl descriptor;

    @Mock
    Item item = mock(Item.class);

    @Mock
    MirrorGateService service = mock(MirrorGateService.class);

    @Spy
    MirrorGateItemListener listener = new MirrorGateItemListener();

    Job[] jobs = new Job[10];

    private final MirrorGateResponse responseOk
            = new MirrorGateResponse(HttpStatus.SC_CREATED, "");
    private final MirrorGateResponse responseError
            = new MirrorGateResponse(HttpStatus.SC_NOT_FOUND, "");

    private final static String MIRRORGATE_URL = "http://localhost:8080/mirrorgate";

    private final static String BUILD_SAMPLE = "http://localhost:8080/job/MirrorGate"
            + "/job/mirrorgate-jenkins-builds-collector/job/test/5/";

    private static final String EXTRA_URL = "http://localhost:8080/test, http://localhost:8080/test2,   ";

    @Before
    @Override
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
        PowerMockito.when(jenkins.getDescriptorByType(any()))
                .thenReturn(descriptor);

        PowerMockito.when(MirrorGateUtils.getMirrorGateAPIUrl())
                .thenReturn(MIRRORGATE_URL);
        PowerMockito.when(MirrorGateUtils.getUsernamePasswordCredentials())
                .thenReturn(null);
        PowerMockito.when(MirrorGateUtils.getExtraUrls())
            .thenReturn(EXTRA_URL);



        Arrays.fill(jobs, createMockingJob());

        when(item.getAllJobs()).thenReturn((Collection) Arrays.asList(jobs));
        when(listener.getMirrorGateService()).thenReturn(service);
    }

    @Test
    public void onDeletedTestWhenServiceResponseOK() {
        when(service.publishBuildData(any())).thenReturn(responseOk);
        when(service.sendBuildDataToExtraEndpoints(any(), any())).thenReturn(responseOk);

        listener.onDeleted(item);

        verify(service, times(jobs.length)).publishBuildData(any());
    }

    @Test
    public void onDeletedTestWhenServiceResponseError() {
        when(service.publishBuildData(any())).thenReturn(responseError);
        when(service.sendBuildDataToExtraEndpoints(any(), any())).thenReturn(responseError);

        listener.onDeleted(item);

        verify(service, times(jobs.length)).publishBuildData(any());
    }

    private Job createMockingJob() {
        Job job = PowerMockito.mock(Job.class);
        Run build = mock(Run.class);

        when(job.getLastBuild()).thenReturn(build);
        PowerMockito.when(job.getAbsoluteUrl()).thenReturn(BUILD_SAMPLE);
        when(build.getParent()).thenReturn(job);

        return job;
    }
}

