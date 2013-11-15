/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 *       Copyright 2013 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import java.util.LinkedList;

import org.forgerock.contactmanager.ContactActivity.SECTION;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

/**
 * This class represents a contact which is actually a full representation of the json config file.
 */
public class Contact {

    private String name;
    private String surname;
    private String displayName;
    private LinkedList<String> homePhoneNumbers = new LinkedList<String>();
    private LinkedList<String> mobilePhoneNumbers = new LinkedList<String>();
    private LinkedList<String> mails = new LinkedList<String>();
    private String address;
    private String postalCode;
    private String location;
    private String state;
    private String function;
    private String organization;
    private LinkedList<String[]> managers = new LinkedList<String[]>();

    private JSONObject contactDetails;
    private JSONObject contactAddress;
    private JSONObject contact;

    private Bitmap photo;
    private String photoLink;

    /**
     * Constructor.
     */
    public Contact() {
        // Default constructor.
    }

    /**
     * Returns the name of the contact.
     *
     * @return The name of the contact.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the contact.
     *
     * @param name
     *            The name of the contact.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the surname of the contact.
     *
     * @return The surname of the contact.
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the contact.
     *
     * @param surname
     *            The surname of the contact.
     */
    public void setSurname(final String surname) {
        this.surname = surname;
    }

    /**
     * Returns all home phone numbers of the contact.
     *
     * @return The home phone numbers of the contact.
     */
    public LinkedList<String> getHomePhoneNumbers() {
        return homePhoneNumbers;
    }

    /**
     * Sets the home phone numbers of this contact.
     *
     * @param homePhoneNumbers
     *            The home phone numbers of this contact.
     */
    public void setHomePhoneNumbers(final LinkedList<String> homePhoneNumbers) {
        this.homePhoneNumbers = homePhoneNumbers;
    }

    /**
     * Returns all mobile phone numbers of the contact.
     *
     * @return The mobile phone numbers of the contact.
     */
    public LinkedList<String> getMobilePhoneNumbers() {
        return mobilePhoneNumbers;
    }

    /**
     * Sets the mobile phone numbers of this contact.
     *
     * @param mobilePhoneNumbers
     *            The mobile phone numbers of this contact.
     */
    public void setMobilePhoneNumbers(final LinkedList<String> mobilePhoneNumbers) {
        this.mobilePhoneNumbers = mobilePhoneNumbers;
    }

    /**
     * Returns the mails of this contact.
     *
     * @return The e-mail addresses of this contact.
     */
    public LinkedList<String> getMails() {
        return mails;
    }

    /**
     * Sets the e-mails addresses of this contact.
     *
     * @param mails
     *            The e-mail addresses of this contact.
     */
    public void setMails(final LinkedList<String> mails) {
        this.mails = mails;
    }

    /**
     * Returns the postal address.
     *
     * @return The postal address line.
     */
    public String getAddress() {
        if (!TextUtils.isEmpty(address)) {
            return address;
        }
        return "";
    }

    /**
     * Sets the postal address line.
     *
     * @param address
     *            The postal adress line.
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * Returns the postal code.
     *
     * @return The postal code.
     */
    public String getPostalCode() {
        if (!TextUtils.isEmpty(postalCode)) {
            return postalCode;
        }
        return "";
    }

    /**
     * Sets the postal code.
     *
     * @param postalCode
     *            The postal code.
     */
    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Returns the postal location.
     *
     * @return The postal location.
     */
    public String getLocation() {
        if (!TextUtils.isEmpty(location)) {
            return location;
        }
        return "";
    }

    /**
     * Sets the postal location.
     *
     * @param location
     *            The postal location.
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * Returns the postal state.
     *
     * @return The postal state.
     */
    public String getState() {
        if (!TextUtils.isEmpty(state)) {
            return state;
        }
        return "";
    }

    /**
     * Sets the postal state.
     *
     * @param state
     *            The postal state.
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * Returns the full postal address containing address, postal code, location and state.
     *
     * @return The full postal address.
     */
    public String getFullAddress() {
        if (contactAddress != null) {
            return new StringBuilder(getAddress()).append(" \n").append(getPostalCode()).append(" ")
                    .append(getLocation()).append(" ").append(getState()).toString();
        }
        return "";
    }

    /**
     * Returns the function.
     *
     * @return The function.
     */
    public String getFunction() {
        return function;
    }

    /**
     * Sets the contact's function.
     *
     * @param function
     *            The contact's function.
     */
    public void setFunction(final String function) {
        this.function = function;
    }

    /**
     * Returns the organization.
     *
     * @return The organization.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the organization.
     *
     * @param organization
     *            The organization to set.
     */
    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    /**
     * Returns the current linked photo.
     *
     * @return Contact's picture.
     */
    public Bitmap getPhoto() {
        return photo;
    }

    /**
     * Sets the contact picture.
     *
     * @param photo
     *            The picture linked to this contact.
     */
    public void setPhoto(final Bitmap photo) {
        this.photo = photo;
    }

