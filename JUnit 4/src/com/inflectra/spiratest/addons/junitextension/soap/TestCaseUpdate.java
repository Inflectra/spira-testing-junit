
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
 *         &lt;element name="remoteTestCase" type="{http://schemas.datacontract.org/2004/07/Inflectra.SpiraTest.Web.Services.v3_0.DataObjects}RemoteTestCase" minOccurs="0"/>
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
    "remoteTestCase"
})
@XmlRootElement(name = "TestCase_Update")
public class TestCaseUpdate {

    @XmlElementRef(name = "remoteTestCase", namespace = "http://www.inflectra.com/SpiraTest/Services/v3.0/", type = JAXBElement.class)
    protected JAXBElement<RemoteTestCase> remoteTestCase;

    /**
     * Gets the value of the remoteTestCase property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RemoteTestCase }{@code >}
     *     
     */
    public JAXBElement<RemoteTestCase> getRemoteTestCase() {
        return remoteTestCase;
    }

    /**
     * Sets the value of the remoteTestCase property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RemoteTestCase }{@code >}
     *     
     */
    public void setRemoteTestCase(JAXBElement<RemoteTestCase> value) {
        this.remoteTestCase = ((JAXBElement<RemoteTestCase> ) value);
    }

}
