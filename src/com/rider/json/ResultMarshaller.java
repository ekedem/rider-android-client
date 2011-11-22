package com.rider.json;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

import com.rider.model.ServerResult;


public interface ResultMarshaller {

    public ServerResult unmarshal(BufferedReader reader, String resource) throws  IOException;

}
