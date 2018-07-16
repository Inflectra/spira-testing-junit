
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
 *         &lt;element name="System_GetSettingsResult" type="{http://schemas.datacontract.org/2004/07/Inflectra.SpiraTest.Web.Services.v3_0.DataObjects}ArrayOfRemoteSetting" minOccurs="0"/>
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
    "systemGetSettingsResult"
})
@XmlRootElement(name = "System_GetSettingsResponse")
public class SystemGetSettingsResponse {

    @XmlElementRef(name = "System_GetSettingsResult", namespace = "http://www.inflectra.com/SpiraTest/Services/v3.0/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfRemoteSetting> systemGetSettingsResult;

    /**
     * Gets the value of the systemGetSettingsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRemoteSetting }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRemoteSetting> getSystemGetSettingsResult() {
        return systemGetSettingsResult;
    }

    /**
     * Sets the value of the systemGetSettingsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRemoteSetting }{@code >}
     *     
     */
    public void setSystemGetSettingsResult(JAXBElement<ArrayOfRemoteSetting> value) {
        this.systemGetSettingsResult = ((JAXBElement<ArrayOfRemoteSetting> ) value);
    }

}
