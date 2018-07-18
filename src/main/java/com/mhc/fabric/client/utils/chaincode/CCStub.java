package com.mhc.fabric.client.utils.chaincode;

import com.mhc.fabric.client.config.FabricConfig;
import com.mhc.fabric.client.models.ChaincodeInfo;
import com.mhc.fabric.client.utils.channel.ChannelUtils;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;

import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_PROPOSALWAITTIME;
import static com.mhc.fabric.client.config.FabricConfigParams.MHC_FABRIC_TRANSACTIONWAITTIME;

public class CCStub {
    static Logger logger = Logger.getLogger(CCStub.class);

    private FabricConfig fabricConfig;
    private NetworkConfig networkConfig;

    public CCStub(FabricConfig fabricConfig, NetworkConfig networkConfig){
        this.fabricConfig = fabricConfig;
        this.networkConfig = networkConfig;
    }

    /**
     * Return the string payload
     * **/
    public String query(HFClient caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo) throws NetworkConfigurationException, InvalidArgumentException, ProposalException, TransactionException {
        Collection<ProposalResponse> queryProposals;
        String payLoad;
        logger.debug(String.format("Enter query with fcn %s, caller %s%n args = %s%nchaincodeInfo: %s", fcn, caller.getUserContext().getName(), Arrays.toString(args), chaincodeInfo.toString()));

        final ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(chaincodeInfo.getCcName())
                .setVersion(chaincodeInfo.getCcVersion())
                .build();

        Channel channel = getChannelFromConfig(caller, chaincodeInfo.getChannelName());
        logger.debug("Channel successfully initialized");

        QueryByChaincodeRequest queryByChaincodeRequest = getQueryCCReq(caller.newQueryProposalRequest(), fcn, args, chaincodeID);

        queryProposals = getPeersQueryResponse(queryByChaincodeRequest, channel);

        logger.debug("Peers returned proposal");

        queryProposals = checkResponses(queryProposals);
        logger.debug("checked proposal passed");

        payLoad = queryProposals.iterator().next().getProposalResponse().getResponse().getPayload().toStringUtf8();
        logger.debug("Returning payload");
        logger.debug(payLoad);
        return payLoad;
    }

    /**
     * Return the txId
     * Null if error
     * **/
    public CompletableFuture<String> invoke(HFClient caller, String fcn, String[] args, ChaincodeInfo chaincodeInfo) throws NetworkConfigurationException, InvalidArgumentException, ProposalException, TransactionException {
        Collection<ProposalResponse> responses;

        logger.debug(String.format("Entered invoke with fcn %s%n args : %s%n caller : %s%n %s", fcn, Arrays.toString(args), caller.getUserContext().getName(), chaincodeInfo.toString()));

        logger.debug("Getting channel from config");
        Channel channel = getChannelFromConfig(caller, chaincodeInfo.getChannelName());
        logger.debug("Successfully obtain channel from config");

        final ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(chaincodeInfo.getCcName())
                .setVersion(chaincodeInfo.getCcVersion())
                .build();

        TransactionProposalRequest transactionProposalRequest = getTransactionRequests(caller.newTransactionProposalRequest(), fcn, args, chaincodeID);

        logger.debug("Sending off Transaction proposal request");
        responses = getPeersTransactionResponse(transactionProposalRequest, channel);

        logger.debug("check of responses are successful");
        responses = checkResponses(responses);

        logger.debug("responses successful");


        return broadcastTransaction(responses, channel);
    }