    /**
     * Returns the display name.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName
     *            The display name.
     */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a list of managers.
     *
     * @return The list of managers.
     */
    public LinkedList<String[]> getManagers() {
        return managers;
    }

    /**
     * Sets the list of managers.
     *
     * @param managers
     *            A list of managers.
     */
    public void setManagers(final LinkedList<String[]> managers) {
        this.managers = managers;
    }

    /**
     * Returns the value of the photo.
     *
     * @return The value stored in the photo field.
     */
    public String getPhotoLink() {
        return photoLink;
    }

    /**
     * Fills the contact object by reading the associated JSON result.
     *
     * @param jsonContact
     *            The JSON representation of this contact.
     * @throws JSONException
     *             If an exception occurs.
     */
    void fillDetailsFromJSON(final String jsonContact) throws JSONException {
        contact = new JSONObject(jsonContact);

        this.setDisplayName(contact.optString(MapperConstants.DISPLAY_NAME));
        this.setName(contact.optString(MapperConstants.GIVEN_NAME));
        this.setSurname(contact.optString(MapperConstants.FAMILY_NAME));

        this.contactDetails = contact.optJSONObject(MapperConstants.CONTACT_INFORMATION);
        if (contactDetails != null) {
            this.setFunction(contactDetails.optString(MapperConstants.DESCRIPTION));
            this.setOrganization(contactDetails.optString(MapperConstants.ORGANIZATION));

            this.photoLink = !TextUtils.isEmpty(contactDetails.optString(MapperConstants.JPEGPHOTO)) ? contactDetails
                    .optString(MapperConstants.JPEGPHOTO) : contactDetails.optString(MapperConstants.JPEGURL);
        }

        this.setMobilePhoneNumbers(getSubValuesFromJson(SECTION.MOBILE_PHONE));
        this.setHomePhoneNumbers(getSubValuesFromJson(SECTION.HOME_PHONE));
        this.setMails(getSubValuesFromJson(SECTION.MAIL));
        this.setManagers(getManagerFromJSONContact());

        this.contactAddress = contact.optJSONObject(MapperConstants.CONTACT_ADDRESS);
        if (contactAddress != null) {
            this.setAddress(contactAddress.optString(MapperConstants.POSTAL_ADDRESS));
            this.setPostalCode(contactAddress.optString(MapperConstants.POSTAL_CODE));
            this.setLocation(contactAddress.optString(MapperConstants.LOCATION));
            this.setState(contactAddress.optString(MapperConstants.STATE));
        }

    }

    /**
     * Retrieves the managers of the current contact.
     *
     * @return A list of managers.
     */
    LinkedList<String[]> getManagerFromJSONContact() {
        final LinkedList<String[]> extractedValues = new LinkedList<String[]>();
        final JSONArray pDetails = contact.optJSONArray(MapperConstants.MANAGER);
        if (pDetails != null) {
            try {
                for (int i = 0; i < pDetails.length(); i++) {
                    final String[] details = new String[2];
                    details[0] = pDetails.getJSONObject(i).optString(MapperConstants.ID);
                    details[1] = pDetails.getJSONObject(i).optString(MapperConstants.DISPLAY_NAME);
                    extractedValues.add(details);
                }
            } catch (final JSONException e) {
                Log.e("Contact:", e.getMessage());
            }
        }
        return extractedValues;
    }

    /**
     * Retrieves all sub values linked to a section.
     *
     * @param section
     *            The selected section.
     * @return A list of sub values linked to a section.
     */
    LinkedList<String> getSubValuesFromJson(final SECTION section) {
        final LinkedList<String> extractedValues = new LinkedList<String>();
        JSONArray pDetails = null;
        if (section == SECTION.MOBILE_PHONE && contactDetails != null) {
            pDetails = contactDetails.optJSONArray(MapperConstants.MOBILE_NUMBER);
        } else if (section == SECTION.HOME_PHONE && contactDetails != null) {
            pDetails = contactDetails.optJSONArray(MapperConstants.TELEPHONE_NUMBER);
        } else if (section == SECTION.MAIL && contactDetails != null) {
            pDetails = contactDetails.optJSONArray(MapperConstants.EMAIL_ADDRESS);
        } /*
           * else if (section == SECTION.ADDRESS) { final String address = getAddressFromJSON(); if
           * (!TextUtils.isEmpty(address)) { extractedValues.add(address); return extractedValues; } return null; }
           */
        if (pDetails != null) {
            for (int i = 0; i < pDetails.length(); i++) {
                try {
                    extractedValues.add(pDetails.get(i).toString());
                } catch (final JSONException e) {
                    Log.e("getSubValuesFromJson:", e.getMessage());
                }
            }
        }
        return extractedValues;
    }

    /**
     * Returns the JSON object representing this contact.
     *
     * @return A JSON Object representation of this contact.
     */
    public JSONObject getContact() {
        return contact;
    }
}
