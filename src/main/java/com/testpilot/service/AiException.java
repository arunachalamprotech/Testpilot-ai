package com.testpilot.service;

/** Problem while asking the AI model (server down, timeout, bad answer). The message is safe to show to the user. */
public class AiException extends Exception {
    public AiException(String message) {
        super(message);
    }
}
