package com.mhc.fabric.client.utils.user;

import com.mhc.fabric.client.config.FabricConfig;
import com.mhc.fabric.client.models.SampleStore;
import com.mhc.fabric.client.models.SampleUser;
import com.mhc.fabric.client.utils.TestUtil;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_NETWORKCONFIG;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_ORGAFFILIATION;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_STORETABLENAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UsersUtilsIT {

    final String secret = "defaultpw";
    final String userName = "testUser";

    NetworkConfig networkConfig;
    FabricConfig fabricConfig;
    SampleStore sampleStore;

    UserUtils userUtils;

    @Before
    public void begin() throws NetworkConfigurationException, IOException, InvalidArgumentException {
        setup();
        this.userUtils = new UserUtils(fabricConfig, networkConfig, sampleStore);
    }


    @Test
    public void testRegisterRandomUser() throws Exception {
        Random random = new Random();

        String ranUser = userName+random.nextInt();
        HFCAClient hfcaClient = getHFCAClient();
        final String affiliation = fabricConfig.getProperty(MHC_FABRIC_ORGAFFILIATION);

        SampleUser testuser = userUtils.registerUser(ranUser, secret, affiliation);

        assertEquals(secret, testuser.getEnrollmentSecret());

    }

    @Test
    public void testEnrollRandomUser() throws Exception {
        Random random = new Random();

        String ranUser = userName+random.nextInt();
        HFCAClient hfcaClient = getHFCAClient();
        final String affiliation = fabricConfig.getProperty(MHC_FABRIC_ORGAFFILIATION);

        SampleUser testuser = userUtils.registerUser(ranUser, secret, affiliation);
        assertNotNull(userUtils.enrollUser(testuser));
    }

    @Test
    public void testgetAdmin() throws org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, EnrollmentException, MalformedURLException {
        SampleUser admin = userUtils.getAdmin();

        assertNotNull(admin.getEnrollment());
    }

    @Test
    public void testCAInfoHasAdminCredentials(){
        NetworkConfig.CAInfo caInfo = networkConfig.getClientOrganization().getCertificateAuthorities().get(0);
        NetworkConfig.UserInfo userInfo = caInfo.getRegistrars().iterator().next();
        assertEquals("adminpw", userInfo.getEnrollSecret());
        assertEquals("admin", userInfo.getName());
    }

    @Test
    public void testHFCAClient() throws MalformedURLException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InfoException {
        NetworkConfig.CAInfo caInfo = networkConfig.getClientOrganization().getCertificateAuthorities().get(0);
        HFCAClient hfcaClient = userUtils.getHFCAClient(caInfo);
        assertNotNull(hfcaClient);
        assertEquals(hfcaClient.getCAName(), caInfo.getCAName());
    }

    @Test
    public void testHFCAClientConnection() throws org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InfoException, MalformedURLException {
        HFCAClient hfcaClient = getHFCAClient();
        assertEquals("Test integration to fabric-ca server", hfcaClient.info().getVersion(), "1.1.0");//test Integration

    }

    private HFCAClient getHFCAClient() throws MalformedURLException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
        NetworkConfig.CAInfo caInfo = networkConfig.getClientOrganization().getCertificateAuthorities().get(0);
        HFCAClient hfcaClient = userUtils.getHFCAClient(caInfo);
        return hfcaClient;
    }

    private void setup() throws IOException, NetworkConfigurationException, InvalidArgumentException {

        this.fabricConfig = TestUtil.getFabricConfig();

        Resource resource = new ClassPathResource(fabricConfig.getProperty(MHC_FABRIC_NETWORKCONFIG));
        this.networkConfig = NetworkConfig.fromJsonStream(resource.getInputStream());

        //TODO modify after repository package is complete
        File file = new File(fabricConfig.getProperty(MHC_FABRIC_STORETABLENAME));
        this.sampleStore = new SampleStore(file);
    }
}
