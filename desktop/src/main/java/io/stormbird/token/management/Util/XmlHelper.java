package io.stormbird.token.management.Util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.web3j.crypto.ECKeyPair;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;

public class XmlHelper {
    /**
     *
     * @param networkid
     * @param contractAddress
     */
    public static void updateContractAddress(String networkid, String contractAddress,
                                             InputStream ticketXMLTemplate, String ticketXMLFilePath){
        //update xml
        DocumentBuilder dBuilder;
        Document xml=null;
        Transformer transformer=null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            dBuilder = dbFactory.newDocumentBuilder();
            xml = dBuilder.parse(ticketXMLTemplate);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            FileHelper.createFileIfNotExists(ticketXMLFilePath);

            xml.getDocumentElement().normalize(); // also good for parcel
            NodeList nList = xml.getElementsByTagNameNS("http://attestation.id/ns/tbml","contract");
            Element contract = (Element) nList.item(0);

            if(contract.getAttribute("id").equals("holding_contract")) {
                nList = contract.getElementsByTagNameNS("http://attestation.id/ns/tbml", "address");
                int size = nList.getLength();
                for (int j = 0; j < size; j++) {
                    contract.removeChild(nList.item(0));
                }
                Element newNode = xml.createElement(contract.getPrefix()+":address");
                newNode.setAttribute("network", networkid);
                newNode.setTextContent(contractAddress);
                contract.appendChild(newNode);
            }
            DOMSource source = new DOMSource(xml);
            StreamResult result = new StreamResult(new File(ticketXMLFilePath));
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void signContractXML(String privateKey,String ticketXMLFilePath){
        DocumentBuilder dBuilder;
        Document xml=null;
        Transformer transformer=null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            dBuilder = dbFactory.newDocumentBuilder();
            xml = dBuilder.parse(new File(ticketXMLFilePath));

            ECKeyPair ecKeyPair = CryptoHelper.getECKeyPairFromPrivateKey(privateKey);
            KeyPair keyPair = CryptoHelper.generatePKCS8(ecKeyPair);
            PublicKey pk=keyPair.getPublic();
            ASN1StreamParser parser = new ASN1StreamParser(pk.getEncoded());
            DERSequence seq = (DERSequence) parser.readObject().toASN1Primitive();
            DERSequence innerSeq = (DERSequence) seq.getObjectAt(0).toASN1Primitive();
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) innerSeq.getObjectAt(1).toASN1Primitive();
            DERBitString key = (DERBitString) seq.getObjectAt(1).toASN1Primitive();

            Element ECKeyValue = xml.createElement("ds:ECDSAKeyValue");
            ECKeyValue.setAttribute("xmlns", "http://www.w3.org/2001/04/xmldsig-more#");
            Element DomainParameters = xml.createElement("ds:DomainParameters");
            Element NamedCurve = xml.createElement("ds:NamedCurve");
            NamedCurve.setAttribute("URI", "urn:oid:" + oid.getId());
            DomainParameters.appendChild(NamedCurve);
            ECKeyValue.appendChild(DomainParameters);

            Element PublicKey = xml.createElement("ds:PublicKey");
            Element PublicKeyX = xml.createElement("ds:X");
            PublicKeyX.setAttribute("Value", ((ECPublicKey) pk).getW().getAffineX().toString());
            Element PublicKeyY = xml.createElement("ds:Y");
            PublicKeyY.setAttribute("Value", ((ECPublicKey) pk).getW().getAffineY().toString());
//                PublicKey.setTextContent(Base64.encodeBase64String(key.getBytes()));
            PublicKey.appendChild(PublicKeyX);
            PublicKey.appendChild(PublicKeyY);
            ECKeyValue.appendChild(PublicKey);
        }catch (Exception e){

        }
    }
}
