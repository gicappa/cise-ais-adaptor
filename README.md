# CISE AIS Adaptor

## Overview 

The CISE AIS Adaptor is a sofwtare meant to read/consume AIS Tracks, 
process them translating them into a CISE Data Model complian message 
and to send PUSH messages to a pre-configured number of consumers.

The standard INPUT is an AIS NMEA transcription and the OUTPUT a number 
of CISE PUSH messages.

There will be two main AIS NMEA message type supported:
* Voyage Message Type 
* Positional Message Type

It should be possible to decide the ouput PUSH message frequency specifying:
- a number of AIS messages that will compose a single PUSH
- an amount of time after which a PUSH message will be sent

## Message Mapping 
