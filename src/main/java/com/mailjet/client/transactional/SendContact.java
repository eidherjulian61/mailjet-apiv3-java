package com.mailjet.client.transactional;

public class SendContact {
    /**
     * Represents an object with email and name (optional) that can be used as To, Cc, Bcc, From etc
     * @param email email address
     * @param name the display name of the person (Will be displayed in email like NAME <EMAIL>
     */
    public SendContact(String email, String name) {
        this(email);
        Name = name;
    }

    /**
     * Represents an object with email that can be used as To, Cc, Bcc, From etc
     * @param email email address
     */
    public SendContact(String email) {
        Email = email;
    }

    private String Name;
    private String Email;
}