    private CompletableFuture<String> broadcastTransaction(Collection<ProposalResponse> responses, Channel channel){
        return channel.sendTransaction(responses)
                .thenApply(BlockEvent.TransactionEvent::<String>getTransactionID)
                .handle((tranEvent, ex) ->{
                    if(ex != null){
                        logger.error(ex);
                        return null;
                    }
                    return tranEvent;
                });
//        return channel.sendTransaction(responses)
//                .thenApply(transactionEvent -> {
//                    if(transactionEvent.isValid()){
//                        return transactionEvent.getTransactionID();
//                    }else{
//                        return new CompletionException(new Exception("Transaction no valid"));
//                    }
//                }).exceptionally(e ->{
//                    if(e instanceof TransactionEventException){
//                        BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
//                        if(te != null){
//                            CompletableFuture<Serializable> ret = new CompletableFuture<>();
//                            String msg = String.format("Transaction with TXID %s failed with message %s", te.getTransactionID(), e.getMessage());
//                            logger.error(msg);
//                            return ret.completeExceptionally(new Exception(msg));
//                        }
//                    }
//                    return new CompletionException(new Exception(String.format("Failed with %s exception %s", e.getClass().getName(), e.getMessage())));
//
//                });
    }

    private Collection<ProposalResponse> getPeersTransactionResponse(TransactionProposalRequest transactionProposalRequest, Channel channel) throws ProposalException, InvalidArgumentException {
        Collection<ProposalResponse> resp;
        try {
            resp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        } catch (ProposalException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }
        logger.debug("Successfully recieved responses from peers");
        return resp;
    }

    private TransactionProposalRequest getTransactionRequests(TransactionProposalRequest tpr,String fcn, String[] args, ChaincodeID chaincodeID){
        logger.debug("Creating TransactionProposalRequest");
        tpr.setFcn(fcn);
        tpr.setArgs(args);
        tpr.setChaincodeID(chaincodeID);
        tpr.setProposalWaitTime(Integer.parseInt(fabricConfig.getProperty(MHC_FABRIC_PROPOSALWAITTIME)));
        logger.debug(tpr.toString());
        return tpr;
    }


    /**TODO this can be checked towards policy, implement a generic version towards policy or use default(below impl is default)
     * **/
    private Collection<ProposalResponse> checkResponses(Collection<ProposalResponse> responses ) throws ProposalException {
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        logger.debug("Checking responses");
        for(ProposalResponse response : responses){
            String txId = response.getTransactionID();
            if(response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS){
                logger.debug(String.format("Successful transaction proposal response TXID: %s from peer %s%n", txId, response.getPeer().getName()));
                successful.add(response);
            }else{
                logger.debug(String.format("Failed transaction proposal response TXID: %s from peer %s%n", txId, response.getPeer().getName()));
                failed.add(response);
            }
        }
        logger.debug("finished iterating through responses");
        if(failed.isEmpty()){
            logger.debug("Zero failed responses, returning all successful responses");
            return successful;
        }
        logger.debug("failed response detected");

        StringBuilder sb = new StringBuilder();
        for(ProposalResponse failedResponse: failed){
            sb.append(String.format("Failed for peer %s with message %s:", failedResponse.getPeer().getName(), failedResponse.getMessage()));
        }
        try {
            throw new ProposalException(sb.toString());
        } catch (ProposalException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }
    }

    private Collection<ProposalResponse> getPeersQueryResponse( QueryByChaincodeRequest queryByChaincodeRequest, Channel channel) throws InvalidArgumentException, ProposalException {
        Collection<ProposalResponse> responses;
        logger.debug("Sending off QueryByChaincodeRequest: getPeersQueryResponse");
        try {
            responses = channel.queryByChaincode(queryByChaincodeRequest);
        } catch (InvalidArgumentException|ProposalException e) {
            e.printStackTrace();
            logger.debug(e);
            throw e;
        }
        return responses;
    }

    private Channel getChannelFromConfig(HFClient hfClient, String channelName) throws NetworkConfigurationException, InvalidArgumentException, TransactionException {
        logger.debug("getChannelFromConfig");
        try {
            return ChannelUtils.constructChannel(hfClient, networkConfig, channelName);
        } catch (TransactionException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }
    }

    private QueryByChaincodeRequest getQueryCCReq(QueryByChaincodeRequest queryByChaincodeRequest, String fcn, String[] args, ChaincodeID chaincodeID){
        logger.debug(String.format("creating new QueryByChaincodeRequest %n fcn = %s %nargs = %s", fcn, Arrays.toString(args)));
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(fcn);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);
        logger.debug(queryByChaincodeRequest.toString());
        return queryByChaincodeRequest;
    }

}
