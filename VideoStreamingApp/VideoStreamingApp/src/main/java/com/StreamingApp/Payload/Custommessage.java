package com.StreamingApp.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class Custommessage {
    private String message;
    private boolean success=false;


}
