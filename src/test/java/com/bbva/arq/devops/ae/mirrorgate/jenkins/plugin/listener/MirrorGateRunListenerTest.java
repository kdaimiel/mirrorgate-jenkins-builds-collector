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

import com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.service.MirrorGateService;
import com.bbva.arq.devops.ae.mirrorgate.jenkins.plugin.utils.MirrorGateResponse;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Job.class})
public class MirrorGateRunListenerTest extends TestCase {
    
    @Mock
    Run build;

    @Mock
    MirrorGateService service;
    
    @Spy
    MirrorGateRunListener listener = new MirrorGateRunListener();
    
    private final MirrorGateResponse responseOk = new MirrorGateResponse(HttpStatus.SC_CREATED, "");
    private final MirrorGateResponse responseError = new MirrorGateResponse(HttpStatus.SC_NOT_FOUND, "");

    private final String buildSample = "http://localhost:8080/job/MirrorGate/job/mirrorgate-jenkins-plugin/job/test/5/";

    @Before
    @Override
    public void setUp() {
        build = createMockingBuild();
        service = mock(MirrorGateService.class);

        when(listener.getMirrorGateService()).thenReturn(service);
    }
        
    @Test
    public void onStartedBuildTest() {
        when(service.publishBuildData(any())).thenReturn(responseOk);
        
        listener.onStarted(build, TaskListener.NULL);
        
        verify(service, times(1)).publishBuildData(any());
    }
    
        @Test
    public void onStartedBuildTestWhenServiceResponseError() {
        when(service.publishBuildData(any())).thenReturn(responseError);
        
        listener.onStarted(build, TaskListener.NULL);
        
        verify(service, times(1)).publishBuildData(any());
    }

    @Test
    public void onCompletedSuccessBuildTest() {
        when(service.publishBuildData(any())).thenReturn(responseOk);
        when(build.getResult()).thenReturn(Result.SUCCESS);

        listener.onCompleted(build, TaskListener.NULL);
        
        verify(service, times(1)).publishBuildData(any());
    }
    
    @Test
    public void onCompletedFailureBuildTest() {
        when(service.publishBuildData(any())).thenReturn(responseOk);
        when(build.getResult()).thenReturn(Result.FAILURE);

        listener.onCompleted(build, TaskListener.NULL);
        
        verify(service, times(1)).publishBuildData(any());
    }
    
    @Test
    public void onCompletedBuildWhenTestServiceResponseError() {
        when(service.publishBuildData(any())).thenReturn(responseError);
        when(build.getResult()).thenReturn(Result.FAILURE);

        listener.onCompleted(build, TaskListener.NULL);
        
        verify(service, times(1)).publishBuildData(any());
    }
    
    @Test
    public void onDeletedBuildTest() {
        listener.onDeleted(build);
        
        /* Should not do anything */
        verify(service, times(0));    
    }
    
    @Test
    public void onFinalizedBuildTest() {
        listener.onDeleted(build);
        
        /* Should not do anything */
        verify(service, times(0));    
    }
    
    private Run createMockingBuild() {
        Job job = PowerMockito.mock(Job.class);
        Run run = mock(Run.class);
        
        when(job.getLastBuild()).thenReturn(run);
        PowerMockito.when(job.getAbsoluteUrl()).thenReturn(buildSample);
        when(run.getParent()).thenReturn(job);
        
        return run;
    }
    
}

