package com.mailjet.client;

import com.mailjet.client.errors.MailjetClientRequestException;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.sms.SmsSend;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SendSmsIT {

    @Test
    public void sendSms_UnsupportedCountry_ThrowsMailjetException() {
        // arrange
        MailjetClient mailjetClient = new MailjetClient(ClientOptions
                .builder()
                .bearerAccessToken(System.getenv("MJ_APITOKEN"))
                .build());

        String ukrainePhoneNumber = "+380507363100";

        MailjetRequest mailjetRequest = new MailjetRequest(SmsSend.resource)
                .property(SmsSend.FROM, "MJPilot")
                .property(SmsSend.TO, ukrainePhoneNumber)
                .property(SmsSend.TEXT, "Have a nice SMS flight with Mailjet!");

        // act
        MailjetClientRequestException requestException = Assert.assertThrows(MailjetClientRequestException.class, () -> mailjetClient.post(mailjetRequest));

        // assert
        Assert.assertEquals(401, requestException.getStatusCode());
        Assert.assertTrue(requestException.getMessage().endsWith("\"ErrorCode\":\"sms-0002\",\"StatusCode\":400,\"ErrorMessage\":\"Unsupported country code.\",\"ErrorRelatedTo\":[\"To\"]}"));
    }

    @Test
    @Ignore("This test will send the real sms")
    public void sendSms_SupportedCountry_ReturnsSuccessResponse() throws MailjetException {
        // arrange
        MailjetClient mailjetClient = new MailjetClient(ClientOptions
                .builder()
                .bearerAccessToken("f02c39f470ed49e5aec0d848ce456cc0")
                .build());

        // to verify other countries, free services like https://receive-smss.com/ can be used
        String germanyPhoneNumber = "+573134921228";

        MailjetRequest mailjetRequest = new MailjetRequest(SmsSend.resource)
                .property(SmsSend.FROM, "MJPilot")
                .property(SmsSend.TO, germanyPhoneNumber)
                .property(SmsSend.TEXT, "Have a nice SMS flight with Mailjet!");

        // act
        MailjetResponse response = mailjetClient.post(mailjetRequest);

        // assert
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Message is being sent", response.getData().getJSONObject(0).getJSONObject("Status").getString("Description"));
        Assert.assertEquals("Have a nice SMS flight with Mailjet!", response.getString("Text"));
    }

    @Test
    public void sendSms_to_a_List() throws MailjetException {
        // arrange
        MailjetClient mailjetClient = new MailjetClient(ClientOptions
                .builder()
                .bearerAccessToken("f02c39f470ed49e5aec0d848ce456cc0")
                .build());

        List<String> listOfNumbers = readRawDataFromCSV("src/test/resources/numbers.csv");

        for(String number : listOfNumbers) {
            MailjetRequest mailjetRequest = new MailjetRequest(SmsSend.resource)
                    .property(SmsSend.FROM, "MJPilot")
                    .property(SmsSend.TO, number)
                    .property(SmsSend.TEXT, "Have a nice SMS flight with Mailjet!");

            // act
            MailjetResponse response = mailjetClient.post(mailjetRequest);
            System.out.println(number + " " + response.getStatus());
        }
    }

    private List<String> readRawDataFromCSV(String fileName) {
        List<String> data = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            String line = br.readLine();
            while (line != null) {
                data.add(line);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return data;
    }
}
