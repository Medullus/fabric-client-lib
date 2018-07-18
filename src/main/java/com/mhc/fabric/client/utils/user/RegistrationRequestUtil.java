package com.mhc.fabric.client.utils.user;

import com.mhc.fabric.client.config.FabricConfig;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_MAXENROLLMENT;


/**
 * HFCA_TYPE_PEER indicates that an identity is acting as a peer
 */
//public static final String HFCA_TYPE_PEER = "peer";
/**
 * HFCA_TYPE_ORDERER indicates that an identity is acting as an orderer
 */
//public static final String HFCA_TYPE_ORDERER = "orderer";
/**
 * HFCA_TYPE_CLIENT indicates that an identity is acting as a client
 */
//public static final String HFCA_TYPE_CLIENT = "client";
/**
 * HFCA_TYPE_USER indicates that an identity is acting as a user
 */
//public static final String HFCA_TYPE_USER = "user";


public class RegistrationRequestUtil {

    public static RegistrationRequest getMemberRR(String name, String pw, String affiliation, FabricConfig fabricConfig) throws Exception {
        RegistrationRequest rr = new RegistrationRequest(name, affiliation);
        rr.setSecret(pw);
        rr.setMaxEnrollments(Integer.parseInt(fabricConfig.getProperty(MHC_FABRIC_MAXENROLLMENT)));//TODO edit here to get from properties
        rr.setType(HFCAClient.HFCA_TYPE_CLIENT);
        rr.addAttribute(new Attribute("new", "money"));//TODO edit here to get attributes from properties
        return rr;
    }

    public static RegistrationRequest getPeerRR(String name, String pw, String affiliation, FabricConfig fabricConfig) throws Exception{
        RegistrationRequest rr = new RegistrationRequest(name, affiliation);
        rr.setSecret(pw);
        rr.setMaxEnrollments(Integer.parseInt(fabricConfig.getProperty(MHC_FABRIC_MAXENROLLMENT)));//TODO edit here to get from properties
        rr.setType(HFCAClient.HFCA_TYPE_PEER);
        rr.addAttribute(new Attribute("new", "money"));//TODO edit here to get attributes from properties
        return rr;
    }

    public static RegistrationRequest getOrdererRR(String name, String pw, String affiliation, FabricConfig fabricConfig) throws Exception {
        RegistrationRequest rr = new RegistrationRequest(name, affiliation);
        rr.setSecret(pw);
        rr.setMaxEnrollments(Integer.parseInt(fabricConfig.getProperty(MHC_FABRIC_MAXENROLLMENT)));//TODO edit here to get from properties
        rr.setType(HFCAClient.HFCA_TYPE_ORDERER);
        rr.addAttribute(new Attribute("new", "money"));//TODO edit here to get attributes from properties
        return rr;
    }
}
