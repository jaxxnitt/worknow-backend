package com.worknow.backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.Collections;

public class GoogleAuthService {

    private static final String CLIENT_ID =
            "YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com";

    public static GoogleIdToken.Payload verify(String idTokenString)
            throws Exception {

        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance()
                )
                        .setAudience(Collections.singletonList(CLIENT_ID))
                        .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new RuntimeException("Invalid ID token");
        }

        return idToken.getPayload();
    }
}
