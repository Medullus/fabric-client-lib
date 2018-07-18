package com.mhc.fabric.client.utils.channel;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.String.format;

public class ChannelUtils {
    private static Logger logger = Logger.getLogger(ChannelUtils.class);
    //use a cache service to cache channels, initialization of channel has a long latency TODO

    public ChannelUtils(){

    }

    public static Channel constructChannel(HFClient hfClient, NetworkConfig networkConfig, String channelName) throws InvalidArgumentException, NetworkConfigurationException, TransactionException {
        Channel channel;
        logger.debug("constructing channel");

        try {

            channel = hfClient.loadChannelFromConfig(channelName, networkConfig);
            if(channel == null){
                throw new NetworkConfigurationException("Channel "+channelName+" cannot be found.");
            }
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        } catch (NetworkConfigurationException e) {
            e.printStackTrace();
            logger.error(e);
            throw e;
        }

        if(channel.isInitialized()){
            return channel;
        }
        return channel.initialize();
    }

    //TODO build a blockinfo pojo and add to list
    public void blockWalker(HFClient client, Channel channel){
        try {
            BlockchainInfo channelInfo = channel.queryBlockchainInfo();

            for(long current = channelInfo.getHeight() -1 ; current > -1; --current){

                BlockInfo returnedBlock = channel.queryBlockByNumber(current);

                final long blockNumber = returnedBlock.getBlockNumber();

                out("current block number %d has data hash: %s", blockNumber, Hex.encodeHexString(returnedBlock.getDataHash()));
                out("current block number %d has previous hash id: %s", blockNumber, Hex.encodeHexString(returnedBlock.getPreviousHash()));
                out("current block number %d has calculated block hash is %s", blockNumber, Hex.encodeHexString(calculateBlockHash(client,
                        blockNumber, returnedBlock.getPreviousHash(), returnedBlock.getDataHash())));

                final int envelopeCount = returnedBlock.getEnvelopeCount();
                out("current block number %d has %d envelope count:", blockNumber, returnedBlock.getEnvelopeCount());

                int i = 0;
                for(BlockInfo.EnvelopeInfo envelopeInfo : returnedBlock.getEnvelopeInfos()){
                    ++i;

                    out("  Transaction number %d has transaction id: %s", i, envelopeInfo.getTransactionID());
                    final String channelId = envelopeInfo.getChannelId();

                    out("  Transaction number %d has channel id: %s", i, channelId);
                    out("  Transaction number %d has epoch: %d", i, envelopeInfo.getEpoch());
                    out("  Transaction number %d has transaction timestamp: %tB %<te,  %<tY  %<tT %<Tp", i, envelopeInfo.getTimestamp());
                    out("  Transaction number %d has type id: %s", i, "" + envelopeInfo.getType());
                    out("  Transaction number %d has nonce : %s", i, "" + Hex.encodeHexString(envelopeInfo.getNonce()));
                    out("  Transaction number %d has submitter mspid: %s,  certificate: %s", i, envelopeInfo.getCreator().getMspid(), envelopeInfo.getCreator().getId());

                    if(envelopeInfo.getType() == BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE){
                        BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

                        out("  Transaction number %d has %d actions", i, transactionEnvelopeInfo.getTransactionActionInfoCount());
                        out("  Transaction number %d isValid %b", i, transactionEnvelopeInfo.isValid());
                        out("  Transaction number %d validation code %d", i, transactionEnvelopeInfo.getValidationCode());

                        int j = 0;
                        for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                            ++j;
                            out("   Transaction action %d has response status %d", j, transactionActionInfo.getResponseStatus());
                            out("   Transaction action %d has response message bytes as string: %s", j,
                                    printableString(new String(transactionActionInfo.getResponseMessageBytes(), "UTF-8")));
                            out("   Transaction action %d has %d endorsements", j, transactionActionInfo.getEndorsementsCount());

                            for (int n = 0; n < transactionActionInfo.getEndorsementsCount(); ++n) {
                                BlockInfo.EndorserInfo endorserInfo = transactionActionInfo.getEndorsementInfo(n);
                                out("Endorser %d signature: %s", n, Hex.encodeHexString(endorserInfo.getSignature()));
                                out("Endorser %d endorser: mspid %s \n certificate %s", n, endorserInfo.getMspid(), endorserInfo.getId());
                            }
                            out("   Transaction action %d has %d chaincode input arguments", j, transactionActionInfo.getChaincodeInputArgsCount());
                            for (int z = 0; z < transactionActionInfo.getChaincodeInputArgsCount(); ++z) {
                                out("     Transaction action %d has chaincode input argument %d is: %s", j, z,
                                        printableString(new String(transactionActionInfo.getChaincodeInputArgs(z), "UTF-8")));
                            }

                            out("   Transaction action %d proposal response status: %d", j,
                                    transactionActionInfo.getProposalResponseStatus());
                            out("   Transaction action %d proposal response payload: %s", j,
                                    printableString(new String(transactionActionInfo.getProposalResponsePayload())));

                            // Check to see if we have our expected event.
                            if (blockNumber == 2) {
                                ChaincodeEvent chaincodeEvent = transactionActionInfo.getEvent();
//                                assertNotNull(chaincodeEvent);
//
//                                assertTrue(Arrays.equals(EXPECTED_EVENT_DATA, chaincodeEvent.getPayload()));
//                                assertEquals(testTxID, chaincodeEvent.getTxId());
//                                assertEquals(CHAIN_CODE_NAME, chaincodeEvent.getChaincodeId());
//                                assertEquals(EXPECTED_EVENT_NAME, chaincodeEvent.getEventName());

                            }
//
//                            TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
//                            if (null != rwsetInfo) {
//                                out("   Transaction action %d has %d name space read write sets", j, rwsetInfo.getNsRwsetCount());
//
//                                for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
//                                    final String namespace = nsRwsetInfo.getNamespace();
//                                    KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();
//
//                                    int rs = -1;
//                                    for (KvRwset.KVRead readList : rws.getReadsList()) {
//                                        rs++;
//
//                                        out("     Namespace %s read set %d key %s  version [%d:%d]", namespace, rs, readList.getKey(),
//                                                readList.getVersion().getBlockNum(), readList.getVersion().getTxNum());
//
//                                        if ("bar".equals(channelId) && blockNumber == 2) {
//                                            if ("example_cc_go".equals(namespace)) {
//                                                if (rs == 0) {
//                                                    assertEquals("a", readList.getKey());
//                                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                                    assertEquals(0, readList.getVersion().getTxNum());
//                                                } else if (rs == 1) {
//                                                    assertEquals("b", readList.getKey());
//                                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                                    assertEquals(0, readList.getVersion().getTxNum());
//                                                } else {
//                                                    fail(format("unexpected readset %d", rs));
//                                                }
//
//                                                TX_EXPECTED.remove("readset1");
//                                            }
//                                        }
//                                    }
//
//                                    rs = -1;
//                                    for (KvRwset.KVWrite writeList : rws.getWritesList()) {
//                                        rs++;
//                                        String valAsString = printableString(new String(writeList.getValue().toByteArray(), "UTF-8"));
//
//                                        out("     Namespace %s write set %d key %s has value '%s' ", namespace, rs,
//                                                writeList.getKey(),
//                                                valAsString);
//
//                                        if ("bar".equals(channelId) && blockNumber == 2) {
//                                            if (rs == 0) {
//                                                assertEquals("a", writeList.getKey());
//                                                assertEquals("400", valAsString);
//                                            } else if (rs == 1) {
//                                                assertEquals("b", writeList.getKey());
//                                                assertEquals("400", valAsString);
//                                            } else {
//                                                fail(format("unexpected writeset %d", rs));
//                                            }
//
//                                            TX_EXPECTED.remove("writeset1");
//                                        }
//                                    }
//                                }
                            }
                        }
                    }
                }


//            }
        } catch (ProposalException|InvalidArgumentException|IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] calculateBlockHash(HFClient client, long blockNumber, byte[] previousHash, byte[] dataHash) throws IOException, InvalidArgumentException {
        if (previousHash == null) {
            throw new InvalidArgumentException("previousHash parameter is null.");
        } else if (dataHash == null) {
            throw new InvalidArgumentException("dataHash parameter is null.");
        } else if (null == client) {
            throw new InvalidArgumentException("client parameter is null.");
        } else {
            CryptoSuite cryptoSuite = client.getCryptoSuite();
            if (null == client) {
                throw new InvalidArgumentException("Client crypto suite has not  been set.");
            } else {
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                DERSequenceGenerator seq = new DERSequenceGenerator(s);
                seq.addObject(new ASN1Integer(blockNumber));
                seq.addObject(new DEROctetString(previousHash));
                seq.addObject(new DEROctetString(dataHash));
                seq.close();
                return cryptoSuite.hash(s.toByteArray());
            }
        }
    }

    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }

    private String printableString(final String string) {
        int maxLogStringLength = 64;
        if (string == null || string.length() == 0) {
            return string;
        }

        String ret = string.replaceAll("[^\\p{Print}]", "?");

        ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");

        return ret;

    }
}
