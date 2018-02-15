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

## General architecture
The software is composed by two modules: 

- ais-adaptor-api
- ais-adaptor-domain

### ais-adaptor-api
Is the entry point that starts the message processing and it focus itself in 
translating the AIS Message from the library specific on to a project domain
specific so to be independent from the AIS library implementation.

The module will load the messages from a file or from a socket and will translate
it to the InternalAISMessage so to be processed by the domain module   

### ais-adaptor-domain
Is the module that translates the InternalAISMessage into a CISE Message and 
that delivers the message to the CISE Gateway

## Message Mapping 

The library that has been used to read the AIS Messages is [tbsalling](https://github.com/tbsalling/aismessages/).

In this library messages type are mapped in an enum bound to the message type number.
Our initial implementation will treat only the following message types dropping 
all the messages with a different message type: 

* PositionReportClassAScheduled(1),
* PositionReportClassAAssignedSchedule(2),
* PositionReportClassAResponseToInterrogation(3),
* ShipAndVoyageRelatedData(5),
