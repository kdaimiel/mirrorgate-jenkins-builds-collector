package jenkins.plugins.mirrorgate;

import com.bbva.arq.devops.ae.mirrorgate.core.model.BuildDataCreateRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpStatus;

import mirrorgate.utils.MirrorGateUtils;

//import org.json.simple.JSONArray;

public class DefaultMirrorGateService implements MirrorGateService {

    private static final Logger LOG = Logger.getLogger(DefaultMirrorGateService.class.getName());

    private String mirrorGateAPIUrl = "";
    private boolean useProxy = false;


    public DefaultMirrorGateService(String mirrorGateAPIUrl, boolean useProxy) {
        super();
        this.mirrorGateAPIUrl = mirrorGateAPIUrl;
        this.useProxy = useProxy;
    }

    public void setMirrorGateAPIUrl(String mirrorGateAPIUrl) {
        this.mirrorGateAPIUrl = mirrorGateAPIUrl;
    }

    @Override
    public MirrorGateResponse publishBuildData(BuildDataCreateRequest request) {
        String responseValue;
        int responseCode = HttpStatus.SC_NO_CONTENT;
        try {
            String jsonString = new String(MirrorGateUtils.convertObjectToJsonBytes(request));
            RestCall restCall = new RestCall(useProxy);
            RestCall.RestCallResponse callResponse = restCall.makeRestCallPost(mirrorGateAPIUrl + "/build", jsonString);
            responseCode = callResponse.getResponseCode();
            responseValue = callResponse.getResponseString().replaceAll("\"", "");
            if (responseCode != HttpStatus.SC_CREATED) {
                LOG.log(Level.SEVERE, "mirrorGate: Build Publisher post may have failed. Response: {0}", responseCode);
            }
            return new MirrorGateResponse(responseCode, responseValue);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "mirrorGate: Error posting to mirrorGate", e);
            responseValue = "";
        }

        return new MirrorGateResponse(responseCode, responseValue);
    }

    @Override
    public boolean testConnection() {
        RestCall restCall = new RestCall(useProxy);
        RestCall.RestCallResponse callResponse = restCall.makeRestCallGet(mirrorGateAPIUrl + "/health");
        int responseCode = callResponse.getResponseCode();

        if (responseCode == HttpStatus.SC_OK) return true;

        LOG.log(Level.WARNING, "mirrorGate Test Connection Failed. Response: {0}", responseCode);
        return false;
    }
}