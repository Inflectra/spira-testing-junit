
package com.inflectra.spiratest.addons.junitextension.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TestSet_CreateFolderResult" type="{http://schemas.datacontract.org/2004/07/Inflectra.SpiraTest.Web.Services.v3_0.DataObjects}RemoteTestSet" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "testSetCreateFolderResult"
})
@XmlRootElement(name = "TestSet_CreateFolderResponse")
public class TestSetCreateFolderResponse {

    @XmlElementRef(name = "TestSet_CreateFolderResult", namespace = "http://www.inflectra.com/SpiraTest/Services/v3.0/", type = JAXBElement.class)
    protected JAXBElement<RemoteTestSet> testSetCreateFolderResult;

    /**
     * Gets the value of the testSetCreateFolderResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RemoteTestSet }{@code >}
     *     
     */
    public JAXBElement<RemoteTestSet> getTestSetCreateFolderResult() {
        return testSetCreateFolderResult;
    }

    /**
     * Sets the value of the testSetCreateFolderResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RemoteTestSet }{@code >}
     *     
     */
    public void setTestSetCreateFolderResult(JAXBElement<RemoteTestSet> value) {
        this.testSetCreateFolderResult = ((JAXBElement<RemoteTestSet> ) value);
    }

}
