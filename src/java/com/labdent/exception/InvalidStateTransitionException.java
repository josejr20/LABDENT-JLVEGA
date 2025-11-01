package com.labdent.exception;

public class InvalidStateTransitionException extends RuntimeException {
    private final String currentState;
    private final String targetState;
    
    public InvalidStateTransitionException(String currentState, String targetState) {
        super(String.format("Transición inválida de %s a %s", currentState, targetState));
        this.currentState = currentState;
        this.targetState = targetState;
    }
    
    public String getCurrentState() {
        return currentState;
    }
    
    public String getTargetState() {
        return targetState;
    }
}